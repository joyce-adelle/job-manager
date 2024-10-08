package ai.davu.job_manager.models;

import ai.davu.job_manager.utils.enums.TaskOperators;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobTask {

    private String id;

    private String command;

    private String scheduleInterval;

    private TaskOperators taskOperators;

    private LocalDate startDate;

    private LocalDate endDate;

}
