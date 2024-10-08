package ai.davu.job_manager.integrations.airflow.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DAGRun {

    @JsonProperty("dag_run_id")
    private String dagRunId;

    @JsonProperty("dag_id")
    private String dagId;

    private String state;

    @JsonProperty("execution_date")
    private String executionDate;

    private String conf;

}
