package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.dto.CountryDTO.Create;
import com.fedex.geopolitical.dto.CountryDTO.Update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeopoliticalDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private long geopoliticalId;
	
	@Digits(integer=38, fraction = 0)
	@NotNull(message = DTOValidationConstants.GEOPL_TYPE_ID_NOT_NULL, groups = {Create.class, Update.class})
	@NumberFormat(style = Style.NUMBER)
	@Range(min = DTOValidationConstants.MIN_NUMBER_ID, max = DTOValidationConstants.MAX_NUMBER_ID, groups = {Update.class, Create.class})
	private long geopoliticalTypeId;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	private Date effectiveDate;
	
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	@JsonSerialize(using = DateHandlerSerializer.class)
	private Date expirationDate;
	
	@JsonIgnore
	private User user;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}

}
