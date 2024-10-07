package ai.davu.job_manager.models;

import ai.davu.job_manager.utils.enums.TaskOperators;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobTask {

    private String id;

    private String command;

    private String startDate;

    private String endDate;

    private String scheduleInterval;

    private TaskOperators taskOperators;

}
