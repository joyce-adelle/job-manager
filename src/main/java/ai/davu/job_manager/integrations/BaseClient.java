package ai.davu.job_manager.integrations;

import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public interface BaseClient {

    Job createJob(@NonNull Job job);

    JobRun runJob(@NonNull String jobId, Map<String, String> conf);

    List<JobRun> getJobRuns(@NonNull String jobId);

}
