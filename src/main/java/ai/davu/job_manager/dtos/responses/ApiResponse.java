package ai.davu.job_manager.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
public class ApiResponse<T> {

	private Instant timeStamp;
	private boolean isSuccessful;
	private T data;
	private int status;
	private String path;

}
