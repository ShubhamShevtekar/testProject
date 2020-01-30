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
import com.fedex.geopolitical.dto.CountryDTO.Create;
import com.fedex.geopolitical.dto.CountryDTO.Update;

import lombok.Data;

@Data
public class GeopoliticalHolidayDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = DTOValidationConstants.BLANK_HOLIDAY_NAME,groups = {Create.class, Update.class})
	@Size(max = 65, message = DTOValidationConstants.HOLIDAY_NAME_MAX_SIZE, groups = {Create.class, Update.class})
	private String holidayName;
	
	@JsonIgnore
	private Long holidayId;
	
	@JsonDeserialize(using = ExpirationDateDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	@Temporal(value = TemporalType.DATE)
	@NotNull(message = DTOValidationConstants.EFFECTIVE_DATE_CANNOT_BE_NULL, groups = {Update.class})
	private Date effectiveDate;
	
	@JsonIgnore
	private User user;
	
	@JsonIgnore
	private Long geopoliticalId;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}
