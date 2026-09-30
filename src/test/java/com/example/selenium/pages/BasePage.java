package com.example.selenium.pages;

import com.aventstack.extentreports.ExtentTest;
import com.example.selenium.utils.ExtentManager;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Shared helpers for all page objects. Every log line goes to the console and the Extent report. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final JavascriptExecutor js;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
    }

    protected void log(String message) {
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.info(message);
        }
        System.out.println(message);
    }

    protected void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }

    protected void clickJs(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    /** Opens a React-Select dropdown by container id and clicks the option with the given text. */
    protected void selectDropdown(String containerId, String optionText) {
        WebElement container = driver.findElement(By.id(containerId));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", container);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("#" + containerId + " div[class*='control--is-disabled']")));
        wait.until(ExpectedConditions.elementToBeClickable(container)).click();

        By option = By.xpath("//div[contains(@id,'-option-') and normalize-space()='" + optionText + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }
}
