/* (C) 2022 */
package com.jrools.rule.exception;

import static java.util.UUID.randomUUID;
import static org.springframework.util.Assert.notNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable  {
	
	public static Message create(String code, String title) {
		return create(code, title, null);
	}
	
	public static Message create(String code, String title, String details) {
		return create(randomUUID().toString(),code, title, details);
	}
	
	public static Message create(String id, String code, String title, String detils) {
		return new Message(id, code, title, detils);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7751152553415337749L;
	
	@JsonProperty("id")
	private final String id;
	
	@JsonProperty("code")
	private final String code;
	
	@JsonProperty("title")
	private final String title;
	
	@JsonProperty("detils")
	private final String detils;

	@JsonCreator
	private Message(@JsonProperty("id") String id, @JsonProperty("code")String code, @JsonProperty("title")String title, @JsonProperty("detils")String detils) {
		notNull(id, "id can not be null");
		notNull(code, "code can not be null");
		notNull(title, "title can not be null");
		notNull(detils, "detils can not be null");
		this.id = id;
		this.code = code;
		this.title = title;
		this.detils = detils;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the detils
	 */
	public String getDetils() {
		return detils;
	}
	
	
	

}
