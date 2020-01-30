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
public class LocaleDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = DTOValidationConstants.BLANK_LANGUAGE_CD, groups = {Update.class, Create.class})
	@Size(max = 3, message = DTOValidationConstants.LANGUAGE_CD_LESS_THAN_2_CHAR, groups = {Update.class, Create.class})
	private String languageCd;
	
	@NotBlank(message = DTOValidationConstants.BLANK_LOCALE_CD, groups = {Update.class, Create.class})
	@Size(max = 18, message = DTOValidationConstants.LOCALE_CD_LESS_THAN_18_CHAR, groups = {Update.class, Create.class})
	private String localeCd;

	@JsonIgnore
	private Long geopoliticalId;
	
	@Temporal(value = TemporalType.DATE)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	private Date cldrVersionDate;

	@Size(max = 18, message = DTOValidationConstants.CLDR_VERSION_NBR_LESS_THAN_18_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String cldrVersionNumber;

	@Size(max = 65, message = DTOValidationConstants.DATE_FULL_FORMAT_DESC_LESS_THAN_65_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String dateFullFormatDescription;

	@Size(max = 65, message = DTOValidationConstants.DATE_LONG_FORMAT_DESC_LESS_THAN_65_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String dateLongFormatDescription;

	@Size(max = 65, message = DTOValidationConstants.DATE_MEDIUM_FORMAT_DESC_LESS_THAN_65_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String dateMediumFormatDescription;

	@Size(max = 65, message = DTOValidationConstants.DATE_SHORT_FORMAT_DESC_LESS_THAN_65_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String dateShortFormatDescription;
	
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
