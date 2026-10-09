package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BasePage {
    private static final Logger log = LogManager.getLogger(BasePage.class);

    public WebDriver driver;
    public WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement findElement(By locator) {
        return findElement(locator, Duration.ofSeconds(10));
    }

    public WebElement findElement(By locator, Duration duration) {
        wait = new WebDriverWait(driver, duration);
        log.debug("🔎 Waiting for element: {}", locator);
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("❌ Element not visible within {} seconds: {}", duration.getSeconds(), locator);
            throw e;
        }
    }

    public List<WebElement> findElements(By locator) {
        return findElements(locator, Duration.ofSeconds(10));
    }

    public List<WebElement> findElements(By locator, Duration duration) {
        wait = new WebDriverWait(driver, duration);
        log.debug("🔎 Waiting for elements: {}", locator);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("❌ Elements not visible within {} seconds: {}", duration.getSeconds(), locator);
            throw e;
        }
        List<WebElement> elements = driver.findElements(locator);
        log.debug("📦 Found {} elements for {}", elements.size(), locator);
        return elements;
    }

    public boolean navigateToPage(String redirectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        log.debug("🌐 Current URL: {}", currentUrl);
        boolean matches = Objects.equals(currentUrl, redirectedUrl);
        if (!matches) {
            log.warn("⚠️ Expected URL {} but was {}", redirectedUrl, currentUrl);
        }
        return matches;
    }
}