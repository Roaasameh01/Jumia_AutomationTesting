package driverFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.LoggerFactory;
import pages.BasePage;

public class GetChromeDriver implements DriverFactory {
    private static final Logger log = LogManager.getLogger(GetChromeDriver.class);

    private static WebDriver driver = null;

    public static WebDriver getDriver() {
        if (driver == null) {
            log.info("🌐 Creating new Chrome driver with --incognito");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            try {
                driver = new ChromeDriver(options);
                log.debug("✅ Chrome driver created");
            } catch (WebDriverException e) {
                log.error("❌ Failed to create Chrome driver. Check Chrome and ChromeDriver versions", e);
                throw e;
            }
        } else {
            log.debug("♻️ Reusing existing Chrome driver");
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            log.info("🛑 Quitting Chrome driver");
            try {
                driver.quit();
                log.debug("🧹 Chrome driver quit successfully");
            } catch (WebDriverException e) {
                log.error("❌ Error while quitting Chrome driver", e);
                throw e;
            } finally {
                driver = null;
            }
        } else {
            log.debug("⏭️ quitDriver called but no driver is active");
        }
    }
}