package com.tunahanozturk.tests;

import com.tunahanozturk.base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.*;
import static org.testng.Assert.assertTrue;

public class NavigateToCareersTest {

    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        DriverFactory.init();
        driver = DriverFactory.get();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }

    @Test
    public void goToCareersAndCheckBlocks() {
        driver.get("https://useinsider.com/");

        // Open "Company" dropdown
        WebElement companyMenu = driver.findElement(By.xpath("//a[@id='navbarDropdownMenuLink' and normalize-space()='Company']"));
        new Actions(driver).moveToElement(companyMenu).perform();

        // Click "Careers"
        WebElement careersLink = driver.findElement(By.xpath("//a[contains(@href,'/careers/')]"));
        careersLink.click();

        // URL verification (for guarantee)
        assertTrue(driver.getCurrentUrl().contains("/careers"), "Careers page did not open!");

        // Locations
        WebElement locationsBlock = driver.findElement(By.xpath("//*[@id='career-our-location']"));
        assertTrue(locationsBlock.isDisplayed(), "Locations block is not visible!");
        // Teams
        WebElement teamsBlock = driver.findElement(By.xpath("//*[@id='career-find-our-calling']"));
        assertTrue(teamsBlock.isDisplayed(), "Teams block is not visible!");
        // Life at Insider
        WebElement lifeAtInsiderBlock = driver.findElement(By.xpath("//*[@class='elementor-section elementor-top-section elementor-element elementor-element-a8e7b90 elementor-section-full_width elementor-section-height-default elementor-section-height-default']"));
        assertTrue(lifeAtInsiderBlock.isDisplayed(), "Life at Insider block is not visible!");
    }
}
