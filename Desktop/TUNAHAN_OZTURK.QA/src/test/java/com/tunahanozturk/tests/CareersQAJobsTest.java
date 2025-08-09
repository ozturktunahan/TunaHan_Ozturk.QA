package com.tunahanozturk.tests;

import com.tunahanozturk.base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class CareersQAJobsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        DriverFactory.init();
        driver = DriverFactory.get();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }

    @Test
    public void verifyQADepartmentAndFilterByIstanbul() {
        driver.get("https://useinsider.com/careers/quality-assurance/");

        // "See all QA jobs" button click
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'See all QA jobs')]"))).click();

        // Wait auto Department fill "Quality Assurance" (I'm waiting because it came as "All" at first)
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("select2-filter-by-department-container"), "Quality Assurance"));

        // Wait to see Location: Istanbul, Turkiye
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("select2-filter-by-location-container"))).click();

        // Select "Istanbul, Turkiye"
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@id,'select2-filter-by-location') and contains(.,'Istanbul, Turkiye')]"))).click();

        // Verify that the selection was actually implemented
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("select2-filter-by-location-container"), "Istanbul, Turkiye"));

        // Results > 0 ?
        WebElement totalElBefore = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("deneme")));
        int before = Integer.parseInt(totalElBefore.getText().trim());

        // Wait until the number changes (after filter)
        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElementLocated(
                        By.id("deneme"), String.valueOf(before))));

        // Get the number after the filter as total
        int total = Integer.parseInt(driver.findElement(By.id("deneme")).getText().trim());
        assertTrue(total > 0, "Results should be greater than 0");

    }
}
