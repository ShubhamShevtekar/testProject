package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.ExpirationDateDeserializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;
import com.fedex.geopolitical.dto.CountryDTO.Create;
import com.fedex.geopolitical.dto.CountryDTO.Update;

import lombok.Data;

@Data
public class TranslationGeopoliticalDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = DTOValidationConstants.BLANK_LANGUAGE_CD, groups = {Update.class, Create.class})
	@Size(max = 3, message = DTOValidationConstants.LANGUAGE_CD_LESS_THAN_2_CHAR, groups = {Update.class, Create.class})
	private String languageCd;
	
	@JsonIgnore
	private Long geopoliticalId;

	@Size(max = 120, message = DTOValidationConstants.TRANSLATION_NAME_LESS_THAN_120_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String translationName;

	@Temporal(value = TemporalType.DATE)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	private Date versionDate;

	@Size(max = 18, message = DTOValidationConstants.VERSION_NBR_LESS_THAN_18_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String versionNumber;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	@NotNull(message = DTOValidationConstants.EFFECTIVE_DATE_CANNOT_BE_NULL, groups = {Update.class})
	private Date effectiveDate;
	
	@JsonDeserialize(using = ExpirationDateDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate;
	
	@Size(max=18,message=DTOValidationConstants.SCRIPT_CODE_SIZE,groups = {Update.class,Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String scrptCd;
	
	private String englLanguageNm;
	
	@JsonIgnore
	private User user;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
}
