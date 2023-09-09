package com.epam.jmp.nosql.elasticsearch.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.epam.jmp.nosql.elasticsearch.exception.NotExpectedResultException;
import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class EmployeeLowLevelResponseReader {

    public static final String SOURCE_NODE_NAME = "_source";

    private final ObjectMapper objectMapper;

    public EmployeeLowLevelResponseReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<Employee> readSingleValue(String jsonString) throws JsonProcessingException {
        Employee result = null;
        JsonNode response = objectMapper.readTree(jsonString);
        int count = getHitsCount(response);
        EmployeeLowLevelResponseReader.log.info("Total records found " + count);

        if (count > 1) {
            throw new NotExpectedResultException(String.format("Not unique result. Expected 1 record, but got %d", count));
        }

        if (count == 1) {
            JsonNode node = getHits(response).get(0).get(EmployeeLowLevelResponseReader.SOURCE_NODE_NAME);
            result = objectMapper.treeToValue(node, Employee.class);
        }

        return Optional.ofNullable(result);
    }

    public Optional<List<Employee>> readMultipleValues(String jsonString) throws JsonProcessingException {
        JsonNode response = objectMapper.readTree(jsonString);
        int count = getHitsCount(response);
        EmployeeLowLevelResponseReader.log.info("Total records found " + count);
        List<JsonNode> nodes = getHits(response);

        if (nodes.isEmpty()) {
            return Optional.empty();
        }

        List<Employee> employees = new ArrayList<>();

        for (JsonNode node : nodes) {
            employees.add(objectMapper.treeToValue(node.get(EmployeeLowLevelResponseReader.SOURCE_NODE_NAME), Employee.class));
        }

        return Optional.of(employees);
    }

    private int getHitsCount(JsonNode response) {
        return response.findPath("hits").findPath("total").get("value").asInt();
    }

    private List<JsonNode> getHits(JsonNode response) {
        return response.findPath("hits").findPath("hits").findParents(SOURCE_NODE_NAME);
    }

}
