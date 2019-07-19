package wsMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import utils.ExcelUtil;

public class PostMethod{
	
	//GeoType
	public static String geoTypePostRequest(String userId, String geoTypeName)
	{
		String payload = "{ \"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalType\":{\"geopoliticalTypeName\":\""+geoTypeName+"\"}}";
		return payload;
	}
	
	public static String geoTypePostRequestWithoutMeta(String geoTypeName)
	{
		String payload ="{\"geopoliticalType\": {\"geopoliticalTypeName\": \""+geoTypeName+"\"}}";
		return payload;
	}
	
	public static String geoTypePostRequestWithoutGeopoliticalTypeName(String userId)
	{
		String payload ="{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalType\": {}}";
		return payload;
	}
	
	public static String geoTypePostRequestWithoutUserName(String userId, String geoTypeName)
	{
		String payload = "{ \"meta\":{\"userName\": "+userId+"},\"geopoliticalType\":{\"geopoliticalTypeName\":\""+geoTypeName+"\"}}";
		return payload;
	}
	
	//GeoRsType
	public static String geoRsTypePostRequest(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCd\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	
	public static String geoRsTypePostRequestWithoutgeopoliticalRelationshipTypeCd(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationshipType\":{"
				+ "\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	
	public static String geoRsTypePostRequestWithoutareaRelationshipTypeDescription(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		//String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationshipType\":{"
			//	+ "\"geopoliticalRelationshipTypeCd\":\""+geoRsTypeCode+"\"}}";
	
		
		String payload ="{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationshipType\": {  \"geopoliticalRelationshipTypeCd\":\""+geoRsTypeCode+"\"}}";
		return payload;

	}
	
	public static String geoRsTypePostRequestWithoutMeta(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCd\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	
	public static String geoRsTypePostRequestWithNullUser(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":"+userId+"},\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCd\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	
	
	public static String geoOrgStdPostRequest(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"orgStdCd\":\""+geoOrgStdCode+"\",\"orgStdNm\":\""+geoOrgStdName+"\"}}";
		return payload;
	}
	
	public static String geoOrgStdPostRequestWithoutMeta(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"geoplOrgStd\":{"
				+ "\"orgStdCd\":\""+geoOrgStdCode+"\",\"orgStdNm\":\""+geoOrgStdName+"\"}}";
		return payload;
	}
	
	public static String geoOrgStdPostRequestWithoutOrgStdCd(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"orgStdNm\":\""+geoOrgStdName+"\"}}";
		return payload;
	}
	
	
	public static String geoOrgStdPostRequestWithoutOrgStdNm(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"orgStdCd\":\""+geoOrgStdCode+"\"}}";
		return payload;
	}
	
	public static String geoOrgStdPostRequestNullUser(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":"+userId+"},\"geoplOrgStd\":{"
				+ "\"orgStdCd\":\""+geoOrgStdCode+"\",\"orgStdNm\":\""+geoOrgStdName+"\"}}";
		return payload;
	}
	
	
	//DepnCtryRltsp	
	public static String depnCntryRltspPostRequest(String userId, String depnCntryRltsp )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dependentCountryRelationship\":{"
				+ "\"dependentRelationshipDescription\":\""+depnCntryRltsp+"\"}}";
		return payload;
	}
	
	public static String depnCntryRltspPostRequestWithoutMeta(String depnCntryRltsp)
	{
		
		String payload = "{\"dependentCountryRelationship\":{\"dependentRelationshipDescription\":\""+depnCntryRltsp+"\"}}";
		return payload;
	}
	
	public static String depnCntryRltspPostRequestWithoutDependentRelationshipDescription(String userId)
	{
		
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dependentCountryRelationship\":{}}";
		return payload;
	}
	
		
	public static String holidaPostRequest(String userId, String holidayName, String holidayParamText )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"holiday\":{"
				+ "\"holidayName\":\""+holidayName+"\",\"holidayDateParamText\":\""+holidayParamText+"\"}}";
		return payload;
	}

	public static String holidaPostRequestWithoutMeta(String holidayName, String holidayParamText )
	{
		String payload = "{\"holiday\":{\"holidayName\":\""+holidayName+"\",\"holidayDateParamText\":\""+holidayParamText+"\"}}";
		return payload;
	}
	
	public static String holidaPostRequestWithoutHolidayName(String userId, String holidayParamText )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"holiday\":{\"holidayDateParamText\":\""+holidayParamText+"\"}}";
		return payload;
	}
	
	public static String holidaPostRequestWithoutHolidayParamText(String userId, String holidayName )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"holiday\":{\"holidayName\":\""+holidayName+"\"}}";
		return payload;
	}
	
