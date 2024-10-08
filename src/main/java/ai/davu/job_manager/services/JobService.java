package ai.davu.job_manager.services;

import ai.davu.job_manager.dtos.requests.CreateJobRequest;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;

import java.util.List;
import java.util.Map;

public interface JobService {

    Job createJob(CreateJobRequest request);

    JobRun runJob(String jobId,  Map<String, String> conf);

    List<JobRun> getJobRuns(String jobId);

}
