// This will update an employee

package edu.uark.registerapp.commands.employees;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uark.registerapp.commands.ResultCommandInterface;
import edu.uark.registerapp.commands.exceptions.NotFoundException;
import edu.uark.registerapp.commands.exceptions.UnprocessableEntityException;
import edu.uark.registerapp.models.api.Employee;
import edu.uark.registerapp.models.entities.EmployeeEntity;
import edu.uark.registerapp.models.enums.EmployeeClassification;
import edu.uark.registerapp.models.repositories.EmployeeRepository;

@Service
public class EmployeeUpdateCommand implements ResultCommandInterface<Employee> 
{
	@Override
	public Employee execute() 
	{
		this.validateProperties();
		this.updateEmployeeEntity();
		return this.apiEmployee;
	}

	// Helper methods
	// Validate incoming Employee request object, names should not be blank
	private void validateProperties() 
	{
		if (StringUtils.isBlank(this.apiEmployee.getFirstName())) 
		{
			throw new UnprocessableEntityException("first name");
		}
		if (StringUtils.isBlank(this.apiEmployee.getLastName())) 
		{
			throw new UnprocessableEntityException("last name");
		}
		if (EmployeeClassification.map(this.apiEmployee.getClassification()) == EmployeeClassification.NOT_DEFINED) 
		{
			throw new UnprocessableEntityException("classification");
		}
	}

	@Transactional
	private void updateEmployeeEntity() 
	{
		// Query employee entity from database
		final Optional<EmployeeEntity> queriedEmployeeEntity =
			this.employeeRepository.findById(this.employeeId);

		if (!queriedEmployeeEntity.isPresent()) 
		{
			// No record with the associated record ID exists in the database.
			throw new NotFoundException("Employee"); 
		}
		// Update queried employee entity with date from incoming Employee object
		this.apiEmployee = queriedEmployeeEntity.get().synchronize(this.apiEmployee); 
		// Save updated employee entity
		this.employeeRepository.save(queriedEmployeeEntity.get()); 
	}

	// Properties
	// Getters and Setters for the universally unique Identifiers of that tuple
	private UUID employeeId;
	public UUID getEmployeeId() 
	{
		return this.employeeId;
	}
	public EmployeeUpdateCommand setEmployeeId(final UUID employeeId) 
	{
		this.employeeId = employeeId;
		return this;
	}

	// Getters and Setters for the API
	// Return updated data of the Employee object
	private Employee apiEmployee;
	public Employee getApiEmployee() 
	{
		return this.apiEmployee;
	}
	public EmployeeUpdateCommand setApiEmployee(final Employee apiEmployee) 
	{
		this.apiEmployee = apiEmployee;
		return this;
	}

	@Autowired
	private EmployeeRepository employeeRepository;
}
