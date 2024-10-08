package ai.davu.job_manager.controllers;

import ai.davu.job_manager.dtos.requests.CreateJobRequest;
import ai.davu.job_manager.dtos.requests.RunJobRequest;
import ai.davu.job_manager.dtos.responses.ApiResponse;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Job Manager", description = "Job (DAG) Management endpoints")
@SecurityRequirement(name = "basicAuth")
public interface JobController {

    @Operation(summary = "Create A Job", description = "Create a new Job (DAG)")
    ResponseEntity<ApiResponse<Job>> createJob(@RequestBody @Valid CreateJobRequest request, HttpServletRequest httpServletRequest);

    @Operation(summary = "Run a Job", description = "Run a job by it's id, allows for configurations, 'conf', in the query parameters")
    ResponseEntity<ApiResponse<JobRun>> runJob(@PathVariable("id") @NotBlank(message = "Job id is required") String id, @RequestBody @Valid RunJobRequest request, HttpServletRequest httpServletRequest);

    @Operation(summary = "Get All Job Runs", description = "Get all the runs on this job (DAG) by it's id")
    ResponseEntity<ApiResponse<List<JobRun>>> getJobRuns(@PathVariable("id") @NotBlank(message = "Job id is required") String id, HttpServletRequest httpServletRequest);

}

