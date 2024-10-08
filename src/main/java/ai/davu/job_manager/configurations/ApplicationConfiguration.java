package ai.davu.job_manager.configurations;

import ai.davu.job_manager.integrations.airflow.interceptors.AirflowClientInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    @Profile("dev")
    public RestTemplate mockRestTemplate(AirflowClientInterceptor mockApiInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(mockApiInterceptor);
        return restTemplate;
    }

    @Bean
    @Profile("!dev")
    public RestTemplate realRestTemplate() {
        return new RestTemplate();
    }

}