package ai.davu.job_manager.controllers;

import ai.davu.job_manager.dtos.responses.ApiResponse;
import ai.davu.job_manager.dtos.responses.MessageResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleValidationException(MethodArgumentNotValidException ex,
                                                                                  HttpServletRequest httpServletRequest) {

        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessages.add(fieldError.getDefaultMessage());
        }

        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message(StringUtils.collectionToCommaDelimitedString(errorMessages))
                        .build())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleConstraintValidationException(ConstraintViolationException ex,
                                                                                            HttpServletRequest httpServletRequest) {

        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> fieldError : ex.getConstraintViolations()) {
            errorMessages.add(fieldError.getMessage());
        }

        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message(StringUtils.collectionToCommaDelimitedString(errorMessages))
                        .build())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                                      HttpServletRequest httpServletRequest) {

        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message("Invalid Path")
                        .build())
                .status(HttpStatus.NOT_FOUND.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception,
                                                                                                  HttpServletRequest httpServletRequest) {

        String message = "Value: '" + exception.getValue() + "' incorrect datatype given for " + exception.getParameter().getParameterName() + " expected datatype: " + Objects.requireNonNull(exception.getRequiredType()).getSimpleName();
        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message((message))
                        .build())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception,
                                                                                              HttpServletRequest httpServletRequest) {

        String message = "Invalid Request: " + Objects.requireNonNull(exception.getMessage()).split(":")[0];
        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message((message))
                        .build())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleServletException(ServletException ex,
                                                                               HttpServletRequest httpServletRequest) {

        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message(ex.getMessage())
                        .build())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<MessageResponse>> handleGenericException(Exception ex, HttpServletRequest httpServletRequest) {

        log.error("GenericException error: {}", ex.getMessage());

        ApiResponse<MessageResponse> errorResponse = ApiResponse.<MessageResponse>builder()
                .isSuccessful(false)
                .data(MessageResponse.builder()
                        .message("Internal server error")
                        .build())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(httpServletRequest.getRequestURI())
                .timeStamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
