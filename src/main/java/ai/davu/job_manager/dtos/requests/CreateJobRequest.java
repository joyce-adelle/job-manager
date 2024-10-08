package ai.davu.job_manager.dtos.requests;

import ai.davu.job_manager.utils.validations.cron.ValidCron;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateJobRequest {

    private String name;

    private String description;

    @ValidCron
    private String scheduleInterval;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    List<TaskRequest> tasks;

}
