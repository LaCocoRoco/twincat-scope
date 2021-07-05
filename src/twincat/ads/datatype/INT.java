package twincat.ads.datatype;

import twincat.ads.AdsClient;
import twincat.ads.AdsException;
import twincat.ads.enums.AdsDataType;

public class INT extends INT16 {
	/*************************/
	/****** constructor ******/
	/*************************/

	public INT(AdsClient ads, int symbolHandle) {
		super(ads, symbolHandle);
	}

	public INT(AdsClient ads, int indexGroup, int indexOffset) throws AdsException {
		super(ads, indexGroup, indexOffset);
	}
	
	public INT(AdsClient ads, String symbolName) throws AdsException {
		super(ads, symbolName);
	}
				
	/*************************/
	/******** override *******/
	/*************************/
	
	@Override	
	public AdsDataType getDataType() {
		return AdsDataType.INT;
	}
}
