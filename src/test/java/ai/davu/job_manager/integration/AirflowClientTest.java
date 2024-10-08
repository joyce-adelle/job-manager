package ai.davu.job_manager.integration;

import ai.davu.job_manager.integrations.airflow.AirflowClient;
import ai.davu.job_manager.models.JobRun;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class AirflowClientTest {

    @Autowired
    AirflowClient client;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createJob() {
    }

    @Test
    void runValidJob() {

        JobRun job = client.runJob("sample_dag_3", Map.of("key", "value"));

        assertThat(job.getJobId()).isEqualTo("sample_dag_3");
        assertThat(job.getState()).isEqualTo("queued");
        assertThat(job.getConf()).isEqualTo(Map.of("key", "value"));
        assertThat(job.getExecutionDate()).isNotNull();

    }

    @Test
    void runValidJobWithoutConf() {

        JobRun job = client.runJob("sample_dag_3", null);

        assertThat(job.getJobId()).isEqualTo("sample_dag_3");
        assertThat(job.getState()).isEqualTo("queued");
        assertThat(job.getConf()).isEqualTo(Map.of());
        assertThat(job.getExecutionDate()).isNotNull();

    }

    @Test
    void runInValidJob() {
        assertThat(client.runJob("sample_dag_5", null)).isNull();
    }

    @Test
    void getValidJobRuns() {

        List<JobRun> runs = client.getJobRuns("sample_dag_1");
        assertThat(runs.size()).isEqualTo(2);

    }

    @Test
    void getInValidJobRuns() {

        List<JobRun> runs = client.getJobRuns("sample_dag_6");
        assertThat(runs.size()).isEqualTo(0);

    }

    @AfterEach
    void tearDown() {
    }

}