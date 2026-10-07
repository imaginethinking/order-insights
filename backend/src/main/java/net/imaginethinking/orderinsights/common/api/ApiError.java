package net.imaginethinking.orderinsights.common.api;

import org.springframework.http.HttpStatus;

public record ApiError(
    int status,
    String error,
    String message,
    String path) {
  public static ApiError of(HttpStatus httpStatus, String message, String path) {
    return new ApiError(
        httpStatus.value(),
        httpStatus.getReasonPhrase(),
        message,
        path);
  }
}
