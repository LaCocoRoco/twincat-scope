package twincat.ads.junit;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twincat.ads.Ads;
import twincat.ads.AdsException;
import twincat.ads.AdsLogger;
import twincat.ads.constants.AmsPort;

public class AdsAmsPortUnitTest {
	Ads ads = new Ads();
	Logger logger = AdsLogger.getLogger();
	
	@Before
	public void startAds() {
		ads.open();
	}
	
	@Test
	public void adsAmsPortUnitTest() {
        for (AmsPort amsPort : AmsPort.values()) {
    		try {
    			ads.setAmsPort(amsPort);
    			ads.readDeviceInfo();
    			logger.info("OK : " + amsPort);
    		} catch (AdsException e) {
    			logger.info("NOK: " + amsPort);
    		}
        }
	}

	@After
	public void stopAds() throws AdsException {
		ads.close();
	}
}