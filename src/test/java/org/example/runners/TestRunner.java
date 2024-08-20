package org.example.runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"org.example.steps"},
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        dryRun = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}