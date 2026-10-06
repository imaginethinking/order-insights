package net.imaginethinking.orderinsights.common.api;

import java.util.StringJoiner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import net.imaginethinking.orderinsights.common.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex,
      HttpServletRequest req) {
    ApiError apiError = ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());

    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
      HttpServletRequest req) {

    String message = "Invalid value for parameter '" + ex.getName() + "'.";

    ApiError apiError = ApiError.of(HttpStatus.BAD_REQUEST, message, req.getRequestURI());

    return ResponseEntity.badRequest().body(apiError);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiError> handleMethodValidation(HandlerMethodValidationException ex,
      HttpServletRequest req) {

    if (ex.isForReturnValue()) {
      ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR,
          "Response validation failed.", req.getRequestURI());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    StringJoiner messages = new StringJoiner("; ");

    for (var result : ex.getParameterValidationResults()) {
      for (var error : result.getResolvableErrors()) {
        String message = error.getDefaultMessage();
        messages.add(message != null ? message : "Invalid request parameter.");
      }
    }

    ApiError apiError =
        ApiError.of(HttpStatus.BAD_REQUEST, messages.toString(), req.getRequestURI());

    return ResponseEntity.badRequest().body(apiError);
  }
}