	//UOM
	public static String uomTypePostRequest(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
				+ "\"uomTypeCd\":\""+uomTypeCode+"\",\"uomTypeNm\":\""+uomTypeName+"\",\"uomTypeDesc\":\""+uomTypeDesc+"\"}}";
		return payload;
	}
	
	public static String uomTypePostRequestWithoutMeta(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
	{
		String payload = "{\"uomType\":{"
				+ "\"uomTypeCd\":\""+uomTypeCode+"\",\"uomTypeNm\":\""+uomTypeName+"\",\"uomTypeDesc\":\""+uomTypeDesc+"\"}}";
		return payload;
	}
	
	public static String uomTypePostRequestWithoutUomTypeCd(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
				+ "\"uomTypeNm\":\""+uomTypeName+"\",\"uomTypeDesc\":\""+uomTypeDesc+"\"}}";
		return payload;
	}
	
	public static String uomTypePostRequestWithoutUomTypeNm(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
				+ "\"uomTypeCd\":\""+uomTypeCode+"\",\"uomTypeDesc\":\""+uomTypeDesc+"\"}}";
		return payload;
	}
	
	public static String uomTypePostRequestWithoutUomTypeDesc(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
				+ "\"uomTypeCd\":\""+uomTypeCode+"\",\"uomTypeNm\":\""+uomTypeName+"\"}}";
		return payload;
	}
	
	
	//Script
	public static String scriptPostRequest(String userId, String scriptCode, String scriptName, String scriptDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
				+ "\"scrptCd\":\""+scriptCode+"\",\"scrptNm\":\""+scriptName+"\",\"scrptDesc\":\""+scriptDesc+"\"}}";
		return payload;
	}
	
	public static String scriptPostRequestWithoutMeta(String userId, String scriptCode, String scriptName, String scriptDesc)
	{
		String payload = "{\"refScript\":{"
				+ "\"scrptCd\":\""+scriptCode+"\",\"scrptNm\":\""+scriptName+"\",\"scrptDesc\":\""+scriptDesc+"\"}}";
		return payload;
	}
	
	public static String scriptPostRequestWithoutSciptCd(String userId, String scriptCode, String scriptName, String scriptDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
				+ "\"scrptNm\":\""+scriptName+"\",\"scrptDesc\":\""+scriptDesc+"\"}}";
		return payload;
	}
	
	public static String scriptPostRequestWithoutScriptNm(String userId, String scriptCode, String scriptName, String scriptDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
				+ "\"scrptCd\":\""+scriptCode+"\",\"scrptDesc\":\""+scriptDesc+"\"}}";
		return payload;
	}
	
	public static String scriptPostRequestWithoutScriptDesc(String userId, String scriptCode, String scriptName, String scriptDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
				+ "\"scrptCd\":\""+scriptCode+"\",\"scrptNm\":\""+scriptName+"\"}}";
		return payload;
	}
	
	
	
	
	//dayOfWeek
	public static String dayOfWeekPostRequest(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
				+ "\"dayOfweekNumber\":\""+dayOfWeekNbr+"\",\"dayOfweekFullName\":\""+dayOfWeekFullName+"\",\"dayOfweekShortName\":\""+dayOfWeekShortName+"\"}}";
		return payload;
	}
	
	public static String dayOfWeekPostRequestWithoutMeta(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"dayOfWeek\":{"
				+ "\"dayOfweekNumber\":\""+dayOfWeekNbr+"\",\"dayOfweekFullName\":\""+dayOfWeekFullName+"\",\"dayOfweekShortName\":\""+dayOfWeekShortName+"\"}}";
		return payload;
	}
	
	public static String dayOfWeekPostRequestWithoutDOWNo(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
				+ "\"dayOfweekFullName\":\""+dayOfWeekFullName+"\",\"dayOfweekShortName\":\""+dayOfWeekShortName+"\"}}";
		return payload;
	}
	
	public static String dayOfWeekPostRequestWithoutDOWShNm(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
				+ "\"dayOfweekNumber\":\""+dayOfWeekNbr+"\",\"dayOfweekFullName\":\""+dayOfWeekFullName+"\"}}";
		return payload;
	}
	
	public static String dayOfWeekPostRequestWithoutDOWFNm(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
				+ "\"dayOfweekNumber\":\""+dayOfWeekNbr+"\",\"dayOfweekShortName\":\""+dayOfWeekShortName+"\"}}";
		return payload;
	}
	
	
	//MonthOfYear
	public static String monthOfYearPostRequest(String userId, String monthOfYearNumber, String monthOfYearShortName )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{"
				+ "\"monthOfYearNumber\":\""+monthOfYearNumber+"\",\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
		return payload;
	}
	
	public static String monthOfYearPostRequesWithoutmonthOfYearShortName(String userId, String monthOfYearNumber)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{\"monthOfYearNumber\":"+monthOfYearNumber+"" +
				"}}";
		return payload;
	}
	
	public static String monthOfYearPostRequestWithoutMeta(String monthOfYearNumber, String monthOfYearShortName )
	{
		String payload = "{\"monthOfYear\":{\"monthOfYearNumber\":\""+monthOfYearNumber+"\",\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
		return payload;
	}
	
	public static String monthOfYearPostRequestWithoutMonthOfYearNumber(String userId, String monthOfYearShortName )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
		return payload;
	}

