/* (C) 2022 */
package com.jrools.rule.exception;

import static java.util.Collections.unmodifiableList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

public class RuleException extends Exception{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 23024777909280717L;


	public static RuleException create(List<Message> errors) {
		return new RuleException(errors);
	}
	
	public static RuleException create(Message error) {
		return new RuleException(error);
	}
	
	public static RuleException create(String message, Throwable cause, List<Message> errors) {
		return new RuleException(message, cause, errors);
	}
	
	public static RuleException create(String message, Throwable cause, Message error) {
		return new RuleException(message, cause, error);
	}
	
	private final List<Message> errors;
	
	
	private RuleException(List<Message> errors) {
		this.errors=isEmpty(errors)?unmodifiableList(errors):null;
	}

	private RuleException(Message error) {
		this.errors=new ArrayList<>(5);
		this.errors.add(error);
		unmodifiableList(this.errors);
	}

	private RuleException(String message, Throwable cause, List<Message> errors) {
		super(message,cause);
		this.errors=isEmpty(errors)?unmodifiableList(errors):null;
	}

	private RuleException(String message, Throwable cause, Message error) {
		super(message,cause);
		this.errors=new ArrayList<>(5);
		this.errors.add(error);
		unmodifiableList(this.errors);
	}

	/**
	 * @return the errors
	 */
	public List<Message> getErrors() {
		return errors;
	}
	
	

}
