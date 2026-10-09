package driverFactory;

import org.openqa.selenium.WebDriver;

public interface DriverFactory {

    public static WebDriver getDriver() {
        return null;
    }

    public static void quitDriver(){}
}
