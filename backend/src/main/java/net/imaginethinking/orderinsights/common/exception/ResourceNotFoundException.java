package net.imaginethinking.orderinsights.common.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String resourceName, String resourceId) {
    this(String.format("%s with ID: %s could not be found.", resourceName, resourceId));
  }
}
