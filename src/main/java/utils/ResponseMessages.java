package utils;

public class ResponseMessages {
	
	//***success messages
	public String geoTypePostSuccessMsg = "Geopolitical Type Details successfully saved with GeopoliticalTypeId :";
	public String geoTypePutSuccessMsg = "Geopolitical Type Details successfully updated with GeopoliticalTypeId :";
	public String geoRsTypePostSuccessMsg = "GeopoliticalRelationshipType Details successfully saved with GeopoliticalRelationshipTypeCd :";
	public String geoRsTypePutSuccessMsg = "GeopoliticalRelationshipType Details successfully updated with GeopoliticalRelationshipTypeCd :";
	public String geoOrgStdPostSuccessMsg = "GEOPLORGSTD Details successfully saved with OrgStdCd :";
	public String geoOrgStdPutSuccessMsg = "GEOPLORGSTD Details successfully updated with OrgStdCd :";
	public String depnCntryRltspPostSuccessMsg = "Dependent Country Relationship Details successfully saved with DependentRelationshipId :";
	public String depnCntryRltspPutSuccessMsg = "Dependent Country Relationship Details successfully updated with DependentRelationshipId :";
	public String holidayPostSuccessMsg = "Holiday Details successfully saved with HolidayId :";
	public String holidayPutSuccessMsg = "Holiday Details successfully updated with HolidayId :";
	public String uomTypePostSuccessMsg = "REFUOMTYPE Details successfully saved with uomTypeCd :";
	public String uomTypePutSuccessMsg = "REFUOMTYPE Details successfully updated with uomTypeCd :";
	public String scriptPostSuccessMsg = "REFSCRIPT Details successfully saved with scrptCd :";
	public String scriptPutSuccessMsg = "REFSCRIPT Details successfully updated with scrptCd :";
	public String dayOfWeekPostSuccessMsg = "Day Of Week Details successfully saved with DayOfWeekNumber :";
	public String dayOfWeekPutSuccessMsg = "Day of week Details successfully updated with DayOfWeekNumber :";
	public String monthOfYearPostSuccessMsg = "Month of Year Details successfully saved with MonthOfYearNumber :";
	public String monthOfYearPutSuccessMsg = "Month of Year Details successfully updated with MonthOfYearNumber :";
	public String countryOrgStdPostSuccessMsg = "CntryOrgStd Details successfully saved with OrgStdCd :";
	public String countryOrgStdPutSuccessMsg = "CntryOrgStd Details successfully updated with OrgStdCd :";
	public String geoRltspPostSuccessMsg = "GeopoliticalRelationship Details successfully saved with GeoplRltspCmptId :";
	public String geoRltspPutSuccessMsg = "GeopoliticalRelationship Details successfully updated with GeoplRltspCmptId :";
	public String stProvStdPostSuccessMsg = " State Province Details successfully saved with stProvCd :";
	public String stProvStdPutSuccessMsg = "State Province Details successfully updated with stProvCd :";
	public String affilTypePostSuccessMsg = "GeoplAffilType Details successfully saved with affilTypeId :";
	public String affilTypePutSuccessMsg = "GeoplAffilType Details successfully updated with affilTypeId :";
	public String langPostSuccessMsg = "RefLanguage Details successfully saved with langCd :";
	
	//***error messages
	public String invalidUrlMsg = "Not Found";
	public String metaBlankMsg = "Meta cannot be a null field!, ";	
	public String userNullMsg = "Username cannot be blank!, ";
	public String recordExistsMsg = "Record already exists.";
	public String invalidPutRequestMsg = "Request method 'PUT' not supported";
	public String exceedLimitMsg = "Not correct format for Id!";
	public String EffectiveDateBlankMsg = "Effective Date cannot be a null field!";
	public String userLenghtMsg = "userName cannot be greater than 25 characters, ";
	
