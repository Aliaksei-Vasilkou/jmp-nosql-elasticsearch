package com.epam.jmp.nosql.elasticsearch.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.epam.jmp.nosql.elasticsearch.exception.NotExpectedResultException;
import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;

@Component
public class EmployeeJavaApiResponseReader {

    private final ObjectMapper objectMapper;

    public EmployeeJavaApiResponseReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<Employee> getSingleResult(SearchResponse<Object> response) {
        TotalHits totalHits = response.hits().total();

        if (totalHits != null) {
            if (totalHits.value() > 1) {
                throw new NotExpectedResultException("Not unique result");
            }

            Object source = response.hits().hits().get(0).source();
            return Optional.of(objectMapper.convertValue(source, Employee.class));
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<List<Employee>> getMultipleResults(SearchResponse<Object> response) {
        TotalHits totalHits = response.hits().total();

        if (totalHits != null) {
            List<Employee> employees = new ArrayList<>();
            List<Hit<Object>> hits = response.hits().hits();

            for (Hit<Object> hit : hits) {
                Object source = hit.source();
                employees.add(objectMapper.convertValue(source, Employee.class));
            }

            return Optional.of(employees);
        }
        else {
            return Optional.empty();
        }
    }
}
