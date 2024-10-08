package ai.davu.job_manager.integrations.airflow;

import ai.davu.job_manager.integrations.BaseClient;
import ai.davu.job_manager.integrations.airflow.models.DAGRun;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AirflowClient implements BaseClient {

    private final String airflowApiBaseUrl;

    private final String username;

    private final String password;

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    public AirflowClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${airflow.api.base.url}") String airflowApiBaseUrl, @Value("${airflow.api.username}") String username, @Value("${airflow.api.password}") String password) {

        this.mapper = mapper;
        this.restTemplate = restTemplate;

        this.airflowApiBaseUrl = airflowApiBaseUrl;
        this.username = username;
        this.password = password;

        testServer();
        log.info("Airflow Connected!!");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    @Override
    public Job createJob(@NonNull Job job) {
        return null;
    }

    @Override
    public JobRun runJob(@NonNull String jobId, Map<String, String> conf) {

        try {

            log.info("Request to run DAG id: {}", jobId);

            if (jobId.isBlank())
                return null;

            String body;
            if (conf == null || conf.isEmpty())
                body = mapper.writeValueAsString(Map.of("conf", mapper.writeValueAsString(Map.of())));
            else
                body = mapper.writeValueAsString(Map.of("conf", mapper.writeValueAsString(conf)));

            HttpEntity<String> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<DAGRun> response = restTemplate.exchange(
                    airflowApiBaseUrl + "/dags/" + jobId + "/dag_runs", HttpMethod.POST, entity, DAGRun.class);

            if (response.getBody() == null || response.getStatusCode() != HttpStatus.OK)
                log.error("Error occurred running DAG id: {}, status: {}", jobId, response.getStatusCode());

            JobRun jobRun = getJobRun(response.getBody());
            log.info("Successfully running DAG id: {}, run id: {}", jobId, jobRun.getId());

            return jobRun;

        } catch (JsonProcessingException e) {
            log.error("Processing error trying to run DAG, error:  ", e);
            return null;
        } catch (HttpClientErrorException e) {
            log.error("Invalid DAG, error:  ", e);
            return null;
        }

    }

    @Override
    public List<JobRun> getJobRuns(@NonNull String jobId) {

        log.info("Request to get all runs for DAG id: {}", jobId);

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<DAGRun[]> response = restTemplate.exchange(
                airflowApiBaseUrl + "/dags/" + jobId + "/dag_runs", HttpMethod.GET, entity, DAGRun[].class);

        List<JobRun> result;

        if (response.getBody() == null)
            result = List.of();

        else
            result = Arrays.stream(response.getBody()).map(this::getJobRun).toList();

        log.info("Successfully fetched all runs for DAG id: {}, total: {}", jobId, result.size());

        return result;

    }

    private void testServer() {

        log.info("Running connection test to Airflow");

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Object> response = restTemplate.exchange(
                airflowApiBaseUrl + "/test", HttpMethod.GET, entity, Object.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Cannot connect to Airflow");

    }

    private JobRun getJobRun(DAGRun data) {

        if (data == null)
            return null;

        Map<String, String> conf = Map.of();

        if (data.getConf() != null && !data.getConf().isBlank()) {
            try {
                conf = mapper.readValue(data.getConf(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error parsing conf: {} ", data.getConf());
            }
        }


        return JobRun.builder()
                .id(data.getDagRunId())
                .jobId(data.getDagId())
                .state(data.getState())
                .executionDate(data.getExecutionDate())
                .conf(conf)
                .build();
    }

}
