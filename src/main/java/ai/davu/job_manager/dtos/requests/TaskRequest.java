package ai.davu.job_manager.dtos.requests;

import ai.davu.job_manager.utils.enums.TaskOperators;
import ai.davu.job_manager.utils.validations.cron.ValidCron;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String command;

    @ValidCron
    private String scheduleInterval;

    private TaskOperators taskOperators;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

}
