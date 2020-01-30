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
public class GeopoliticalAffiliationDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String affilTypeName;
	
	@JsonIgnore
	private Long affilTypeId; 
	
	@JsonIgnore
	private Long geopoliticalId;
	
	@NotBlank(message = DTOValidationConstants.GEOPL_AFFIL_CODE_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max=10,message = DTOValidationConstants.GEOPL_AFFILIATION_CODE_SIZE, groups = {Create.class, Update.class})
	private String affilTypeCd;
	
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
