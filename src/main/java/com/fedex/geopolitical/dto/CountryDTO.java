package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.ExpirationDateDeserializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;

@Data
public class CountryDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long geopoliticalId;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NotNull(message = DTOValidationConstants.COUNTRY_NUMBER_CD_NOT_NULL, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger countryNumberCd;
	
	@NotBlank(message = DTOValidationConstants.BLANK_COUNTRY_CD,groups = {Update.class, Create.class})
	@Size(max = 2, message = DTOValidationConstants.COUNTRY_CD_LESS_THAN_2_CHAR, groups = {Update.class, Create.class})
	private String countryCd;
	
	@NotBlank(message = DTOValidationConstants.BLANK_THREE_CHAR_COUNTRY_CD,groups = {Update.class, Create.class})
	@Size(max = 3, message = DTOValidationConstants.THREE_CHAR_COUNTRY_CD_LESS_THAN_3_CHAR, groups = {Update.class,Create.class})
	private String threeCharCountryCd;
	
	@Size(max = 1, message = DTOValidationConstants.INDEPENDENT_FLAG_LESS_THAN_1_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String independentFlag;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	private Long dependentRelationshipId;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger dependentCountryCd;
	
	@Size(max = 25, message = DTOValidationConstants.POSTAL_FORMAT_DESC_LESS_THAN_25_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String postalFormatDescription;
	
	@Size(max = 1, message = DTOValidationConstants.POSTAL_FLAG_LESS_THAN_1_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String postalFlag;
	
	@Digits(integer=38, fraction = 0, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger postalLengthNumber;
	
	@Size(max = 25, message = DTOValidationConstants.FIRST_WORK_WEEK_DAY_NAME_LESS_THAN_25_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String firstWorkWeekDayName;
	
	@Size(max = 25, message = DTOValidationConstants.LAST_WORK_WEEK_DAY_NAME_LESS_THAN_25_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String lastWorkWeekDayName;
	
	@Size(max = 25, message = DTOValidationConstants.WEEKEND_FIRST_DAY_NAME_LESS_THAN_25_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String weekendFirstDayName;
	
	@Size(max = 5, message = DTOValidationConstants.INTERNET_DOMAIN_NAME_LESS_THAN_5_CHAR, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String internetDomainName;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	@NotNull(message = DTOValidationConstants.GEOPOLITICAL_TYPE_NOT_NULL, groups = {Create.class})
	private GeopoliticalTypeDTO geopoliticalType;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<GeopoliticalUnitOfMeasureDTO> geopoliticalUnitOfMeasures;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<GeopoliticalHolidayDTO> geopoliticalHolidays;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<GeopoliticalAffiliationDTO> geopoliticalAffiliations;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<TranslationGeopoliticalDTO> translationGeopoliticals;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<LocaleDTO> locales;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<CountryDialingDTO> countryDialings;
	
	@Valid
	@JsonInclude(Include.NON_NULL)
	private Set<CurrencyDTO> currencies;
	
	@JsonIgnore
	private User user;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@Temporal(value = TemporalType.DATE)
	@NotNull(message = DTOValidationConstants.EFFECTIVE_DATE_CANNOT_BE_NULL, groups = {Update.class})
	private Date effectiveDate;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = ExpirationDateDeserializer.class)
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate; 

	public interface Create{}
	public interface Update{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}
