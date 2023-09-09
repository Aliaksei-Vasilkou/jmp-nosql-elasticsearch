package com.epam.jmp.nosql.elasticsearch.utils;

public class QueryTemplate {

    public static final String FIND_BY_ID_QUERY_TEMPLATE = """
            {
              "query": {
                "term": {
                  "_id": {
                    "value": "%s"
                  }
                }
              }
            }
            """;

    public static final String FIND_BY_FIELD_VALUE_QUERY_TEMPLATE = """
            {
              "query": {
                "term": {
                  "%s": {
                    "value": "%s"
                  }
                }
              }
            }
            """;

    public static final String AGGREGATE_QUERY_TEMPLATE = """
            {
              "size": 0,
              "aggs": {
                "result": {
                  "terms": {
                    "field": "%s"
                  }
                }
              }
            }
            """;
}
