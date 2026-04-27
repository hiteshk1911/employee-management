package com.hitesh.employeemanagement.controller;

import com.hitesh.employeemanagement.dto.EmployeeRequestDto;
import com.hitesh.employeemanagement.dto.EmployeeResponseDto;
import com.hitesh.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody EmployeeRequestDto request) {

        EmployeeResponseDto response =
                employeeService.createEmployee(request);

        URI location =
                URI.create("/api/employees/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(
            @PathVariable Long id) {

        EmployeeResponseDto response =
                employeeService.getEmployeeById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request) {

        EmployeeResponseDto response =
                employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployees(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page must be >= 0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size must be >= 1")
            @Max(value = 100, message = "size must be <= 100")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction
    ) {

        Page<EmployeeResponseDto> response =
                employeeService.getAllEmployees(
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EmployeeResponseDto>> searchEmployees(

            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            String department,

            @RequestParam(required = false)
            BigDecimal minSalary,

            @RequestParam(required = false)
            BigDecimal maxSalary,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page must be >= 0")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size must be >= 1")
            @Max(value = 100, message = "size must be <= 100")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction
    ) {

        Page<EmployeeResponseDto> response =
                employeeService.searchEmployees(
                        name,
                        department,
                        minSalary,
                        maxSalary,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(response);
    }
}