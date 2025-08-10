package com.tunahanozturk.tests;
import com.tunahanozturk.base.DriverFactory;
import com.tunahanozturk.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.time.Duration;
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

    @AfterMethod(alwaysRun = true) // If the test result fails, take SS
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.takeScreenshot(driver, result.getName());
        }
        DriverFactory.quit();
    }

    @Test
    public void filter_QAJobs_by_Location_and_Department_thenVerify() {
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
        // I ran the test as "total<0" to see that a screenshot was taken in the fail scenario.

        // Wait until the filter is reflected on the cards
        wait.until(driver -> {
            // Card locations
            java.util.List<org.openqa.selenium.WebElement> cards =
                    driver.findElements(By.xpath("//div[@class='position-list-item-wrapper bg-light']"));

            // if 0 card
            if (cards.size() == 0) {
                return false;
            }

            // Checking
            for (org.openqa.selenium.WebElement card : cards) {
                try {
                    String position = card.findElement(By.xpath(".//p[contains(@class,'position-title')]")).getText().trim();
                    String department = card.findElement(By.xpath(".//span[contains(@class,'position-department')]")).getText().trim();
                    String location = card.findElement(By.xpath(".//div[contains(@class,'position-location')]")).getText().trim();

                    // I use !contains here and return false on purpose to fail if any card does not meet the criteria, ensuring all filtered cards have correct information.
                    if (!position.contains("Quality Assurance")) {
                        return false;
                    }
                    if (!department.contains("Quality Assurance")) {
                        return false;
                    }
                    if (!location.contains("Istanbul, Turkiye")) {
                        return false;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    return false;
                }
            }
            return true;
        });

        // View Role button click and Lever page verification
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//div[@class='position-list-item-wrapper bg-light']"), 0));

        String mainWindow = driver.getWindowHandle();
        java.util.List<WebElement> cards = driver.findElements(
                By.xpath("//div[@class='position-list-item-wrapper bg-light']"));

        for (int i = 1; i <= cards.size(); i++) {
            // Card
            WebElement card = driver.findElement(
                    By.xpath("(//div[@class='position-list-item-wrapper bg-light'])[" + i + "]"));
            new Actions(driver).moveToElement(card).perform();

            // Wait for View Role button and click
            By viewRoleBtn = By.xpath("(//div[@class='position-list-item-wrapper bg-light'])[" + i + "]//a[@class='btn btn-navy rounded pt-2 pr-5 pb-2 pl-5' and normalize-space()='View Role']");
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(viewRoleBtn));

            try {
                wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }

            // Move to new tab
            wait.until(d -> d.getWindowHandles().size() > 1);
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(mainWindow)) {  //move to "non-homepage" page
                    driver.switchTo().window(handle);
                    break;
                }
            }

            // jobs.lever.co check
            String url = driver.getCurrentUrl();
            assertTrue(url.contains("jobs.lever.co"),
                    "Not redirected to Lever application form.");

            // Close tab
            driver.close();
            driver.switchTo().window(mainWindow);
        }

    }
}
