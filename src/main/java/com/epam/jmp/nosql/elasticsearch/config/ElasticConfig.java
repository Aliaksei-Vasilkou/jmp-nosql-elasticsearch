package com.epam.jmp.nosql.elasticsearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                        new HttpHost("<elastic_host>", 9243, "https"))
                .setDefaultHeaders(buildAuthenticationHeaders())
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000)).build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport restClientTransport) {
        return new ElasticsearchClient(restClientTransport);
    }

    @Bean
    public RestClientTransport restClientTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    private Header[] buildAuthenticationHeaders() {
        return new Header[] { new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey <api_key>") };
    }
}
