// This will create a new employee

package edu.uark.registerapp.commands.employees;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uark.registerapp.commands.ResultCommandInterface;
import edu.uark.registerapp.commands.employees.helpers.EmployeeHelper;
import edu.uark.registerapp.commands.exceptions.UnprocessableEntityException;
import edu.uark.registerapp.models.api.Employee;
import edu.uark.registerapp.models.entities.EmployeeEntity;
import edu.uark.registerapp.models.enums.EmployeeClassification;
import edu.uark.registerapp.models.repositories.EmployeeRepository;

@Service
public class EmployeeCreateCommand implements ResultCommandInterface<Employee> {
	@Override
	public Employee execute() {
		this.validateProperties();

		// If this is the first employee to be added and make them the general manager
		if (this.isInitialEmployee) {
			this.apiEmployee.setClassification(
				EmployeeClassification.GENERAL_MANAGER.getClassification());
		}

		 // Create a new ENTITY object from the API object details.
		final EmployeeEntity employeeEntity =
			this.employeeRepository.save(new EmployeeEntity(this.apiEmployee));

		// Synchronize information generated by the database upon INSERT.
		this.apiEmployee.setId(employeeEntity.getId());
		// Only send the password over the network when modifying the database.
		this.apiEmployee.setPassword(StringUtils.EMPTY);
		// Only send the password over the network when modifying the database.
		this.apiEmployee.setCreatedOn(employeeEntity.getCreatedOn());
		this.apiEmployee.setEmployeeId(
			EmployeeHelper.padEmployeeId(
				employeeEntity.getEmployeeId()));

		return this.apiEmployee;
	}

	// Helper methods
	// This checks the fields on the view. 
	// Will not accept if any of the fields are blank.
	private void validateProperties() {
		if (StringUtils.isBlank(this.apiEmployee.getFirstName())) {
			throw new UnprocessableEntityException("first name");
		}
		if (StringUtils.isBlank(this.apiEmployee.getLastName())) {
			throw new UnprocessableEntityException("last name");
		}
		if (StringUtils.isBlank(this.apiEmployee.getPassword())) {
			throw new UnprocessableEntityException("password");
		}

		// If there is a single employee in the DB then select a position for the new addition
		if (!this.isInitialEmployee
			&& (EmployeeClassification.map(this.apiEmployee.getClassification()) == EmployeeClassification.NOT_DEFINED)) {

			throw new UnprocessableEntityException("classification");
		}
	}

	// Getters and Setters for the new employee
	private Employee apiEmployee;
	public Employee getApiEmployee() {
		return this.apiEmployee;
	}
	public EmployeeCreateCommand setApiEmployee(final Employee apiEmployee) {
		this.apiEmployee = apiEmployee;
		return this;
	}

	// Getters and Setters for the first employee, the general manager.
	private boolean isInitialEmployee;
	public boolean getIsInitialEmployee() {
		return this.isInitialEmployee;
	}
	public EmployeeCreateCommand setIsInitialEmployee(
		final boolean isInitialEmployee
	) {

		this.isInitialEmployee = isInitialEmployee;
		return this;
	}

	@Autowired
	private EmployeeRepository employeeRepository;

	public EmployeeCreateCommand() {
		this.isInitialEmployee = false;
	}
}
