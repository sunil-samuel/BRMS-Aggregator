@simple-rules
Feature: Simple Rules Test Scenarios
  Test different scenario for customer rules

  @customer-task
  Scenario Outline: Test Customer Task
    Test the customer task (ruleflow) within the BPMN

    Given an employee
    And name of employee as "<name>"
    And group of employee as <group>
    And salary of employee as <salary>
    When I process the employee
    Then the bonus is <bonus>

    Examples: 
      | name      | group | salary | bonus  |
      | John Doe  | A     | 100000 | 100.40 |
      | John Doe2 | B     | 200000 | 100.40 |
      | Jane Doe  | c     | 300000 | 100.40 |
