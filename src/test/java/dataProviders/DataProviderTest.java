package dataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import pages.BasePage;

public class DataProviderTest {
    private static final Logger log = LogManager.getLogger(DataProviderTest.class);

    @DataProvider (name = "validCredentails")
    public Object[][] validCredentails() {
        log.info("📋 Supplying validCredentails data");
        Object[][] data = new Object[][]{};
        if (data.length == 0) {
            log.warn("⚠️ validCredentails returned no rows");
        }
        return data;
    }

    @DataProvider (name = "invalidCredentails")
    public Object[][] invalidCredentails() {
        log.info("📋 Supplying invalidCredentails data");
        Object[][] data = new Object[][]{};
        if (data.length == 0) {
            log.warn("⚠️ invalidCredentails returned no rows");
        }
        return data;
    }
}