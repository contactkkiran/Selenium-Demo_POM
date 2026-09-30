package com.demo;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * Selenium demo class.
 *  - Waits demo on https://demoqa.com/dynamic-properties
 *  - Table parsing on https://the-internet.herokuapp.com/tables
 *  - Common web elements on https://demoqa.com (text box, check box, radio button,
 *    buttons, links (working ones only), select menu, alerts, file upload)
 */
public class WaitsDemoTest {

    // ---- Page URLs ----
    private static final String DYNAMIC_PROPERTIES_URL = "https://demoqa.com/dynamic-properties";
    private static final String TABLE_URL        = "https://the-internet.herokuapp.com/tables";
    private static final String TEXT_BOX_URL     = "https://demoqa.com/text-box";
    private static final String CHECK_BOX_URL    = "https://demoqa.com/checkbox";
    private static final String RADIO_BUTTON_URL = "https://demoqa.com/radio-button";
    private static final String BUTTONS_URL      = "https://demoqa.com/buttons";
    private static final String LINKS_URL        = "https://demoqa.com/links";
    private static final String SELECT_MENU_URL  = "https://demoqa.com/select-menu";
    private static final String ALERTS_URL       = "https://demoqa.com/alerts";
    private static final String UPLOAD_URL       = "https://demoqa.com/upload-download";

    // ---- Locators for the waits page ----
    private static final By ENABLE_AFTER = By.id("enableAfter");
    private static final By VISIBLE_AFTER = By.id("visibleAfter");

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        if (Boolean.getBoolean("headless")) {          // mvn test -Dheadless=true
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        driver.get(DYNAMIC_PROPERTIES_URL);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    /** Opens a page and removes the ad banner and footer that block clicks on DemoQA. */
    private void openPage(String url) {
        driver.get(url);
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('#fixedban, footer').forEach(e => e.remove());");
    }

    /** Scrolls to the element and clicks it with JavaScript (avoids overlay problems). */
    private void clickJs(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

    private WebDriverWait waitFor() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ------------------------------------------------------------------
    // WAITS
    // ------------------------------------------------------------------

    // 1. No wait: fails because the button appears only after 5 seconds
    @Test(priority = 1, expectedExceptions = NoSuchElementException.class,
          description = "No wait: NoSuchElementException is expected")
    public void testNoWait() {
        driver.findElement(VISIBLE_AFTER).click();
    }

    // 2. Thread.sleep: works, but always waits the full time (bad practice)
    @Test(priority = 2, description = "Thread.sleep: fixed pause, avoid in real projects")
    public void testThreadSleep() throws InterruptedException {
        Thread.sleep(6000);
        WebElement button = driver.findElement(VISIBLE_AFTER);
        Assert.assertTrue(button.isDisplayed(), "Button should be visible after 6 seconds");
    }

    // 3. Implicit wait: set once, applies to every findElement
    @Test(priority = 3, description = "Implicit wait: global, waits for element presence")
    public void testImplicitWait() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebElement button = driver.findElement(VISIBLE_AFTER);
        Assert.assertTrue(button.isDisplayed(), "Button should be visible");
    }

    // 4. Explicit wait: wait for a condition on a specific element (recommended)
    @Test(priority = 4, description = "Explicit wait: waits for a condition, recommended")
    public void testExplicitWait() {
        WebDriverWait wait = waitFor();

        wait.until(ExpectedConditions.elementToBeClickable(ENABLE_AFTER)).click();

        WebElement visible = wait.until(ExpectedConditions.visibilityOfElementLocated(VISIBLE_AFTER));
        System.out.println("Visible button text: " + visible.getText());
        Assert.assertTrue(visible.isDisplayed(), "Button should be visible");
    }

    // 5. Fluent wait: custom polling and ignored exceptions
    @Test(priority = 5, description = "Fluent wait: custom polling interval and ignored exceptions")
    public void testFluentWait() {
        Wait<WebDriver> fluent = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        WebElement el = fluent.until(d -> d.findElement(VISIBLE_AFTER));
        System.out.println("Fluent found: " + el.getText());
        Assert.assertTrue(el.isDisplayed(), "Button should be visible");
    }

    // ------------------------------------------------------------------
    // TABLE
    // ------------------------------------------------------------------

    // 6. Table parsing: read rows and cells of an HTML table
    @Test(priority = 6, description = "Parse table values: rows, cells and one cell by position")
    public void testParseTableValues() {
        driver.get(TABLE_URL);      // setUp opened the waits page, so go to the table page

        List<WebElement> rows = driver.findElements(By.cssSelector("#table1 tbody tr"));
        System.out.println("Total rows: " + rows.size());

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            System.out.println(cells.get(0).getText() + " | "
                    + cells.get(1).getText() + " | "
                    + cells.get(2).getText() + " | "
                    + cells.get(3).getText());
        }

        String email = driver.findElement(By.xpath("//table[@id='table1']/tbody/tr[1]/td[3]")).getText();
        System.out.println("Row 1 email: " + email);

        Assert.assertEquals(rows.size(), 4, "Unexpected number of rows");
        Assert.assertTrue(email.contains("@"), "Cell should contain an email address");
    }

    // ------------------------------------------------------------------
    // WEB ELEMENTS
    // ------------------------------------------------------------------

