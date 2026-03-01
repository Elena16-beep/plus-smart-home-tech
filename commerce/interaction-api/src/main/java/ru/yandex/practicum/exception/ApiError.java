package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private Throwable cause;
    private List<StackTraceElement> stackTrace;
    private HttpStatus httpStatus;
    private String userMessage;
    private String message;
    private List<Throwable> suppressed;
    private String localizedMessage;

    public static ApiError fromException(Exception exception, HttpStatus status) {
        return ApiError.builder()
                .cause(exception.getCause())
                .stackTrace(Arrays.asList(exception.getStackTrace()))
                .httpStatus(status)
                .userMessage(exception.getMessage())
                .message(exception.toString())
                .localizedMessage(exception.getLocalizedMessage())
                .suppressed(Arrays.asList(exception.getSuppressed()))
                .build();
    }
}