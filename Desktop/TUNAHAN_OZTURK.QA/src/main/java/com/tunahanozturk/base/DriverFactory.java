package com.tunahanozturk.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void init() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        DRIVER.set(driver);
    }

    public static WebDriver get() {
        return DRIVER.get();
    }

    public static void quit() {
        WebDriver d = DRIVER.get();
        if (d != null) {
            d.quit();
            DRIVER.remove();
        }
    }
}
