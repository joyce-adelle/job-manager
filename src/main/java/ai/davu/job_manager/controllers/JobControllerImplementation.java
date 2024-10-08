package ai.davu.job_manager.controllers;

import ai.davu.job_manager.dtos.requests.CreateJobRequest;
import ai.davu.job_manager.dtos.requests.RunJobRequest;
import ai.davu.job_manager.dtos.responses.ApiResponse;
import ai.davu.job_manager.models.Job;
import ai.davu.job_manager.models.JobRun;
import ai.davu.job_manager.services.JobService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobControllerImplementation implements JobController {

    JobService jobService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Job>> createJob(CreateJobRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.<Job>builder()
                .data(jobService.createJob(request))
                .isSuccessful(true)
                .status(HttpStatus.OK.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build());
    }

    @Override
    @PostMapping("/{id}/run")
    public ResponseEntity<ApiResponse<JobRun>> runJob(String id, RunJobRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.<JobRun>builder()
                .data(jobService.runJob(id, request.getConf()))
                .isSuccessful(true)
                .status(HttpStatus.OK.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<JobRun>>> getJobRuns(String id, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(ApiResponse.<List<JobRun>>builder()
                .data(jobService.getJobRuns(id))
                .isSuccessful(true)
                .status(HttpStatus.OK.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build());
    }

}
