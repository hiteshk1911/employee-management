package com.hitesh.employeemanagement.mapper;

import com.hitesh.employeemanagement.dto.EmployeeRequestDto;
import com.hitesh.employeemanagement.dto.EmployeeResponseDto;
import com.hitesh.employeemanagement.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDto request) {
        return Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .build();
    }

    public EmployeeResponseDto toResponse(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}