	//CntryOrgStd
	public static String cntryOrgStdPostRequest(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutMeta(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutCntryCd(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutCntryShNm(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutCntryFNm(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutOrgStdCd(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutEffectiveDate(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String cntryOrgStdPostRequestWithoutExpirationDate(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
				+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
						+ ",\"orgStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\"}}";
		return payload;
	}
	
	
	//GEOPLRLTSP
		public static String geoRltspPostRequest(String userId, String fromGeopoliticalId, String toGeopoliticalId, String relationshipTypeCode,
				 String effectiveDate, String expirationDate)
		{
			String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationship\":{"
					+ "\"fromGeopoliticalId\":\""+fromGeopoliticalId+"\",\"toGeopoliticalId\":\""+toGeopoliticalId+"\",\"relationshipTypeCode\":\""+relationshipTypeCode+"\""
							+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
			return payload;
		}
		
		public static String geoRltspPostRequestWithoutMeta(String fromGeopoliticalId, String toGeopoliticalId, String relationshipTypeCode,String effectiveDate, String expirationDate)
		{
			String payload = "{\"geopoliticalRelationship\": {\"fromGeopoliticalId\":\""+fromGeopoliticalId+"\",\"toGeopoliticalId\":\""+toGeopoliticalId+"\",\"relationshipTypeCode\": \""+relationshipTypeCode+"\",\"effectiveDate\": \""+effectiveDate+"\",   \"expirationDate\":  \""+expirationDate+"\"}}";
			return payload;
		}
		
		public static String geoRltspPostRequestWithoutFromGeopoliticalId(String userId, String toGeopoliticalId, String relationshipTypeCode,String effectiveDate, String expirationDate)
		{
			String payload = "{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationship\":{    \"toGeopoliticalId\": \""+toGeopoliticalId+"\",\"relationshipTypeCode\": \""+relationshipTypeCode+"\", \"effectiveDate\": \""+effectiveDate+"\",\"expirationDate\": \""+expirationDate+"\"}}";
			return payload;
		}

		public static String geoRltspPostRequestWithoutToGeopoliticalId(String userId, String fromGeopoliticalId, String relationshipTypeCode,String effectiveDate, String expirationDate)
		{
			String payload = "{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationship\":{    \"fromGeopoliticalId\": \""+fromGeopoliticalId+"\",\"relationshipTypeCode\": \""+relationshipTypeCode+"\", \"effectiveDate\": \""+effectiveDate+"\",\"expirationDate\": \""+expirationDate+"\"}}";
			return payload;
		}
		
		public static String geoRltspPostRequestWithoutRelationshipTypeCode(String userId, String fromGeopoliticalId, String toGeopoliticalId,String effectiveDate, String expirationDate)
		{
			String payload = "{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationship\":{    \"fromGeopoliticalId\": \""+fromGeopoliticalId+"\",\"toGeopoliticalId\": \""+toGeopoliticalId+"\", \"effectiveDate\": \""+effectiveDate+"\",\"expirationDate\": \""+expirationDate+"\"}}";
			return payload;
		}
		
		public static String geoRltspPostRequestWithoutEffectiveDate(String userId, String fromGeopoliticalId, String toGeopoliticalId,String relationshipTypeCode,String expirationDate)
		{
			String payload = "{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationship\":{    \"fromGeopoliticalId\": \""+fromGeopoliticalId+"\",\"toGeopoliticalId\": \""+toGeopoliticalId+"\", \"relationshipTypeCode\": \""+relationshipTypeCode+"\", \"expirationDate\": \""+expirationDate+"\"}}";
			return payload;
		}
		
		public static String geoRltspPostRequestWithoutExpirationDate(String userId, String fromGeopoliticalId, String toGeopoliticalId,String relationshipTypeCode,String effectiveDate)
		{
			String payload = "{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationship\":{    \"fromGeopoliticalId\": \""+fromGeopoliticalId+"\",\"toGeopoliticalId\": \""+toGeopoliticalId+"\",  \"relationshipTypeCode\": \""+relationshipTypeCode+"\", \"effectiveDate\": \""+effectiveDate+"\"}}";
			return payload;
		}
	
	
	
	
	//StProvStd
	public static String stProvStdPostRequest(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvCd\":\""+stProvCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutMeta(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvCd\":\""+stProvCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutOrgStdCd(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"stProvCd\":\""+stProvCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutstProvCd(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutStProvNm(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvCd\":\""+stProvCd+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutEffectiveDt(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvCd\":\""+stProvCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}
	
	public static String stProvStdPostRequestWithoutExpirationDt(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
				+ "\"orgStdCd\":\""+orgStdCd+"\",\"stProvCd\":\""+stProvCd+"\",\"stProvNm\":\""+stProvNm+"\""
						+ ",\"effectiveDate\":\""+effectiveDate+"\"}}";
		return payload;
	}
	
	//***AffilType
	public static String affilTypePostRequest(String userId, String affilTypeCode, String affilTypeName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
				+ "\"affilTypeCode\":\""+affilTypeCode+"\",\"affilTypeName\":\""+affilTypeName+"\"}}";
		return payload;
	}
	
	//*** Language
	public static String langPostRequest(String userId, String langCd, String languageNm, String languageDesc, String nativeScriptLanguageNm, 
			String threeCharLangCd, String scrptCd, String dowNbr1, String transDowName1, String dowNbr2, String transDowName2, String dowNbr3, String transDowName3, 
			String dowNbr4, String transDowName4, String dowNbr5, String transDowName5, String dowNbr6, String transDowName6, String dowNbr7, String transDowName7,
			String mthOfYrNbr1, String transMoyName1, String mthOfYrNbr2, String transMoyName2, String mthOfYrNbr3, String transMoyName3, String mthOfYrNbr4, String transMoyName4, 
			String mthOfYrNbr5, String transMoyName5, String mthOfYrNbr6, String transMoyName6, String mthOfYrNbr7, String transMoyName7, String mthOfYrNbr8, String transMoyName8, 
			String mthOfYrNbr9, String transMoyName9, String mthOfYrNbr10, String transMoyName10, String mthOfYrNbr11, String transMoyName11, String mthOfYrNbr12, String transMoyName12)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"language\":{"
				+ "\"langCd\":\""+langCd+"\",\"languageNm\":\""+languageNm+"\",\"languageDesc\":\""+languageDesc+"\","
				+ "\"nativeScriptLanguageNm\":\""+nativeScriptLanguageNm+"\",\"threeCharLangCd\":\""+threeCharLangCd+"\",\"scrptCd\":\""+scrptCd+"\","
				+ "\"translatedDOWs\":["
						+ "{\"dowNbr\":\""+dowNbr1+"\",\"transDowName\":\""+transDowName1+"\"},"
						+ "{\"dowNbr\":\""+dowNbr2+"\",\"transDowName\":\""+transDowName2+"\"},"
						+ "{\"dowNbr\":\""+dowNbr3+"\",\"transDowName\":\""+transDowName3+"\"},"
						+ "{\"dowNbr\":\""+dowNbr4+"\",\"transDowName\":\""+transDowName4+"\"},"
						+ "{\"dowNbr\":\""+dowNbr5+"\",\"transDowName\":\""+transDowName5+"\"},"
						+ "{\"dowNbr\":\""+dowNbr6+"\",\"transDowName\":\""+transDowName6+"\"},"
						+ "{\"dowNbr\":\""+dowNbr7+"\",\"transDowName\":\""+transDowName7+"\"}],"
				+ "\"translatedMOYs\":["
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr1+"\",\"transMoyName\":\""+transMoyName1+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr2+"\",\"transMoyName\":\""+transMoyName2+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr3+"\",\"transMoyName\":\""+transMoyName3+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr4+"\",\"transMoyName\":\""+transMoyName4+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr5+"\",\"transMoyName\":\""+transMoyName5+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr6+"\",\"transMoyName\":\""+transMoyName6+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr7+"\",\"transMoyName\":\""+transMoyName7+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr8+"\",\"transMoyName\":\""+transMoyName8+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr9+"\",\"transMoyName\":\""+transMoyName9+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr10+"\",\"transMoyName\":\""+transMoyName10+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr11+"\",\"transMoyName\":\""+transMoyName11+"\"},"
						+ "{\"mthOfYrNbr\":\""+mthOfYrNbr12+"\",\"transMoyName\":\""+transMoyName12+"\"}]"
				+ "}}";
		return payload;
	}

}
