package com.epam.jmp.nosql.elasticsearch.service;

import java.util.List;
import java.util.Map;

import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(String id);

    void create(Employee employee) throws JsonProcessingException;

    void delete(String id);

    List<Employee> search(Map<String, String> params);

    JsonNode aggregate(String fieldName);
}
