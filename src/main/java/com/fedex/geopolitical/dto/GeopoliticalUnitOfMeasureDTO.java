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
public class GeopoliticalUnitOfMeasureDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = DTOValidationConstants.REF_UOM_TYPE_CD_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max = 10, message = DTOValidationConstants.DATATYPE_SIZE_EXCEEDED_UOM_TYPE_CD, groups = {Update.class, Create.class})
	private String uomTypeCd;
	
	@JsonIgnore
	private Long geopoliticalId;
	
	@JsonIgnore
	private String uomTypeName;
	
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
