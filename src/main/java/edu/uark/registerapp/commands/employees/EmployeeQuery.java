// Find employee by Id

package edu.uark.registerapp.commands.employees;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uark.registerapp.commands.ResultCommandInterface;
import edu.uark.registerapp.commands.exceptions.NotFoundException;
import edu.uark.registerapp.models.api.Employee;
import edu.uark.registerapp.models.entities.EmployeeEntity;
import edu.uark.registerapp.models.repositories.EmployeeRepository;

@Service
public class EmployeeQuery implements ResultCommandInterface<Employee> 
{
	// Functionality
	@Override
	public Employee execute() 
	{
		// Query employee from database by record ID
		final Optional<EmployeeEntity> employeeEntity =
			this.employeeRepository.findById(this.employeeId);

		if (employeeEntity.isPresent()) 
		{
			// Map employee entity to Employee object
			return new Employee(employeeEntity.get());
		} 
		else 
		{
			// Employee was not found
			throw new NotFoundException("Employee");
		}
	}

	// Create employee record ID with getters and setters
	private UUID employeeId;
	public UUID getEmployeeId() 
	{
		return this.employeeId;
	}
	public EmployeeQuery setEmployeeId(final UUID employeeId) 
	{
		this.employeeId = employeeId;
		return this;
	}

	@Autowired
	private EmployeeRepository employeeRepository;
}
