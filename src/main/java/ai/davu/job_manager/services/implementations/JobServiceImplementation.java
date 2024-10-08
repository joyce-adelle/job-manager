package ai.davu.job_manager.services.implementations;

import ai.davu.job_manager.dtos.requests.CreateJobRequest;
import ai.davu.job_manager.integrations.airflow.AirflowClient;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import ai.davu.job_manager.services.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobServiceImplementation implements JobService {

    private final AirflowClient airflowClient;

    @Override
    public Job createJob(CreateJobRequest request) {
        return airflowClient.createJob(Job.builder()
                //to ensure uniqueness of Id
                .id(request.getName() + generateRandomNumber())
                .endDate(request.getEndDate())
                .startDate(request.getStartDate())
                .scheduleInterval(request.getScheduleInterval())
                .description(request.getDescription())
                .build());
    }

    @Override
    public JobRun runJob(String jobId, Map<String, String> conf) {
        return airflowClient.runJob(jobId, conf);
    }

    @Override
    public List<JobRun> getJobRuns(String jobId) {
        return airflowClient.getJobRuns(jobId);
    }

    private static String generateRandomNumber() {

        SecureRandom random = new SecureRandom();
        return String.valueOf(10000 + random.nextInt(90000));

    }

}
