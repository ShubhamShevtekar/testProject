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

@Data
public class RefScriptDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = DTOValidationConstants.REF_SCRIPT_TYPE_CD_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max=18,message = DTOValidationConstants.SCRPT_CD_NOT_MORE_THAN_EIGHTEEN, groups = {Update.class,Create.class})
	private String scrptCd;
	
	@Size(max=4000,message = DTOValidationConstants.SCRPT_CD_DESC_SIZE, groups = {Update.class,Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String scrptDesc;
	
	@NotBlank(message = DTOValidationConstants.BLANK_SCRIPT_NAME, groups = {Update.class, Create.class})
	@Size(max=256,message = DTOValidationConstants.SCRPT_CD_NAME_SIZE, groups = {Update.class,Create.class})
	private String scrptNm;
	
	@JsonIgnore
	private User user;
	
	public interface Create{}
	public interface Update{}
	
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date effectiveDate;
	
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date expirationDate;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}

}
