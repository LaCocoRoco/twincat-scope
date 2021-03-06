package twincat.ads.datatype;

import twincat.ads.AdsClient;
import twincat.ads.AdsException;
import twincat.ads.common.Variable;
import twincat.ads.constant.DataType;

public class STRING extends Variable {
	/*********************************/
	/**** local constant variable ****/
	/*********************************/

	private static final String NULL_TERMINATION = "\u0000";

	/*********************************/
	/********** constructor **********/
	/*********************************/

	public STRING(AdsClient adsClient, int symbolHandle) {
		super(adsClient, DataType.STRING.size, symbolHandle);
	}

	public STRING(AdsClient adsClient, int symbolHandle,short dataSize) {
		super(adsClient, dataSize, symbolHandle);
	}

	public STRING(AdsClient adsClient, int indexGroup, int indexOffset) throws AdsException {
		super(adsClient, DataType.STRING.size, indexGroup, indexOffset);
	}

	public STRING(AdsClient adsClient, int indexGroup, int indexOffset, short dataSize) throws AdsException {
		super(adsClient, dataSize, indexGroup, indexOffset);
	}
	
	public STRING(AdsClient adsClient, String symbolName) throws AdsException {
		super(adsClient, DataType.STRING.size, adsClient.readHandleOfSymbolName(symbolName));
	}
	
	public STRING(AdsClient adsClient, String symbolName, short dataSize) throws AdsException {
		super(adsClient, dataSize, adsClient.readHandleOfSymbolName(symbolName));
	}
		
	/*********************************/
	/******** override method ********/
	/*********************************/

	@Override
	public DataType getDataType() {
		return DataType.STRING;
	}

	@Override
	public boolean toBoolean() {
		return !STRING.arrayToString(data).isEmpty() ? true : false;
	}

	@Override
	public byte toByte() {
		return (byte) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public short toShort() {
		return (short) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public int toInteger() {
		return (int) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public long toLong() {
		return (long) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public float toFloat() {
		return (float) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public double toDouble() {
		return (double) (!STRING.arrayToString(data).isEmpty() ? 1 : 0);
	}

	@Override
	public String toString() {
		return STRING.arrayToString(data);
	}

	@Override
	public Variable write(boolean value) throws AdsException {
		super.write(STRING.valueToArray(Boolean.toString(value), true));
		return this;
	}

	@Override
	public Variable write(byte value) throws AdsException {
		super.write(STRING.valueToArray(Byte.toString(value), true));
		return this;
	}

	@Override
	public Variable write(short value) throws AdsException {
		super.write(STRING.valueToArray(Short.toString(value), true));
		return this;
	}

	@Override
	public Variable write(int value) throws AdsException {
		super.write(STRING.valueToArray(Integer.toString(value), true));
		return this;
	}

	@Override
	public Variable write(long value) throws AdsException {
		super.write(STRING.valueToArray(Long.toString(value), true));
		return this;
	}

	@Override
	public Variable write(float value) throws AdsException {
		super.write(STRING.valueToArray(Float.toString(value), true));
		return this;
	}

	@Override
	public Variable write(double value) throws AdsException {
		super.write(STRING.valueToArray(Double.toString(value), true));
		return this;
	}

	@Override
	public Variable write(String value) throws AdsException {
		String data = value.concat(NULL_TERMINATION);
		super.write(STRING.valueToArray(data, true));
		return this;
	}

	/*********************************/
	/** public static final method ***/
	/*********************************/

	public static final String arrayToString(byte[] data) {
		String value = new String();
		for (int i = 0; i < data.length && data[i] != 0; ++i)
			value += (char) data[i];
		return value;
	}

	public static final byte[] stringToArray(String data) {
		return STRING.valueToArray(data, false);
	}

	public static final byte[] valueToArray(String data, boolean termination) {
		if (termination) data += NULL_TERMINATION;
		return data.getBytes();
	}
}
