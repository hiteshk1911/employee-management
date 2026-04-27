package com.hitesh.employeemanagement.service;

import com.hitesh.employeemanagement.dto.EmployeeRequestDto;
import com.hitesh.employeemanagement.dto.EmployeeResponseDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto request);
    EmployeeResponseDto getEmployeeById(Long id);
    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request);
    void deleteEmployee(Long id);
    Page<EmployeeResponseDto> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction
    );
    Page<EmployeeResponseDto> searchEmployees(
            String name,
            String department,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            int page,
            int size,
            String sortBy,
            String direction
    );
}