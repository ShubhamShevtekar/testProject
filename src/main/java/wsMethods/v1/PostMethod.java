package wsMethods.v1;

import java.util.HashMap;

public class PostMethod{

	//GeoType
public static String geoTypePostRequest(String userId, String geoTypeName)
{
	String payload = "{ \"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalType\":{\"geopoliticalTypeName\":\""+geoTypeName+"\"}}";
	return payload;
}

public static String geoTypePostRequestMissingComma(String userId, String geoTypeName)
{
	String payload = "{ \"meta\":{\"userName\":\""+userId+"\"}\"geopoliticalType\":{\"geopoliticalTypeName\":\""+geoTypeName+"\"}}";
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

        //geoType Graph QL
	public static String geoTypeGraphQLRequestWithoutParameter()
	{
		String payload = "{\"query\":\"{  geopoliticalTypes {    geopoliticalTypeId    geopoliticalTypeName  }}\"}";
		return payload;
	}

	public static String geoTypeWithParameterGraphQL(String geoTypeName)
	{
		String payload =  "{\"query\":\"{  geopoliticalTypes (geopoliticalTypeName : \\\""+geoTypeName+"\\\") {    geopoliticalTypeId    geopoliticalTypeName    }}\"}";
		return payload;
	}

	public static String geoTypeWithParameterWith1AttributeGraphQL(String geoTypeName)
	{
		String payload =  "{\"query\":\"{  geopoliticalTypes (geopoliticalTypeName : \\\""+geoTypeName+"\\\") {    geopoliticalTypeId    }}\"}";
		return payload;
	}

	public static String geoTypeInvalidAttribute1GraphQL(String geoTypeName)
	{
		String payload =  "{\"query\":\"{  geopoliticalTypes (geopoliticalTypeName : \\\""+geoTypeName+"\\\") {    geopoliticalTypeId1    geopoliticalTypeName    }}\"}";
		return payload;
	}

	public static String geoTypeInvalid2AttributesGraphQL(String geoTypeName)
	{
		String payload =  "{\"query\":\"{  geopoliticalTypes (geopoliticalTypeName : \\\""+geoTypeName+"\\\") {    geopoliticalTypeId1    geopoliticalTypeName2    }}\"}";
		return payload;
	}


	//GeoRsType
	public static String geoRsTypePostRequest(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	public static String geoRsTypePostCommaMissingRequest(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\"\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}
	//GRAPHQL
	public static String geoRsTypeGraphQlRequest(String geoRsTypeCode){
		String payload = "{\"query\":\"{  relationshipTypes (relationshipTypeCode :\\\""+geoRsTypeCode+"\\\") {    geopoliticalRelationshipTypeCode    areaRelationshipTypeDescription  }}\"}";

		return payload;
	}

	public static String geoRsTypeGraphQlRequestWithTwoParameters(String geoRsTypeCode, String geoRsTypeDesc){
		String payload = "{\"query\":\"{  relationshipTypes (relationshipTypeCode :\\\""+geoRsTypeCode+"\\\", relationshipTypeDescription :\\\""+geoRsTypeDesc+"\\\") {    geopoliticalRelationshipTypeCode    areaRelationshipTypeDescription  }}\"}";

		return payload;
	}

	public static String geoRsTypeGraphQlRequestWithInvalidAttributeName(String geoRsTypeCode){
		String payload = "{\"query\":\"{  relationshipTypes (relationshipTypeCode :\\\""+geoRsTypeCode+"\\\") {    geopoliticalRelationshipTypeCode1    areaRelationshipTypeDescription1  }}\"}";

		return payload;
	}

	public static String geoRsTypeGraphQlRequestWithoutParameters(){
		String payload = "{\"query\":\"{  relationshipTypes  {    geopoliticalRelationshipTypeCode    areaRelationshipTypeDescription  }}\"}";

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


		String payload ="{\"meta\": {\"userName\": \""+userId+"\"},\"geopoliticalRelationshipType\": {  \"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\"}}";
		return payload;

	}

	public static String geoRsTypePostRequestWithoutMeta(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}

	public static String geoRsTypePostRequestWithNullUser(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\":{\"userName\":"+userId+"},\"geopoliticalRelationshipType\":{"
				+ "\"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\",\"areaRelationshipTypeDescription\":\""+geoRsTypeDesc+"\"}}";
		return payload;
	}

	public static String geoRsTypePostRequestWithNullAreaDesc(String userId, String geoRsTypeCode, String geoRsTypeDesc)
	{
		String payload = "{\"meta\": {\"userName\": \""+userId+"\"}," +
				"\"geopoliticalRelationshipType\": {" +
				"\"geopoliticalRelationshipTypeCode\":\""+geoRsTypeCode+"\"," +
				"\"areaRelationshipTypeDescription\":"+geoRsTypeDesc+"" +
				"  }" +
				"}";
			return payload;
		}
public static String depnCntryRltspGraphQLWithTwoWrongAttribute(String dependentCountryCd )
{

	String payload ="{\"query\": \"{    countries (dependentCountryCd : \\\""+dependentCountryCd+"\\\") {      geopoliticalIds      dependentCountryRelationships {        " +
"dependentRelationshipDescription      }      }  }  \"}";
return payload;
}

public static String depnCntryRltspTypeGraphQLWithWrongAttributes()
{

	String payload = "{\"query\":\"{  dependentCountryRelationshipTypes {    dependentRelationshipIds    dependentRelationshipDescriptions  }}\"}";
	return payload;
}



public static String depnCntryRltspTypeGraphQL()
{

	String payload = "{\"query\":\"{  dependentCountryRelationshipTypes {    dependentRelationshipId    dependentRelationshipDescription  }}\"}";
	return payload;
}
public static String depnCntryRltspGraphQL()
{

	String payload = "{\"query\": \"{    countries {      geopoliticalId      dependentCountryRelationship {        dependentRelationshipDescription      }      }  }  \"}";
	return payload;
}



public static String depnCntryRltspGraphQLWithParam(String dependentCountryCd )
{

	String payload ="{\"query\": \"{    countries (dependentCountryCd : \\\""+dependentCountryCd+"\\\") {      geopoliticalId      dependentCountryRelationship {        " +
"dependentRelationshipDescription      }      }  }  \"}";
return payload;
}




//OrgStd
//OrgStd
	public static String geoOrgStdPostRequest(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
		return payload;
	}

	public static String geoOrgStdPostRequestMissingComma(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\"\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
		return payload;
	}

	public static String geoOrgStdPostRequestWithoutMeta(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"geoplOrgStd\":{"
				+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
		return payload;
	}

	public static String geoOrgStdPostRequestWithoutOrgStdCd(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
		return payload;
	}


	public static String geoOrgStdPostRequestWithoutOrgStdNm(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
				+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\"}}";
		return payload;
	}

	public static String geoOrgStdPostRequestNullUser(String userId, String geoOrgStdCode, String geoOrgStdName)
	{
		String payload = "{\"meta\":{\"userName\":"+userId+"},\"geoplOrgStd\":{"
				+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
		return payload;
	}

/*public static String geoOrgStdPostRequest(String userId, String geoOrgStdCode, String geoOrgStdName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
		+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
	return payload;
}

public static String geoOrgStdPostRequestWithoutMeta(String userId, String geoOrgStdCode, String geoOrgStdName)
{
	String payload = "{\"geoplOrgStd\":{"
		+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
	return payload;
}

public static String geoOrgStdPostRequestWithoutOrgStdCd(String userId, String geoOrgStdCode, String geoOrgStdName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
		+ "\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
	return payload;
}


public static String geoOrgStdPostRequestWithoutOrgStdNm(String userId, String geoOrgStdCode, String geoOrgStdName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geoplOrgStd\":{"
		+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\"}}";
	return payload;
}

public static String geoOrgStdPostRequestNullUser(String userId, String geoOrgStdCode, String geoOrgStdName)
{
	String payload = "{\"meta\":{\"userName\":"+userId+"},\"geoplOrgStd\":{"
		+ "\"organizationStandardCode\":\""+geoOrgStdCode+"\",\"organizationStandardName\":\""+geoOrgStdName+"\"}}";
	return payload;
}
*/
public static String geoOrgStdGraphQLWithoutparam()
{
	String payload = "{\"query\":\"{  geopoliticalOrganizationStandards" +
 "{    organizationStandardCode    organizationStandardName  " +
 "}}\"}";
	return payload;
}

public static String geoOrgStdGraphQLWithParam(String orgStdCd)
{
	String payload = "{\"query\":\"{  geopoliticalOrganizationStandards (orgStdCd : \\\""+orgStdCd+"\\\") {    organizationStandardCode    organizationStandardName  }}\"}";
	return payload;

}

public static String geoOrgStdGraphQlRequestWithInvalidAttributeName(String  orgStdCd)
{
	String payload = "{\"query\":\"{  geopoliticalOrganizationStandards (orgStdCd : \\\""+orgStdCd+"\\\") {    organizationStandardCodes1    organizationStandardNames1  }}\"}";
	return payload;
}

public static String geoOrgStdGraphQlRequestWithInvalidParameterName(String  orgStdCd)
{
	String payload = "{\"query\":\"{  geopoliticalOrganizationStandards (orgStdCd : \\\""+orgStdCd+"\\\") {    organizationStandardCode    organizationStandardName  }}\"}";
	return payload;
}



//DepnCtryRltsp
public static String depnCntryRltspPostRequest(String userId, String depnCntryRltsp )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dependentCountryRelationship\":{"
		+ "\"dependentRelationshipDescription\":\""+depnCntryRltsp+"\"}}";
	return payload;
}

public static String depnCntryRltspGqlWithParameters(String userId, String depnCntryRltsp, String depnCntryRltspDesc )
{
	String payload = "{\"query\": \"{    countries {      geopoliticalId :\""+userId+"\"    dependentCountryRelationship :\""+depnCntryRltsp+"\", dependentRelationshipDescription :\""+depnCntryRltspDesc+"\")  {      geopoliticalId      dependentCountryRelationship       dependentRelationshipDescription         }  }  \"}";
	return payload;
}

public static String depnCntryRltspGqlWithoutParameters( )
{
	String payload = "{\"query\": \"{{      geopoliticalId      dependentCountryRelationship       dependentRelationshipDescription        }  }  \"}";
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
		+ "\"holidayName\":\""+holidayName+"\",\"holidayDateParameterText\":\""+holidayParamText+"\"}}";
	return payload;
}

public static String holidaPostRequestwithMissingComma(String userId, String holidayName, String holidayParamText )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"}\"holiday\":{"
		+ "\"holidayName\":\""+holidayName+"\" \"holidayDateParameterText\":\""+holidayParamText+"\"}}";
	return payload;
}

public static String holidaPostRequestWithoutMeta(String holidayName, String holidayParamText )
{
	String payload = "{\"holiday\":{\"holidayName\":\""+holidayName+"\",\"holidayDateParameterText\":\""+holidayParamText+"\"}}";
	return payload;
}

public static String holidaPostRequestWithoutHolidayName(String userId, String holidayParamText )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"holiday\":{\"holidayDateParameterText\":\""+holidayParamText+"\"}}";
	return payload;
}

public static String holidaPostRequestWithoutHolidayParamText(String userId, String holidayName )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"holiday\":{\"holidayName\":\""+holidayName+"\"}}";
	return payload;
}

