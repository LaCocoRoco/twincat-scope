package twincat.ads.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UploadInfo {
    /*********************************/
    /**** local constant variable ****/
    /*********************************/

    private static final int INFO_BUFFER_LENGTH = 24;
   
    /*********************************/
    /******** global variable ********/
    /*********************************/

    private int symbolCount = 0;
  
    private int symbolLength = 0;
    
    private int dataTypeCount = 0;
    
    private int dataTypeLength = 0;
    
    private int extraCount = 0;
    
    private int extraLength = 0;
    
    /*********************************/
    /********** constructor **********/
    /*********************************/

    public UploadInfo() {
        /* empty */
    }
   
    public UploadInfo(byte[] buffer) {
        parseUploadInfo(buffer);
    }

    /*********************************/
    /******** setter & getter ********/
    /*********************************/
    
    public int getSymbolCount() {
        return symbolCount;
    }

    public void setSymbolCount(int symbolCount) {
        this.symbolCount = symbolCount;
    }

    public int getSymbolLength() {
        return symbolLength;
    }

    public void setSymbolLength(int symbolLength) {
        this.symbolLength = symbolLength;
    }

    public int getDataTypeCount() {
        return dataTypeCount;
    }

    public void setDataTypeCount(int dataTypeCount) {
        this.dataTypeCount = dataTypeCount;
    }

    public int getDataTypeLength() {
        return dataTypeLength;
    }

    public void setDataTypeLength(int dataTypeLength) {
        this.dataTypeLength = dataTypeLength;
    }

    public int getExtraCount() {
        return extraCount;
    }

    public void setExtraCount(int extraCount) {
        this.extraCount = extraCount;
    }

    public int getExtraLength() {
        return extraLength;
    }

    public void setExtraLength(int extraLength) {
        this.extraLength = extraLength;
    }
    
    /*********************************/
    /********* public method *********/
    /*********************************/
   
    public void parseUploadInfo(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        
        if (byteBuffer.remaining() >= INFO_BUFFER_LENGTH) {
            symbolCount    = byteBuffer.getInt();
            symbolLength   = byteBuffer.getInt();
            dataTypeCount  = byteBuffer.getInt();
            dataTypeLength = byteBuffer.getInt();
            extraCount     = byteBuffer.getInt();
            extraLength    = byteBuffer.getInt();
        }
    }
}
