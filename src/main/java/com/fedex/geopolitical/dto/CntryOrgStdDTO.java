package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;


/**
 * The persistent class for the CNTRY_ORG_STD database table.
 * 
 */
@Data
public class CntryOrgStdDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long geopoliticalId;
	
	@Size(max=10, message=DTOValidationConstants.ORG_STD_CD_NAME_MAX_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.ORG_STD_NOT_NULL, groups = {Update.class, Create.class})
	private String orgStandardCode;
	
	@Size(max=10, message=DTOValidationConstants.CNTRY_CD_MAX_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.CNTRY_CO_NOT_NULL, groups = {Update.class, Create.class})
	private String countryCode;
	
	@Size(max=120, message=DTOValidationConstants.COUNTRY_FULL_NAME_MAX_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String countryFullName;
	
	@Size(max=65, message=DTOValidationConstants.COUNTRY_SHORT_NAME_MAX_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String countryShortName;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date effectiveDate;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date expirationDate;
	

	
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