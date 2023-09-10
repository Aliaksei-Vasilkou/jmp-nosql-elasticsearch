package com.epam.jmp.nosql.elasticsearch.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.epam.jmp.nosql.elasticsearch.exception.EmployeeNotFoundException;
import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.epam.jmp.nosql.elasticsearch.utils.EmployeeJavaApiResponseReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.log4j.Log4j2;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Log4j2
@Service
public class EmployeeJavaApiService implements EmployeeService {

    private static final String EMPLOYEES_INDEX = "employees";

    private final ElasticsearchClient esClient;
    private final EmployeeJavaApiResponseReader responseReader;
    private final ObjectMapper objectMapper;

    public EmployeeJavaApiService(ElasticsearchClient elasticsearchClient, EmployeeJavaApiResponseReader responseReader, ObjectMapper objectMapper) {
        this.esClient = elasticsearchClient;
        this.responseReader = responseReader;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Employee> findAll() {
        Optional<List<Employee>> result = Optional.empty();
        try {
            SearchResponse<Object> response = esClient.search(s -> s
                            .index(EMPLOYEES_INDEX),
                    Object.class
            );
            result = responseReader.getMultipleResults(response);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result.orElse(Collections.emptyList());
    }

    @Override
    public Employee findById(String id) {
        Optional<Employee> result = Optional.empty();

        try {
            SearchResponse<Object> response = esClient.search(s -> s
                            .index(EMPLOYEES_INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field("_id")
                                            .query(id)
                                    )
                            ),
                    Object.class
            );
            result = responseReader.getSingleResult(response);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result.orElseThrow(() -> new EmployeeNotFoundException("Employee not found by id=" + id));
    }

    @Override
    public void create(Employee employee) {
        try {
            esClient.index(i -> i
                    .index(EMPLOYEES_INDEX)
                    .document(employee)
            );
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            esClient.delete(d -> d.index(EMPLOYEES_INDEX).id(id));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> search(Map<String, String> params) {
        Map.Entry<String, String> parameter = params.entrySet().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parameter must be provided"));
        Optional<List<Employee>> result = Optional.empty();

        try {
            SearchResponse<Object> response = esClient.search(s -> s
                            .index(EMPLOYEES_INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field(parameter.getKey())
                                            .query(parameter.getValue())
                                    )
                            ),
                    Object.class
            );
            result = responseReader.getMultipleResults(response);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result.orElse(Collections.emptyList());
    }

    @Override
    public JsonNode aggregate(String fieldName) {
        try {
            SearchResponse<Void> response = esClient.search(s -> s
                            .index(EMPLOYEES_INDEX)
                            .size(0)
                            .aggregations("result", a -> a
                                    .terms(t -> t
                                            .field(fieldName))),
                    Void.class
            );
            List<StringTermsBucket> result = response.aggregations().get("result").sterms().buckets().array();
            return buildJsonResponse(result);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private JsonNode buildJsonResponse(List<StringTermsBucket> result) {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayNode bucketArray = objectMapper.createArrayNode();

        for (StringTermsBucket bucket : result) {
            ObjectNode bucketNode = objectMapper.createObjectNode();
            bucketNode.put("key", bucket.key().stringValue());
            bucketNode.put("docCount", bucket.docCount());
            bucketArray.add(bucketNode);
        }
        jsonNode.set("buckets", bucketArray);

        return jsonNode;
    }
}
