package ai.davu.job_manager.integrations.airflow.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