	///geoType
	public String geoTypeNameBlankMsg = "GeopoliticalTypeName cannot be blank!, ";		
	public String geoTypeGeopoliticalTypeNameLimitMsg = "GeopoliticalTypeName cannot be greater than 50 characters!, ";
	public String geoTypeUserNameLimitMsg = "";
	
	//geoRsType
	public String geoRsTypeCodeBlankMsg = "GeopoliticalRelationshipTypeCd cannot be blank!, ";
	public String geoRsTypeExistingValue = "Record already exists.";
	public String geoRsTypeMetaNull = "Meta cannot be a null field!, ";
	public String geoRsTypeNullUser = "Username cannot be blank!, ";
	public String geoRsTypegeoRelTypeCdLengthMsg = "GeopoliticalRelationshipTypeCd cannot be greater than 20 characters!, ";
	public String geoRsTypegeopoRelTypeDescMsg = "AreaRelationshipTypeDescription cannot be greater than 100 characters!, ";
	public String geoRsTypeUserLenghtMsg = "";
	
	//***geoOrgStd
	public String geoOrgStdCodeBlankMsg = "Organization Standard Code cannot be a null or blank field!, ";
	public String geoOrgStdCodeNotInJson = "Organization Standard Code cannot be a null or blank field!, Organization Standard Code cannot be a null or blank field!, ";
	public String geoOrgStdCodeLengthMsg = "Organization Standard Code cannot be greater than 10 characters!, ";
	public String geoorgStdNmLengthMsg = "Organization Standard Name cannot be greater than 65 characters!, ";
	public String geoOrgStdCodeExistingValue = "Record already exists.";
	public String geoOrgStdUserLenghtMsg = "";
	
	//depnCntryRltsp
	public String depnCntryRltspDescBlankMsg = "DependentRelationshipDescription cannot be blank!, ";
	public String depnCntryRltspDescLimitMsg = "DependentRelationshipDescription cannot be greater than 65 characters!, ";
	public String depnCntryRltspUserNameLimitMsg = "";
	
	//holiday
	public String holidayNameBlankMsg = "Holiday Name cannot be blank!, ";
	public String holidayNameLimitMsg = "HolidayName cannot be greater than 65 characters, ";
	public String holidayDateParamTextLimitMsg = "Holiday date param text cannot be greater than 400 characters, "; 
	public String holidayUserNameLimitMsg = "";
	public String uomTypeCodeBlankMsg = "UOM Code cannot be a null field!, ";
	public String scriptCodeBlankMsg = "sciptCd cannot be a null field!, ";
	public String dayOfWeekNbrBlankMsg = "DOW Number cannot be a null field!, ";
	public String monthOfYearNbmrBlankMsg = "MOY Number cannot be a null field!, ";
	
	//UOM type
	public String UomTypeNmNullMsg = "Uom Type Name cannot be a blank or null field!, ";
	public String UomTypeCdNullMsg = "UOM Code cannot be a null field!, UOM Code cannot be a null field!, ";
	public String UomTypeDescNullMsg = "UOM Type Desc cannot be blank!, ";
	public String UomTypeCdLengthMsg = "UOM Type Code cannot be greater 10 characters!, ";
	public String UomTypeNmLengthMsg = "UOM Type Name cannot be greater 256 characters!, ";
	public String UomTypeDescLengthMsg = "UOM Type Desc cannot be greater 1000 characters!, ";
	
	//script
	public String scriptNameBlankMsg = "Script Name cannot be a blank or null field!, ";
	public String scriptCdBlankMsg = "scrptCd cannot be a null field!, ";
	public String scriptCdBlankMsg1 = "scrptCd cannot be a null field!, scrptCd cannot be a null field!, ";
	public String scriptCdLengthMsg = "Script Code cannot be greater than 18 characters!, ";
	public String scriptNameLengthMsg = "Script Name cannot be greater than 256 characters, ";
	public String scriptDescLengthMsg = "Script Description cannot be greater than 4000 characters, ";
	public String invalidUserLengthMsg = "";
	public String scriptNmBlankMsg = "Script Name cannot be a blank or null field!, Script Name cannot be a blank or null field!, ";
	
