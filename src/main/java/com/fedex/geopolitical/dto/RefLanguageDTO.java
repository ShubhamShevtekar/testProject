package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
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
public class RefLanguageDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotBlank(message = DTOValidationConstants.REF_LANGUAGE_CD_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max=3,message = DTOValidationConstants.CANNOT_EXCEED_TWO_CHARS, groups = {Update.class,Create.class})
	private String langCd;
	
	@NotBlank(message = DTOValidationConstants.LANG_NAME_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max=256,message = DTOValidationConstants.REF_LANGUAGE_NAME_SIZE, groups = {Update.class,Create.class})
	private String englLanguageNm;
	
	@Size(max=256,message = DTOValidationConstants.REF_LANGUAGE_NATIVE_SCRPT_SIZE, groups = {Update.class,Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String nativeScriptLanguageNm;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private List<TrnslDowDTO> translatedDOWs;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private List<TrnslMthOfYrDTO> translatedMOYs;
	
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
	
	public interface Update{}
	public interface Create{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
}
