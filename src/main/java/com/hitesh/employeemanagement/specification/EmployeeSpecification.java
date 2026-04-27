package com.hitesh.employeemanagement.specification;

import com.hitesh.employeemanagement.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class EmployeeSpecification {

    public static Specification<Employee> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<Employee> hasDepartment(
            String department) {

        return (root, query, cb) ->
                department == null || department.isBlank()
                        ? null
                        : cb.equal(
                        cb.lower(root.get("department")),
                        department.toLowerCase()
                );
    }

    public static Specification<Employee> salaryGreaterThanOrEqual(
            BigDecimal minSalary) {

        return (root, query, cb) ->
                minSalary == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("salary"),
                        minSalary
                );
    }

    public static Specification<Employee> salaryLessThanOrEqual(
            BigDecimal maxSalary) {

        return (root, query, cb) ->
                maxSalary == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("salary"),
                        maxSalary
                );
    }
}