	//DoW
	//public String dayOfWeekShNmBlankMsg = "Numeric value required for field "+"\"dayOfweekNumber\"";
	public String dayOfWeekShNmBlankMsg = "DOW Short Name cannot be a null field!, ";
	public String dayOfWeekNbrLengthMsg = "Numeric value required for field \"dayOfweekNumber\"";
	public String dayOfWeekShNmLengthMsg = "DOW short name cannot be greater than 9 characters, ";
	public String dayOfWeekFNmLengthMsg = "DOW Full name cannot be greater than 256 characters, ";
	
	
	//monthOfYear
	public String monthOfYearNumberBlankMsg = "MOY Number cannot be a null field!, ";
	public String monthOfYearNumberLengthMsg = "Numeric value required for field \"monthOfYearNumber\"";
	public String monthOfYearShortLengthMsg = "Month Of Year short name cannot be greater than 18 characters, ";
	public String monthOfYearUserLenghtMsg = "";
	
	//***CntryOrgStd
	public String countryOrgStdContryCodeBlankMsg = "Country Code cannot be a null field!, ";
	public String countryOrgStdCntryShNmBlankMsg = "Country Short Name cannot be blank!, ";
	public String countryOrgStdOrgStdCdBlankMsg = "Organisation Standard code cannot be a null field!, ";
	public String blankEffectiveDateMsg = "Effective Date cannot be a null field!";
	public String blankExpirationDateMsg = "";
	public String CntryCdNotFoundMsg = "Country not found";
	public String OrgStdCdNotFoundMsg = "Organisation standard Code not existing";
	public String invalidEffectiveDateMsg = "Invalid Date Format";
	public String countryOrgStdContryCodeLengthMsg = "";
	public String countryOrgStdCntryShNmLengthMsg = "Country Short Name cannot be greater than 65 characters, "; 
	public String countryOrgStdCntryFNmLengthMsg = "Country Full Name cannot be greater than 120 characters, ";
	public String countryOrgStdOrgStdCdLengthMsg = "Org standard code cannot be greater than 10 characters, ";
	public String userLengthMsg = "";
	
	//***GeoRltsp
	public String geoRltspFromGeoIdBlankMsg = "Country not found";
	public String geoRltspToGeoIdBlankMsg = "State not found";
	public String geoRltspRelationshipTypeCodeBlankMsg = "Relationship type Code cannot be a null field!, ";
	public String geoRltspeffectiveDateBlankMsg = "Effective Date cannot be a null field!";
	public String geoRltspRelationshipTypeCodeInvalidMsg = "Geopolitical Entity not found";
	public String geoRltspnDateInvalidMsg = "Invalid Date Format";
	public String geoRltspExceedLimitMsg ="JSON parse error";
	public String geoRltspRelationshipTypeCodeExceedLimitMsg = "Relationship code  cannot be greater than 20 characters , "; 
	public String geoRltspUserLenghtMsg = "userName cannot be greater than 25 characters, ";
	
		
	//***StProvStd
	public String stProvStdOrgStdCdBlankMsg = "Organization Standard Code cannot be a null or blank field!, ";
	public String stProvStdstProvCdBlankMsg = "State Province Code cannot be blank!, ";
	public String stProvStdOrgStdCdNotFoundMsg = "Organization Standard Code not found";
	public String stProvStdOrgStdCdBlankMsg1 = "Organization Standard Code cannot be a null or blank field!, Organization Standard Code cannot be a null or blank field!, ";
	public String stProvStdOrgStdCdLengthMsg = "Organization Standard Code cannot be greater than 10 characters, ";
	public String stProvStdstProvCdLengthMsg = "State Province Standard Code cannot be greater than 10 characters, ";
	public String stProvStdstProvNmLengthMsg = "State Province name cannot be greater than 120 characters, ";
	
	//***AffilType
	public String affilTypeCodeBlankMsg = "affilTypeCode cannot be a null field!, ";
	
	
}
