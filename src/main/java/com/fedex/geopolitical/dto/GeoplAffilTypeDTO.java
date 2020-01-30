package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;


/**
 * The persistent class for the HOLIDAY database table.
 * 
 */

@Data
public class GeoplAffilTypeDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long affilTypeId;
	
	@NotBlank(message = DTOValidationConstants.GEOPL_AFFIL_TYPE_CODE_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max=10,message = DTOValidationConstants.GEOPL_AFFIL_CODE_SIZE, groups = {Create.class, Update.class})
	private String affilTypeCode;
	
	@Size(max=65,message = DTOValidationConstants.GEOPL_AFFIL_NAME_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String affilTypeName;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date effectiveDate;
	
	@JsonIgnore
	private User user;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date expirationDate;

	public interface Create{}
	public interface Update{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	

}