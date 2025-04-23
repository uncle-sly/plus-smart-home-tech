package ru.yandex.practicum.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

   private Throwable cause;
   private List<StackTraceElement> stackTrace;
   private HttpStatus httpStatus;
   private String userMessage;
   private String message;
   private List<Throwable> suppressed;
   private String localizedMessage;

}