public static String holidayGraphQl()
{
	String payload = "{\"query\":\"{  holidays  {    holidayId    holidayName    holidayDateParameterText  }}\"}";
	return payload;
}
public static String holidayParameterGraphQl(String holidayName)
{
	String payload =  "{\"query\":\"{  holidays (holidayName : \\\""+holidayName+"\\\") {    holidayId    holidayName    holidayDateParameterText  }}\"}";
	return payload;
}

public static String holidayParameterOneWrongAttrbuteGraphQl(String holidayName)
{
	String payload =  "{\"query\":\"{  holidays (holidayName : \\\""+holidayName+"\\\") {    holidayId    holidayName    holidayDateParameterTexts  }}\"}";
	return payload;
}

public static String holidayParameterTwoeWrongAttrbuteGraphQl(String holidayName)
{
	String payload =  "{\"query\":\"{  holidays (holidayName : \\\""+holidayName+"\\\") {    holidayId    holidayNames    holidayDateParameterTexts  }}\"}";
	return payload;
}

public static String holidayParameterWithAttributeGraphQl(String holidayName)
{
	String payload =  "{\"query\":\"{  holidays (holidayName : \\\""+holidayName+"\\\") {    holidayId   holidayDateParameterText  }}\"}";
	return payload;
}

//UOM
public static String uomTypePostRequest(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
		+ "\"uomTypeCode\":\""+uomTypeCode+"\",\"uomTypeName\":\""+uomTypeName+"\",\"uomTypeDescription\":\""+uomTypeDesc+"\"}}";
	return payload;
}

public static String uomTypePostRequestMissingComma(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
		+ "\"uomTypeCode\":\""+uomTypeCode+"\" \"uomTypeName\":\""+uomTypeName+"\" \"uomTypeDescription\":\""+uomTypeDesc+"\"}}";
	return payload;
}

//GRAPHQL
public static String uomTypeGraphQLRequestWithParam(String uomTypeCode)
{
	String payload = "{\"query\": \"{    uomTypes (uomTypeCd :\\\""+uomTypeCode+"\\\") {      uomTypeCode      uomTypeName      uomTypeDesc    }  }  \"}";
	return payload;
}

public static String uomTypeGraphQLRequestWithoutUomTypeCd(String uomTypeCode)
{
	String payload = "{\"query\": \"{    uomTypes (uomTypeCd :\\\""+uomTypeCode+"\\\") {      uomTypeName      uomTypeDesc    }  }  \"}";
	return payload;
}

public static String uomTypeGraphQLRequestWithoutParam()
{
	String payload = "{\"query\": \"{    uomTypes {      uomTypeCode      uomTypeName      uomTypeDesc    }  }  \"}";
	return payload;
}

public static String uomTypeGraphQLRequestWithInvalidParam(String uomTypeCode)
{
	String payload = "{\"query\": \"{    uomTypes (uomTypeCd :\\\""+uomTypeCode+"\\\") {      uomTypeCode      uomTypeName1      uomTypeDesc    }  }  \"}";
	return payload;
}

public static String uomTypeGraphQLRequestWithAllInvalidParam(String uomTypeCode)
{
	String payload = "{\"query\": \"{    uomTypes (uomTypeCd :\\\""+uomTypeCode+"\\\") {      uomTypeCode1      uomTypeName1      uomTypeDesc1    }  }  \"}";
	return payload;
}

public static String uomTypePostRequestWithoutMeta(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"uomType\":{"
		+ "\"uomTypeCode\":\""+uomTypeCode+"\",\"uomTypeName\":\""+uomTypeName+"\",\"uomTypeDescription\":\""+uomTypeDesc+"\"}}";
	return payload;
}

public static String uomTypePostRequestWithoutUomTypeCd(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
		+ "\"uomTypeName\":\""+uomTypeName+"\",\"uomTypeDescription\":\""+uomTypeDesc+"\"}}";
	return payload;
}

public static String uomTypePostRequestWithoutUomTypeNm(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
		+ "\"uomTypeCode\":\""+uomTypeCode+"\",\"uomTypeDescription\":\""+uomTypeDesc+"\"}}";
	return payload;
}

public static String uomTypePostRequestWithoutUomTypeDesc(String userId, String uomTypeCode, String uomTypeName, String uomTypeDesc)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"uomType\":{"
		+ "\"uomTypeCode\":\""+uomTypeCode+"\",\"uomTypeName\":\""+uomTypeName+"\"}}";
	return payload;
}

