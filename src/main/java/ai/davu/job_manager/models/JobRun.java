package ai.davu.job_manager.models;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobRun {

    private String id;

    private String jobId;

    private String executionDate;

    private Map<String, String> conf;

    private String state;

}
