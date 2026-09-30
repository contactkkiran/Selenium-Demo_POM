package com.demo;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class WaitsDemo {
    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/dynamic-properties");

        // 1. No wait -> fails, because the button appears only after 5 seconds
        // driver.findElement(By.id("visibleAfter")).click();   // NoSuchElementException

        // 2. Thread.sleep (bad practice)
        // Thread.sleep(6000);

        // 3. Implicit wait (set once, applies to every findElement)
        // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // 4. Explicit wait (recommended)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableAfter"))).click();
        WebElement visible = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("visibleAfter")));
        System.out.println("Visible button text: " + visible.getText());

        // 5. Fluent wait (custom polling, ignores NoSuchElementException)
        Wait<WebDriver> fluent = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        WebElement el = fluent.until(d -> d.findElement(By.id("visibleAfter")));
        System.out.println("Fluent found: " + el.getText());

        driver.quit();
    }
}
