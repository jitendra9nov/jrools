/* (C) 2022 */
package com.jrools.rule.utils;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.jrools.rule.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class WebClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebClientUtil.class);

	@Autowired
	private WebClient webClient;

	/**
	 * @param <T>
	 * @param url
	 * @param headers
	 * @param resType
	 * @return
	 * @throws ApiException
	 */
	public <T> ResponseEntity<T> callForResponse(final String url,
			final Class <T>resType) {

		ResponseEntity<T> respEntity = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calling API {}", url);
			}

			respEntity = webClient.get()
					.uri(url)
					.header(HttpHeaders.ACCEPT, "application/prs.openbanking.opendata.v2.2+json")
					.retrieve().toEntity(resType).block();
			 

		} 
		catch (WebClientResponseException e) {
			LOGGER.error("Error while calling {}", url, e);

			throw new ServiceException(e.getMessage(),e.getStatusCode());
			
		}
		catch (Exception e) {
			LOGGER.error("Error while calling {}", url, e);

			throw new ServiceException(e.getMessage(),INTERNAL_SERVER_ERROR);
			
		}

		return respEntity;

	}

}
