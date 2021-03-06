package twincat.ads.datatype;

import twincat.ads.AdsClient;
import twincat.ads.AdsException;
import twincat.ads.common.Variable;
import twincat.ads.constant.DataType;
import twincat.ads.constant.AdsError;

public class INT8 extends Variable {
	/*********************************/
	/********** constructor **********/
	/*********************************/

	public INT8(AdsClient adsClient, int symbolHandle) {
		super(adsClient, DataType.INT8.size, symbolHandle);
	}

	public INT8(AdsClient adsClient, int indexGroup, int indexOffset) throws AdsException {
		super(adsClient, DataType.INT8.size, indexGroup, indexOffset);
	}
	
	public INT8(AdsClient adsClient, String symbolName) throws AdsException {
		super(adsClient, DataType.INT8.size, adsClient.readHandleOfSymbolName(symbolName));
	}
	
	/*********************************/
	/******** override method ********/
	/*********************************/

	@Override
	public DataType getDataType() {
		return DataType.INT8;
	}

	@Override
	public boolean toBoolean() {
		return INT8.arrayToByte(data) > 0 ? true : false;
	}

	@Override
	public byte toByte() {
		return (byte) INT8.arrayToByte(data);
	}

	@Override
	public short toShort() {
		return (short) INT8.arrayToByte(data);
	}

	@Override
	public int toInteger() {
		return (int) INT8.arrayToByte(data);
	}

	@Override
	public long toLong() {
		return (long) INT8.arrayToByte(data);
	}

	@Override
	public float toFloat() {
		return (float) INT8.arrayToByte(data);
	}

	@Override
	public double toDouble() {
		return (double) INT8.arrayToByte(data);
	}

	@Override
	public String toString() {
		return Byte.toString(INT8.arrayToByte(data));
	}

	@Override
	public Variable write(boolean value) throws AdsException {
		byte data = value ? (byte) 1 : (byte) 0;
		super.write(INT8.byteToArray(data));
		return this;
	}

	@Override
	public Variable write(byte value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(short value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(int value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(long value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(float value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(double value) throws AdsException {
		super.write(INT8.byteToArray((byte) value));
		return this;
	}

	@Override
	public Variable write(String value) throws AdsException {
		try  {
			byte data = Byte.parseByte(value);
			super.write(INT8.byteToArray(data));
		} catch(NumberFormatException e) {
			throw new AdsException(AdsError.VARIABLE_WRITE_PARSE_ERROR);
		}
		return this;
	}

	/*********************************/
	/** public static final method ***/
	/*********************************/

	public static final byte arrayToByte(byte[] data) {
		if (data.length != DataType.INT8.size) return 0;
		return data[0];
	}

	public static final byte[] byteToArray(byte data) {
		return new byte[] { data };
	}
}
