package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

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

import lombok.Data;


/**
 * The persistent class for the GEOPL_RLTSP database table.
 * 
 */
@Data
public class GeopoliticalRelationshipDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Digits(integer=50, fraction = 0, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	@NotNull(message = DTOValidationConstants.FROM_NOT_NULL, groups = {Update.class, Create.class})
	private BigInteger fromGeopoliticalId;
	
	@Digits(integer=50, fraction = 0, groups = {Update.class, Create.class})
	@NumberFormat(style = Style.NUMBER)
	@NotNull(message = DTOValidationConstants.TO_NOT_NULL, groups = {Update.class, Create.class})
	private BigInteger toGeopoliticalId;
	
	@Size(max=20, message=DTOValidationConstants.GEOPOL_RELATIONSHIP_MAX_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.RELATIONSHIP_CODE_NOT_NULL, groups = {Update.class, Create.class})
	private String relationshipTypeCode;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date effectiveDate;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date expirationDate;
	
	@JsonIgnore
	private User user;
	
	public interface Update{}
	public interface Create{}
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}