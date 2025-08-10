package com.tunahanozturk.utils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            // SS
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Name & Date Format
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = timestamp + "_" + testName + ".png";

            // Path
            File destination = new File("Fail Test Screenshots" + File.separator + fileName);

            // Create file with mkdirs
            destination.getParentFile().mkdirs();

            // Copy/Save
            Files.copy(source.toPath(), destination.toPath());

            System.out.println("Screenshot saved: " + destination.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Screenshot could not be taken: " + e.getMessage());
        }
    }
}
