package pages.baseTest;

import driverFactory.GetChromeDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;
import utils.jsonFileManager;

public class BaseTest {
    private static final Logger log = LogManager.getLogger(BaseTest.class);
    public static jsonFileManager jsonFileManager;

    public static WebDriver driver;
    public SoftAssert softAssert;

    @BeforeMethod
    public void setUp() {
        jsonFileManager = new jsonFileManager("src/main/resources/file.json");
        log.info("🚀 Starting Chrome driver");
        driver = GetChromeDriver.getDriver();
        log.debug("🔍 Driver created: {}", driver);
        driver.get("https://www.saucedemo.com");
    }

    @AfterMethod
    public void tearDown() {
        if (driver == null) {
            log.warn("⚠️ tearDown called but BaseTest.driver is null");
        }
        log.info("🛑 Quitting Chrome driver");
        GetChromeDriver.quitDriver();
        driver = null;
        log.debug("🧹 Driver quit and references cleared");
    }
}