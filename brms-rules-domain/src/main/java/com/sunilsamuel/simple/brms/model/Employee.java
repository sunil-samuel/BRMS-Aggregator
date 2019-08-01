package com.sunilsamuel.simple.brms.model;

import java.io.Serializable;

/**
 * Keep track of the employee and see if the employee is also a customer.
 * 
 * @author Sunil Samuel
 *
 */
public class Employee implements Serializable {
	private static final long serialVersionUID = -7016123897293442933L;
	private String name;
	private int id;
	private String status;
	private double salary;
	private String group;
	private boolean isCustomer;
	private double bonus;

	public Employee(String name, int id, String status, String group, boolean isCustomer) {
		super();
		this.name = name;
		this.id = id;
		this.status = status;
		this.group = group;
		this.isCustomer = isCustomer;
	}

	public Employee(String name, int id, String status, double salary, String group, boolean isCustomer, double bonus) {
		super();
		this.name = name;
		this.id = id;
		this.status = status;
		this.salary = salary;
		this.group = group;
		this.isCustomer = isCustomer;
		this.bonus = bonus;
	}

	public Employee() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isCustomer() {
		return isCustomer;
	}

	public void setCustomer(boolean isCustomer) {
		this.isCustomer = isCustomer;
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + id;
		result = prime * result + (isCustomer ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (Double.doubleToLongBits(bonus) != Double.doubleToLongBits(other.bonus))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (id != other.id)
			return false;
		if (isCustomer != other.isCustomer)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Employee [name=").append(name).append(", id=").append(id).append(", status=").append(status)
				.append(", salary=").append(salary).append(", group=").append(group).append(", isCustomer=")
				.append(isCustomer).append(", bonus=").append(bonus).append("]");
		return builder.toString();
	}

}
