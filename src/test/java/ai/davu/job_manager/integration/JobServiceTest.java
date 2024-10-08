package ai.davu.job_manager.integration;

import ai.davu.job_manager.dtos.requests.CreateJobRequest;
import ai.davu.job_manager.dtos.requests.TaskRequest;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.services.JobService;
import ai.davu.job_manager.utils.enums.TaskOperators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JobServiceTest {

    @Value("${airflow.dag.dir}")
    private Path airFlowDagPath;

    @Autowired
    JobService jobService;

    @Test
    void testCreateJob() {

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskOperators(TaskOperators.BASHOPERATOR);
        taskRequest.setCommand("Echo hello DAG!");
        taskRequest.setName("task 1");
        taskRequest.setStartDate(LocalDate.now());
        taskRequest.setScheduleInterval("");

        CreateJobRequest request = new CreateJobRequest();
        request.setName("test_dag");
        request.setStartDate(LocalDate.now());
        request.setTasks(List.of(taskRequest));
        request.setDescription("Test DAG");
        request.setScheduleInterval("");
        request.setEndDate(LocalDate.now().plusDays(9));

        Job job = jobService.createJob(request);

        assertThat(job.getId()).startsWith(request.getName());
        assertThat(job.getDescription()).isEqualTo(request.getDescription());
        assertThat(job.getEndDate()).isEqualTo(request.getEndDate());
        assertThat(job.getStartDate()).isEqualTo(request.getStartDate());
        assertThat(job.getScheduleInterval()).isEqualTo(request.getScheduleInterval());

        assertTrue((new File(airFlowDagPath.resolve(job.getId() + ".py").toUri()).exists()));

    }

    @AfterEach
    void tearDown() throws IOException {
        FileSystemUtils.deleteRecursively(airFlowDagPath);
    }

}
