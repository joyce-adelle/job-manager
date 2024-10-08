package ai.davu.job_manager.dtos.requests;

import ai.davu.job_manager.utils.validations.cron.ValidCron;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateJobRequest {

    @NotBlank
    private String name;

    private String description;

    @ValidCron
    private String scheduleInterval;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotEmpty(message = "At least one task is required")
    List< @NotNull @Valid TaskRequest> tasks;

}
