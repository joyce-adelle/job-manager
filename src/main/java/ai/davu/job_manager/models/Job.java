package ai.davu.job_manager.models;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    private String id;

    private String description;

    private String scheduleInterval;

    private LocalDate startDate;

    private LocalDate endDate;

}
