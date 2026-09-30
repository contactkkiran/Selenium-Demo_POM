package com.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Page object for https://demoqa.com/automation-practice-form (fluent: methods return the page). */
public class RegistrationPage extends BasePage {

    private static final String URL = "https://demoqa.com/automation-practice-form";

    // ---- Locators ----
    private final By firstName  = By.id("firstName");
    private final By lastName   = By.id("lastName");
    private final By email      = By.id("userEmail");
    private final By mobile     = By.id("userNumber");
    private final By dob        = By.id("dateOfBirthInput");
    private final By subjects   = By.id("subjectsInput");
    private final By upload     = By.id("uploadPicture");
    private final By address    = By.id("currentAddress");
    private final By submitBtn  = By.id("submit");
    private final By modalTitle = By.id("example-modal-sizes-title-lg");
    private final By modalRows  = By.cssSelector(".modal-body tbody tr");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public RegistrationPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstName));
        // Remove ad banner / footer that block clicks
        js.executeScript("document.querySelectorAll('#fixedban, footer').forEach(e => e.remove());");
        log("Opened registration page: " + URL);
        return this;
    }

    public RegistrationPage enterFirstName(String value) {
        type(firstName, value);
        log("Entered first name: " + value);
        return this;
    }

    public RegistrationPage enterLastName(String value) {
        type(lastName, value);
        log("Entered last name: " + value);
        return this;
    }

    public RegistrationPage enterEmail(String value) {
        type(email, value);
        log("Entered email: " + value);
        return this;
    }

    /** 1 = Male, 2 = Female, 3 = Other */
    public RegistrationPage selectGender(int index) {
        clickJs(driver.findElement(By.cssSelector("label[for='gender-radio-" + index + "']")));
        log("Selected gender option #" + index);
        return this;
    }

    public RegistrationPage enterMobile(String value) {
        type(mobile, value);
        log("Entered mobile: " + value);
        return this;
    }

    public RegistrationPage enterDateOfBirth(String value) {
        Keys selectAll = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND : Keys.CONTROL;
        driver.findElement(dob).sendKeys(Keys.chord(selectAll, "a"), value, Keys.ENTER);
        log("Entered date of birth: " + value);
        return this;
    }

    public RegistrationPage addSubject(String subject) {
        WebElement el = driver.findElement(subjects);
        el.sendKeys(subject);
        el.sendKeys(Keys.ENTER);
        log("Added subject: " + subject);
        return this;
    }

    /** 1 = Sports, 2 = Reading, 3 = Music */
    public RegistrationPage selectHobby(int index) {
        clickJs(driver.findElement(By.cssSelector("label[for='hobbies-checkbox-" + index + "']")));
        log("Selected hobby option #" + index);
        return this;
    }

    public RegistrationPage uploadPicture() {
        try {
            Path photo = Files.createTempFile("student-photo", ".png");
            driver.findElement(upload).sendKeys(photo.toAbsolutePath().toString());
            log("Uploaded picture: " + photo.getFileName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public RegistrationPage enterAddress(String value) {
        type(address, value);
        log("Entered address: " + value);
        return this;
    }

    public RegistrationPage selectState(String state) {
        selectDropdown("state", state);
        log("Selected state: " + state);
        return this;
    }

    public RegistrationPage selectCity(String city) {
        selectDropdown("city", city);
        log("Selected city: " + city);
        return this;
    }

    public RegistrationPage submit() {
        clickJs(driver.findElement(submitBtn));
        log("Clicked Submit");
        return this;
    }

    /** Waits briefly and returns true if the confirmation modal appears. */
    public boolean isModalDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.visibilityOfElementLocated(modalTitle));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getModalHeading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle)).getText();
    }

    /** Reads the confirmation table into label -> value pairs. */
    public Map<String, String> getSubmittedData() {
        Map<String, String> data = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(modalRows)) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                data.put(cells.get(0).getText(), cells.get(1).getText());
            }
        }
        return data;
    }
}
