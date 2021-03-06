package twincat.ads.junit;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twincat.TwincatLogger;
import twincat.ads.AdsClient;
import twincat.ads.AdsException;
import twincat.ads.common.SymbolInfo;
import twincat.ads.constant.AmsNetId;
import twincat.ads.constant.AmsPort;

/** 
 * VAR_GLOBAL
 *     junit_array_small       :ARRAY[0..2] OF INT;
 *     junit_array_simple      :ARRAY[0..2, 2..4, 4..6] OF INT;
 *     junit_array_complex     :ARRAY[0..2, 4..8] OF TON;
 *     junit_array_index_var   :ARRAY[junit_a..junit_b, junit_c..junit_d] OF INT;
 * END_VAR
 *
 * VAR_GLOBAL CONSTANT
 *     junit_a                 :INT := 2;
 *     junit_b                 :INT := 5;
 *     junit_c                 :INT := 3;
 *     junit_d                 :INT := 20;
 * END_VAR
 */

public class SymbolInfoBySymbolNameUnitTest {
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
            adsClient.setAmsPort(AmsPort.TC2PLC1);

            SymbolInfo symbolInfo = adsClient.readSymbolInfoBySymbolName(".junit_array_index_var");

            logger.info("InfoLength : " + symbolInfo.getLength());
            logger.info("IndexGroup : " + symbolInfo.getIndexGroup());
            logger.info("IndexOffset: " + symbolInfo.getIndexOffset());
            logger.info("DataSize   : " + symbolInfo.getDataSize());
            logger.info("DataType   : " + symbolInfo.getDataType());
            logger.info("SymbolFlag : " + symbolInfo.getSymbolFlag());
            logger.info("SymbolName : " + symbolInfo.getSymbolName());
            logger.info("Type       : " + symbolInfo.getType());
            logger.info("Comment    : " + symbolInfo.getComment());
        } catch (AdsException e) {
            logger.info(e.getAdsErrorMessage());
        }
    }

    @After
    public void stop() throws AdsException {
        adsClient.close();
    }
}
