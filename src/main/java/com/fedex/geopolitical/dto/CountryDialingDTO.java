package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.ExpirationDateDeserializer;
import com.fedex.geopolitical.dto.CountryDTO.Create;
import com.fedex.geopolitical.dto.CountryDTO.Update;

import lombok.Data;

@Data
public class CountryDialingDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = DTOValidationConstants.BLANK_INITIAL_DIALING_PREFIX_CD, groups = {Create.class, Update.class})
	@Size(max = 7, message = DTOValidationConstants.INITIAL_DIALING_PREFIX_CD_LESS_THAN_7_CHAR, groups = {Create.class, Update.class})
	private String intialDialingPrefixCd;
	
	@JsonIgnore
	private Long geopoliticalId;
	
	@NotBlank(message = DTOValidationConstants.BLANK_INITIAL_DIALING_CD, groups = {Create.class, Update.class})
	@Size(max = 3, message = DTOValidationConstants.INITIAL_DIALING_CD_LESS_THAN_3_CHAR, groups = {Create.class, Update.class})
	private String intialDialingCd;

	@Digits(integer=38, fraction = 0, groups = {Create.class, Update.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger landPhMaxLthNbr;

	@Digits(integer=38, fraction = 0, groups = {Create.class, Update.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger landPhMinLthNbr;

	@Digits(integer=38, fraction = 0, groups = {Create.class, Update.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger moblPhMaxLthNbr;

	@Digits(integer=38, fraction = 0, groups = {Create.class, Update.class})
	@NumberFormat(style = Style.NUMBER)
	private BigInteger moblPhMinLthNbr;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	@NotNull(message = DTOValidationConstants.EFFECTIVE_DATE_CANNOT_BE_NULL, groups = {Update.class})
	private Date effectiveDate;
	
	@JsonDeserialize(using = ExpirationDateDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate;
	
	@JsonIgnore
	private User user;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}
