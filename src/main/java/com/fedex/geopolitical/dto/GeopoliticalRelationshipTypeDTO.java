package com.fedex.geopolitical.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;

@Data
public class GeopoliticalRelationshipTypeDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@NotBlank(message = DTOValidationConstants.BLANK_GEOPL_RLTSP_TYPE_CD,groups = {Update.class, Create.class})
	@Size(max = 20, message = DTOValidationConstants.GEOPL_RLTSP_TYPE_CD_LESS_THAN_20_CHAR, groups = {Update.class, Create.class})
	private String geopoliticalRelationshipTypeCd;
	
	@Size(max = 100, message = DTOValidationConstants.AREA_RLTSP_TYPE_DESC_LESS_THAN_100_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String areaRelationshipTypeDescription;
	
	@JsonIgnore
	private User user;
	
	public interface Create{}
	public interface Update{}

	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
}
