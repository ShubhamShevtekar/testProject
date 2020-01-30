package com.fedex.geopolitical.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fedex.geopolitical.constants.DTOValidationConstants;
import com.fedex.geopolitical.custom.annotation.StringDeserializer;

import lombok.Data;

@Data
public class RefUomTypeDTO implements Serializable{
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public interface Create{}
	public interface Update{}
	
	@NotBlank(message = DTOValidationConstants.REF_UOM_TYPE_CD_NOT_NULL, groups = {Update.class, Create.class})
	@Size(max = 10, message = DTOValidationConstants.DATATYPE_SIZE_EXCEEDED_UOM_TYPE_CD, groups = {Update.class, Create.class})
	private String uomTypeCd;
	
	@Size(max=1000,message = DTOValidationConstants.DATATYPE_SIZE_EXCEEDED_UOM_TYPE_DESC, groups = {Update.class, Create.class})
	@JsonDeserialize(using = StringDeserializer.class)
	private String uomTypeDesc;
	
	@Size(max=256,message = DTOValidationConstants.DATATYPE_SIZE_EXCEEDED_UOM_TYPE_NM, groups = {Update.class, Create.class})
	@NotBlank(message = DTOValidationConstants.BLANK_UOM_TYPE_NAME, groups = {Update.class, Create.class})
	private String uomTypeNm;
	
	@JsonIgnore
	private User user;
	
	@Override
	public String toString()
	{
	  return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
	}
	
}
