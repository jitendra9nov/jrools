package com.jrools.rule.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	 @ExceptionHandler(value = {ServiceException.class})
	  public ResponseEntity<ErrorMessage> resourceNotFoundException(ServiceException ex, WebRequest request) {
	    ErrorMessage message = ErrorMessage.create(ex.getStatusCode().value(), ex.getMessage(), request.getDescription(false));
	    
	    
	    return new ResponseEntity<>(message, ex.getStatusCode());
	  }

}
