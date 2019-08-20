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
	public String langPostSuccessMsg = "Language Details successfully saved with langCd :";
	public String langPutSuccessMsg = "Language Details successfully updated with langCd :";
	public String cntryPostSuccessMsg = "Country Details successfully saved with GeoplId :";
	public String cntryPutSuccessMsg = "Country Details successfully updated with GeoplId:";
	
	
	//***Get general success message
	public String getSuccessMsg = "Records Fetched Successfully";
	
	//***Get general error message
	public String getErrorMsg = "There are no records for the specified criteria";
	
	//***Get format error message
	public String getFormatErrorMsg = "Not correct format for Id!";
	
	//***error messages
	
	public String requiredFieldMsg = " is a required field";
	public String invalidUrlMsg = "Not Found";
	public String metaBlankMsg = " cannot be a null field!";	
	public String userNullMsg = "Username cannot be blank!, ";
	public String recordExistsMsg = "Record already exists.";
	public String invalidPutRequestMsg = "Request method 'PUT' not supported";
	public String exceedLimitMsg = "Not correct format for Id!";
	public String dateBlankMsg = " cannot be a null field!";
	public String userLenghtMsg = "User Name cannot be greater than 25 characters";
	public String recordNotFoundMsg = "Record not found";
	public String wrongFormatIdMsg = "Not correct format for Id!";
	public String lengthExceeds25Char = " cannot be greater than 25 characters";
	public String lengthExceeds10Char = " cannot be greater 10 characters!";
	public String lengthExceeds256Char = " cannot be greater 256 characters!";
	public String lengthExceeds1000Char = " cannot be greater 1000 characters!";
	public String lengthExceeds3Char = " cannot be greater than 3 characters";
	public String lengthExceeds256Char1 = " cannot be greater than 256 characters";
	public String lengthExceeds65Char = " cannot be greater than 65 characters";
	public String lengthExceeds10Char1 = " cannot be greater than 10 characters";
	public String lengthExceeds120Char = " cannot be greater than 120 characters";
	public String lengthExceeds38Char = "cannot be greater than 38 digits";
	public String lengthExceeds18Char = " cannot be greater than 18 characters";
	public String jsonParserErrorMsg = "JSON parse error: InvalidFormatException:  Cannot deserialize value of type `java.lang.Long` from String ";
	public String lengthExceeds9Char1=" cannot be greater than 9 characters";
	public String lengthExceeds50Char = " cannot be greater than 50 characters!";
	public String lengthExceeds256CharMsg1 =" cannot be greater than 256 characters";
	public String lengthExceeds20Char1="  cannot be greater than 20 characters ";
	public String lengthExceeds100Char1=" cannot be greater than 100 characters!";	
	public String countryNotFoundMsg = "Country not found";
	public String invalidDateMsg="Invalid Date Format";
	public String geoRltspeffectiveDateBlankMsg="Effective Date cannot be a null field!";
	public String requiredField = " is a required field";
	public String lengthExceeds18Char1 = " cannot be greater than 18 characters!";
	public String lengthExceeds4000Char1 = " cannot be greater than 4000 characters";
	public String metaValidationMsg = " cannot be a null field!";   
	public String EffectiveDateBlankMsg = "Effective Date cannot be a null field!";
	public String lengthExceeds65CharMsg = " cannot be greater than 65 characters";
	public String lengthExceeds400CharMsg = " cannot be greater than 400 characters";
	
	///geoType
	public String geoTypeNameBlankMsg = "Geopolitical Type Name not found null";		
	public String geoTypeInvalidUrl = "The request was rejected because the URL contained a potentially malicious String \"%25\"";
	
	//geoRltsp
	public String invalidToGeopoliticalIdMsg="State not found";
	public String invalidRelationshipTypeCodedMsg= "Relationship Type code not found";
	
	//geoRsType
	public String geoRsTypeCodeBlankMsg = "geopoliticalRelationshipTypeCd cannot be null, ";

	//***geoOrgStd
	public String geoOrgStdCodeBlankMsg = "orgStdCd cannot be null, ";
	public String geoOrgStdCodeNotInJson = "Organization Standard Code cannot be a null or blank field!, Organization Standard Code cannot be a null or blank field!, ";
	public String geoOrgStdCodeLengthMsg = "Organization Standard Code cannot be greater than 10 characters!, ";
	public String geoorgStdNmLengthMsg = "Organization Standard Name cannot be greater than 65 characters!, ";
	
	//depnCntryRltsp
	public String depnCntryRltspDescBlankMsg = "dependentRelationshipDescription cannot be null, ";
	public String depnCntryRltspDescLimitMsg = "DependentRelationshipDescription cannot be greater than 65 characters!, ";
	public String depnCntryRltspIdNotInDbMsg = "Not correct format for Id!";
	
	//holiday
	public String holidayNameBlankMsg = "holidayName cannot be null, ";
	
	//UOM type
	public String UomTypeNmNullMsg = "uomTypeNm cannot be null, ";
	public String UomTypeCdNullMsg = "uomTypeCd cannot be null, ";
	
	//script
	public String scriptNameBlankMsg = "scrptNm cannot be null, ";
	public String dayOfWeekShNmBlankMsg = "dayOfweekShortName cannot be null, ";
	
	//DOW
	public String dayOfWeekNbrLengthMsg = "Numeric value required for field \"dayOfweekNumber\"";
	
	public String countryOrgStdContryCodeBlankMsg = "Country Code cannot be a null field!, ";
	public String countryOrgStdCntryShNmBlankMsg = "Country Short Name cannot be blank!, ";
	public String countryOrgStdOrgStdCdBlankMsg = "Organisation Standard code cannot be a null field!, ";	
	public String OrgStdCdNotFoundMsg = "Organisation standard Code not existing";

	//***GeoRltsp
	public String geoRltspFromGeoIdBlankMsg = "geopoliticalTypeName cannot be null, ";
	
	//***StProvStd
	public String stProvStdOrgStdCdBlankMsg = "Organization Standard Code cannot be a null or blank field!, ";
	public String stProvStdstProvCdBlankMsg = "State Province Code cannot be blank!, ";
	public String stProvStdOrgStdCdNotFoundMsg = "Organization Standard Code not found";

	//***Country
	public String geopoliticalTypeNameBlanckMsg ="Geopolitical Type Name not found ";
	
}
