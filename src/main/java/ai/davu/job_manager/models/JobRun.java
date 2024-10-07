package ai.davu.job_manager.models;

import java.util.Map;

public class JobRun {

    private String id;

    private String jobId;

    private String executionDate;

    private Map<String, String> conf;

    private String state;

}
