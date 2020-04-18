package com.sunilsamuel.simple.brms.test.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute a BRMS rule given a set of facts. This class uses the command factory
 * to process the rules.
 * 
 * @author Sunil Samuel (web_github@sunilsamuel.com)
 *
 */
public class RulesEngineSupport {

	private boolean debug = false;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private KieContainer kieContainer;
	private List<Command<?>> commands;
	private ExecutionResults results;
	/**
	 * The caller can determine whether to use stateful or stateless kie sessions.
	 * Depending on what they choose, one of the two session will be used.
	 */
	private Object ksession;
	/**
	 * Keep track of whether the user wanted stateless session.
	 */
	private Boolean stateLess = null;

	/**
	 * The name of the rule to activate if the user want to enable just one.
	 */
	private String ruleName;

	/**
	 * The name of the ksession if the user wants to enable just one.
	 */
	private String sessionName;

	/**
	 * The name of the rulegroup-flow (agenda) to set the focus to if the user wants
	 * to enable just one.
	 */

	private String ruleFlowName;

	/**
	 * Use the kie service factory to get the kie container. If we include another
	 * kjar within this package, then it is possible that KieServices factory see
	 * that as the default classloader. By providing the ClassLoader as a parameter,
	 * the caller can force to use a specific classloader.
	 * 
	 * @param cl The classloader to use to create the session.
	 * 
	 * @throws Exception
	 */
	public RulesEngineSupport(ClassLoader cl) {
		if (cl == null) {
			kieContainer = KieServices.Factory.get().getKieClasspathContainer();
		} else {
			kieContainer = KieServices.Factory.get().getKieClasspathContainer(cl);
		}
		commands = new ArrayList<Command<?>>();
	}

	/**
	 * Method to print all of the available kbases that are seen by this kie
	 * container.
	 */
	public void listKessions() {
		Collection<String> kBaseNames = kieContainer.getKieBaseNames();
		for (String kBaseName : kBaseNames) {
			System.out.println("KBase name [" + kBaseName + "]");
			Collection<String> sessionNames = kieContainer.getKieSessionNamesInKieBase(kBaseName);
			for (String sName : sessionNames) {
				System.out.println("\t[" + sName + "]");
			}
		}
	}

	/**
	 * An override of the constructor that allows the user the ability to use the
	 * default global classloader to load the sessions.
	 */
	public RulesEngineSupport() {
		this(null);
	}

	/**
	 * Set the debug option that will enable debug during the rules processing.
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Set the stateless options.
	 * 
	 * @param stateLess
	 */
	public void setStateLess(boolean stateLess) {
		this.stateLess = new Boolean(stateLess);
	}

	/**
	 * Capture the names of the rules to activate if the user just wants to run one
	 * or more specific rules.
	 * 
	 * @param ruleName
	 */
	public void setRuleNameToActivate(String ruleName) {
		this.ruleName = ruleName;
	}

	/**
	 * Capture the name of the session to enable if the user chose to do so.
	 * 
	 * @param sessionName
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	/**
	 * Capture the name of the rule flow group to set the focus to, if the user
	 * wishes to run just one ruleflow.
	 * 
	 * @param ruleFlowName
	 */
	public void setRuleFlowName(String ruleFlowName) {
		this.ruleFlowName = ruleFlowName;
	}

	/**
	 * Insert the objects as BRMS facts that will be used to run the rules.
	 * 
	 * @param auditFileName Audit file name
	 * @param facts         The objects to insert into the BRMS engine
	 */
	public void initializeProcess(String auditFileName, Object... facts) {
		for (Object fact : facts) {
			commands.add(CommandFactory.newInsert(fact));
		}
		commands.add(CommandFactory.newEnableAuditLog(auditFileName));
		fireRules();
	}

	public void fireRules() {
		if (ruleName != null) {
			commands.add(new FireAllRulesCommand(new RuleNameEqualsAgendaFilter(ruleName)));
		} else {
			commands.add(CommandFactory.newFireAllRules());
		}
	}

	/**
	 * Insert one fact at a time and run the processes separately.
	 * 
	 * @param fact The objects to insert into the BRMS engine
	 */
	public void addFact(Object fact) {
		commands.add(CommandFactory.newInsert(fact));
	}

	/**
	 * Add the audit filename to write output to.
	 * 
	 * @param auditFileName Audit file name
	 */
	public void addAuditLog(String auditFileName) {
		commands.add(CommandFactory.newEnableAuditLog(auditFileName));
	}

	/**
	 * Add the name of the process id for the bpmn2 modeler to activate the rules.
	 * 
	 * @param processId
	 */
	public void addProcess(String processId) {
		commands.add(CommandFactory.newStartProcess(processId));
	}

	/**
	 * Overloaded method that will add the process id but parameters.
	 * 
	 * @param processId
	 * @param parameters
	 */
	public void addProcess(String processId, Map<String, Object> parameters) {
		commands.add(CommandFactory.newStartProcess(processId, parameters));
	}

