package com.example.selenium.tests;

import com.example.selenium.pages.RegistrationPage;
import com.example.selenium.utils.ExtentManager;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class RegistrationTest extends BaseTest {

    @Test(description = "Register a new student with all fields and verify the confirmation popup")
    public void testValidStudentRegistration() {
        RegistrationPage page = new RegistrationPage(driver);

        page.open()
            .enterFirstName("Ravi")
            .enterLastName("Kumar")
            .enterEmail("ravi.kumar@example.com")
            .selectGender(1)
            .enterMobile("9876543210")
            .enterDateOfBirth("15 Aug 2000")
            .addSubject("Maths")
            .addSubject("Computer Science")
            .selectHobby(1)
            .selectHobby(3)
            .uploadPicture()
            .enterAddress("Plot 12, Chandanagar, Hyderabad")
            .selectState("NCR")
            .selectCity("Delhi")
            .submit();

        Assert.assertTrue(page.isModalDisplayed(), "Confirmation popup was not displayed");
        Assert.assertEquals(page.getModalHeading(), "Thanks for submitting the form");

        Map<String, String> data = page.getSubmittedData();
        data.forEach((label, value) -> ExtentManager.getTest().info(label + " -> " + value));

        Assert.assertEquals(data.get("Student Name"), "Ravi Kumar");
        Assert.assertEquals(data.get("Student Email"), "ravi.kumar@example.com");
        Assert.assertEquals(data.get("Mobile"), "9876543210");
        Assert.assertEquals(data.get("State and City"), "NCR Delhi");
    }
}