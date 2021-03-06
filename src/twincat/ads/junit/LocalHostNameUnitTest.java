package twincat.ads.junit;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twincat.TwincatLogger;
import twincat.ads.AdsClient;
import twincat.ads.AdsException;
import twincat.ads.constant.AmsNetId;
import twincat.ads.constant.AmsPort;

public class LocalHostNameUnitTest {
    private final AdsClient adsClient = new AdsClient();
    private final Logger logger = TwincatLogger.getLogger();

    @Before
    public void start() {
        adsClient.open();
    }

    @Test
    public void test() {
        try {
            adsClient.setAmsNetId(AmsNetId.LOCAL);
            adsClient.setAmsPort(AmsPort.SYSTEMSERVICE);

            logger.info(adsClient.readLocalHostName());
        } catch (AdsException e) {
            logger.info(e.getAdsErrorMessage());
        }
    }

    @After
    public void stop() throws AdsException {
        adsClient.close();
    }
}
