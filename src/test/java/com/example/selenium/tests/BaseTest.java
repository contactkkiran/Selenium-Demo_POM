package com.example.selenium.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.example.selenium.utils.ExtentManager;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/** Starts the browser and the Extent test before each test, and records the result after it. */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        Test annotation = method.getAnnotation(Test.class);
        String description = (annotation != null) ? annotation.description() : "";
        ExtentManager.createTest(method.getName(), description);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--disable-notifications");
        if (Boolean.getBoolean("headless")) {          // mvn test -Dheadless=true
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        ExtentTest test = ExtentManager.getTest();
        try {
            if (result.getStatus() == ITestResult.SUCCESS) {
                test.pass("Test passed", screenshot());
            } else if (result.getStatus() == ITestResult.FAILURE) {
                test.fail(result.getThrowable(), screenshot());
            } else if (result.getStatus() == ITestResult.SKIP) {
                test.skip(result.getThrowable());
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public void writeReport() {
        ExtentManager.flush();
    }

    private com.aventstack.extentreports.model.Media screenshot() {
        String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        return MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build();
    }
}
