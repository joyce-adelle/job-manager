package ai.davu.job_manager.integrations.airflow.interceptors;

import ai.davu.job_manager.integrations.airflow.models.DAGRun;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Component
@Profile("dev")
public class AirflowClientInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private static final HashMap<String, List<DAGRun>> DAGs = new HashMap<>();

    public AirflowClientInterceptor(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        DAGs.put("sample_dag_1", Stream.of(DAGRun.builder()
                        .dagId("sample_dag_1")
                        .dagRunId("sample_dag_1_run_1")
                        .state("paused")
                        .executionDate(formatter.format(LocalDateTime.parse("2024-10-07T11:34:15")))
                        .conf("{\"key\":\"value\"}")
                        .build(),
                DAGRun.builder()
                        .dagId("sample_dag_2")
                        .dagRunId("sample_dag_1_run_2")
                        .state("running")
                        .executionDate(formatter.format(LocalDateTime.parse("2024-10-08T11:34:15")))
                        .conf("{}")
                        .build()).collect(toCollection(ArrayList::new)));

        DAGs.put("sample_dag_2", Stream.of(DAGRun.builder()
                .dagId("sample_dag_2")
                .dagRunId("sample_dag_2_run_1")
                .state("running")
                .executionDate(formatter.format(LocalDateTime.now()))
                .conf("{}")
                .build()).collect(toCollection(ArrayList::new)));

        DAGs.put("sample_dag_3", new ArrayList<>());

    }

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) {

        log.info("Incoming request, method:{}, uri:{} ", request.getMethod(), request.getURI());

        if (request.getMethod().equals(HttpMethod.GET) && request.getURI().getPath().equals("/api/experimental/dags/sample_dag_1/dag_runs")) {
            try {
                return createResponse(
                        objectMapper.writeValueAsString(DAGs.get("sample_dag_1"))
                        , HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("Error converting sample_dag_1 to Json, error: {}", e.getMessage());
                return createResponse("", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getMethod().equals(HttpMethod.GET) && request.getURI().getPath().equals("/api/experimental/dags/sample_dag_2/dag_runs")) {
            try {
                return createResponse(
                        objectMapper.writeValueAsString(DAGs.get("sample_dag_2"))
                        , HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("Error converting sample_dag_2 to Json, error: {}", e.getMessage());
                return createResponse("", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getMethod().equals(HttpMethod.GET) && request.getURI().getPath().equals("/api/experimental/dags/sample_dag_3/dag_runs")) {
            try {
                return createResponse(
                        objectMapper.writeValueAsString(DAGs.get("sample_dag_3"))
                        , HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("Error converting sample_dag_3 to Json, error: {}", e.getMessage());
                return createResponse("", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getMethod().equals(HttpMethod.GET) && request.getURI().getPath().startsWith("/api/experimental/dags") && request.getURI().getPath().endsWith("/dag_runs")) {
            String path = request.getURI().getPath();
            String dagId = path.substring(path.indexOf("/dags/") + 6, path.indexOf("/dag_runs"));
            try {
                return createResponse(
                        objectMapper.writeValueAsString(DAGs.getOrDefault(dagId, List.of()))
                        , HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("Error converting {} to Json, error: {}", dagId, e.getMessage());
                return createResponse("", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getMethod().equals(HttpMethod.POST) && request.getURI().getPath().startsWith("/api/experimental/dags") && request.getURI().getPath().endsWith("/dag_runs")) {

            try {

                String path = request.getURI().getPath();
                String dagId = path.substring(path.indexOf("/dags/") + 6, path.indexOf("/dag_runs"));


                if (!DAGs.containsKey(dagId))
                    return createResponse("", HttpStatus.NOT_FOUND);

                List<DAGRun> runs = DAGs.get(dagId);
                DAGRun run = DAGRun.builder()
                        .dagId(dagId)
                        .dagRunId(dagId + "_run_" + (runs.size() + 1))
                        .state("queued")
                        .executionDate(formatter.format(LocalDateTime.now()))
                        .build();
                if (body.length > 0) {
                    JsonNode jsonNode = objectMapper.readTree(new String(body, StandardCharsets.UTF_8));
                    run.setConf(jsonNode.get("conf").asText());
                }

                runs.add(run);
                DAGs.putIfAbsent(dagId, runs);

                return createResponse(
                        objectMapper.writeValueAsString(run)
                        , HttpStatus.OK);
            } catch (JsonProcessingException e) {
                log.error("Error parsing run to Json, error: {}", e.getMessage());
                return createResponse("", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getMethod().equals(HttpMethod.GET) && request.getURI().getPath().equals("/api/experimental/test")) {
            return createResponse("", HttpStatus.OK);
        }
        return createResponse("", HttpStatus.NOT_FOUND);

    }

    private ClientHttpResponse createResponse(String body, HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public @NonNull HttpStatus getStatusCode() {
                return status;
            }

            @Override
            public @NonNull String getStatusText() {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
                // Nothing to close
            }

            @Override
            public @NonNull InputStream getBody() {
                return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public @NonNull HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }

    public static void refreshAirflowDAGs(String dagId) {
        DAGs.putIfAbsent(dagId, new ArrayList<>());
    }

}
