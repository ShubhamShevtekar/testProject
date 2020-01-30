package com.fedex.geopolitical.dtoresponse;

import java.io.Serializable;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("data")
	@Valid
	private Object data;

}
