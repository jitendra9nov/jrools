package com.jrools.rule.exception;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7032232069111602439L;
	private int statusCode;
	  private Date timestamp;
	  private String message;
	  private String description;
	  
	  private ErrorMessage(int statusCode, Date timestamp, String message, String description) {
	    this.statusCode = statusCode;
	    this.timestamp = timestamp;
	    this.message = message;
	    this.description = description;
	  }


	public ErrorMessage() {

	}

	public static ErrorMessage create(int statusCode, String message, String description) {

		return new ErrorMessage(statusCode, new Date(), message, description);

	}


	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}


	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	

}