    // 7. Text box: type into input fields and check the output
    @Test(priority = 7, description = "Text box: sendKeys and verify the submitted output")
    public void testTextBox() {
        openPage(TEXT_BOX_URL);

        driver.findElement(By.id("userName")).sendKeys("Ravi Kumar");
        driver.findElement(By.id("userEmail")).sendKeys("ravi.kumar@example.com");
        driver.findElement(By.id("currentAddress")).sendKeys("Chandanagar, Hyderabad");
        driver.findElement(By.id("permanentAddress")).sendKeys("Hyderabad, India");
        clickJs(driver.findElement(By.id("submit")));

        String name = waitFor().until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#output #name"))).getText();
        String email = driver.findElement(By.cssSelector("#output #email")).getText();
        System.out.println(name + " | " + email);

        Assert.assertTrue(name.contains("Ravi Kumar"), "Name not shown in output");
        Assert.assertTrue(email.contains("ravi.kumar@example.com"), "Email not shown in output");
    }

    // 8. Check box: tick a box and read the result text
    @Test(priority = 8, description = "Check box: select and verify the result text")
    public void testCheckBox() {
        openPage(CHECK_BOX_URL);

        WebElement homeLabel = driver.findElement(By.cssSelector("label[for='tree-node-home']"));
        clickJs(homeLabel);

        WebElement checkbox = driver.findElement(By.id("tree-node-home"));
        Assert.assertTrue(checkbox.isSelected(), "Home checkbox should be selected");

        String result = driver.findElement(By.id("result")).getText();
        System.out.println("Result: " + result);
        Assert.assertTrue(result.contains("home"), "Result should list the selected item");
    }

    // 9. Radio button: pick one option; a disabled option cannot be selected
    @Test(priority = 9, description = "Radio button: select an option and check a disabled one")
    public void testRadioButton() {
        openPage(RADIO_BUTTON_URL);

        clickJs(driver.findElement(By.cssSelector("label[for='yesRadio']")));
        String result = driver.findElement(By.cssSelector(".text-success")).getText();
        System.out.println("Selected: " + result);
        Assert.assertEquals(result, "Yes");

        clickJs(driver.findElement(By.cssSelector("label[for='impressiveRadio']")));
        Assert.assertEquals(driver.findElement(By.cssSelector(".text-success")).getText(), "Impressive");

        Assert.assertFalse(driver.findElement(By.id("noRadio")).isEnabled(), "'No' radio should be disabled");
    }

    // 10. Buttons: double click, right click and normal click
    @Test(priority = 10, description = "Buttons: double click, right click and dynamic click")
    public void testButtons() {
        openPage(BUTTONS_URL);
        Actions actions = new Actions(driver);

        WebElement doubleBtn = driver.findElement(By.id("doubleClickBtn"));
        actions.doubleClick(doubleBtn).perform();
        Assert.assertTrue(driver.findElement(By.id("doubleClickMessage")).getText()
                .contains("double click"));

        WebElement rightBtn = driver.findElement(By.id("rightClickBtn"));
        actions.contextClick(rightBtn).perform();
        Assert.assertTrue(driver.findElement(By.id("rightClickMessage")).getText()
                .contains("right click"));

        clickJs(driver.findElement(By.xpath("//button[text()='Click Me']")));
        Assert.assertTrue(driver.findElement(By.id("dynamicClickMessage")).getText()
                .contains("dynamic click"));
    }

    // 11. Links: a link that opens a new tab, and an API link (working links only)
    @Test(priority = 11, description = "Links: new tab link and a working API link (broken links skipped)")
    public void testLinks() {
        openPage(LINKS_URL);
        WebDriverWait wait = waitFor();

        // Link that opens a new tab
        String parent = driver.getWindowHandle();
        clickJs(driver.findElement(By.id("simpleLink")));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(parent)) {
                driver.switchTo().window(handle);
            }
        }
        System.out.println("New tab URL: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains("demoqa.com"));
        driver.close();
        driver.switchTo().window(parent);

        // API link that responds with 201 Created (a working link)
        clickJs(driver.findElement(By.id("created")));
        String response = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("linkResponse"))).getText();
        System.out.println(response);
        Assert.assertTrue(response.contains("201"), "Expected status 201");
    }

    // 12. Select menu: choose an option from a drop-down
    @Test(priority = 12, description = "Select menu: single select and multi select")
    public void testSelectMenu() {
        openPage(SELECT_MENU_URL);

        Select colour = new Select(driver.findElement(By.id("oldSelectMenu")));
        colour.selectByVisibleText("Green");
        Assert.assertEquals(colour.getFirstSelectedOption().getText(), "Green");

        Select cars = new Select(driver.findElement(By.id("cars")));
        Assert.assertTrue(cars.isMultiple(), "Cars list should allow multiple selection");
        cars.selectByVisibleText("Volvo");
        cars.selectByVisibleText("Audi");
        Assert.assertEquals(cars.getAllSelectedOptions().size(), 2);
    }

    // 13. Alerts: simple alert, confirm box and prompt box
    @Test(priority = 13, description = "Alerts: accept, confirm and prompt")
    public void testAlerts() {
        openPage(ALERTS_URL);
        WebDriverWait wait = waitFor();

        // Simple alert
        clickJs(driver.findElement(By.id("alertButton")));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(alert.getText(), "You clicked a button");
        alert.accept();

        // Confirm box: click OK
        clickJs(driver.findElement(By.id("confirmButton")));
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        Assert.assertTrue(driver.findElement(By.id("confirmResult")).getText().contains("Ok"));

        // Prompt box: type text and accept (the button id really is spelled "promtButton")
        clickJs(driver.findElement(By.id("promtButton")));
        Alert prompt = wait.until(ExpectedConditions.alertIsPresent());
        prompt.sendKeys("Ravi");
        prompt.accept();
        Assert.assertTrue(driver.findElement(By.id("promptResult")).getText().contains("Ravi"));
    }

    
}