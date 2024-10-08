package ai.davu.job_manager.dtos.requests;

import ai.davu.job_manager.utils.enums.TaskOperators;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TaskRequest {

    private String name;

    private String command;

    private String scheduleInterval;

    private TaskOperators taskOperators;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

}
