package ai.davu.job_manager.integrations.airflow;

import ai.davu.job_manager.integrations.BaseClient;
import ai.davu.job_manager.integrations.airflow.interceptors.AirflowClientInterceptor;
import ai.davu.job_manager.integrations.airflow.models.DAGRun;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import ai.davu.job_manager.models.JobTask;
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

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AirflowClient implements BaseClient {

    private final String airflowApiBaseUrl;

    private final String username;

    private final String password;

    private final Path airflowDagPath;

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    public AirflowClient(RestTemplate restTemplate, ObjectMapper mapper, @Value("${airflow.api.base.url}") String airflowApiBaseUrl, @Value("${airflow.api.username}") String username, @Value("${airflow.api.password}") String password, @Value("${airflow.dag.dir}") Path airflowDagPath) throws IOException {

        this.mapper = mapper;
        this.restTemplate = restTemplate;

        this.airflowApiBaseUrl = airflowApiBaseUrl;
        this.username = username;
        this.password = password;
        this.airflowDagPath = airflowDagPath;

        if (!Files.exists(airflowDagPath))
            Files.createDirectories(airflowDagPath);

        testServer();
        log.info("Airflow Connected!!");

    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    @Override
    public Job createJob(@NonNull Job job, List<JobTask> tasks) {

        //Should throw exception
        if (job.getId() == null || tasks.isEmpty())
            return null;

        String script = generateDAGScript(job, tasks);
        saveDAGScript(job.getId(), script);

        //Mocking Airflow's check of the DAG folder to create DAGs
        AirflowClientInterceptor.refreshAirflowDAGs(job.getId());
        return job;

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

    private String generateDAGScript(Job job, List<JobTask> tasks) {

        // Generate Python script for the DAG
        StringBuilder script = new StringBuilder();
        script.append("from airflow import DAG\n");
        script.append("from airflow.operators.bash import BashOperator\n");
        script.append("from datetime import timedelta, datetime\n\n");

        script.append("default_args = {\n");
        script.append("    'owner': 'airflow',\n");
        script.append("    'depends_on_past': False,\n");
        script.append("    'email_on_failure': False,\n");
        script.append("    'email_on_retry': False,\n");
        script.append("    'retries': 1,\n");
        script.append("    'retry_delay': timedelta(minutes=1),\n");
        script.append("}\n\n");

        script.append("dag = DAG(\n");
        script.append("    dag_id='").append(job.getId()).append("',\n");
        script.append("    default_args=default_args,\n");
        script.append("    description='").append(job.getDescription()).append("',\n");

        if (job.getScheduleInterval() == null || job.getScheduleInterval().isBlank())
            script.append("    schedule_interval=None,\n");
        else
            script.append("    schedule_interval='").append(job.getScheduleInterval()).append("',\n");

        if (job.getStartDate() != null) {
            LocalDate startDate = job.getStartDate();
            script.append("    start_date=datetime(").append(startDate.getYear()).append(",").append(startDate.getMonth().getValue()).append(",").append(startDate.getDayOfWeek().getValue()).append("),\n");
        }

        if (job.getEndDate() != null) {
            LocalDate endDate = job.getEndDate();
            script.append("    end_date=datetime(").append(endDate.getYear()).append(",").append(endDate.getMonth().getValue()).append(",").append(endDate.getDayOfWeek().getValue()).append("),\n");
        }

        script.append("    catchup=False\n");
        script.append(")\n\n");

        for (JobTask task : tasks) {
            script.append(generateTaskScript(task));
        }

        if (tasks.size() > 1)
            script.append(tasks.stream().map(task -> task.getId().replace(" ", "")).collect(Collectors.joining(" >> ")));

        script.append("\n\n");

        return script.toString();

    }

    private String generateTaskScript(JobTask task) {

        StringBuilder script = new StringBuilder();
        script.append(task.getId().replace(" ", "")).append(" = BashOperator(\n");
        script.append("    task_id='").append(task.getId()).append("',\n");
        script.append("    bash_command='").append(task.getCommand()).append("',\n");

        if (task.getStartDate() != null) {
            LocalDate startDate = task.getStartDate();
            script.append("    start_date=datetime(").append(startDate.getYear()).append(",").append(startDate.getMonth().getValue()).append(",").append(startDate.getDayOfWeek().getValue()).append("),\n");
        }

        if (task.getEndDate() != null) {
            LocalDate endDate = task.getEndDate();
            script.append("    end_date=datetime(").append(endDate.getYear()).append(",").append(endDate.getMonth().getValue()).append(",").append(endDate.getDayOfWeek().getValue()).append("),\n");
        }

        script.append("    dag=dag\n");
        script.append(")\n\n");

        return script.toString();

    }

    private void saveDAGScript(String dagId, String script) {
        Path filePath = airflowDagPath.resolve(dagId + ".py");
        try (FileWriter writer = new FileWriter(filePath.toUri().getPath())) {
            writer.write(script);
        } catch (IOException e) {
            log.error("Failed to save DAG script", e);
            // Throw error for retry
        }
    }

}
