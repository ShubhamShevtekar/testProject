package com.fedex.geopolitical.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.DateHandlerDeserializer;
import com.fedex.geopolitical.custom.annotation.DateHandlerSerializer;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;

@Data
public class StProvStdDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long geopoliticalId;
	
	@NotBlank(message = DTOValidationConstants.ST_PROV_CD_NOT_BLANK, groups = {Update.class, Create.class})
	@Size(max=10,message = DTOValidationConstants.ST_PROV_CD_SIZE, groups = {Update.class, Create.class})
	private String stProvCd;
	
	@Size(max=120,message = DTOValidationConstants.ST_PROV_NM_SIZE, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String stProvNm;
	
	public interface Create{}
	public interface Update{}
	
	@Size(max=10,message = DTOValidationConstants.ORG_STD_CD_SIZE, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.ORG_STD_CD_NOT_BLANK, groups = {Update.class, Create.class})
	private String orgStdCd;
	
	@JsonIgnore
	private User user;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date effectiveDate;
	
	@JsonSerialize(using = DateHandlerSerializer.class)
	@JsonDeserialize(using = DateHandlerDeserializer.class)
	private Date expirationDate;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
}
