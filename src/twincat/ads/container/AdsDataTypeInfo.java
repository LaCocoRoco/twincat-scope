package twincat.ads.container;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import twincat.ads.constants.AdsDataType;
import twincat.ads.constants.AdsDataTypeFlag;
import twincat.ads.datatype.STRING;

public class AdsDataTypeInfo {
    /*************************/
    /** constant attributes **/
    /*************************/

    private static final int INFO_BUFFER_LENGTH = 38;

    /*************************/
    /*** global attributes ***/
    /*************************/

    private int length = 0;

    private int version = 0;

    private int hashValue = 0;

    private int typeHashValue = 0;

    private int dataSize = 0;

    private int offset = 0;

    private String dataTypeName = new String();

    private String comment = new String();

    private String type = new String();

    private AdsDataType dataType = AdsDataType.UNKNOWN;

    private AdsDataTypeFlag dataTypeFlag = AdsDataTypeFlag.UNKNOWN;

    private final List<AdsDataTypeInfo> subDataTypeInfoList = new ArrayList<AdsDataTypeInfo>();

    /*************************/
    /****** constructor ******/
    /*************************/

    public AdsDataTypeInfo(byte[] buffer) {
        getDataTypeInfo(buffer, 0);
    }

    public AdsDataTypeInfo(byte[] buffer, int index) {
        getDataTypeInfo(buffer, index);
    }

    /*************************/
    /**** setter & getter ****/
    /*************************/

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getHashValue() {
        return hashValue;
    }

    public void setHashValue(int hashValue) {
        this.hashValue = hashValue;
    }

    public int getTypeHashValue() {
        return typeHashValue;
    }

    public void setTypeHashValue(int typeHashValue) {
        this.typeHashValue = typeHashValue;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AdsDataType getDataType() {
        return dataType;
    }

    public void setDataType(AdsDataType dataType) {
        this.dataType = dataType;
    }

    public AdsDataTypeFlag getDataTypeFlag() {
        return dataTypeFlag;
    }

    public void setDataTypeFlag(AdsDataTypeFlag dataTypeFlag) {
        this.dataTypeFlag = dataTypeFlag;
    }

    public List<AdsDataTypeInfo> getSubDataTypeInfoList() {
        return subDataTypeInfoList;
    }

    /*************************/
    /******** private ********/
    /*************************/

    @SuppressWarnings("unused")
    private void getDataTypeInfo(byte[] buffer, int index) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        if (byteBuffer.remaining() >= index) {
            byteBuffer.position(index);

            if (byteBuffer.remaining() >= INFO_BUFFER_LENGTH) {
                length = byteBuffer.getInt();
                version = byteBuffer.getInt();
                hashValue = byteBuffer.getInt();
                typeHashValue = byteBuffer.getInt();
                dataSize = byteBuffer.getInt();
                offset = byteBuffer.getInt();
                int dataType = byteBuffer.getInt();
                int dataTypeFlag = byteBuffer.getInt();
                int nameLength = byteBuffer.getShort() + 1;
                int typeLength = byteBuffer.getShort() + 1;
                int commentLength = byteBuffer.getShort() + 1;
                int arrayDimension = byteBuffer.getShort();
                int subItemCount = byteBuffer.getShort();
                this.dataType = AdsDataType.getByValue(dataType);
                this.dataTypeFlag = AdsDataTypeFlag.getByValue(dataTypeFlag);

                if (byteBuffer.remaining() >= nameLength) {
                    byte[] symbolNameBuffer = new byte[nameLength];
                    byteBuffer.get(symbolNameBuffer, 0, nameLength);
                    dataTypeName = STRING.arrayToValue(symbolNameBuffer);
                }

                if (byteBuffer.remaining() >= typeLength) {
                    byte[] typeBuffer = new byte[typeLength];
                    byteBuffer.get(typeBuffer, 0, typeLength);
                    type = STRING.arrayToValue(typeBuffer);
                }

                if (byteBuffer.remaining() >= commentLength) {
                    byte[] commentBuffer = new byte[commentLength];
                    byteBuffer.get(commentBuffer, 0, commentLength);
                    comment = STRING.arrayToValue(commentBuffer);
                }

                int subItemIndex = byteBuffer.position();
                for (int i = 0; i < subItemCount; i++) {
                    AdsDataTypeInfo dataTypeInfo = new AdsDataTypeInfo(buffer, subItemIndex);
                    subDataTypeInfoList.add(dataTypeInfo);
                    subItemIndex += dataTypeInfo.getLength();
                }
            }
        }
    }

