package com.example.selenium.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Creates one ExtentReports instance per run and keeps the current test per thread. */
public final class ExtentManager {

    private static ExtentReports extent;
    private static Path reportPath;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentManager() { }

    public static synchronized ExtentReports getExtent() {
        if (extent == null) {
            try {
                Path dir = Paths.get("test-output");
                Files.createDirectories(dir);
                String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                reportPath = dir.resolve("ExtentReport_" + ts + ".html");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath.toString());
            spark.config().setDocumentTitle("Student Registration Report");
            spark.config().setReportName("Student Registration - Selenium + TestNG");
            spark.config().setTheme(Theme.STANDARD);

            // Open on the Dashboard (pie charts) first, then the other views
            spark.viewConfigurer().viewOrder().as(new ViewName[] {
                    ViewName.DASHBOARD, ViewName.TEST, ViewName.EXCEPTION, ViewName.LOG
            });

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Application URL", "https://demoqa.com/automation-practice-form");
        }
        return extent;
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = getExtent().createTest(name, description);
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent Report: " + reportPath.toAbsolutePath());
        }
    }
}