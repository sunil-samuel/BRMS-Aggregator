package com.sunilsamuel.customer.rules.test.steps;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunilsamuel.simple.brms.model.Employee;
import com.sunilsamuel.simple.brms.test.support.RulesEngineSupport;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CustomerSteps {
	private Employee employee;
	private RulesEngineSupport rulesEngineSupport;
	
	private Logger logger = 		LoggerFactory.getLogger(getClass());

	@Given("an employee")
	public void employee() {
		employee = new Employee();
		employee.setCustomer(true);
		logger.info("SUNILSUNIL:HERE");
	}

	@Given("name of employee as {string}")
	public void name(String name) {
		employee.setName(name);
	}

	@Given("group of employee as {word}")
	public void group(String group) {
		employee.setGroup(group);
	}

	@Given("salary of employee as {double}")
	public void salry(double salary) {
		employee.setSalary(salary);
	}

	@When("I process the employee")
	public void processEmployee() {
		rulesEngineSupport = new RulesEngineSupport();
		rulesEngineSupport.addFact(employee);
		rulesEngineSupport.setSessionName("customerStatefulSession");
		rulesEngineSupport.setRuleFlowName("Customer");
		rulesEngineSupport.setDebug(false);
		rulesEngineSupport.fireRules();
		rulesEngineSupport.executeRules();
	}

	@Then("the bonus is {double}")
	public void validateBonus(double bonus) {
		Assert.assertEquals(bonus, employee.getBonus(), 0);
	}

}
