package com.hitesh.employeemanagement.service.impl;

import com.hitesh.employeemanagement.dto.EmployeeRequestDto;
import com.hitesh.employeemanagement.dto.EmployeeResponseDto;
import com.hitesh.employeemanagement.entity.Employee;
import com.hitesh.employeemanagement.exception.ResourceAlreadyExistsException;
import com.hitesh.employeemanagement.exception.ResourceNotFoundException;
import com.hitesh.employeemanagement.mapper.EmployeeMapper;
import com.hitesh.employeemanagement.repository.EmployeeRepository;
import com.hitesh.employeemanagement.service.EmployeeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.hitesh.employeemanagement.specification.EmployeeSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Set;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Employee with email " + request.getEmail() + " already exists"
            );
        }

        Employee employee = employeeMapper.toEntity(request);

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponseDto getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id " + id
                        )
                );

        return employeeMapper.toResponse(employee);
    }

    @Override
    @CachePut(value = "employees", key = "#id")
    public EmployeeResponseDto updateEmployee(
            Long id,
            EmployeeRequestDto request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id " + id
                        )
                );

        if (employeeRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new ResourceAlreadyExistsException(
                    "Employee with email " + request.getEmail() + " already exists"
            );
        }

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Employee not found with id " + id
            );
        }

        employeeRepository.deleteById(id);
    }

    @Override
    public Page<EmployeeResponseDto> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction) {

        Set<String> allowedSortFields = Set.of(
                "id",
                "name",
                "email",
                "department",
                "salary",
                "createdAt",
                "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy field: " + sortBy
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new IllegalArgumentException(
                    "direction must be asc or desc"
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeRepository
                .findAll(pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponseDto> searchEmployees(
            String name,
            String department,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            int page,
            int size,
            String sortBy,
            String direction) {

        Set<String> allowedSortFields = Set.of(
                "id",
                "name",
                "email",
                "department",
                "salary",
                "createdAt",
                "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy field: " + sortBy
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new IllegalArgumentException(
                    "direction must be asc or desc"
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Employee> spec =
                Specification.where(
                        EmployeeSpecification.hasName(name)
                ).and(
                        EmployeeSpecification.hasDepartment(
                                department
                        )
                ).and(
                        EmployeeSpecification
                                .salaryGreaterThanOrEqual(
                                        minSalary
                                )
                ).and(
                        EmployeeSpecification
                                .salaryLessThanOrEqual(
                                        maxSalary
                                )
                );

        return employeeRepository
                .findAll(spec, pageable)
                .map(employeeMapper::toResponse);
    }
}