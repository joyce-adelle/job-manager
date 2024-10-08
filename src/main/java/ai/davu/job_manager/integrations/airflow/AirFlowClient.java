package ai.davu.job_manager.integrations.airflow;

import ai.davu.job_manager.integrations.BaseClient;
import ai.davu.job_manager.integrations.airflow.models.DAGRun;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AirFlowClient implements BaseClient {

    @Value("${airflow.api.host}")
    private String airflowApiHost;

    @Value("${airflow.api.username}")
    private String username;

    @Value("${airflow.api.password}")
    private String password;

    private final RestTemplate restTemplate;

    public AirFlowClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        testServer();
        log.info("Airflow Connected!!");
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    @Override
    public Job createJob(Job job) {
        return null;
    }

    @Override
    public JobRun runJob(String jobId, Map<String, String> conf) {

        log.info("Request to run DAG id: {}", jobId);

        Map<String, Map<String, String>> body = Map.of("conf", conf);
        HttpEntity<Map<String, Map<String, String>>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<DAGRun> response = restTemplate.exchange(
                airflowApiHost + "/dags/" + jobId + "/dag_runs", HttpMethod.POST, entity, DAGRun.class);

        if (response.getBody() == null || response.getStatusCode() != HttpStatus.OK)
            log.error("Error occurred running DAG id: {}, status: {}", jobId, response.getStatusCode());

        JobRun jobRun = getJobRun(response.getBody());
        log.info("Successfully running DAG id: {}, run id: {}", jobId, jobRun.getId());

        return jobRun;

    }

    @Override
    public List<JobRun> getJobRuns(String jobId) {

        log.info("Request to get all runs for DAG id: {}", jobId);

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<DAGRun[]> response = restTemplate.exchange(
                airflowApiHost + "/dags/" + jobId + "/dag_runs", HttpMethod.GET, entity, DAGRun[].class);

        List<JobRun> result;

        if (response.getBody() == null)
            result = List.of();

        else
            result = Arrays.stream(response.getBody()).map(AirFlowClient::getJobRun).toList();

        log.info("Successfully fetched all runs for DAG id: {}, total: {}", jobId, result.size());

        return result;

    }

    private void testServer() {

        log.info("Running connection test to Airflow");

        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<Object> response = restTemplate.exchange(
                airflowApiHost + "/test", HttpMethod.GET, entity, Object.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Cannot connect to Airflow");

    }

    private static JobRun getJobRun(DAGRun data) {

        if (data == null)
            return null;

        return JobRun.builder()
                .id(data.getDagRunId())
                .jobId(data.getDagId())
                .state(data.getState())
                .executionDate(data.getExecutionDate())
                .conf(data.getConf())
                .build();
    }

}
