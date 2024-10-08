package ai.davu.job_manager.unit;

import ai.davu.job_manager.integrations.airflow.interceptors.AirflowClientInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AirflowClientInterceptorTest extends BaseTest {

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @InjectMocks
    AirflowClientInterceptor interceptor = new AirflowClientInterceptor(new ObjectMapper());

    @Test
    void testInterceptUnknownEndpoint() throws IOException, URISyntaxException {

        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/experimental/dags/tasks"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        verify(execution, times(0)).execute(eq(request), any());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testInterceptGetCorrectDagRunsEndpoint() throws IOException, URISyntaxException {

        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/experimental/dags/sample_dag_1/dag_runs"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        verify(execution, times(0)).execute(eq(request), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("[{\"state\":\"paused\",\"conf\":\"{\\\"key\\\":\\\"value\\\"}\",\"dag_run_id\":\"sample_dag_1_run_1\",\"dag_id\":\"sample_dag_1\",\"execution_date\":\"2024-10-07T11:34:15\"},{\"state\":\"running\",\"conf\":\"{}\",\"dag_run_id\":\"sample_dag_1_run_2\",\"dag_id\":\"sample_dag_2\",\"execution_date\":\"2024-10-08T11:34:15\"}]", responseBody);

    }

    @Test
    void testInterceptGetIncorrectDagRunsEndpoint() throws IOException, URISyntaxException {

        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/experimental/dags/sample_dag_4/dag_runs"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        verify(execution, times(0)).execute(eq(request), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("[]", responseBody);

    }

    @Test
    void testInterceptRunDagEndpoint() throws IOException, URISyntaxException {

        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/experimental/dags/sample_dag_2/dag_runs"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        ClientHttpResponse response = interceptor.intercept(request, "{\"conf\":\"{\\\"key\\\":\\\"value\\\"}\"}".getBytes(StandardCharsets.UTF_8), execution);

        verify(execution, times(0)).execute(eq(request), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(responseBody).startsWith("{\"state\":\"queued\",\"conf\":\"{\\\"key\\\":\\\"value\\\"}\",\"dag_run_id\":\"sample_dag_2_run_2\",\"dag_id\":\"sample_dag_2\",\"execution_date\":");

    }

    @Test
    void testAddDagThenRun() throws URISyntaxException, IOException {

        AirflowClientInterceptor.refreshAirflowDAGs("test_dag");

        when(request.getURI()).thenReturn(new URI("http://localhost:8080/api/experimental/dags/test_dag/dag_runs"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        verify(execution, times(0)).execute(eq(request), any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("[]", responseBody);
    }

}