//Script
public static String scriptPostRequest(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
		+ "\"scriptCode\":\""+scriptCode+"\",\"scriptName\":\""+scriptName+"\",\"scriptDescription\":\""+scriptDesc+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String scriptPostRequestMissingComma(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
		+ "\"scriptCode\":\""+scriptCode+"\",\"scriptName\":\""+scriptName+"\"\"scriptDescription\":\""+scriptDesc+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String scriptPostRequestWithoutMeta(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"refScript\":{"
		+ "\"scriptCode\":\""+scriptCode+"\",\"scriptName\":\""+scriptName+"\",\"scriptDescription\":\""+scriptDesc+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String scriptPostRequestWithoutSciptCd(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
		+ "\"scriptName\":\""+scriptName+"\",\"scriptDescription\":\""+scriptDesc+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String scriptPostRequestWithoutScriptNm(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
		+ "\"scriptCode\":\""+scriptCode+"\",\"scriptDescription\":\""+scriptDesc+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String scriptPostRequestWithoutScriptDesc(String userId, String scriptCode, String scriptName, String scriptDesc,
		String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"refScript\":{"
		+ "\"scriptCode\":\""+scriptCode+"\",\"scriptName\":\""+scriptName+"\""
		+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

     //Script Graph QL
public static String scriptGraphQLRequestWithoutParameter()
{
	String payload = "{\"query\": \"{    scripts {      scriptCode      scriptName      scriptDescription    }  }  \"}";
	return payload;
}

	public static String scriptGraphQLRequestWithParameter(String scriptCode)
	{
		String payload =  "{\"query\":\"{  scripts (scriptCode : \\\""+scriptCode+"\\\") {    scriptCode    scriptName    scriptDescription  }}\"}";
		return payload;
	}

	public static String scriptGraphQLWithParameterWithAttributes(String scriptCode)
	{
		String payload =  "{\"query\":\"{  scripts (scriptCode : \\\""+scriptCode+"\\\") {    scriptName    scriptDescription  }}\"}";
		return payload;
	}

	public static String scriptGraphQLWith1AtrributeInvalid(String scriptCode)
	{
		String payload =  "{\"query\":\"{  scripts (scriptCode : \\\""+scriptCode+"\\\") {    scriptCode    scriptName    scriptDescription1  }}\"}";
		return payload;
	}

	public static String scriptGraphQLWith2AtrributeInvalid(String scriptCode)
	{
		String payload =  "{\"query\":\"{  scripts (scriptCode : \\\""+scriptCode+"\\\") {    scriptCode    scriptName2    scriptDescription2  }}\"}";
		return payload;
	}

//dayOfWeek
public static String dayOfWeekPostRequest(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
	String payload = "{" +
			"\"meta\":" +
			"{" +
			"\"userName\":\""+userId+"\"" +
			"}," +
			"\"dayOfWeek\":" +
			"{" +
			"\"dayOfWeekNumber\":"+dayOfWeekNbr+"," +
			"\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\"," +
			"\"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"" +
			"}" +
			"}";

			return payload;
}

public static String dayOfWeekPostRequestWithoutMeta(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
	String payload = "{" +
			"\"dayOfWeek\":" +
			"{" +
			"\"dayOfWeekNumber\":"+dayOfWeekNbr+"," +
			"\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\"," +
			"\"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"" +
			"}" +
			"}";

			return payload;
}

public static String dayOfWeekPostRequestWithoutDOWNo(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
	String payload = "{" +
			"\"meta\":" +
			"{" +
			"\"userName\":\""+userId+"\"" +
			"}," +
			"\"dayOfWeek\":" +
			"{" +
			"\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\"," +
			"\"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"" +
			"}" +
			"}";
	return payload;
}

public static String dayOfWeekPostRequestWithoutDOWShNm(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
	String payload = "{" +
			"\"meta\":" +
			"{" +
			"\"userName\":\""+userId+"\"" +
			"}," +
			"\"dayOfWeek\":" +
			"{" +
			"\"dayOfWeekNumber\":"+dayOfWeekNbr+"," +
			"\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\"" +
			"}" +
			"}";
	return payload;
}

public static String dayOfWeekPostRequestWithNulldayOfWeekNbr(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
//	String payload1 = "{"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
//		+ "\"dayOfweekNumber\":"+dayOfWeekNbr+",\"dayOfweekFullName\":\""+dayOfWeekFullName+"\",\"dayOfweekShortName\":\""+dayOfWeekShortName+"\"}}";

	String payload = "{" +
			"\"meta\":" +
			"{" +
			"\"userName\":\""+userId+"\"" +
			"}," +
			"\"dayOfWeek\":" +
			"{" +
			"\"dayOfWeekNumber\":"+dayOfWeekNbr+"," +
			"\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\"," +
			"\"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"" +
			"}" +
			"}";

	return payload;
}

public static String dayOfWeekPostRequestWithoutDOWFNm(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
		+ "\"dayOfWeekNumber\":"+dayOfWeekNbr+",\"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"}}";
	return payload;
}


//MonthOfYear
public static String monthOfYearPostRequest(String userId, String monthOfYearNumber, String monthOfYearShortName )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{"
		+ "\"monthOfYearNumber\":\""+monthOfYearNumber+"\",\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
	return payload;
}
public static String monthOfYearPostwitCommaMissingRequest(String userId, String monthOfYearNumber, String monthOfYearShortName )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{"
		+ "\"monthOfYearNumber\":\""+monthOfYearNumber+"\"\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
	return payload;
}

public static String monthOfYearPostRequestmonthOfYearNumberAsNumber(String userId, String monthOfYearNumber, String monthOfYearShortName )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{"
		+ "\"monthOfYearNumber\":"+monthOfYearNumber+",\"monthOfYearShortName\":\""+monthOfYearShortName+"\"}}";
	return payload;
}

public static String monthOfYearPostRequestWithNullMonthOfYearShortName(String userId, String monthOfYearNumber, String monthOfYearShortName )
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"monthOfYear\":{"
		+ "\"monthOfYearNumber\":\""+monthOfYearNumber+"\",\"monthOfYearShortName\":"+monthOfYearShortName+"}}";
	return payload;
}

public static String monthOfYearPostRequestWithoutUserName(String userId, String monthOfYearNumber, String monthOfYearShortName )
{
	String payload = "{\"meta\":{\"userName\":"+userId+"},\"monthOfYear\":{"
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
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String cntryOrgStdPostRequestWithoutMeta(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"countryOrgStdDTO\":{"
		+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String cntryOrgStdPostRequestWithoutCntryCd(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
		+ "\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String cntryOrgStdPostRequestWithoutCntryShNm(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
		+ "\"countryCode\":\""+countryCode+"\",\"countryFullName\":\""+countryFullName+"\""
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String cntryOrgStdPostRequestWithoutCntryFNm(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
		+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\""
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
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
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String cntryOrgStdPostRequestWithoutExpirationDate(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
		+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
				+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\"}}";
	return payload;
}


//GEOPL RLTSP
public static String geoRltspPostRequest(String userId, String fromGeopoliticalId, String toGeopoliticalId, String relationshipTypeCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationship\":{"
			+ "\"fromGeopoliticalId\":\""+fromGeopoliticalId+"\",\"toGeopoliticalId\":\""+toGeopoliticalId+"\",\"relationshipTypeCode\":\""+relationshipTypeCode+"\""
					+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String geoRltspMissingCommaPostRequest(String userId, String fromGeopoliticalId, String toGeopoliticalId, String relationshipTypeCode,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalRelationship\":{"
			+ "\"fromGeopoliticalId\":\""+fromGeopoliticalId+"\" \"toGeopoliticalId\":\""+toGeopoliticalId+"\" \"relationshipTypeCode\":\""+relationshipTypeCode+"\""
					+ " \"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
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

// GEOPL RLTSP Graph QL
// This method used for TC_01
public static String geoRltspGraphQLRequestWithoutParameter()
{
String payload = "{\"query\":\"{  relationships {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
return payload;
}
// This method used for TC_02
public static String geopRltspWithAllParameters(String relationshipTypeCode, String fromGeopoliticalId, String toGeopoliticalId,
		 String targetDate, String endDate)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\", fromGeopoliticalId : \\\""+fromGeopoliticalId+"\\\", toGeopoliticalId : \\\""+toGeopoliticalId+"\\\", targetDate : \\\""+targetDate+"\\\", endDate : \\\""+endDate+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_03
public static String geopRltspWithrelationshipTypeCodeParameterWith3Attributes(String relationshipTypeCode)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    }}\"}";
	return payload;
}
// This method used for TC_04
public static String geopRltspWith2ParametersWithAllAttributes(String relationshipTypeCode ,String fromGeopoliticalId, String toGeopoliticalId)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode :\\\""+relationshipTypeCode+"\\\", fromGeopoliticalId : \\\""+fromGeopoliticalId+"\\\", toGeopoliticalId : \\\""+toGeopoliticalId+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_05
public static String geopRltspWithTrgDateEnDateParametersWithAllAttributes(String relationshipTypeCode, String targetDate, String endDate)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\", targetDate : \\\""+targetDate+"\\\", endDate : \\\""+endDate+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_06
public static String geopRltspWith1InvalidParametersWithAllAttributes(String relationshipTypeCode)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_07
public static String geopRltspWith2InvalidParametersWithAllAttributes(String relationshipTypeCode, String targetDate, String endDate)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\", targetDate : \\\""+targetDate+"\\\", endDate : \\\""+endDate+"\\\") {    geopoliticalComponentId    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_08
public static String geoRltspGraphQLRequestWith1InvalidAttribute(String relationshipTypeCode)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\") {    geopoliticalComponentId1    relatedGeopoliticalComponentId    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}
// This method used for TC_09
public static String geoRltspGraphQLRequestWith2InvalidAttribute(String relationshipTypeCode)
{
	String payload =  "{\"query\":\"{  relationships(relationshipTypeCode : \\\""+relationshipTypeCode+"\\\") {    geopoliticalComponentId1    relatedGeopoliticalComponentId2    geopoliticalRelationshipTypeCode    effectiveDate    expirationDate  }}\"}";
	return payload;
}




//StProvStd
public static String stProvStdPostRequest(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutMeta(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutorganizationStandardCode(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutstateProvinceCode(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutstateProvinceName(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutEffectiveDt(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"expirationDate\":\""+expirationDate+"\"}}";
	return payload;
}

public static String stProvStdPostRequestWithoutExpirationDt(String userId, String orgStdCd, String stProvCd, String stProvNm,
		 String effectiveDate, String expirationDate)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
		+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
				+ ",\"effectiveDate\":\""+effectiveDate+"\"}}";
	return payload;
}

//GRAPHQL
public static String stProvStdGraphQLRequstWithParam(String geopoliticalId, String orgStdCd, String stProvCd, String countryCode,
		String effectiveDate, String expirationDate) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", countryCode :\\\"" + countryCode + "\\\", stProvCd :\\\"" + stProvCd + "\\\", orgStdCd :\\\"" + orgStdCd + "\\\", targetDate :\\\"" + effectiveDate + "\\\", endDate :\\\"" + expirationDate + "\\\") {    geopoliticalId    organizationStandardCode    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithoutParam() {
	String payload = "{\"query\":\"{  stProvStds  {    geopoliticalId    organizationStandardCode    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithoutDates(String geopoliticalId, String orgStdCd, String stProvCd, String countryCode) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", countryCode :\\\"" + countryCode + "\\\", stProvCd :\\\"" + stProvCd + "\\\", orgStdCd :\\\"" + orgStdCd + "\\\") {    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithInvalidDate(String geopoliticalId,
		String effectiveDate, String expirationDate) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", targetDate :\\\"" + effectiveDate + "\\\", endDate :\\\"" + expirationDate + "\\\") {    geopoliticalId    organizationStandardCode    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithDates(String geopoliticalId, String effectiveDate, String expirationDate) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", targetDate :\\\"" + effectiveDate + "\\\", endDate :\\\"" + expirationDate + "\\\") {    geopoliticalId    organizationStandardCode    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithOneInvalidParam(String geopoliticalId, String orgStdCd, String stProvCd, String countryCode,
		String effectiveDate, String expirationDate) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", countryCode :\\\"" + countryCode + "\\\", stProvCd :\\\"" + stProvCd + "\\\", orgStdCd :\\\"" + orgStdCd + "\\\", targetDate :\\\"" + effectiveDate + "\\\", endDate :\\\"" + expirationDate + "\\\") {    geopoliticalId    organizationStandardCode1    stateProvinceCode    stateProvinceName    effectiveDate    expirationDate  }}\"}";
	return payload;
}

public static String stProvStdGraphQLRequstWithAllInvalidParam(String geopoliticalId, String orgStdCd, String stProvCd, String countryCode,
		String effectiveDate, String expirationDate) {
	String payload = "{\"query\":\"{  stProvStds (geopoliticalId :\\\"" + geopoliticalId + "\\\", countryCode :\\\"" + countryCode + "\\\", stProvCd :\\\"" + stProvCd + "\\\", orgStdCd :\\\"" + orgStdCd + "\\\", targetDate :\\\"" + effectiveDate + "\\\", endDate :\\\"" + expirationDate + "\\\") {    geopoliticalId1    organizationStandardCode1    stateProvinceCode1    stateProvinceName1    effectiveDate1    expirationDate1  }}\"}";
	return payload;
}


//***AffilType
/*public static String affilTypePostRequest(String userId, String affilTypeCode, String affilTypeName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
			+ "\"affilTypeCode\":\""+affilTypeCode+"\",\"affilTypeName\":\""+affilTypeName+"\"}}";
	return payload;
}
*/
public static String affilTypePostRequest(String userId, String affilTypeCode, String affilTypeName /*String effectiveDate, String expirationDate*/)
{
	/*String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
			+ "\"affiliationTypeCode\":\""+affilTypeCode+"\",\"affiliationTypeName\":\""+affilTypeName+"\","
					+ "\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";*/
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
			+ "\"affiliationTypeCode\":\""+affilTypeCode+"\",\"affiliationTypeName\":\""+affilTypeName+"\"}}";
	return payload;
}

public static String affilTypePostRequestMissingcomma(String userId, String affilTypeCode, String affilTypeName /*String effectiveDate, String expirationDate*/)
{

	String payload = "{\"meta\":{\"userName\":\""+userId+"\"}  \"geopoliticalAffiliationType\":{"
			+ "\"affiliationTypeCode\":\""+affilTypeCode+"\"  \"affiliationTypeName\":\""+affilTypeName+"\"}}";
	return payload;
}

public static String affilTypeGraphQLRequestWithParam(String userId, String affilTypeCode, String affilTypeName)
{
	String payload = "{\"query\":\"{  affiliationTypes (affilTypeCode : String) {    affilTypeId    affilTypeCode    affilTypeName  }}\"}";
	return payload;
}

public static String affilTypeGraphQLRequestWithoutParam()
{
	String payload = "{\"query\":\"{  affiliationTypes {    affilTypeId    affilTypeCode    affilTypeName  }}\"}";
	return payload;
}

public static String affilTypePostRequestWithoutMeta(String userId, String affilTypeCode, String affilTypeName)
{
	String payload = "{\"geopoliticalAffiliationType\":{"
			+ "\"affiliationTypeCode\":\""+affilTypeCode+"\",\"affiliationTypeName\":\""+affilTypeName+"\"}}";
	return payload;
}


public static String affilTypePostRequestWithoutAffilTypeCd(String userId, String affilTypeCode, String affilTypeName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
			+ "\"affiliationTypeName\":\""+affilTypeName+"\"}}";
	return payload;
}

public static String affilTypePostRequestWithoutAffilTypeNm(String userId, String affilTypeCode, String affilTypeName)
{
	String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"geopoliticalAffiliationType\":{"
		+ "\"affiliationTypeCode\":\""+affilTypeCode+"\"}}";
	return payload;
}

public static String affilTypeGraphQLWithParamRequest(String affilTypeCode)
{
	String payload = "{\"query\":\"{  affiliationTypes (affilTypeCode : \\\""+affilTypeCode+"\\\") {    affilTypeId    affilTypeCode    affilTypeName  }}\"}";
	return payload;
}

public static String affilTypeGraphQLWithParamRequest1(String affilTypeCode)
{
	String payload = "{\"query\":\"{  affiliationTypes (affilTypeCode : \\\""+affilTypeCode+"\\\")  {    affilTypeId    affilTypeCode   }}\"}";
	return payload;
}

public static String affilTypeGraphQLWithParamRequest2(/*String affilTypeCode*/)
{
	String payload = "{\"query\":\"{  affiliationTypes (affilTypeCode : \\\"UN\\\")  {    affilTypeId1    affilTypeCode1   }}\"}";
	return payload;
}

// Language


//as per swagger
public static String langPostNewRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}



//as per swagger
public static String langPostMulitpleLocaleCdNewRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs, String localeCd1, String countryCode1) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}, "
			+ "{ "
			+ "\"localeCode\":\"" + localeCd1 + "\","
			+ "\"countryCode\":\"" + countryCode1 + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "} "
			+ "]" + "}"  + "}";

	return payload;

}



//as per swagger
public static String langPostNewWithoutExpirationDateRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}



//as per swagger
public static String langPostNewWithoutEffectiveDateRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}



//as per swagger
public static String langPostNewRequestCommaMissing(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\" "
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostNewWithoutMetaRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostNewWithoutUserNameRequest( String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{"
			+ "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostNewWithoutnativeScriptLanguageNameRequest(String userName, String languageCode, String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostNewWithoutLangNameRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostNewWithoutLangCdRequest(String userName, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostWithoutDOWRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate, HashMap<String, String> translatedMOYs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","
			+ "\"translatedMOYs\":[" + "{"
			+ "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
			+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
			+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostWithoutMOYNewRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate,
		HashMap<String, String> translatedDOWs) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\","

			+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
			+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
			+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
			+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
			+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]"
			+ "}" + "]" + "}"  + "}";

	return payload;

}


//as per swagger
public static String langPostWithoutDOWandMOYNewRequest(String userName, String languageCode, String nativeScriptLanguageName,String nativeScriptCode,
		String languageName,  String localeCd, String countryCode, String localesScriptCd,
		 String cldrVersionDate, String cldrVersionNumber, String dateFullFormatDescription,
		String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
		 String localesEffectiveDate, String localesExpirationDate) {
	String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
			+ "\"languageCode\":\"" + languageCode + "\","
			+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
			+ "\"nativeScriptCode\":\"" + nativeScriptCode + "\","
			+ "\"languageName\":\"" + languageName + "\","
			+ "\"locales\":[ {"
			+ "\"localeCode\":\"" + localeCd + "\","
			+ "\"countryCode\":\"" + countryCode + "\","
			+ "\"scriptCode\":\"" + localesScriptCd + "\","
			+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
			+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
			+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
			+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
			+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
			+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
			+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
			+ "\"expirationDate\":\"" + localesExpirationDate + "\""
			+ "}" + "]" + "}"  + "}";

	return payload;

}








	public static String langPostRequest(String userName, String languageCode, String nativeScriptLanguageName,
			String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
			String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
			String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
			String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
			HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
		String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
				+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
				+ "\"languageName\":\"" + languageName + "\","
				+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
				+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
				+ "\"monthOfYearNumber\":" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
				+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

		return payload;

	}

	public static String langPostRequestwithCommaMissing(String userName, String languageCode, String nativeScriptLanguageName,
			String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
			String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
			String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
			String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
			HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
		String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
				+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
				+ "\"languageName\":\"" + languageName + "\","
				+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
				+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "" + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
				+ "\"monthOfYearNumber\":" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedMonthOfYearName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
				+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

		return payload;

	}

	public static String langPostRequestWithoutQuotes(String userName, String languageCode, String nativeScriptLanguageName,
			String nativeScriptLanguageNm, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
			String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
			String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
			String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
			HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
		String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
				+ "\"languageCode\":\"" + languageCode + "\","
				+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
				+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
				+ "\"nativeScriptLanguageNm\":\"" + nativeScriptLanguageNm + "\","
				+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + ","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":"
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
				+ "\"monthOfYearNumber\":" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
				+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

		return payload;

	}

	public static String langPostRequestMoYLength(String userName, String languageCode, String nativeScriptLanguageName,
			String nativeScriptLanguageNm, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
			String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
			String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
			String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
			HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
		String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
				+ "\"languageCode\":\"" + languageCode + "\","
				+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
				+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
				+ "\"nativeScriptLanguageNm\":\"" + nativeScriptLanguageNm + "\","
				+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
				+ "\"monthOfYearNumber\":" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":"
				+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "," + "\"translatedDayOfWeekName\":\""
				+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
				+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

		return payload;

	}

		public static String langPostRequestWithoutDOW(String userName, String languageCode, String nativeScriptLanguageName,
				String nativeScriptLanguageNm, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				 HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"languageName\":\"" + nativeScriptLanguageNm + "\","
					+ "\"translatedDOWs\":[],"
					+ "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;


		}

		public static String langPostRequestWithoutMOY(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
			HashMap<String, String> translatedDOWs) {
		String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
				+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\""
				+ nativeScriptLanguageName + "\"," + "\"languageName\":\"" + languageName + "\"," + "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\"," + "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\"," + "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\"," + "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\"," + "\"cldrVersionDate\":\"" + cldrVersionDate
				+ "\"," + "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\"," + "\"effectiveDate\":\""
				+ localesEffectiveDate + "\"," + "\"expirationDate\":\"" + localesExpirationDate + "\""
				+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\""
				+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2")
				+ "\"" + "}," + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber3")
				+ "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4")
				+ "\"" + "}," + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber5")
				+ "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{"
				+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\","
				+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6")
				+ "\"" + "}," + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber7")
				+ "\"," + "\"translatedDayOfWeekName\":\""
				+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "],"
				+ "\"translatedMOYs\":[]" + "\"" + "}" + "]" + "}" + "]" + "}" + "}";

		return payload;

	}

		public static String langPostRequestWithoutDOWAndMOY(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate ){
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"languageName\":\"" + languageName + "\","
					+ "\"translatedDOWs\":[],"
					+ "\"translatedMOYs\":[]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutlanguageCode(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "" + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"languageName\":\"" + languageName + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutEnglLangNm(String userName, String languageCode, String nativeScriptLanguageName,
				String nativeScriptLanguageNm, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\"," + ""
					+ "\"nativeScriptLanguageNm\":\"" + nativeScriptLanguageNm + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutNatScrptLangNm(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\"," + "\"languageName\":\"" + languageName + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ ""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutScrptCd(String userName, String languageCode, String nativeScriptLanguageName,
				String nativeScriptLanguageNm, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{" + "\"userName\":\"" + userName + "\"" + "}," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"nativeScriptLanguageNm\":\"" + nativeScriptLanguageNm + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutUserNm(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"meta\":{ }," + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"languageName\":\"" + languageName + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}

		public static String langPostRequestWithoutMeta(String userName, String languageCode, String nativeScriptLanguageName,
				String languageName, String geopoliticalId, String countryCode, String localesLanguageCd, String localeCd,
				String localesScriptCd, String cldrVersionNumber, String cldrVersionDate, String dateFullFormatDescription,
				String dateLongFormatDescription, String dateMediumFormatDescription, String dateShortFormatDescription,
				String engLanguageName, String localesEffectiveDate, String localesExpirationDate,
				HashMap<String, String> translatedDOWs, HashMap<String, String> translatedMOYs) {
			String payload = "{" + "\"language\":{"
					+ "\"languageCode\":\"" + languageCode + "\"," + "\"nativeScriptLanguageName\":\"" + nativeScriptLanguageName + "\","
					+ "\"languageName\":\"" + languageName + "\","
					+ "\"locales\":["
				+ "{" + "\"languageCode\":\"" + localesLanguageCd + "\","
				+ "\"localeCode\":\"" + localeCd + "\","
				+ "\"scriptCode\":\"" + localesScriptCd + "\","
				+ "\"geopoliticalId\":\"" + geopoliticalId + "\","
				+ "\"countryCode\":\"" + countryCode + "\","
				+ "\"engLanguageName\":\"" + engLanguageName + "\","
				+ "\"cldrVersionNumber\":\"" + cldrVersionNumber + "\","
				+ "\"cldrVersionDate\":\"" + cldrVersionDate + "\","
				+ "\"dateFullFormatDescription\":\"" + dateFullFormatDescription + "\","
				+ "\"dateLongFormatDescription\":\"" + dateLongFormatDescription + "\","
				+ "\"dateMediumFormatDescription\":\"" + dateMediumFormatDescription + "\","
				+ "\"dateShortFormatDescription\":\"" + dateShortFormatDescription + "\","
				+ "\"effectiveDate\":\"" + localesEffectiveDate + "\","
				+ "\"expirationDate\":\"" + localesExpirationDate + "\""
					+ "\"translatedDOWs\":[" + "{" + "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber1") + "\","
					+ "\"translatedDayOfWeekName\":\"" + translatedDOWs.get("translatedDOWs_translatedDayOfWeekName1") + "\"" + "}," + "{"
					+ "\"dayOfWeekNumber\":\"" + translatedDOWs.get("translatedDOWs_dayOfWeekNumber2") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName2") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber3") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName3") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber4") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName4") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber5") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName5") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber6") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName6") + "\"" + "}," + "{" + "\"dayOfWeekNumber\":\""
					+ translatedDOWs.get("translatedDOWs_dayOfWeekNumber7") + "\"," + "\"translatedDayOfWeekName\":\""
					+ translatedDOWs.get("translatedDOWs_translatedDayOfWeekName7") + "\"" + "}" + "]," + "\"translatedMOYs\":[" + "{"
					+ "\"monthOfYearNumber\":\"" + translatedMOYs.get("translatedMOYs_monthOfYearNumber1") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName1") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber2") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName2") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber3") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName3") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber4") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName4") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber5") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName5") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber6") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName6") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber7") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName7") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber8") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName8") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber9") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName9") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber10") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName10") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber11") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName11") + "\"" + "}," + "{" + "\"monthOfYearNumber\":\""
					+ translatedMOYs.get("translatedMOYs_monthOfYearNumber12") + "\"," + "\"translatedMonthOfYearName\":\""
					+ translatedMOYs.get("translatedMOYs_translatedMonthOfYearName12") + "\"" + "}" + "]"
					+ "\"" + "}" + "]" + "}" + "]" + "}" + "}";

			return payload;

		}


