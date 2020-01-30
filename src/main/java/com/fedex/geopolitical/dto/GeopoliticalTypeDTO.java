package com.fedex.geopolitical.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fedex.geopolitical.constants.DTOValidationConstants;

import lombok.Data;

@Data
public class GeopoliticalTypeDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long geopoliticalTypeId;
	
	@NotBlank(message = DTOValidationConstants.BLANK_GEOPL_TYPE_NAME, groups = {Update.class, Create.class, com.fedex.geopolitical.dto.CountryDTO.Create.class})
	@Size(max = 50, message = DTOValidationConstants.GEOPL_TYPE_NAME_LESS_THAN_50_CHAR, groups = {Update.class, Create.class, com.fedex.geopolitical.dto.CountryDTO.Create.class})
	private String geopoliticalTypeName;
	
	@JsonIgnore
	private User user;
	
	public interface Update{}
	public interface Create{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}

}
