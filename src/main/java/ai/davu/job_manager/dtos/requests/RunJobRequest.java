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
import java.util.Map;

@Data
public class RunJobRequest {

    private Map<String, String> conf;

}
