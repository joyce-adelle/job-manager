package ai.davu.job_manager.unit;

import ai.davu.job_manager.integrations.airflow.AirFlowClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

class AirFlowClientTest {

    @Mock
    RestTemplate template;

    @InjectMocks
    AirFlowClient client = new AirFlowClient(template);

    @BeforeEach
    void setUp() {
    }

    @Test
    void createJob() {
    }

    @Test
    void runJob() {
        client.runJob("", null);
    }

    @Test
    void getJobRuns() {
    }

    @AfterEach
    void tearDown() {
    }

}