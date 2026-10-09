package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.File;

public class Screenshot {

    private static final Logger log = LogManager.getLogger(Screenshot.class);

    /**
     * Takes a screenshot of the current page.
     *
     * @param driver the driver to capture from
     * @return the screenshot file, or null if the driver itself is null
     */
    public static File takeScreenshot(WebDriver driver) {
        if (driver == null) {
            log.warn("⚠️ Cannot take screenshot: driver is null");
            return null;
        }

        log.info("📸 Taking screenshot");
        try {
            File image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            log.info("✅ Screenshot saved: {}", image.getAbsolutePath());
            return image;
        } catch (ClassCastException e) {
            log.error("❌ Driver {} does not support screenshots", driver.getClass().getSimpleName(), e);
            throw e;
        } catch (WebDriverException e) {
            log.error("❌ Failed to take screenshot", e);
            throw e;
        }
    }
}