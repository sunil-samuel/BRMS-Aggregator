package com.sunilsamuel.customer.rules.test;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)

@CucumberOptions(strict = true, features = "src/test/resources/com/sunilsamuel/customer/rules/cucumber", glue = "com.sunilsamuel.customer.rules.test.steps", plugin = {
		"pretty", "html:target/cucumber-reports" }, monochrome = true, tags = { "not @Ignore" })

public class CucumberSetupTest {

}
