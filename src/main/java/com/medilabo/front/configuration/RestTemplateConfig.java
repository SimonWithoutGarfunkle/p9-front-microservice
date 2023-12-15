package com.medilabo.front.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static com.medilabo.front.Constants.URL_GATEWAY;

/**
 * Configuration class for setting up a customized RestTemplate for all controllers
 */
@Configuration
public class RestTemplateConfig {

    private static Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);


    /**
     * Add an interceptor to auto fill the authorization header of http requests send to backend applications
     *
     * @return The configured RestTemplate with authentication headers.
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        logger.debug("Add header autofill");
        ClientHttpRequestInterceptor interceptor = (httpRequest, bytes, execution) -> {
            httpRequest.getHeaders().set("Authorization", "Basic " + Base64.getEncoder().encodeToString(("user:user").getBytes()));
            return execution.execute(httpRequest, bytes);
        };
        restTemplate.getInterceptors().add(interceptor);
        return restTemplate;
    }

}
