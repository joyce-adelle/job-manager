package ai.davu.job_manager.integrations;

import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;

import java.util.List;
import java.util.Map;

public interface BaseClient {

    Job createJob(Job job);

    JobRun runJob(String jobId, Map<String, String> conf);

    List<JobRun> getJobRuns(String jobId);

}
