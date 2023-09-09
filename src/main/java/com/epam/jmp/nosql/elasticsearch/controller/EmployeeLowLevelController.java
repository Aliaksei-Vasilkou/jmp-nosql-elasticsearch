package com.epam.jmp.nosql.elasticsearch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.epam.jmp.nosql.elasticsearch.service.EmployeeLowLevelService;

@RestController
@RequestMapping("/v1/employees")
public class EmployeeLowLevelController {

    private final EmployeeLowLevelService employeeService;

    public EmployeeLowLevelController(EmployeeLowLevelService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        employeeService.create(employee);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        employeeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Request example: http://localhost:8080/v1/employees/search?skills=java
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> search(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(employeeService.search(params));
    }

    // Request example: http://localhost:8080/v1/employees/aggregate?field=skills.keyword
    @GetMapping("/aggregate")
    public ResponseEntity<?> aggregate(@RequestParam String field) {
        return ResponseEntity.ok(employeeService.aggregate(field));
    }
}
