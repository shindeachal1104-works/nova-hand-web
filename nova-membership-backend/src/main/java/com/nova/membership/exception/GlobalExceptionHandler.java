package com.nova.membership.exception;

import com.nova.membership.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Form validation + binding errors (@Valid @ModelAttribute / @RequestBody). */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ApiResponse> validation(Exception ex) {
        BindingResult result = (ex instanceof BindException be)
                ? be.getBindingResult()
                : ((MethodArgumentNotValidException) ex).getBindingResult();

        String message = result.getFieldErrors().stream()
                .map(e -> e.isBindingFailure()
                        ? "Invalid value for " + e.getField() + "."
                        : e.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" "));
        if (message.isBlank()) message = "Please check the form and try again.";
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiResponse> badCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ResponseEntity<ApiResponse> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    /** Two people registering the same email/mobile at the same moment (unique constraint). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse> duplicate(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("This email or mobile number is already registered."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse> fileTooLarge() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("Uploaded file is too large. Maximum allowed size is 5 MB."));
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<ApiResponse> badMultipart(MultipartException ex) {
        log.warn("Multipart problem: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error("The form upload could not be read. Please try again."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse> unreadable() {
        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid request body."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse> wrongMethod(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiResponse> wrongMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not found."));
    }

    /** Last resort. It is LOGGED now - before, real errors were swallowed and impossible to debug. */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> generic(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong on the server."));
    }
}