    /*************************/
    /********* public ********/
    /*************************/
 
    public List<AdsSymbol> getDataTypeSymbolList(List<AdsDataTypeInfo> dataTypeInfoList) {
        List<AdsSymbol> symbolList = new ArrayList<AdsSymbol>();

        // parse type info
        AdsTypeInfo typeInfo = new AdsTypeInfo(type);

        // test
        if (typeInfo.isPointer()) return symbolList;
        
        // get named array list
        List<String> namedArrayList = new ArrayList<String>();
        if (!typeInfo.getTypeArray().isEmpty()) {
            namedArrayList.addAll(typeInfo.getNamedTypeArray());
        }

        // is complex data type
        if (dataType.equals(AdsDataType.BIGTYPE)) {

            // get type symbol list from type info
            List<AdsSymbol> typeSymbolList = new ArrayList<AdsSymbol>();
            for (AdsDataTypeInfo dataTypeInfo : dataTypeInfoList) {
                if (typeInfo.getTypeName().equals(dataTypeInfo.getDataTypeName())) {
                    typeSymbolList = dataTypeInfo.getDataTypeSymbolList(dataTypeInfoList);
                    break;
                }
            }

            // is data type
            if (dataTypeFlag.equals(AdsDataTypeFlag.DATA_TYPE)) {
                for (AdsSymbol typeSymbol : typeSymbolList) {
                    if (!namedArrayList.isEmpty()) {
                        // add array
                        for (String namedArray : namedArrayList) {
                            // add symbol
                            AdsSymbol symbol = new AdsSymbol();
                            symbol.setName(namedArray + typeSymbol.getName());
                            symbol.setType(typeSymbol.getType());
                            symbolList.add(symbol);
                        }
                    } else {
                        // add symbol
                        AdsSymbol symbol = new AdsSymbol();
                        symbol.setName(typeSymbol.getName());
                        symbol.setType(typeSymbol.getType());
                        symbolList.add(symbol);
                    }
                }
            }

            // is data item
            if (dataTypeFlag.equals(AdsDataTypeFlag.DATA_ITEM)) {
                for (AdsSymbol typeSymbol : typeSymbolList) {
                    if (!namedArrayList.isEmpty()) {
                        // add array
                        for (String namedArray : namedArrayList) {
                            // add symbol
                            AdsSymbol symbol = new AdsSymbol();
                            symbol.setName(dataTypeName + namedArray + "." + typeSymbol.getName());
                            symbol.setType(typeSymbol.getType());
                            symbolList.add(symbol);
                        }
                    } else {
                        // add symbol
                        AdsSymbol symbol = new AdsSymbol();
                        symbol.setName(dataTypeName + "." + typeSymbol.getName());
                        symbol.setType(typeSymbol.getType());
                        symbolList.add(symbol);
                    }
                }
            }
        }

        // is primitive data type
        if (!dataType.equals(AdsDataType.BIGTYPE)) {
            if (!namedArrayList.isEmpty()) {
                // add array
                for (String namedArray : namedArrayList) {
                    // add symbol
                    AdsSymbol symbol = new AdsSymbol();
                    symbol.setName(dataTypeName + namedArray);
                    symbol.setType(dataType);
                    symbolList.add(symbol);
                }
            } else {
                // add symbol
                AdsSymbol symbol = new AdsSymbol();
                symbol.setName(dataTypeName);
                symbol.setType(dataType);
                symbolList.add(symbol);
            }
        }

        // collect sub symbol data
        List<AdsSymbol> subSymbolList = new ArrayList<AdsSymbol>();
        for (AdsDataTypeInfo dataTypeInfo : subDataTypeInfoList) {
            subSymbolList.addAll(dataTypeInfo.getDataTypeSymbolList(dataTypeInfoList));
        }
   
        for (AdsSymbol subSymbol : subSymbolList) { 
            // add symbol
            AdsSymbol symbol = new AdsSymbol();
            symbol.setName(subSymbol.getName());
            symbol.setType(subSymbol.getType());
            symbolList.add(symbol);
        }

        return symbolList;
    }
}
