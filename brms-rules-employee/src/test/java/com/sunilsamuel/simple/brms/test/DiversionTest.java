package com.sunilsamuel.simple.brms.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;

import com.sunilsamuel.simple.brms.model.Employee;

/**
 * These test cases use the CommandFactory to add commands to the kie session.
 * 
 * @author Sunil Samuel
 *
 */
public class DiversionTest {
	KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();

	@Test
	public void person1() {
		Employee emp = new Employee("Person1", 100, "Entry", "A", false);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person1", 100, "Entry", 35000.00 * 1.25, "A", false, 0d);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void person2() {
		Employee emp = new Employee("Person2", 200, "Junior", "C", true);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person2", 200, "Junior", 55000.00 * 1.30, "C", true, 100.40d);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void person3() {
		Employee emp = new Employee("Person3", 300, "Senior", "B", false);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person3", 300, "Senior", 100000.00 * 1.28, "B", false, 0d);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void person4() {
		Employee emp = new Employee("Person4", 400, "Entry", "C", true);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person4", 400, "Entry", 35000.00 * 1.30, "C", true, 100.40d);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void person5() {
		Employee emp = new Employee("Person5", 500, "Junior", "A", false);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person5", 500, "Junior", 55000.00 * 1.25, "A", false, 0d);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void person6() {
		Employee emp = new Employee("Person6", 600, "Senior", "B", true);

		Employee actual = processEmployee(emp);
		Employee expected = new Employee("Person6", 600, "Senior", 100000.00 * 1.28, "B", true, 100.40d);

		Assert.assertEquals(expected, actual);
	}

	private Employee processEmployee(Employee emp) {
		List<Command<?>> commands;

		/**
		 * Create the commands to run the rules and get the query.
		 */
		commands = new ArrayList<Command<?>>();

		commands.add(CommandFactory.newInsert(emp));
		commands.add(CommandFactory.newStartProcess("com.sunilsamuel.diversion.rules.process"));
		commands.add(CommandFactory.newFireAllRules());
		commands.add(CommandFactory.newQuery("$employee", "get_employee"));
		StatelessKieSession ksession = kieContainer.newStatelessKieSession("diversionStateLess");

		BatchExecutionCommand batch = CommandFactory.newBatchExecution(commands);
		ExecutionResults results = ksession.execute(batch);

		FlatQueryResults queryResults = (FlatQueryResults) results.getValue("$employee");
		Iterator<QueryResultsRow> iterator = queryResults.iterator();
		return (Employee) (iterator.hasNext() ? iterator.next().get("$employee") : null);
	}

}