/*public static String cntryGraphQLRequest(String geopoliticalId,String  countryCd,String  countryShortName,String  orgStandardCode,String  dependentCountryCd,
		String  targetDate,String  endDate) {

		String payload = "{"query":"{  countries(geopoliticalId : \"3424394677886817422\", countryCode : \"AM\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independantFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLengthNumber    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLength      mobilePhoneMaximumLength      landPhoneMinimumLength      landPhoneMaximumLength      effectiveDate      expirationDate    }    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }    geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }    geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }    locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }    translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }    countryOrgStd {      geopoliticalId      countryShortName      countryFullName      organisationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {        organisationStandardCode        organisationStandardName      }    }    geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }    dependentCountryRelationship {      dependentRelationshipId      dependentRelationshipDescription    }    stateProvStndList {      geopoliticalId      organisationStandardCode      stateProvinceCode      stateProvinceName      effectiveDate      expirationDate    }  }}"}";


		return payload;

	}*/



public static String cntryPostRequest(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
		String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
		String dependentRelationshipId,String dependentCountryCd,
		String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
		String countryEffectiveDate,String countryExpirationDate,
		String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
		String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
		String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
		String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
		String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
		String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
		String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

		String payload = "{" +
				"\"meta\":{" +
				"\"userName\":\""+userId+"\"" +
				"}," +
				"\"country\":{" +
				"\"countryNumericCode\":\""+countryNumberCd+"\"," +
				"\"countryCode\":\""+countryCd+"\"," +
				"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
				"\"independentFlag\":\""+independentFlag+"\"," +
				"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
				"\"postalFlag\":\""+postalFlag+"\"," +
				"\"postalLength\":\""+postalLengthNumber+"\"," +
				"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
				"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
				"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
				"\"internetDomainName\":\""+internetDomainName+"\"," +
				"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
				"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
				"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
				"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
				"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
				"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
				"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
				"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
				"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
				"\"expirationDate\":\""+countryExpirationDate+"\"," +
				"\"currencies\":[" +
				"{" +
				"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
				"\"currencyCode\":\""+currencyCd+"\"," +
				"\"minorUnitCode\":\""+minorUnitCd+"\"," +
				"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
				"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
				"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalUnitOfMeasures\":[" +
				"{" +
				"\"uomTypeCode\":\""+uomTypeCd+"\"," +
				"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalHolidays\":[" +
				"{" +
				"\"holidayName\":\""+holidayName+"\"," +
				"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalAffiliations\":[" +
				"{" +
				"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
				"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
				"}" +
				"]," +
				/*"\"locales\":[" +
				"{" +
				"\"languageCode\":\""+localesLanguageCd+"\"," +
				"\"localeCode\":\""+localeCd+"\"," +
				"\"scriptCode\":\""+localesScriptCd+"\"," +
				"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
				"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
				"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
				"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
				"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
				"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
				"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
				"\"expirationDate\":\""+localesExpirationDate+"\"" +
				"}" +
				"]," +*/
				"\"translationGeopoliticals\":[" +
				"{" +
				"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
				"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
				"\"translatedName\":\""+translationName+"\"," +
				"\"versionNumber\":\""+versionNumber+"\"," +
				"\"versionDate\":\""+versionDate+"\"," +
				"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
				"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalType\":" +
				"{" +
				"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
				"}" +
				"}" +
				"}";

		return payload;

	}

