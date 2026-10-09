package driverFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.LoggerFactory;

public class GetFirefoxDriver implements DriverFactory {
    private static final Logger log = LogManager.getLogger(GetFirefoxDriver.class);

    private static WebDriver driver = null;

    public static WebDriver getDriver() {
        if (driver == null) {
            log.info("🦊 Creating new Firefox driver with --private");
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--private");
            try {
                driver = new FirefoxDriver(options);
                log.debug("✅ Firefox driver created");
            } catch (WebDriverException e) {
                log.error("❌ Failed to create Firefox driver. Check Firefox and geckodriver versions", e);
                throw e;
            }
        } else {
            log.debug("♻️ Reusing existing Firefox driver");
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            log.info("🛑 Quitting Firefox driver");
            try {
                driver.quit();
                log.debug("🧹 Firefox driver quit successfully");
            } catch (WebDriverException e) {
                log.error("❌ Error while quitting Firefox driver", e);
                throw e;
            } finally {
                driver = null;
            }
        } else {
            log.debug("⏭️ quitDriver called but no driver is active");
        }
    }
}