	/**
	 * Create the query command so that the objects can be retrieved from the BRMS
	 * engine.
	 * 
	 * @param queryIdentifier The variable identifier used within the query.
	 * @param queryName       The name of the query
	 */
	public void queryCommand(String queryIdentifier, String queryName) {
		commands.add(CommandFactory.newQuery(queryIdentifier, queryName));
	}

	/**
	 * Execute the rules and store the results to be used to retrieve the response.
	 * 
	 */
	public void executeRules() {
		createSession();
		createRuleFlow();
		if (debug) {
			((KieRuntime) ksession).addEventListener(new DefaultAgendaEventListener() {
				public void afterMatchFired(AfterMatchFiredEvent event) {
					super.afterMatchFired(event);
					System.out.println(event);
				}
			});
		}
		BatchExecutionCommand batch = CommandFactory.newBatchExecution(commands);
		results = ((CommandExecutor) ksession).execute(batch);
		closeSession();
	}

	/**
	 * Closes the kie session if it is an instnace of KieSession (stateful).
	 */
	public void closeSession() {
		if (ksession != null && ksession instanceof KieSession) {
			((KieSession) ksession).dispose();
		}
	}

	/**
	 * Check the rule flow name to see if we should set a focus onto a rule flow.
	 */
	private void createRuleFlow() {
		if (ruleFlowName != null) {
			if (!(ksession instanceof KieSession)) {
				logger.error("The kie session has to be stateful to do this.");
				return;
			}
			if (debug) {
				logger.info("Using rule flow name (agenda) [{}]", ruleFlowName);
			}
			((KieSession) ksession).getAgenda().getAgendaGroup(ruleFlowName).setFocus();
		}
	}

	/**
	 * Create the session by check if a session name was provided. If not, then use
	 * default session.
	 */
	private void createSession() {
		logger.debug("Creating kie session [{}] session name [{}]", sessionName == null ? "without" : "with",
				sessionName);
		/**
		 * If the user did not chose either stateful or stateless, then we will try to
		 * create a stateful first, then a stateless. This will depend on how the
		 * kmodule.xml file is set up.
		 */
		if (stateLess == null) {
			/**
			 * Try to create a stateful session.
			 */
			try {
				ksession = (sessionName == null ? this.kieContainer.newKieSession()
						: this.kieContainer.newKieSession(sessionName));
			} catch (RuntimeException e) {
				logger.info("Could not create stateful session.  Trying to create stateless");
				ksession = (sessionName == null ? this.kieContainer.newStatelessKieSession()
						: this.kieContainer.newStatelessKieSession(sessionName));
			}
		} else {
			/**
			 * If the user forced to use either stateful or stateless, then make sure to do
			 * what the user wanted.
			 */
			if (stateLess == false) {
				ksession = (sessionName == null ? this.kieContainer.newKieSession()
						: this.kieContainer.newKieSession(sessionName));
			} else {
				ksession = (sessionName == null ? this.kieContainer.newStatelessKieSession()
						: this.kieContainer.newStatelessKieSession(sessionName));
			}
		}

		if (ksession == null) {
			logger.warn("Exception", new Exception(
					"The session named [" + sessionName + "] was not found.  Please check the kmodule.xml file."));
		}
	}

	/**
	 * Return the entire ksession back to the caller.
	 * 
	 * @return
	 */
	public Object getKieSession() {
		return this.ksession;
	}

	@SuppressWarnings("unchecked")
	public <T> List<?> get(String queryIdentifier, T clazz) {
		FlatQueryResults queryResults = (FlatQueryResults) results.getValue(queryIdentifier);
		if (queryResults == null) {
			return null;
		}
		Iterator<QueryResultsRow> iterator = queryResults.iterator();
		return (List<T>) (iterator.hasNext() ? iterator.next().get(queryIdentifier) : null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOne(String queryIdentifier, Class<T> clazz) {
		FlatQueryResults queryResults = (FlatQueryResults) results.getValue(queryIdentifier);
		if (queryResults == null) {
			return null;
		}
		Iterator<QueryResultsRow> iterator = queryResults.iterator();
		return (T) (iterator.hasNext() ? iterator.next().get(queryIdentifier) : null);
	}

	/**
	 * Another way to get the fact given a fact handle.
	 * 
	 * @param <T>
	 * @param handle
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getFactHandle(String handle, Class<T> clazz) {
		if (results == null) {
			System.out.println("Result is null, did you fireRules and executeRules?");
			return null;
		}
		DefaultFactHandle rval = (DefaultFactHandle) results.getFactHandle(handle);
		return (T) rval.getObject();
	}

	/**
	 * H E L P E R - C L A S S E S
	 */
	/**
	 * Agenda filter used to enable one specific rule given the name of the rule as
	 * defined within the drl file.
	 * 
	 * @author Sunil Samuel (sunil@redhat.com)
	 *
	 */
	public class RuleNameEqualsAgendaFilter implements AgendaFilter {
		private String ruleName;

		public RuleNameEqualsAgendaFilter(String ruleName) {
			this.ruleName = ruleName;
		}

		@Override
		public boolean accept(Match match) {
			return match.getRule().getName().equals(ruleName);
		}
	}
}
