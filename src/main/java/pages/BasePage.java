package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BasePage {

    protected static final Logger log = LogManager.getLogger(BasePage.class);

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public WebElement findElement(By locator) {
        return findElement(locator, Duration.ofSeconds(15));
    }

    public WebElement findElement(By locator, Duration duration) {
        WebDriverWait customWait = new WebDriverWait(driver, duration);
        log.debug("Waiting for element: {}", locator);
        try {
            return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element not visible within {} seconds: {}", duration.getSeconds(), locator);
            throw e;
        }
    }

    public List<WebElement> findElements(By locator) {
        return findElements(locator, Duration.ofSeconds(15));
    }

    public List<WebElement> findElements(By locator, Duration duration) {
        WebDriverWait customWait = new WebDriverWait(driver, duration);
        log.debug("Waiting for elements: {}", locator);
        try {
            customWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            log.error("Elements not found within {} seconds: {}", duration.getSeconds(), locator);
            throw e;
        }
        List<WebElement> elements = driver.findElements(locator);
        log.debug("Found {} elements for {}", elements.size(), locator);
        return elements;
    }

    public void click(By locator) {
        log.debug("Clicking on: {}", locator);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    public void click(WebElement element) {
        log.debug("Clicking on element");
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void type(By locator, String text) {
        log.debug("Typing '{}' into: {}", text, locator);
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        String text = findElement(locator).getText().trim();
        log.debug("Got text '{}' from: {}", text, locator);
        return text;
    }

    public String getText(WebElement element) {
        return element.getText().trim();
    }

    public boolean isDisplayed(By locator) {
        try {
            return findElement(locator, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void jsClick(By locator) {
        WebElement element = findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void closePopupsIfExist() {
        try {
            By closeBtn = By.cssSelector("button.cls[aria-label='newsletter_popup_close-cta']");
            if (isDisplayed(closeBtn)) {
                click(closeBtn);
                log.info("Closed Newsletter popup");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.debug("No newsletter popup found");
        }

        try {
            By acceptCookies = By.cssSelector("button[class*='accept'], button#accept");
            if (isDisplayed(acceptCookies)) {
                click(acceptCookies);
                log.info("Accepted cookies");
                Thread.sleep(500);
            }
        } catch (Exception e) {
            log.debug("No cookies banner found");
        }
    }

    public boolean navigateToPage(String expectedUrl) {
        String currentUrl = driver.getCurrentUrl();
        log.debug("Current URL: {}", currentUrl);
        return Objects.equals(currentUrl, expectedUrl);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}