public static String cntryPostRequestMissingComma(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
		String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
		String dependentRelationshipId,String dependentCountryCd,
		String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
		String countryEffectiveDate,String countryExpirationDate,
		String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
		String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
		String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
		String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
		String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
		String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
		String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

		String payload = "{" +
				"\"meta\":{" +
				"\"userName\":\""+userId+"\"" +
				"}," +
				"\"country\":{" +
				"\"countryNumericCode\":\""+countryNumberCd+"\"," +
				"\"countryCode\":\""+countryCd+"\"," +
				"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
				"\"independentFlag\":\""+independentFlag+"\"," +
				"\"postalFormatDescription\":\""+postalFormatDescription+"\"" +
				"\"postalFlag\":\""+postalFlag+"\"," +
				"\"postalLength\":\""+postalLengthNumber+"\"," +
				"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
				"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
				"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
				"\"internetDomainName\":\""+internetDomainName+"\"," +
				"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
				"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
				"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
				"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
				"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
				"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
				"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
				"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
				"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
				"\"expirationDate\":\""+countryExpirationDate+"\"," +
				"\"currencies\":[" +
				"{" +
				"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
				"\"currencyCode\":\""+currencyCd+"\"," +
				"\"minorUnitCode\":\""+minorUnitCd+"\"," +
				"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
				"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
				"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalUnitOfMeasures\":[" +
				"{" +
				"\"uomTypeCode\":\""+uomTypeCd+"\"," +
				"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalHolidays\":[" +
				"{" +
				"\"holidayName\":\""+holidayName+"\"," +
				"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalAffiliations\":[" +
				"{" +
				"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
				"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
				"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
				"}" +
				"]," +
				/*"\"locales\":[" +
				"{" +
				"\"languageCode\":\""+localesLanguageCd+"\"," +
				"\"localeCode\":\""+localeCd+"\"," +
				"\"scriptCode\":\""+localesScriptCd+"\"," +
				"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
				"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
				"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
				"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
				"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
				"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
				"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
				"\"expirationDate\":\""+localesExpirationDate+"\"" +
				"}" +
				"]," +*/
				"\"translationGeopoliticals\":[" +
				"{" +
				"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
				"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
				"\"translatedName\":\""+translationName+"\"," +
				"\"versionNumber\":\""+versionNumber+"\"," +
				"\"versionDate\":\""+versionDate+"\"," +
				"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
				"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
				"}" +
				"]," +
				"\"geopoliticalType\":" +
				"{" +
				"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
				"}" +
				"}" +
				"}";

		return payload;

	}

		public static String cntryPostRequestWithNullGeoTypeName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":"+geopoliticalTypeName +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutCntryCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutThreeCharCntryCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutIndependentFlag(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutInitialDialingPrefixCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutIntialDialingCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutCurrencyNumberCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutCurrencyCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutminorUnitCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutMoneyFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutUomTypeCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutphoneNumberFormatPattern(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String countryEffectiveDate,String countryExpirationDate,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutholidayName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutAffilTypeCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutLocaleLanguageCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutLocaleCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithouttranslationGeopoliticalsLangCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutGeoTypeNm(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutMeta(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutUserName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutPostalFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutPostalFlag(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutPostalLengthNumber(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutFirstWorkWeekDayName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutWeekendFirstDayName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutInternetDomainName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutDependentRelationshipId(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutDependentCountryCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutEffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutExpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutLandPhMaxLthNbr(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutlandPhMinLthNbr(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutmoblPhMaxLthNbr(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequest_without_phoneNumberFormatPattern(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String countryEffectiveDate,String countryExpirationDate,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
					/*	"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +*/
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutmoblPhMinLthNbr(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutCDEffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestCDExpirationDt(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutcldrVersionNumber(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutcldrVersionDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutdateFullFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutdateLongFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutdateMediumFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutdateShortFormatDescription(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutTranslationName(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutTGversionNumber(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutTGversionDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutcountryNumberCd(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestCurEffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestCurExpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestUomEffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestUomExpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestHolidayeffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestHolidayexpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


		public static String cntryPostRequestWithoutAffileffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutAffilexpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutLocaleEffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestLocaleExpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestWithoutTGeffectiveDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}

		public static String cntryPostRequestTGexpirationDate(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
				String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
				String dependentRelationshipId,String dependentCountryCd,String countryEffectiveDate,String countryExpirationDate,
				String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
				String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
				String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
				String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
				String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
				String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
				String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
				String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

				String payload = "{" +
						"\"meta\":{" +
						"\"userName\":\""+userId+"\"" +
						"}," +
						"\"country\":{" +
						"\"countryNumericCode\":\""+countryNumberCd+"\"," +
						"\"countryCode\":\""+countryCd+"\"," +
						"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
						"\"independentFlag\":\""+independentFlag+"\"," +
						"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
						"\"postalFlag\":\""+postalFlag+"\"," +
						"\"postalLength\":\""+postalLengthNumber+"\"," +
						"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
						"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
						"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
						"\"internetDomainName\":\""+internetDomainName+"\"," +
						"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
						"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
						"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
						"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
						"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
						"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
						"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
						"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
						"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
						"\"expirationDate\":\""+countryExpirationDate+"\"," +
						"\"currencies\":[" +
						"{" +
						"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
						"\"currencyCode\":\""+currencyCd+"\"," +
						"\"minorUnitCode\":\""+minorUnitCd+"\"," +
						"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
						"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
						"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalUnitOfMeasures\":[" +
						"{" +
						"\"uomTypeCode\":\""+uomTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalHolidays\":[" +
						"{" +
						"\"holidayName\":\""+holidayName+"\"," +
						"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalAffiliations\":[" +
						"{" +
						"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
						"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
						"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
						"}" +
						"]," +
						/*"\"locales\":[" +
						"{" +
						"\"languageCode\":\""+localesLanguageCd+"\"," +
						"\"localeCode\":\""+localeCd+"\"," +
						"\"scriptCode\":\""+localesScriptCd+"\"," +
						"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
						"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
						"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
						"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
						"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
						"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
						"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
						"\"expirationDate\":\""+localesExpirationDate+"\"" +
						"}" +
						"]," +*/
						"\"translationGeopoliticals\":[" +
						"{" +
						"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
						"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
						"\"translatedName\":\""+translationName+"\"," +
						"\"versionNumber\":\""+versionNumber+"\"," +
						"\"versionDate\":\""+versionDate+"\"," +
						"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"" +
						"}" +
						"]," +
						"\"geopoliticalType\":" +
						"{" +
						"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
						"}" +
						"}" +
						"}";

				return payload;

			}


/*
		public static String addressLabelPostRequestWithoutMeta(String userId, String countryCode, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode,
				String applicable, String scriptCode) {
			String payload = "{\"meta\":{\"addressLabels \":{" + "\"countryCode\":\"" + countryCode
					+ "\",\"addressLineNumber\":\"" + addressLineNumber + "\"" + ",\"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",\"fullAddressLineDescription\":\"" + fullAddressLineDescription
					+ "\"" + ",\"languageCode\":\"" + languageCode + "\",\"applicable\":\"" + applicable + "\""
					+ ",\"scriptCode\":\"" + scriptCode + "\"}}";
			return payload;
		}


		public static String addressLabelPostRequestnullLangCD(String userId, String geopoliticalId, String languageCode,
				String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
				String applicableFlag, String effectiveDate, String expirationDate) {

			String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
					+ geopoliticalId + ",\"languageCode\":" + languageCode + "" + ",\"addressLineNumber\":"
					+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
					+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
					+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
					+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
			return payload;

		}

		// Address Label PUT
		public static String addressLabelPutRequest(String userId, String countryCd, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode, String flag,
				String scriptCode) {

			String payload = "{ " + "  \"meta\": { " + "    \"userName\": \"" + userId + "\" " + "   }, "
					+ "    \"addressLabels \": { " + " \"countryCode\": \"" + countryCd + "\","
					+ " \"addressLineNumber\": \"" + addressLineNumber + "\"," + " \"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",  \"fullAddressLineDescription\": \"" + fullAddressLineDescription
					+ "\"," + " \"languageCode\": \" " + languageCode + " \"," + " \"applicable\": \"" + flag + "\", "
					+ " \"scriptCode\": \" " + scriptCode + "" + "    } " + "} ";
			return payload;
		}

		public static String addressLabelPutRequestWithoutMeta(String userId, String countryCd, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode, String flag,
				String scriptCode) {

			String payload = "{ " + "  \"addressLabels \": { " + " \"countryCode\": \"" + countryCd + "\","
					+ " \"addressLineNumber\": \"" + addressLineNumber + "\"," + " \"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",  \"fullAddressLineDescription\": \"" + fullAddressLineDescription
					+ "\"," + " \"languageCode\": \" " + languageCode + " \"," + " \"applicable\": \"" + flag + "\", "
					+ " \"scriptCode\": \" " + scriptCode + "" + "    } " + "} ";
			return payload;
		}


	public static String addressLabelPostRequest(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+",\"localeCode\":\""+localeCode+"\""
						+ ",\"addressLineNumber\":"+addressLineNumber+",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestWithoutEffectiveDate(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag,  String expirationDate) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\""
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;
	}

	public static String addressLabelPostRequestWithoutExpiratioDate(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\"}}";
		return payload;

	}

	public static String addressLabelWithoutCommaPostRequest(String userId, String geopoliticalId, String languageCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+" \"languageCode\":\""+languageCode+"\""
						+ ",\"addressLineNumber\":"+addressLineNumber+" \"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ " \"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestgeoplIdNull(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+",\"localeCode\":\""+localeCode+"\""
						+ ",\"addressLineNumber\":"+addressLineNumber+",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}


	public static String addressLabelPostRequestApllFlagNull(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+",\"localeCode\":\""+localeCode+"\""
						+ ",\"addressLineNumber\":"+addressLineNumber+",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":"+applicableFlag+""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestUsernameNull(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":"+userId+"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+",\"localeCode\":\""+localeCode+"\""
						+ ",\"addressLineNumber\":"+addressLineNumber+",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestnullLocalCD(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
				+ "\"geopoliticalId\":"+geopoliticalId+",\"localeCode\":"+localeCode+""
						+ ",\"addressLineNumber\":"+addressLineNumber+",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestWithoutMeta(String userId, String geopoliticalId, String localeCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate)
	{
		String payload =
				"{\"addressLabel\":{"
				+ "\"geopoliticalId\":\""+geopoliticalId+"\",\"localeCode\":\""+localeCode+"\""
						+ ",\"addressLineNumber\":\""+addressLineNumber+"\",\"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
								+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
										+ ",\"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}

*/

		public static String addressLabelPostRequestWithoutMeta(String userId, String countryCode, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode,
				String applicable, String scriptCode) {
			String payload = "{\"meta\":{\"addressLabels \":{" + "\"countryCode\":\"" + countryCode
					+ "\",\"addressLineNumber\":\"" + addressLineNumber + "\"" + ",\"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",\"fullAddressLineDescription\":\"" + fullAddressLineDescription
					+ "\"" + ",\"languageCode\":\"" + languageCode + "\",\"applicable\":\"" + applicable + "\""
					+ ",\"scriptCode\":\"" + scriptCode + "\"}}";
			return payload;
		}

		// Address Label PUT
		public static String addressLabelPutRequest(String userId, String countryCd, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode, String flag,
				String scriptCode) {

			String payload = "{ " + "  \"meta\": { " + "    \"userName\": \"" + userId + "\" " + "   }, "
					+ "    \"addressLabels \": { " + " \"countryCode\": \"" + countryCd + "\","
					+ " \"addressLineNumber\": \"" + addressLineNumber + "\"," + " \"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",  \"fullAddressLineDescription\": \"" + fullAddressLineDescription
					+ "\"," + " \"languageCode\": \" " + languageCode + " \"," + " \"applicable\": \"" + flag + "\", "
					+ " \"scriptCode\": \" " + scriptCode + "" + "    } " + "} ";
			return payload;
		}

		public static String addressLabelPutRequestWithoutMeta(String userId, String countryCd, String addressLineNumber,
				String brandAddressLineDescription, String fullAddressLineDescription, String languageCode, String flag,
				String scriptCode) {

			String payload = "{ " + "  \"addressLabels \": { " + " \"countryCode\": \"" + countryCd + "\","
					+ " \"addressLineNumber\": \"" + addressLineNumber + "\"," + " \"brandAddressLineDescription\":\""
					+ brandAddressLineDescription + "\",  \"fullAddressLineDescription\": \"" + fullAddressLineDescription
					+ "\"," + " \"languageCode\": \" " + languageCode + " \"," + " \"applicable\": \"" + flag + "\", "
					+ " \"scriptCode\": \" " + scriptCode + "" + "    } " + "} ";
			return payload;
		}


		public static String addressLabelPostRequest(String userId, String geopoliticalId, String languageCode,
				String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
				String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

			String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
					+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" + ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
					+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
					+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
					+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
					+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
			return payload;

		}
		
		
		public static String addressLabelPostRequestWithoutScriptCd(String userId, String geopoliticalId, String languageCode,
				String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
				String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

			String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
					+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" + ",\"addressLineNumber\":"
					+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
					+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
					+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
					+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
			return payload;

		}

	public static String addressLabelPostRequestWithoutEffectiveDate(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag,  String expirationDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\""
				+ ",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;
	}
	

	public static String addressLabelPostRequestWithoutExpiratioDate(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\"}}";
		return payload;

	}

	public static String addressLabelWithoutCommaPostRequest(String userId, String geopoliticalId, String languageCode, String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription, String applicableFlag, String effectiveDate, String expirationDate, String scriptCode)
	{

	String payload =
			"{\"meta\":{\"userName\":\""+userId+"\"},\"addressLabel\":{"
			+ "\"geopoliticalId\":"+geopoliticalId+" \"languageCode\":\""+languageCode+"\"" + ",\"scriptCode\":\"" + scriptCode + "\"" 
			+ ",\"addressLineNumber\":"+addressLineNumber+" \"fullAddressLineLabelDescription\":\""+fullAddressLineLabelDescription+"\""
			+ ",\"brandAddressLineLabelDescription\":\""+brandAddressLineLabelDescription+"\",\"applicableFlag\":\""+applicableFlag+"\""
			+ " \"effectiveDate\":\""+effectiveDate+"\",\"expirationDate\":\""+expirationDate+"\"}}";
		return payload;

	}

	public static String addressLabelPostRequestgeoplIdNull(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode  + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;

	}


	public static String addressLabelPostRequestApllFlagNull(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":" + applicableFlag + "" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;

	}

	public static String addressLabelPostRequestUsernameNull(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":" + userId + "},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":\"" + languageCode + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;

	}

	public static String addressLabelPostRequestnullLangCD(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {

		String payload = "{\"meta\":{\"userName\":\"" + userId + "\"},\"addressLabel\":{" + "\"geopoliticalId\":"
				+ geopoliticalId + ",\"languageCode\":" + languageCode + "" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":"
				+ addressLineNumber + ",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;

	}

	public static String addressLabelPostRequestWithoutMeta(String userId, String geopoliticalId, String languageCode,
			String addressLineNumber, String fullAddressLineLabelDescription, String brandAddressLineLabelDescription,
			String applicableFlag, String effectiveDate, String expirationDate, String scriptCode) {
		String payload = "{\"addressLabel\":{" + "\"geopoliticalId\":\"" + geopoliticalId + "\",\"languageCode\":\""
				+ languageCode + "\"" +  ",\"scriptCode\":\"" + scriptCode + "\"" + ",\"addressLineNumber\":\"" + addressLineNumber
				+ "\",\"fullAddressLineLabelDescription\":\"" + fullAddressLineLabelDescription + "\""
				+ ",\"brandAddressLineLabelDescription\":\"" + brandAddressLineLabelDescription
				+ "\",\"applicableFlag\":\"" + applicableFlag + "\"" + ",\"effectiveDate\":\"" + effectiveDate
				+ "\",\"expirationDate\":\"" + expirationDate + "\"}}";
		return payload;
	}

		
/**
 * Country Post Request New*/
	public static String cntryPostRequestNew(String userId,String countryNumberCd,String countryCd,String threeCharCountryCd,String independentFlag,String postalFormatDescription,
			String postalFlag,String postalLengthNumber,String firstWorkWeekDayName,String lastWorkWeekDayName,String weekendFirstDayName,String internetDomainName,
			String dependentRelationshipId,String dependentCountryCd, String countryEffectiveDate,String countryExpirationDate,
			String intialDialingCd,String landPhMaxLthNbr,String landPhMinLthNbr,String moblPhMaxLthNbr,String moblPhMinLthNbr,
			String currencyNumberCd,String currencyCd,String minorUnitCd,String moneyFormatDescription,String currenciesEffectiveDate,
			String currenciesExpirationDate,String uomTypeCd,String geopoliticalUnitOfMeasuresEffectiveDate,String geopoliticalUnitOfMeasuresExpirationDate,
			String holidayName,String geopoliticalHolidaysEffectiveDate,String geopoliticalHolidaysExpirationDate,String affilTypeCd,
			String geopoliticalAffiliationsEffectiveDate,String geopoliticalAffiliationsExpirationDate,String localesLanguageCd,String localeCd,String localesScriptCd,String cldrVersionNumber,
			String cldrVersionDate,String dateFullFormatDescription,String dateLongFormatDescription,String dateMediumFormatDescription,String dateShortFormatDescription,
			String localesEffectiveDate,String localesExpirationDate,String translationGeopoliticalsLanguageCd,String translationGeopoliticalsScriptCd,String translationName,String versionNumber,String versionDate,
			String translationGeopoliticalsEffectiveDate,String translationGeopoliticalsExpirationDate,String geopoliticalTypeName, String phoneNumberFormatPattern) {

			String payload = "{" +
					"\"meta\":{" +
					"\"userName\":\""+userId+"\"" +
					"}," +
					"\"country\":{" +
					"\"countryNumericCode\":\""+countryNumberCd+"\"," +
					"\"countryCode\":\""+countryCd+"\"," +
					"\"threeCharacterCountryCode\":\""+threeCharCountryCd+"\"," +
					"\"independentFlag\":\""+independentFlag+"\"," +
					"\"postalFormatDescription\":\""+postalFormatDescription+"\"," +
					"\"postalFlag\":\""+postalFlag+"\"," +
					"\"postalLength\":\""+postalLengthNumber+"\"," +
					"\"firstWorkWeekDayName\":\""+firstWorkWeekDayName+"\"," +
					"\"lastWorkWeekDayName\":\""+lastWorkWeekDayName+"\"," +
					"\"weekendFirstDayName\":\""+weekendFirstDayName+"\"," +
					"\"internetDomainName\":\""+internetDomainName+"\"," +
					"\"dependentRelationshipId\":\""+dependentRelationshipId+"\"," +
					"\"dependentCountryCode\":\""+dependentCountryCd+"\"," +
					"\"internationalDialingCode\":\""+intialDialingCd+"\"," +
					"\"landPhoneMaximumLength\":"+landPhMaxLthNbr+"," +
					"\"landPhoneMinimumLength\":"+landPhMinLthNbr+"," +
					"\"mobilePhoneMaximumLength\":"+moblPhMaxLthNbr+"," +
					"\"mobilePhoneMinimumLength\":"+moblPhMinLthNbr+"," +
					"\"phoneNumberFormatPattern\":\""+phoneNumberFormatPattern+"\"," +
					"\"effectiveDate\":\""+countryEffectiveDate+"\"," +
					"\"expirationDate\":\""+countryExpirationDate+"\"," +
					"\"currencies\":[" +
					"{" +
					"\"currencyNumericCode\":\""+currencyNumberCd+"\"," +
					"\"currencyCode\":\""+currencyCd+"\"," +
					"\"minorUnitCode\":\""+minorUnitCd+"\"," +
					"\"moneyFormatDescription\":\""+moneyFormatDescription+"\"," +
					"\"effectiveDate\":\""+currenciesEffectiveDate+"\"," +
					"\"expirationDate\":\""+currenciesExpirationDate+"\"" +
					"}" +
					"]," +
					"\"geopoliticalUnitOfMeasures\":[" +
					"{" +
					"\"uomTypeCode\":\""+uomTypeCd+"\"," +
					"\"uomTypeName\":\""+uomTypeCd+"\"," +
					"\"effectiveDate\":\""+geopoliticalUnitOfMeasuresEffectiveDate+"\"," +
					"\"expirationDate\":\""+geopoliticalUnitOfMeasuresExpirationDate+"\"" +
					"}" +
					"]," +
					"\"geopoliticalHolidays\":[" +
					"{" +
					"\"holidayName\":\""+holidayName+"\"," +
					"\"holidayId\":\""+holidayName+"\"," +
					"\"effectiveDate\":\""+geopoliticalHolidaysEffectiveDate+"\"," +
					"\"expirationDate\":\""+geopoliticalHolidaysExpirationDate+"\"" +
					"}" +
					"]," +
					"\"geopoliticalAffiliations\":[" +
					"{" +
					"\"affiliationTypeCode\":\""+affilTypeCd+"\"," +
					"\"affiliationTypeName\":\""+affilTypeCd+"\"," +
					"\"affiliationTypeId\":\""+affilTypeCd+"\"," +
					"\"effectiveDate\":\""+geopoliticalAffiliationsEffectiveDate+"\"," +
					"\"expirationDate\":\""+geopoliticalAffiliationsExpirationDate+"\"" +
					"}" +
					"]," +
					/*"\"locales\":[" +
					"{" +
					"\"languageCode\":\""+localesLanguageCd+"\"," +
					"\"localeCode\":\""+localeCd+"\"," +
					"\"scriptCode\":\""+localesScriptCd+"\"," +
					"\"cldrVersionNumber\":\""+cldrVersionNumber+"\"," +
					"\"cldrVersionDate\":\""+cldrVersionDate+"\"," +
					"\"dateFullFormatDescription\":\""+dateFullFormatDescription+"\"," +
					"\"dateLongFormatDescription\":\""+dateLongFormatDescription+"\"," +
					"\"dateMediumFormatDescription\":\""+dateMediumFormatDescription+"\"," +
					"\"dateShortFormatDescription\":\""+dateShortFormatDescription+"\"," +
					"\"effectiveDate\":\""+localesEffectiveDate+"\"," +
					"\"expirationDate\":\""+localesExpirationDate+"\"" +
					"}" +
					"]," +*/
					"\"translationGeopoliticals\":[" +
					"{" +
					"\"languageCode\":\""+translationGeopoliticalsLanguageCd+"\"," +
					"\"scriptCode\":\""+translationGeopoliticalsScriptCd+"\"," +
					"\"translatedName\":\""+translationName+"\"," +
					"\"engLanguageName\":\""+translationName+"\"," +
					"\"versionNumber\":\""+versionNumber+"\"," +
					"\"versionDate\":\""+versionDate+"\"," +
					"\"effectiveDate\":\""+translationGeopoliticalsEffectiveDate+"\"," +
					"\"expirationDate\":\""+translationGeopoliticalsExpirationDate+"\"" +
					"}" +
					"]," +
					"\"geopoliticalType\":" +
					"{" +
					"\"geopoliticalTypeName\":\""+geopoliticalTypeName+"\"" +
					"}" +
					"}" +
					"}";

			return payload;

		}

	// Missing comma method

	public static String depnCntryRltspMissingCommaPostRequest(String userId, String depnCntryRltsp )
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"} \"dependentCountryRelationship\":{"
			+ "\"dependentRelationshipDescription\":\""+depnCntryRltsp+"\"}}";
		return payload;
	}

	public static String dayofweekCommaMissingPostRequest(String userId, String dayOfWeekNbr, String dayOfWeekShortName, String dayOfWeekFullName)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"dayOfWeek\":{"
			+ "\"dayOfWeekNumber\":"+dayOfWeekNbr+",\"dayOfWeekFullName\":\""+dayOfWeekFullName+"\" \"dayOfWeekShortName\":\""+dayOfWeekShortName+"\"}}";
		return payload;
	}

	public static String cntryOrgStdPostMissingCommaRequest(String userId, String countryCode, String countryShortName, String countryFullName, String orgStandardCode,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"countryOrgStdDTO\":{"
			+ "\"countryCode\":\""+countryCode+"\",\"countryShortName\":\""+countryShortName+"\",\"countryFullName\":\""+countryFullName+"\""
					+ ",\"organizationStandardCode\":\""+orgStandardCode+"\",\"effectiveDate\":\""+effectiveDate+"\" \"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}

	public static String stProvStdCommaMissingtRequest(String userId, String orgStdCd, String stProvCd, String stProvNm,
			 String effectiveDate, String expirationDate)
	{
		String payload = "{\"meta\":{\"userName\":\""+userId+"\"},\"stProvStd\":{"
			+ "\"organizationStandardCode\":\""+orgStdCd+"\",\"stateProvinceCode\":\""+stProvCd+"\",\"stateProvinceName\":\""+stProvNm+"\""
					+ ",\"effectiveDate\":\""+effectiveDate+"\" \"expirationDate\":\""+expirationDate+"\"}}";
		return payload;
	}


}


