package com.epam.jmp.nosql.elasticsearch.service;

import static com.epam.jmp.nosql.elasticsearch.utils.QueryTemplate.AGGREGATE_QUERY_TEMPLATE;
import static com.epam.jmp.nosql.elasticsearch.utils.QueryTemplate.FIND_BY_FIELD_VALUE_QUERY_TEMPLATE;
import static com.epam.jmp.nosql.elasticsearch.utils.QueryTemplate.FIND_BY_ID_QUERY_TEMPLATE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

import com.epam.jmp.nosql.elasticsearch.exception.EmployeeNotFoundException;
import com.epam.jmp.nosql.elasticsearch.model.Employee;
import com.epam.jmp.nosql.elasticsearch.utils.EmployeeLowLevelResponseReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EmployeeLowLevelService implements EmployeeService {

    private static final String GET_REQUEST = "GET";
    private static final String POST_REQUEST = "POST";
    private static final String DELETE_REQUEST = "DELETE";
    private static final String EMPLOYEES_ENDPOINT = "/employees";
    private static final String SEARCH_EMPLOYEES_ENDPOINT = EMPLOYEES_ENDPOINT + "/_search";

    private final RestClient restClient;
    private final EmployeeLowLevelResponseReader responseReader;
    private final ObjectMapper objectMapper;

    public EmployeeLowLevelService(RestClient restClient, EmployeeLowLevelResponseReader responseReader, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.responseReader = responseReader;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Employee> findAll() {
        log.info("Searching all employees");
        Optional<List<Employee>> result = Optional.empty();
        try {
            Request request = new Request(GET_REQUEST, SEARCH_EMPLOYEES_ENDPOINT);
            Response response = restClient.performRequest(request);
            result = responseReader.readMultipleValues(EntityUtils.toString(response.getEntity()));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return result.orElse(Collections.emptyList());
    }

    @Override
    public Employee findById(String id) {
        log.info("Searching employee by id=" + id);
        Optional<Employee> result = Optional.empty();
        try {
            Request request = new Request(GET_REQUEST, SEARCH_EMPLOYEES_ENDPOINT);
            request.setJsonEntity(applyParametersToTemplate(FIND_BY_ID_QUERY_TEMPLATE, id));
            Response response = restClient.performRequest(request);
            result = responseReader.readSingleValue(EntityUtils.toString(response.getEntity()));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result.orElseThrow(() -> new EmployeeNotFoundException("Employee not found by id=" + id));
    }

    @Override
    public void create(Employee employee) {
        log.info("Creating employee");
        try {
            Request request = new Request(POST_REQUEST, EMPLOYEES_ENDPOINT + "/_doc");
            request.setJsonEntity(objectMapper.writeValueAsString(employee));
            restClient.performRequest(request);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        log.info("Deleting employee bu id=" + id);
        try {
            Request request = new Request(DELETE_REQUEST, EMPLOYEES_ENDPOINT + "/_doc/" + id);
            restClient.performRequest(request);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> search(Map<String, String> params) {
        Optional<List<Employee>> result = Optional.empty();
        try {
            Request request = new Request(GET_REQUEST, SEARCH_EMPLOYEES_ENDPOINT);
            Map.Entry<String, String> param = params.entrySet().stream().findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Parameter must be provided"));
            request.setJsonEntity(applyParametersToTemplate(FIND_BY_FIELD_VALUE_QUERY_TEMPLATE, param.getKey(), param.getValue()));
            Response response = restClient.performRequest(request);
            result = responseReader.readMultipleValues(EntityUtils.toString(response.getEntity()));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return result.orElse(Collections.emptyList());
    }

    @Override
    public JsonNode aggregate(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field must be provided");
        }

        JsonNode result = null;

        try {
            Request request = new Request(GET_REQUEST, SEARCH_EMPLOYEES_ENDPOINT);
            request.setJsonEntity(applyParametersToTemplate(AGGREGATE_QUERY_TEMPLATE, fieldName));
            Response response = restClient.performRequest(request);
            result = objectMapper.readTree(EntityUtils.toString(response.getEntity()));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    private String applyParametersToTemplate(String template, String... params) {
        return String.format(template, params);
    }
}
