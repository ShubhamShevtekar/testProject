package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.v1.DbConnect;
import utils.v1.ExcelUtil;
import utils.v1.Miscellaneous;
import utils.v1.Queries;
import utils.v1.Reporting;
import utils.v1.ResponseMessages;
import utils.v1.RetrieveEndPoints;
import utils.v1.TestResultValidation;
import utils.v1.ValidationFields;
import wsMethods.v1.GetResponse;

public class CountryGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String geopoliticalId, countryShortName, orgStandardCode, targetDate, endDate, TestCaseDescription, scenarioType,
			userId, countryNumberCd, countryCode, threeCharacterCountryCode, independentFlag, postalFormatDescription,
			postalFlag, postalLength, firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName,
			internetDomainName, dependentRelationshipId, dependentCountryCd, countryEffectiveDate,
			countryExpirationDate, intialDialingPrefixCd, intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr,
			moblPhMaxLthNbr, moblPhMinLthNbr, countryDialingsEffectiveDate, countryDialingsExpirationDate,
			currencyNumberCd, currencyCd, minorUnitCd, moneyFormatDescription, currenciesEffectiveDate,
			currenciesExpirationDate, uomTypeCd, geopoliticalUnitOfMeasuresEffectiveDate,
			geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
			geopoliticalHolidaysExpirationDate, affilTypeId, affilTypeCd, geopoliticalAffiliationsEffectiveDate,
			geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd, cldrVersionNumber,
			cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
			dateShortFormatDescription, localesEffectiveDate, localesExpirationDate, translationGeopoliticalsLanguageCd,
			translationGeopoliticalsScriptCd, translationName, versionNumber, versionDate,
			translationGeopoliticalsEffectiveDate, translationGeopoliticalsExpirationDate, geopoliticalTypeName;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	Connection con;
	Statement stmt;
	static Logger logger = Logger.getLogger(CountryGraphQL.class);
	String actuatorGraphQLversion = "1.0.0";
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		String actuatorGraphQLVersionURL = RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName,
				level + ".graphQL.version");
		// actuatorGraphQLversion =resultValidation.versionValidation(fileName,
		// tokenKey, tokenVal,actuatorGraphQLVersionURL);
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}

	/*@AfterMethod
	public void after() {
		try {
			con.isClosed();
			con.close();
			
		} catch (SQLException e) {
			test.fail("DB connection close failed or connection not active: " + e);
		}
		System.gc();
		Runtime.getRuntime().gc();
	}*/

	@Test
	public void TC_01() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		testDataFields(scenarioName, testCaseID);
		// String payload = "{\"query\":\"{ countries(geopoliticalId :
		// \\\""+geopoliticalId+"\\\", countryCode :
		// \\\""+countryCode+"\\\",countryShortName :
		// \\\""+countryShortName+"\\\", orgStandardCode :
		// \\\""+orgStandardCode+"\\\",targetDate :
		// \\\""+targetDate+"\\\",endDate : \\\""+endDate+"\\\") {
		// geopoliticalId countryNumericCode countryCode
		// threeCharacterCountryCode independentFlag dependentRelationshipId
		// dependantCountryCode postalFormatDescription postalFlag postalLength
		// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
		// internetDomainName effectiveDate expirationDate countryDialings {
		// geopoliticalId internationalDialingPrefixCode
		// internationalDialingCode mobilePhoneMinimumLengthNumber
		// mobilePhoneMaximumLengthNumber landPhoneMinimumLengthNumber
		// landPhoneMaximumLengthNumber effectiveDate expirationDate }
		// currencies { geopoliticalId currencyNumericCode currencyCode
		// minorUnitCode moneyFormatDescription effectiveDate expirationDate }
		// geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId effectiveDate
		// expirationDate refUOMType { uomTypeCode uomTypeName uomTypeDesc } }
		// geopoliticalHolidays { geopoliticalId holidayId effectiveDate
		// expirationDate holiday { holidayId holidayName
		// holidayDateParameterText } } geopoliticalAffiliations { affilTypeId
		// geopoliticalId effectiveDate expirationDate
		// geoPoliticalAffiliatedType { affilTypeId affilTypeCode affilTypeName
		// } } locales { localeCode geopoliticalId languageCode scriptCode
		// dateFullFormatDescription dateLongFormatDescription
		// dateMediumFormatDescription dateShortFormatDescription
		// cldrVersionNumber cldrVersionDate effectiveDate expirationDate
		// languages { languageCode threeCharacterLanguageCode languageName
		// nativeScriptLanguageName scriptCode languageDescription } }
		// translatedGeopoliticals { geopoliticalId languageCode scriptCode
		// translatedName versionNumber versionDate effectiveDate expirationDate
		// } countryOrgStd { geopoliticalId countryShortName countryFullName
		// organizationStandardCode effectiveDate expirationDate
		// geoPoliticalOrganizationStandard { organizationStandardCode
		// organizationStandardName } } geopoliticalAreas { geopoliticalId
		// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType {
		// geopoliticalTypeId geopoliticalTypeName } }
		// dependentCountryRelationship { dependentRelationshipId
		// dependentRelationshipDescription } stateProvStndList { geopoliticalId
		// organizationStandardCode stateProvinceCode stateProvinceName
		// effectiveDate expirationDate } }}\"}";
		String payload = "{\"query\":\"{countries (targetDate : \\\"2020-08-01\\\",endDate : \\\"9999-12-31\\\") {geopoliticalId countryNumericCode countryCode threeCharacterCountryCode independentFlag dependentRelationshipId dependentCountryCode postalFormatDescription postalFlag postalLength firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName internetDomainName internationalDialingCode landPhoneMaximumLength landPhoneMinimumLength mobilePhoneMaximumLength mobilePhoneMinimumLength phoneNumberFormatPattern  effectiveDate expirationDate currencies { currencyNumericCode currencyCode minorUnitCode moneyFormatDescription effectiveDate expirationDate } countryOrganizationStandards {organizationStandardCode organizationStandardName countryFullName countryShortName effectiveDate expirationDate } addressLabels {localeCode brandAddressLineDescription  addressLineNumber applicable } }}\"}";
				
		countryWithGeoplId(testCaseID, payload);
	}

	@Test
	public void TC_02() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// String payload = "{\"query\":\"{ countries { geopoliticalId
		// countryNumericCode countryCode threeCharacterCountryCode
		// independentFlag dependentRelationshipId dependantCountryCode
		// postalFormatDescription postalFlag postalLength firstWorkWeekDayName
		// lastWorkWeekDayName weekendFirstDayName internetDomainName
		// effectiveDate expirationDate countryDialings { geopoliticalId
		// internationalDialingPrefixCode internationalDialingCode
		// mobilePhoneMinimumLengthNumber mobilePhoneMaximumLengthNumber
		// landPhoneMinimumLengthNumber landPhoneMaximumLengthNumber
		// effectiveDate expirationDate } currencies { geopoliticalId
		// currencyNumericCode currencyCode minorUnitCode moneyFormatDescription
		// effectiveDate expirationDate } geopoliticalUnitOfMeasures {
		// uomTypeCode geopoliticalId effectiveDate expirationDate refUOMType {
		// uomTypeCode uomTypeName uomTypeDesc } } geopoliticalHolidays {
		// geopoliticalId holidayId effectiveDate expirationDate holiday {
		// holidayId holidayName holidayDateParameterText } }
		// geopoliticalAffiliations { affilTypeId geopoliticalId effectiveDate
		// expirationDate geoPoliticalAffiliatedType { affilTypeId affilTypeCode
		// affilTypeName } } locales { localeCode geopoliticalId languageCode
		// scriptCode dateFullFormatDescription dateLongFormatDescription
		// dateMediumFormatDescription dateShortFormatDescription
		// cldrVersionNumber cldrVersionDate effectiveDate expirationDate
		// languages { languageCode threeCharacterLanguageCode languageName
		// nativeScriptLanguageName scriptCode languageDescription } }
		// translatedGeopoliticals { geopoliticalId languageCode scriptCode
		// translatedName versionNumber versionDate effectiveDate expirationDate
		// } countryOrgStd { geopoliticalId countryShortName countryFullName
		// organizationStandardCode effectiveDate expirationDate
		// geoPoliticalOrganizationStandard { organizationStandardCode
		// organizationStandardName } } geopoliticalAreas { geopoliticalId
		// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType {
		// geopoliticalTypeId geopoliticalTypeName } }
		// dependentCountryRelationship { dependentRelationshipId
		// dependentRelationshipDescription } stateProvStndList { geopoliticalId
		// organizationStandardCode stateProvinceCode stateProvinceName
		// effectiveDate expirationDate } }}\"}";
		//String payload = "{\"query\":\"{  countries {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    countryOrganizationStandards {      geopoliticalId      countryShortName      countryFullName      organizationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {      organizationStandardCode        organizationStandardName      }    }    addressLabels {      geopoliticalId      localeCode      addressLineNumber      brandAddressLineDescription      applicable    }  }}\",\"variables\":null,\"operationName\":null}";
		String payload = "{\"query\":\"{  countries {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    currencies {      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    countryOrganizationStandards {      countryShortName      countryFullName      organizationStandardCode       organizationStandardName      effectiveDate      expirationDate    }    addressLabels {      localeCode      addressLineNumber      brandAddressLineDescription      applicable    }  }}\"}";
		countryNoParameters(testCaseID, payload);
	}

	@Test
	public void TC_03() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		// String payload = "{\"query\":\"{ countries(geopoliticalId :
		// \\\""+geopoliticalId+"\\\",targetDate :
		// \\\""+targetDate+"\\\",endDate : \\\""+endDate+"\\\") {
		// geopoliticalId countryNumericCode countryCode
		// threeCharacterCountryCode independentFlag dependentRelationshipId
		// dependantCountryCode postalFormatDescription postalFlag postalLength
		// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
		// internetDomainName effectiveDate expirationDate countryDialings {
		// geopoliticalId internationalDialingPrefixCode
		// internationalDialingCode mobilePhoneMinimumLengthNumber
		// mobilePhoneMaximumLengthNumber landPhoneMinimumLengthNumber
		// landPhoneMaximumLengthNumber effectiveDate expirationDate }
		// currencies { geopoliticalId currencyNumericCode currencyCode
		// minorUnitCode moneyFormatDescription effectiveDate expirationDate }
		// geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId effectiveDate
		// expirationDate refUOMType { uomTypeCode uomTypeName uomTypeDesc } }
		// geopoliticalHolidays { geopoliticalId holidayId effectiveDate
		// expirationDate holiday { holidayId holidayName
		// holidayDateParameterText } } geopoliticalAffiliations { affilTypeId
		// geopoliticalId effectiveDate expirationDate
		// geoPoliticalAffiliatedType { affilTypeId affilTypeCode affilTypeName
		// } } locales { localeCode geopoliticalId languageCode scriptCode
		// dateFullFormatDescription dateLongFormatDescription
		// dateMediumFormatDescription dateShortFormatDescription
		// cldrVersionNumber cldrVersionDate effectiveDate expirationDate
		// languages { languageCode threeCharacterLanguageCode languageName
		// nativeScriptLanguageName scriptCode languageDescription } }
		// translatedGeopoliticals { geopoliticalId languageCode scriptCode
		// translatedName versionNumber versionDate effectiveDate expirationDate
		// } countryOrgStd { geopoliticalId countryShortName countryFullName
		// organizationStandardCode effectiveDate expirationDate
		// geoPoliticalOrganizationStandard { organizationStandardCode
		// organizationStandardName } } geopoliticalAreas { geopoliticalId
		// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType {
		// geopoliticalTypeId geopoliticalTypeName } }
		// dependentCountryRelationship { dependentRelationshipId
		// dependentRelationshipDescription } stateProvStndList { geopoliticalId
		// organizationStandardCode stateProvinceCode stateProvinceName
		// effectiveDate expirationDate } }}\"}";
		String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId + "\\\",targetDate : \\\""
				+ targetDate + "\\\",endDate : \\\"" + endDate
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    countryOrganizationStandards {      geopoliticalId      countryShortName      countryFullName      organizationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {      organizationStandardCode        organizationStandardName      }    }    addressLabels {      geopoliticalId      localeCode      addressLineNumber      brandAddressLineDescription      applicable    }  }}\",\"variables\":null,\"operationName\":null}";
		countryWithGeoplId(testCaseID, payload);
	}

	@Test
	public void TC_04() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		// String payload = "{\"query\":\"{ countries(geopoliticalId :
		// \\\""+geopoliticalId+"\\\") { geopoliticalId countryNumericCode
		// countryCode threeCharacterCountryCode independentFlag
		// dependentRelationshipId dependantCountryCode postalFormatDescription
		// postalFlag postalLength firstWorkWeekDayName lastWorkWeekDayName
		// weekendFirstDayName internetDomainName effectiveDate expirationDate
		// currencies { geopoliticalId currencyNumericCode currencyCode
		// minorUnitCode moneyFormatDescription effectiveDate expirationDate }
		// countryOrgStd { geopoliticalId countryShortName countryFullName
		// organizationStandardCode effectiveDate expirationDate
		// geoPoliticalOrganizationStandard { organizationStandardCode
		// organizationStandardName } } }}\"}";
		String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    countryOrganizationStandards {      geopoliticalId      countryShortName      countryFullName      organizationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {      organizationStandardCode        organizationStandardName      }    }    addressLabels {      geopoliticalId      localeCode      addressLineNumber      brandAddressLineDescription      applicable    }  }}\",\"variables\":null,\"operationName\":null}";
		countryWithGeoplIdCurrenciesCntryOrgStd(testCaseID, payload);
	}

	@Test
	public void TC_05() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLengthNumber      mobilePhoneMaximumLengthNumber      landPhoneMinimumLengthNumber      landPhoneMaximumLengthNumber      effectiveDate      expirationDate    }     geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }      }}\"}";

		countryWithGeoplIdCntryDialingsGeoplUom(testCaseID, payload);
	}

	@Test
	public void TC_06() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate       geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }     locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }      }}\"}";
		countryWithGeopldHolidaysLocales(testCaseID, payload);
	}

	@Test
	public void TC_07() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		testDataFields(scenarioName, testCaseID);
		String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate     geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }      translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }       geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }   }}\"}";
		countryWithGeopldGeoplAffilTrnslGeoplType(testCaseID, payload);
	}

	@Test
	public void TC_08() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		testDataFields(scenarioName, testCaseID);
		// String payload = "{\"query\":\"{ countries(geopoliticalId :
		// \\\""+geopoliticalId+"\\\", countryCode : \\\""+countryCode+"\\\") {
		// geopoliticalId countryNumericCode countryCode
		// threeCharacterCountryCode independentFlag dependentRelationshipId
		// dependantCountryCode postalFormatDescription postalFlag postalLength
		// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
		// internetDomainName effectiveDate expirationDate currencies {
		// geopoliticalId currencyNumericCode currencyCode minorUnitCode
		// moneyFormatDescription effectiveDate expirationDate } countryOrgStd {
		// geopoliticalId countryShortName countryFullName
		// organizationStandardCode effectiveDate expirationDate
		// geoPoliticalOrganizationStandard { organizationStandardCode
		// organizationStandardName } } }}\"}";
		String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
				+ "\\\", countryCode : \\\"" + countryCode
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    countryOrganizationStandards {      geopoliticalId      countryShortName      countryFullName      organizationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {      organizationStandardCode        organizationStandardName      }    }    addressLabels {      geopoliticalId      localeCode      addressLineNumber      brandAddressLineDescription      applicable    }  }}\",\"variables\":null,\"operationName\":null}";
		countryWithGeoplIdCurrenciesCntryOrgStd(testCaseID, payload);
	}

	@Test
	public void TC_09() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		testDataFields(scenarioName, testCaseID);
		// String payload = "{\"query\":\"{ countries(geopoliticalId :
		// \\\""+geopoliticalId+"\\\", countryCode : \\\""+countryCode+"\\\") {
		// geopoliticalId countryNumericCode countryCode
		// threeCharacterCountryCode independentFlag dependentRelationshipId
		// dependantCountryCode postalFormatDescription postalFlag postalLength
		// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
		// internetDomainName effectiveDate expirationDate }}\"}";
		// countryNoParameters(testCaseID,payload);
		String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
				+ "\\\", countryCode : \\\"" + countryCode
				+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    }}\",\"variables\":null,\"operationName\":null}";
		countryWithGeoplId(testCaseID, payload);
	}

	@Test
	public void TC_10() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			// String payload = "{\"query\":\"{ countries(geopoliticalId :
			// \\\""+geopoliticalId+"\\\") { geopoliticalId countryNumericCode
			// countryCode threeCharacterCountryCode independentFlag
			// dependentRelationshipId dependantCountryCode
			// postalFormatDescription postalFlag postalLength
			// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
			// internetDomainName effectiveDate expirationDate countryDialings {
			// geopoliticalId internationalDialingPrefixCode
			// internationalDialingCode mobilePhoneMinimumLengthNumber
			// mobilePhoneMaximumLengthNumber landPhoneMinimumLengthNumber
			// landPhoneMaximumLengthNumber effectiveDate expirationDate }
			// currencies { geopoliticalId currencyNumericCode currencyCode
			// minorUnitCode moneyFormatDescription effectiveDate expirationDate
			// } geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId
			// effectiveDate expirationDate refUOMType { uomTypeCode uomTypeName
			// uomTypeDesc } } geopoliticalHolidays { geopoliticalId holidayId
			// effectiveDate expirationDate holiday { holidayId holidayName
			// holidayDateParameterText } } geopoliticalAffiliations {
			// affilTypeId geopoliticalId effectiveDate expirationDate
			// geoPoliticalAffiliatedType { affilTypeId affilTypeCode
			// affilTypeName } } locales { localeCode geopoliticalId
			// languageCode scriptCode dateFullFormatDescription
			// dateLongFormatDescription dateMediumFormatDescription
			// dateShortFormatDescription cldrVersionNumber cldrVersionDate
			// effectiveDate expirationDate languages { languageCode
			// threeCharacterLanguageCode languageName nativeScriptLanguageName
			// scriptCode languageDescription } } translatedGeopoliticals {
			// geopoliticalId languageCode scriptCode translatedName
			// versionNumber versionDate effectiveDate expirationDate }
			// countryOrgStd { geopoliticalId countryShortName countryFullName
			// organizationStandardCode effectiveDate expirationDate
			// geoPoliticalOrganizationStandard { organizationStandardCode
			// organizationStandardName } } geopoliticalAreas { geopoliticalId
			// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType
			// { geopoliticalTypeId geopoliticalTypeName } }
			// dependentCountryRelationship { dependentRelationshipId
			// dependentRelationshipDescription } stateProvStndList {
			// geopoliticalId organizationStandardCode stateProvinceCode
			// stateProvinceName effectiveDate expirationDate } }}\"}";
			String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    }}\",\"variables\":null,\"operationName\":null}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (responseRows.size() == 0) {

					logger.info("No records are getting received in response when sending the invalid geopolitical Id");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("No records are getting received in response when sending the invalid geopolitical Id");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp == null) {
					logger.error("Response validation failed as timestamp not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp not present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_11() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			// String payload = "{\"query\":\"{ countries(geopoliticalId :
			// \\\""+geopoliticalId+"\\\",targetDate :
			// \\\""+targetDate+"\\\",endDate : \\\""+endDate+"\\\") {
			// geopoliticalId countryNumericCode countryCode
			// threeCharacterCountryCode independentFlag dependentRelationshipId
			// dependantCountryCode postalFormatDescription postalFlag
			// postalLength firstWorkWeekDayName lastWorkWeekDayName
			// weekendFirstDayName internetDomainName effectiveDate
			// expirationDate countryDialings { geopoliticalId
			// internationalDialingPrefixCode internationalDialingCode
			// mobilePhoneMinimumLengthNumber mobilePhoneMaximumLengthNumber
			// landPhoneMinimumLengthNumber landPhoneMaximumLengthNumber
			// effectiveDate expirationDate } currencies { geopoliticalId
			// currencyNumericCode currencyCode minorUnitCode
			// moneyFormatDescription effectiveDate expirationDate }
			// geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId
			// effectiveDate expirationDate refUOMType { uomTypeCode uomTypeName
			// uomTypeDesc } } geopoliticalHolidays { geopoliticalId holidayId
			// effectiveDate expirationDate holiday { holidayId holidayName
			// holidayDateParameterText } } geopoliticalAffiliations {
			// affilTypeId geopoliticalId effectiveDate expirationDate
			// geoPoliticalAffiliatedType { affilTypeId affilTypeCode
			// affilTypeName } } locales { localeCode geopoliticalId
			// languageCode scriptCode dateFullFormatDescription
			// dateLongFormatDescription dateMediumFormatDescription
			// dateShortFormatDescription cldrVersionNumber cldrVersionDate
			// effectiveDate expirationDate languages { languageCode
			// threeCharacterLanguageCode languageName nativeScriptLanguageName
			// scriptCode languageDescription } } translatedGeopoliticals {
			// geopoliticalId languageCode scriptCode translatedName
			// versionNumber versionDate effectiveDate expirationDate }
			// countryOrgStd { geopoliticalId countryShortName countryFullName
			// organizationStandardCode effectiveDate expirationDate
			// geoPoliticalOrganizationStandard { organizationStandardCode
			// organizationStandardName } } geopoliticalAreas { geopoliticalId
			// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType
			// { geopoliticalTypeId geopoliticalTypeName } }
			// dependentCountryRelationship { dependentRelationshipId
			// dependentRelationshipDescription } stateProvStndList {
			// geopoliticalId organizationStandardCode stateProvinceCode
			// stateProvinceName effectiveDate expirationDate } }}\"}";
			String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
					+ "\\\",targetDate : \\\"" + targetDate + "\\\",endDate : \\\"" + endDate
					+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    }}\",\"variables\":null,\"operationName\":null}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (responseRows.size() == 0) {

					logger.info(
							"No records are getting received in response when sending the effective and expiration data range not matching with any records");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"No records are getting received in response when sending the effective and expiration data range not matching with any records");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp == null) {
					logger.error("Response validation failed as timestamp not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp not present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_12() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			// String payload = "{\"query\":\"{ countries(geopoliticalId :
			// \\\""+geopoliticalId+"\\\") { geopliticalId countryNumericCode
			// countryCode threeCharacterCountryCode independentFlag
			// dependentRelationshipId dependantCountryCode
			// postalFormatDescription postalFlag postalLength
			// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
			// internetDomainName effectiveDate expirationDate countryDialings {
			// geopoliticalId internationalDialingPrefixCode
			// internationalDialingCode mobilePhoneMinimumLengthNumber
			// mobilePhoneMaximumLengthNumber landPhoneMinimumLengthNumber
			// landPhoneMaximumLengthNumber effectiveDate expirationDate }
			// currencies { geopoliticalId currencyNumericCode currencyCode
			// minorUnitCode moneyFormatDescription effectiveDate expirationDate
			// } geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId
			// effectiveDate expirationDate refUOMType { uomTypeCode uomTypeName
			// uomTypeDesc } } geopoliticalHolidays { geopoliticalId holidayId
			// effectiveDate expirationDate holiday { holidayId holidayName
			// holidayDateParameterText } } geopoliticalAffiliations {
			// affilTypeId geopoliticalId effectiveDate expirationDate
			// geoPoliticalAffiliatedType { affilTypeId affilTypeCode
			// affilTypeName } } locales { localeCode geopoliticalId
			// languageCode scriptCode dateFullFormatDescription
			// dateLongFormatDescription dateMediumFormatDescription
			// dateShortFormatDescription cldrVersionNumber cldrVersionDate
			// effectiveDate expirationDate languages { languageCode
			// threeCharacterLanguageCode languageName nativeScriptLanguageName
			// scriptCode languageDescription } } translatedGeopoliticals {
			// geopoliticalId languageCode scriptCode translatedName
			// versionNumber versionDate effectiveDate expirationDate }
			// countryOrgStd { geopoliticalId countryShortName countryFullName
			// organisationStandardCode effectiveDate expirationDate
			// geoPoliticalOrganizationStandard { organisationStandardCode
			// organisationStandardName } } geopoliticalAreas { geopoliticalId
			// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType
			// { geopoliticalTypeId geopoliticalTypeName } }
			// dependentCountryRelationship { dependentRelationshipId
			// dependentRelationshipDescription } stateProvStndList {
			// geopoliticalId organisationStandardCode stateProvinceCode
			// stateProvinceName effectiveDate expirationDate } }}\"}";
			String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId1    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    }}\",\"variables\":null,\"operationName\":null}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			String errorMsg1 = js.getString("errors[0].error");
			String errorMsg2 = js.getString("errors[0].message");

			int Wscode = res.statusCode();
			String expectMessage = resMsgs.countryInvalidAttributeGraphQLMsg1;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.equals("ValidationError") && errorMsg2.equals(expectMessage)) {

					logger.info(
							"No records are getting received in response when sending the invalid geopolitical attribute name");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"No records are getting received in response when sending the invalid geopolitical attribute name");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_13() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			// String payload = "{\"query\":\"{ countries(geopoliticalId :
			// \\\""+geopoliticalId+"\\\") { geopliticalId countryNumercCode
			// countryCode threeCharacterCountryCode independentFlag
			// dependentRelationshipId dependantCountryCode
			// postalFormatDescription postalFlag postalLength
			// firstWorkWeekDayName lastWorkWeekDayName weekendFirstDayName
			// internetDomainName effectiveDate expirationDate countryDialings {
			// geopoliticalId internationalDialingPrefixCode
			// internationalDialingCode mobilePhoneMinimumLengthNumber
			// mobilePhoneMaximumLengthNumber landPhoneMinimumLengthNumber
			// landPhoneMaximumLengthNumber effectiveDate expirationDate }
			// currencies { geopoliticalId currencyNumericCode currencyCode
			// minorUnitCode moneyFormatDescription effectiveDate expirationDate
			// } geopoliticalUnitOfMeasures { uomTypeCode geopoliticalId
			// effectiveDate expirationDate refUOMType { uomTypeCode uomTypeName
			// uomTypeDesc } } geopoliticalHolidays { geopoliticalId holidayId
			// effectiveDate expirationDate holiday { holidayId holidayName
			// holidayDateParameterText } } geopoliticalAffiliations {
			// affilTypeId geopoliticalId effectiveDate expirationDate
			// geoPoliticalAffiliatedType { affilTypeId affilTypeCode
			// affilTypeName } } locales { localeCode geopoliticalId
			// languageCode scriptCode dateFullFormatDescription
			// dateLongFormatDescription dateMediumFormatDescription
			// dateShortFormatDescription cldrVersionNumber cldrVersionDate
			// effectiveDate expirationDate languages { languageCode
			// threeCharacterLanguageCode languageName nativeScriptLanguageName
			// scriptCode languageDescription } } translatedGeopoliticals {
			// geopoliticalId languageCode scriptCode translatedName
			// versionNumber versionDate effectiveDate expirationDate }
			// countryOrgStd { geopoliticalId countryShortName countryFullName
			// organisationStandardCode effectiveDate expirationDate
			// geoPoliticalOrganizationStandard { organisationStandardCode
			// organisationStandardName } } geopoliticalAreas { geopoliticalId
			// geopoliticalTypeId effectiveDate expirationDate geoPoliticalType
			// { geopoliticalTypeId geopoliticalTypeName } }
			// dependentCountryRelationship { dependentRelationshipId
			// dependentRelationshipDescription } stateProvStndList {
			// geopoliticalId organisationStandardCode stateProvinceCode
			// stateProvinceName effectiveDate expirationDate } }}\"}";
			String payload = "{\"query\":\"{  countries (geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId1    countryNumericCode1    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependentCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    phoneNumberFormatPattern    internationalDialingCode    landPhoneMinimumLength    landPhoneMaximumLength    mobilePhoneMinimumLength    mobilePhoneMaximumLength    }}\",\"variables\":null,\"operationName\":null}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].error"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage1 = resMsgs.countryInvalidAttributeGraphQLMsg1;
			String expectMessage2 = resMsgs.countryInvalidAttributeGraphQLMsg3;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if ((errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage1))
						&& (errorMsg1.get(1).equals("ValidationError") && errorMsg2.get(1).equals(expectMessage2))) {

					logger.info(
							"No records are getting received in response when sending the invalid geopolitical and countryNumericCode attribute name");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"No records are getting received in response when sending the invalid geopolitical and countryNumericCode attribute name");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage1 + "and" + expectMessage2);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", expectMessage1 + "and" + expectMessage2);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_14() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLengthNumber      mobilePhoneMaximumLengthNumber      landPhoneMinimumLengthNumber      landPhoneMaximumLengthNumber      effectiveDate      expirationDate    }    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }    geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }    geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }    locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }    translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }    countryOrgStd {      geopoliticalId      countryShortName      countryFullName      organisationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {        organisationStandardCode        organisationStandardName      }    }    geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }    dependentCountryRelationship {      dependentRelationshipId      dependentRelationshipDescription    }    stateProvStndList {      geopoliticalId      organisationStandardCode      stateProvinceCode      stateProvinceName      effectiveDate      expirationDate    }  }}\"}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			String expiredToken = "v1%3AAPP3534861%3ACNs7wqTWQDe1xJivTxQcPl9%2Bb94XKxfVKC9WQbULqn5hKunN9PKQwv%2BE7ZXK%2FQwqpsf66XzflXZVcQOpMk%2BtufNG3awVeYy9FQeqY%2Btosnt7ONkSHd8I3sIUXHEEuVXEBKJe1pUoVOauy1BvIPMQeYDP2HmxtaiZ5zlXuu2nXI4%3D%3AAPP3534861";
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], expiredToken, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
			String errorMsg1 = js.getString("meta.message.internalMessage");
			String	errorMsg2 = js.getString("errors.message");

	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.countryExpiredTokenGraphQLMsg;
	        String meta = js.getString("meta");
	        String timestamp = js.getString("meta.timestamp");
	        if(Wscode == 401)
		    {
	        	logger.info("Response status code 401 validation passed: "+Wscode);
	        	test.pass("Response status code 401 validation passed: "+Wscode);
	        	ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
	        	//***error message validation

	        	if(errorMsg1.equals("Security Error") && errorMsg2.contains(expectMessage))
				{

	        		logger.info("No records are getting received in response when sending the expired token");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("No records are getting received in response when sending the expired token");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoTypeName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
			} else {

				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_15() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLengthNumber      mobilePhoneMaximumLengthNumber      landPhoneMinimumLengthNumber      landPhoneMaximumLengthNumber      effectiveDate      expirationDate    }    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }    geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }    geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }    locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }    translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }    countryOrgStd {      geopoliticalId      countryShortName      countryFullName      organisationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {        organisationStandardCode        organisationStandardName      }    }    geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }    dependentCountryRelationship {      dependentRelationshipId      dependentRelationshipDescription    }    stateProvStndList {      geopoliticalId      organisationStandardCode      stateProvinceCode      stateProvinceName      effectiveDate      expirationDate    }  }}\"}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			String invalidToken = "v1%3AAPP3534861%3AX9Z6LxTsQaqGSBgYt75nuRYV6RxUd2HQqrTcnlebLHKAK8Ohv8yB0jn0uryBIkdLkuFjZfNA5jjL%2FHd%2B3PHx9u36ozad4QEKz2Ag7P71uBX6xvSqmpEM1pRdBpcKXGGcwQ4JPSdDXX15Av%2FH3pUJoVZbgfKuBizus%2F4jhk9BGA%3D%3AAPP3534862";
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], invalidToken, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
			String errorMsg1 = js.getString("meta.message.internalMessage");
			String	errorMsg2 = js.getString("errors.message");

	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.countryInvalidTokenGraphQLMsg;
	        String meta = js.getString("meta");
	        String timestamp = js.getString("meta.timestamp");
	        if(Wscode == 401)
		    {
	        	logger.info("Response status code 401 validation passed: "+Wscode);
	        	test.pass("Response status code 401 validation passed: "+Wscode);
	        	ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
	        	//***error message validation

	        	if(errorMsg1.equals("Security Error") && errorMsg2.contains(expectMessage))
				{

	        		logger.info("No records are getting received in response when sending the invalid token");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("No records are getting received in response when sending the invalid token");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoTypeName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
			} else {

				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}
	
	@Test
	public void TC_16() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLengthNumber      mobilePhoneMaximumLengthNumber      landPhoneMinimumLengthNumber      landPhoneMaximumLengthNumber      effectiveDate      expirationDate    }    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }    geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }    geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }    locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }    translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }    countryOrgStd {      geopoliticalId      countryShortName      countryFullName      organisationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {        organisationStandardCode        organisationStandardName      }    }    geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }    dependentCountryRelationship {      dependentRelationshipId      dependentRelationshipDescription    }    stateProvStndList {      geopoliticalId      organisationStandardCode      stateProvinceCode      stateProvinceName      effectiveDate      expirationDate    }  }}\"}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			String invalidToken = "v1%3AAPP3534861%3AX9Z6LxTsQaqGSBgYt75nuRYV6RxUd2HQqrTcnlebLHKAK8Ohv8yB0jn0uryBIkdLkuFjZfNA5jjL%2FHd%2B3PHx9u36ozad4QEKz2Ag7P71uBX6xvSqmpEM1pRdBpcKXGGcwQ4JPSdDXX15Av%2FH3pUJoVZbgfKuBizus%2F4jhk9BGA%3D%3AAPP3534862";
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], invalidToken, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.missingHTTPHeaderInRequestMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 401 && meta != null) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
	        	//***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {

	        		logger.info("No records are getting received in response when sending blank http header");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("No records are getting received in response when sending the invalid token");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoTypeName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
			} else {

				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}
	
	
	@Test
	public void TC_17() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = "{\"query\":\"{  countries(geopoliticalId : \\\"" + geopoliticalId
					+ "\\\") {    geopoliticalId    countryNumericCode    countryCode    threeCharacterCountryCode    independentFlag    dependentRelationshipId    dependantCountryCode    postalFormatDescription    postalFlag    postalLength    firstWorkWeekDayName    lastWorkWeekDayName    weekendFirstDayName    internetDomainName    effectiveDate    expirationDate    countryDialings {      geopoliticalId      internationalDialingPrefixCode      internationalDialingCode      mobilePhoneMinimumLengthNumber      mobilePhoneMaximumLengthNumber      landPhoneMinimumLengthNumber      landPhoneMaximumLengthNumber      effectiveDate      expirationDate    }    currencies {      geopoliticalId      currencyNumericCode      currencyCode      minorUnitCode      moneyFormatDescription      effectiveDate      expirationDate    }    geopoliticalUnitOfMeasures {      uomTypeCode      geopoliticalId      effectiveDate      expirationDate      refUOMType {        uomTypeCode        uomTypeName        uomTypeDesc      }    }    geopoliticalHolidays {      geopoliticalId      holidayId      effectiveDate      expirationDate      holiday {        holidayId        holidayName        holidayDateParameterText      }    }    geopoliticalAffiliations {      affilTypeId      geopoliticalId      effectiveDate      expirationDate      geoPoliticalAffiliatedType {        affilTypeId        affilTypeCode        affilTypeName      }    }    locales {      localeCode      geopoliticalId      languageCode      scriptCode      dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      languages {        languageCode        threeCharacterLanguageCode        languageName        nativeScriptLanguageName        scriptCode        languageDescription      }    }    translatedGeopoliticals {      geopoliticalId      languageCode      scriptCode      translatedName      versionNumber      versionDate      effectiveDate      expirationDate    }    countryOrgStd {      geopoliticalId      countryShortName      countryFullName      organisationStandardCode      effectiveDate      expirationDate      geoPoliticalOrganizationStandard {        organisationStandardCode        organisationStandardName      }    }    geopoliticalAreas {      geopoliticalId      geopoliticalTypeId      effectiveDate      expirationDate      geoPoliticalType {        geopoliticalTypeId        geopoliticalTypeName      }    }    dependentCountryRelationship {      dependentRelationshipId      dependentRelationshipDescription    }    stateProvStndList {      geopoliticalId      organisationStandardCode      stateProvinceCode      stateProvinceName      effectiveDate      expirationDate    }  }}\"}";
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], "", getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.missingHTTPHeaderInRequestMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 401 && meta != null) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
	        	//***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {

	        		logger.info("No records are getting received in response when sending blank http header");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("No records are getting received in response when sending the invalid token");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoTypeName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
			} else {

				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCd" + expectMessage);
				Assert.fail("Test Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithGeoplId(String testCaseID, String payload) {
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			// PostMethod.cntryGraphQLRequest(geopoliticalId,countryCd/*,countryShortName,orgStandardCode,dependentCountryCd,targetDate,endDate*/);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			String actualRespVersionNum = js.getString("meta.version");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGetQuery(geopoliticalId, targetDate, endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				// List<String> getResultDB =
				// DbConnect.getResultSetFor(countryGetQuery, fields, fileName,
				// testCaseID);
				List<String> getResultDB = new ArrayList<>();
				try {
					stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet result = stmt.executeQuery(countryGetQuery);
					result.last();
					result.beforeFirst();
					String checkNull = null;
					while (result.next()) {
						for (int d = 0; d < fields.size(); d++) {
							checkNull = result.getString(fields.get(d));
							if (StringUtils.isBlank(checkNull)) {
								checkNull = "";
							}
							getResultDB.add(checkNull.trim());
						}
					}
					stmt.close();
				} catch (SQLException e) {
					ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
							"DB Connection Exception: " + e.toString());
					test.fail("DB connection failed: " + e);
					Assert.fail("Test Failed");
				}

				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = responseRows.size() - 1; i >= 0; i = i - 1) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internationalDialingCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internationalDialingCode"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMinimumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					} else {
						int z = 1;
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							for (int i = 0; i < getResponseRows.size(); i = i + fields.size()) {
								if (getResultDB.get(j).toString().equals(getResponseRows.get(i).toString())
										&& getResultDB.get(j + 1).toString()
												.equals(getResponseRows.get(i + 1).toString())
										&& getResultDB.get(j + 2).toString()
												.equals(getResponseRows.get(i + 2).toString())
										&& getResultDB.get(j + 3).toString()
												.equals(getResponseRows.get(i + 3).toString())
										&& getResultDB.get(j + 4).toString()
												.equals(getResponseRows.get(i + 4).toString())
										&& getResultDB.get(j + 5).toString()
												.equals(getResponseRows.get(i + 5).toString())
										&& getResultDB.get(j + 6).toString()
												.equals(getResponseRows.get(i + 6).toString())
										&& getResultDB.get(j + 7).toString()
												.equals(getResponseRows.get(i + 7).toString())
										&& getResultDB.get(j + 8).toString()
												.equals(getResponseRows.get(i + 8).toString())
										&& getResultDB.get(j + 9).toString()
												.equals(getResponseRows.get(i + 9).toString())
										&& getResultDB.get(j + 10).toString()
												.equals(getResponseRows.get(i + 10).toString())
										&& getResultDB.get(j + 11).toString()
												.equals(getResponseRows.get(i + 11).toString())
										&& getResultDB.get(j + 12).toString()
												.equals(getResponseRows.get(i + 12).toString())
										&& getResultDB.get(j + 13).toString()
												.equals(getResponseRows.get(i + 13).toString())
										&& getResultDB.get(j + 14).toString()
												.equals(getResponseRows.get(i + 14).toString())
										&& getResultDB.get(j + 15).toString()
												.equals(getResponseRows.get(i + 15).toString())
										&& getResultDB.get(j + 16).toString()
												.equals(getResponseRows.get(i + 16).toString())
										&& getResultDB.get(j + 17).toString()
												.equals(getResponseRows.get(i + 17).toString())
										&& getResultDB.get(j + 18).toString()
												.equals(getResponseRows.get(i + 18).toString())
										&& getResultDB.get(j + 19).toString()
												.equals(getResponseRows.get(i + 19).toString())
										&& getResultDB.get(j + 20).toString()
												.equals(getResponseRows.get(i + 20).toString())) {
									String[] responseDbFieldValues = { getResponseRows.get(i).toString(),
											getResultDB.get(j).toString(), getResponseRows.get(i + 1).toString(),
											getResultDB.get(j + 1).toString(), getResponseRows.get(i + 2).toString(),
											getResultDB.get(j + 2).toString(), getResponseRows.get(i + 3).toString(),
											getResultDB.get(j + 3).toString(), getResponseRows.get(i + 4).toString(),
											getResultDB.get(j + 4).toString(), getResponseRows.get(i + 5).toString(),
											getResultDB.get(j + 5).toString(), getResponseRows.get(i + 6).toString(),
											getResultDB.get(j + 6).toString(), getResponseRows.get(i + 7).toString(),
											getResultDB.get(j + 7).toString(), getResponseRows.get(i + 8).toString(),
											getResultDB.get(j + 8).toString(), getResponseRows.get(i + 9).toString(),
											getResultDB.get(j + 9).toString(), getResponseRows.get(i + 10).toString(),
											getResultDB.get(j + 10).toString(), getResponseRows.get(i + 11).toString(),
											getResultDB.get(j + 11).toString(), getResponseRows.get(i + 12).toString(),
											getResultDB.get(j + 12).toString(), getResponseRows.get(i + 13).toString(),
											getResultDB.get(j + 13).toString(), getResponseRows.get(i + 14).toString(),
											getResultDB.get(j + 14).toString(), getResponseRows.get(i + 15).toString(),
											getResultDB.get(j + 15).toString(), getResponseRows.get(i + 16).toString(),
											getResultDB.get(j + 16).toString(), getResponseRows.get(i + 17).toString(),
											getResultDB.get(j + 17).toString(), getResponseRows.get(i + 18).toString(),
											getResultDB.get(j + 18).toString(), getResponseRows.get(i + 19).toString(),
											getResultDB.get(j + 19).toString(), getResponseRows.get(i + 20).toString(),
											getResultDB.get(j + 20).toString() };
									String[] responseDbFieldNames = { "Response_geopoliticalId: ",
											"DB_geopoliticalId: ", "Response_countryNumberCd: ", "DB_countryNumberCd: ",
											"Response_countryCd: ", "DB_countryCd: ", "Response_threeCharCountryCd: ",
											"DB_threeCharCountryCd: ", "Response_independentFlag: ",
											"DB_independentFlag: ", "Response_dependentRelationshipId: ",
											"DB_dependentRelationshipId: ", "Response_dependentCountryCd: ",
											"DB_dependentCountryCd: ", "Response_postalFormatDescription: ",
											"DB_postalFormatDescription: ", "Response_postalFlag: ", "DB_postalFlag: ",
											"Response_postalLengthNumber: ", "DB_postalLengthNumber: ",
											"Response_firstWorkWeekDayName: ", "DB_firstWorkWeekDayName: ",
											"Response_lastWorkWeekDayName: ", "DB_lastWorkWeekDayName: ",
											"Response_weekendFirstDayName: ", "DB_weekendFirstDayName: ",
											"Response_internetDomainName: ", "DB_internetDomainName: ",
											"Response_internationalDialingCode: ", "DB_internationalDialingCode: ",
											"Response_landPhoneMaxLengthNumber: ", "DB_landPhoneMaxLengthNumber: ",
											"Response_landPhoneMinLengthNumber: ", "DB_landPhoneMinLengthNumber: ",
											"Response_mobilePhoneMaxLengthNumber: ", "DB_mobilePhoneMaxLengthNumber: ",
											"Response_mobilePhoneMinLengthNumber: ", "DB_mobilePhoneMinLengthNumber: ",
											"Response_effectiveDate: ", "DB_effectiveDate: ",
											"Response_expirationDate: ", "DB_expirationDate: " };
									writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
											responseDbFieldNames);
									test.info("Record " + z + " Validation:");
									test.pass(writableResult.replaceAll("\n", "<br />"));
									ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
											"", "", writableResult, "Pass", "");

									if (testCaseID != "TC_09") {
										test.info("Currency validation starts:");
										countryCurrencyValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
										test.info("OrgStd validation starts:");
										countryOrgStdValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
										/*
										 * test.info(
										 * "UOM Type validation starts:");
										 * countryUomTypeValidation(responsestr,
										 * testCaseID, z - 1, targetDate,
										 * endDate); test.info(
										 * "Geopolitical Type validation starts:"
										 * );
										 * countryGeoTypeValidation(responsestr,
										 * testCaseID, z - 1, targetDate,
										 * endDate); test.info(
										 * "Holiday validation starts:");
										 * countryHolidayValidation(responsestr,
										 * testCaseID, z - 1, targetDate,
										 * endDate); test.info(
										 * "Affil Type validation starts:");
										 * countryAffilTypeValidation(
										 * responsestr, testCaseID, z - 1,
										 * targetDate, endDate); //test.info(
										 * "Trnsl Geopolitical validation starts:"
										 * ); countryTrnslGeoplValidation(
										 * responsestr, testCaseID, z - 1,
										 * targetDate, endDate); test.info(
										 * "Locale validation starts:");
										 * countryLocaleValidation(responsestr,
										 * testCaseID, z - 1, targetDate,
										 * endDate); test.info(
										 * "Cntry Dial validation starts:");
										 * countryDialValidation(responsestr,
										 * testCaseID, z - 1, targetDate,
										 * endDate);
										 */
										test.info("CountryAdressLabel validation starts:");
										countryAddressValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
									}
									z++;
								}
								/*
								 * else { j = j + fields.size(); //j++; String[]
								 * responseDbFieldValues = {
								 * getResponseRows.get(i).toString(),
								 * getResultDB.get(j).toString(),
								 * getResponseRows.get(i + 1).toString(),
								 * getResultDB.get(j + 1).toString(),
								 * getResponseRows.get(i + 2).toString(),
								 * getResultDB.get(j + 2).toString(),
								 * getResponseRows.get(i + 3).toString(),
								 * getResultDB.get(j + 3).toString(),
								 * getResponseRows.get(i + 4).toString(),
								 * getResultDB.get(j + 4).toString(),
								 * getResponseRows.get(i + 5).toString(),
								 * getResultDB.get(j + 5).toString(),
								 * getResponseRows.get(i + 6).toString(),
								 * getResultDB.get(j + 6).toString(),
								 * getResponseRows.get(i + 7).toString(),
								 * getResultDB.get(j + 7).toString(),
								 * getResponseRows.get(i + 8).toString(),
								 * getResultDB.get(j + 8).toString(),
								 * getResponseRows.get(i + 9).toString(),
								 * getResultDB.get(j + 9).toString(),
								 * getResponseRows.get(i + 10).toString(),
								 * getResultDB.get(j + 10).toString(),
								 * getResponseRows.get(i + 11).toString(),
								 * getResultDB.get(j + 11).toString(),
								 * getResponseRows.get(i + 12).toString(),
								 * getResultDB.get(j + 12).toString(),
								 * getResponseRows.get(i + 13).toString(),
								 * getResultDB.get(j + 13).toString(),
								 * getResponseRows.get(i + 14).toString(),
								 * getResultDB.get(j + 14).toString(),
								 * getResponseRows.get(i + 15).toString(),
								 * getResultDB.get(j + 15).toString(),
								 * getResponseRows.get(i + 16).toString(),
								 * getResultDB.get(j + 16).toString(),
								 * getResponseRows.get(i + 17).toString(),
								 * getResultDB.get(j + 17).toString(),
								 * getResponseRows.get(i + 18).toString(),
								 * getResultDB.get(j + 18).toString(),
								 * getResponseRows.get(i + 19).toString(),
								 * getResultDB.get(j + 19).toString(),
								 * getResponseRows.get(i + 20).toString(),
								 * getResultDB.get(j + 20).toString() };
								 * String[] responseDbFieldNames = {
								 * "Response_geopoliticalId: ",
								 * "DB_geopoliticalId: ",
								 * "Response_countryNumberCd: ",
								 * "DB_countryNumberCd: ",
								 * "Response_countryCd: ", "DB_countryCd: ",
								 * "Response_threeCharCountryCd: ",
								 * "DB_threeCharCountryCd: ",
								 * "Response_independentFlag: ",
								 * "DB_independentFlag: ",
								 * "Response_dependentRelationshipId: ",
								 * "DB_dependentRelationshipId: ",
								 * "Response_dependentCountryCd: ",
								 * "DB_dependentCountryCd: ",
								 * "Response_postalFormatDescription: ",
								 * "DB_postalFormatDescription: ",
								 * "Response_postalFlag: ", "DB_postalFlag: ",
								 * "Response_postalLengthNumber: ",
								 * "DB_postalLengthNumber: ",
								 * "Response_firstWorkWeekDayName: ",
								 * "DB_firstWorkWeekDayName: ",
								 * "Response_lastWorkWeekDayName: ",
								 * "DB_lastWorkWeekDayName: ",
								 * "Response_weekendFirstDayName: ",
								 * "DB_weekendFirstDayName: ",
								 * "Response_internetDomainName: ",
								 * "DB_internetDomainName: ",
								 * "Response_internationalDialingCode: ",
								 * "DB_internationalDialingCode: ",
								 * "Response_landPhoneMaxLengthNumber: ",
								 * "DB_landPhoneMaxLengthNumber: ",
								 * "Response_landPhoneMinLengthNumber: ",
								 * "DB_landPhoneMinLengthNumber: ",
								 * "Response_mobilePhoneMaxLengthNumber: ",
								 * "DB_mobilePhoneMaxLengthNumber: ",
								 * "Response_mobilePhoneMinLengthNumber: ",
								 * "DB_mobilePhoneMinLengthNumber: ",
								 * "Response_effectiveDate: ",
								 * "DB_effectiveDate: ",
								 * "Response_expirationDate: ",
								 * "DB_expirationDate: " }; writableResult =
								 * Miscellaneous.geoFieldInputNames(
								 * responseDbFieldValues, responseDbFieldNames);
								 * test.info("Record " + z + " Validation:");
								 * test.pass(writableResult.replaceAll("\n",
								 * "<br />")); ex.writeExcel(fileName,
								 * testCaseID, TestCaseDescription,
								 * scenarioType, "NA", "", "", "", "",
								 * writableResult, "Pass", "");
								 * 
								 * test.info("Currency validation starts:");
								 * countryCurrencyValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("OrgStd validation starts:");
								 * countryOrgStdValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("UOM Type validation starts:");
								 * countryUomTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info(
								 * "Geopolitical Type validation starts:");
								 * countryGeoTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Holiday validation starts:");
								 * countryHolidayValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Affil Type validation starts:");
								 * countryAffilTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * //test.info(
								 * "Trnsl Geopolitical validation starts:");
								 * countryTrnslGeoplValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Locale validation starts:");
								 * countryLocaleValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Cntry Dial validation starts:");
								 * countryDialValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info(
								 * "CountryAdressLabel validation starts:");
								 * countryAddressValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate); z++;
								 * }
								 */
							}
						}
					}
					test.pass("Test Passed");
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, "", "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithGeoplIdCurrenciesCntryOrgStd(String testCaseID, String payload) {
		try {

			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGraphQLQuery(geopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = responseRows.size() - 1; i >= 0; i = i - 1) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internationalDialingCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internationalDialingCode"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMinimumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						for (int i = 0; i < getResponseRows.size(); i = i + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(i).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(i + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(i + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(i + 3).toString())
									&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(i + 4).toString())
									&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(i + 5).toString())
									&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(i + 6).toString())
									&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(i + 7).toString())
									&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(i + 8).toString())
									&& getResultDB.get(j + 9).toString().equals(getResponseRows.get(i + 9).toString())
									&& getResultDB.get(j + 10).toString().equals(getResponseRows.get(i + 10).toString())
									&& getResultDB.get(j + 11).toString().equals(getResponseRows.get(i + 11).toString())
									&& getResultDB.get(j + 12).toString().equals(getResponseRows.get(i + 12).toString())
									&& getResultDB.get(j + 13).toString().equals(getResponseRows.get(i + 13).toString())
									&& getResultDB.get(j + 14).toString().equals(getResponseRows.get(i + 14).toString())
									&& getResultDB.get(j + 15).toString().equals(getResponseRows.get(i + 15).toString())
									&& getResultDB.get(j + 16).toString().equals(getResponseRows.get(i + 16).toString())
									&& getResultDB.get(j + 17).toString().equals(getResponseRows.get(i + 17).toString())
									&& getResultDB.get(j + 18).toString().equals(getResponseRows.get(i + 18).toString())
									&& getResultDB.get(j + 19).toString().equals(getResponseRows.get(i + 19).toString())
									&& getResultDB.get(j + 20).toString()
											.equals(getResponseRows.get(i + 20).toString())) {
								String[] responseDbFieldValues = { getResponseRows.get(i).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(i + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(i + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(i + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(i + 4).toString(),
										getResultDB.get(j + 4).toString(), getResponseRows.get(i + 5).toString(),
										getResultDB.get(j + 5).toString(), getResponseRows.get(i + 6).toString(),
										getResultDB.get(j + 6).toString(), getResponseRows.get(i + 7).toString(),
										getResultDB.get(j + 7).toString(), getResponseRows.get(i + 8).toString(),
										getResultDB.get(j + 8).toString(), getResponseRows.get(i + 9).toString(),
										getResultDB.get(j + 9).toString(), getResponseRows.get(i + 10).toString(),
										getResultDB.get(j + 10).toString(), getResponseRows.get(i + 11).toString(),
										getResultDB.get(j + 11).toString(), getResponseRows.get(i + 12).toString(),
										getResultDB.get(j + 12).toString(), getResponseRows.get(i + 13).toString(),
										getResultDB.get(j + 13).toString(), getResponseRows.get(i + 14).toString(),
										getResultDB.get(j + 14).toString(), getResponseRows.get(i + 15).toString(),
										getResultDB.get(j + 15).toString(), getResponseRows.get(i + 16).toString(),
										getResultDB.get(j + 16).toString(), getResponseRows.get(i + 17).toString(),
										getResultDB.get(j + 17).toString(), getResponseRows.get(i + 18).toString(),
										getResultDB.get(j + 18).toString(), getResponseRows.get(i + 19).toString(),
										getResultDB.get(j + 19).toString(), getResponseRows.get(i + 20).toString(),
										getResultDB.get(j + 20).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
										"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
										"Response_independentFlag: ", "DB_independentFlag: ",
										"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
										"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
										"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
										"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
										"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
										"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
										"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
										"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
										"DB_internetDomainName: ", "Response_internationalDialingCode: ",
										"DB_internationalDialingCode: ", "Response_landPhoneMaxLengthNumber: ",
										"DB_landPhoneMaxLengthNumber: ", "Response_landPhoneMinLengthNumber: ",
										"DB_landPhoneMinLengthNumber: ", "Response_mobilePhoneMaxLengthNumber: ",
										"DB_mobilePhoneMaxLengthNumber: ", "Response_mobilePhoneMinLengthNumber: ",
										"DB_mobilePhoneMinLengthNumber: ", "Response_effectiveDate: ",
										"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Pass", "");

								test.info("Currency validation starts:");
								countryCurrencyValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
								test.info("OrgStd validation starts:");
								countryOrgStdValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
								/*
								 * test.info("UOM Type validation starts:");
								 * countryUomTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info(
								 * "Geopolitical Type validation starts:");
								 * countryGeoTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Holiday validation starts:");
								 * countryHolidayValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Affil Type validation starts:");
								 * countryAffilTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * //test.info(
								 * "Trnsl Geopolitical validation starts:");
								 * countryTrnslGeoplValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Locale validation starts:");
								 * countryLocaleValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Cntry Dial validation starts:");
								 * countryDialValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 */
								test.info("CountryAdressLabel validation starts:");
								countryAddressValidation(responsestr, testCaseID, z - 1, targetDate, endDate);

								z++;
							}
							/*
							 * else { j = j + fields.size(); //j++; String[]
							 * responseDbFieldValues = {
							 * getResponseRows.get(i).toString(),
							 * getResultDB.get(j).toString(),
							 * getResponseRows.get(i + 1).toString(),
							 * getResultDB.get(j + 1).toString(),
							 * getResponseRows.get(i + 2).toString(),
							 * getResultDB.get(j + 2).toString(),
							 * getResponseRows.get(i + 3).toString(),
							 * getResultDB.get(j + 3).toString(),
							 * getResponseRows.get(i + 4).toString(),
							 * getResultDB.get(j + 4).toString(),
							 * getResponseRows.get(i + 5).toString(),
							 * getResultDB.get(j + 5).toString(),
							 * getResponseRows.get(i + 6).toString(),
							 * getResultDB.get(j + 6).toString(),
							 * getResponseRows.get(i + 7).toString(),
							 * getResultDB.get(j + 7).toString(),
							 * getResponseRows.get(i + 8).toString(),
							 * getResultDB.get(j + 8).toString(),
							 * getResponseRows.get(i + 9).toString(),
							 * getResultDB.get(j + 9).toString(),
							 * getResponseRows.get(i + 10).toString(),
							 * getResultDB.get(j + 10).toString(),
							 * getResponseRows.get(i + 11).toString(),
							 * getResultDB.get(j + 11).toString(),
							 * getResponseRows.get(i + 12).toString(),
							 * getResultDB.get(j + 12).toString(),
							 * getResponseRows.get(i + 13).toString(),
							 * getResultDB.get(j + 13).toString(),
							 * getResponseRows.get(i + 14).toString(),
							 * getResultDB.get(j + 14).toString(),
							 * getResponseRows.get(i + 15).toString(),
							 * getResultDB.get(j + 15).toString(),
							 * getResponseRows.get(i + 16).toString(),
							 * getResultDB.get(j + 16).toString(),
							 * getResponseRows.get(i + 17).toString(),
							 * getResultDB.get(j + 17).toString(),
							 * getResponseRows.get(i + 18).toString(),
							 * getResultDB.get(j + 18).toString(),
							 * getResponseRows.get(i + 19).toString(),
							 * getResultDB.get(j + 19).toString(),
							 * getResponseRows.get(i + 20).toString(),
							 * getResultDB.get(j + 20).toString() }; String[]
							 * responseDbFieldNames = {
							 * "Response_geopoliticalId: ",
							 * "DB_geopoliticalId: ",
							 * "Response_countryNumberCd: ",
							 * "DB_countryNumberCd: ", "Response_countryCd: ",
							 * "DB_countryCd: ", "Response_threeCharCountryCd: "
							 * , "DB_threeCharCountryCd: ",
							 * "Response_independentFlag: ",
							 * "DB_independentFlag: ",
							 * "Response_dependentRelationshipId: ",
							 * "DB_dependentRelationshipId: ",
							 * "Response_dependentCountryCd: ",
							 * "DB_dependentCountryCd: ",
							 * "Response_postalFormatDescription: ",
							 * "DB_postalFormatDescription: ",
							 * "Response_postalFlag: ", "DB_postalFlag: ",
							 * "Response_postalLengthNumber: ",
							 * "DB_postalLengthNumber: ",
							 * "Response_firstWorkWeekDayName: ",
							 * "DB_firstWorkWeekDayName: ",
							 * "Response_lastWorkWeekDayName: ",
							 * "DB_lastWorkWeekDayName: ",
							 * "Response_weekendFirstDayName: ",
							 * "DB_weekendFirstDayName: ",
							 * "Response_internetDomainName: ",
							 * "DB_internetDomainName: ",
							 * "Response_internationalDialingCode: ",
							 * "DB_internationalDialingCode: ",
							 * "Response_landPhoneMaxLengthNumber: ",
							 * "DB_landPhoneMaxLengthNumber: ",
							 * "Response_landPhoneMinLengthNumber: ",
							 * "DB_landPhoneMinLengthNumber: ",
							 * "Response_mobilePhoneMaxLengthNumber: ",
							 * "DB_mobilePhoneMaxLengthNumber: ",
							 * "Response_mobilePhoneMinLengthNumber: ",
							 * "DB_mobilePhoneMinLengthNumber: ",
							 * "Response_effectiveDate: ", "DB_effectiveDate: ",
							 * "Response_expirationDate: ",
							 * "DB_expirationDate: " }; writableResult =
							 * Miscellaneous.geoFieldInputNames(
							 * responseDbFieldValues, responseDbFieldNames);
							 * test.info("Record " + z + " Validation:");
							 * test.pass(writableResult.replaceAll("\n",
							 * "<br />")); ex.writeExcel(fileName, testCaseID,
							 * TestCaseDescription, scenarioType, "NA", "", "",
							 * "", "", writableResult, "Pass", "");
							 * 
							 * test.info("Currency validation starts:");
							 * countryCurrencyValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * test.info("OrgStd validation starts:");
							 * countryOrgStdValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "UOM Type validation starts:");
							 * countryUomTypeValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Geopolitical Type validation starts:");
							 * countryGeoTypeValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Holiday validation starts:");
							 * countryHolidayValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Affil Type validation starts:");
							 * countryAffilTypeValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * //test.info(
							 * "Trnsl Geopolitical validation starts:");
							 * countryTrnslGeoplValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * test.info("Locale validation starts:");
							 * countryLocaleValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Cntry Dial validation starts:");
							 * countryDialValidation(responsestr, testCaseID, z
							 * - 1, targetDate, endDate); test.info(
							 * "CountryAdressLabel validation starts:");
							 * countryAddressValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); z++; }
							 */
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithGeoplIdCntryDialingsGeoplUom(String testCaseID, String payload) {
		try {

			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGraphQLQuery(geopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = responseRows.size() - 1; i >= 0; i = i - 1) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internationalDialingCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internationalDialingCode"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMinimumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					boolean rowMatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
									&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(e + 4).toString())
									&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(e + 5).toString())
									&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(e + 6).toString())
									&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(e + 7).toString())
									&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(e + 8).toString())
									&& getResultDB.get(j + 9).toString().equals(getResponseRows.get(e + 9).toString())
									&& getResultDB.get(j + 10).toString().equals(getResponseRows.get(e + 10).toString())
									&& getResultDB.get(j + 11).toString().equals(getResponseRows.get(e + 11).toString())
									&& getResultDB.get(j + 12).toString().equals(getResponseRows.get(e + 12).toString())
									&& getResultDB.get(j + 13).toString().equals(getResponseRows.get(e + 13).toString())
									&& getResultDB.get(j + 14).toString().equals(getResponseRows.get(e + 14).toString())
									&& getResultDB.get(j + 15).toString()
											.equals(getResponseRows.get(e + 15).toString())) {
								rowMatch = true;

								String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
										getResultDB.get(j + 4).toString(), getResponseRows.get(e + 5).toString(),
										getResultDB.get(j + 5).toString(), getResponseRows.get(e + 6).toString(),
										getResultDB.get(j + 6).toString(), getResponseRows.get(e + 7).toString(),
										getResultDB.get(j + 7).toString(), getResponseRows.get(e + 8).toString(),
										getResultDB.get(j + 8).toString(), getResponseRows.get(e + 9).toString(),
										getResultDB.get(j + 9).toString(), getResponseRows.get(e + 10).toString(),
										getResultDB.get(j + 10).toString(), getResponseRows.get(e + 11).toString(),
										getResultDB.get(j + 11).toString(), getResponseRows.get(e + 12).toString(),
										getResultDB.get(j + 12).toString(), getResponseRows.get(e + 13).toString(),
										getResultDB.get(j + 13).toString(), getResponseRows.get(e + 14).toString(),
										getResultDB.get(j + 14).toString(), getResponseRows.get(e + 15).toString(),
										getResultDB.get(j + 15).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
										"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
										"Response_independentFlag: ", "DB_independentFlag: ",
										"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
										"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
										"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
										"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
										"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
										"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
										"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
										"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
										"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
										"Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Pass", "");

								test.info("UOM Type validation starts:");
								countryUomTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);

								test.info("Cintry Dial validation starts:");
								countryDialValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
								z++;
								break;

							}

						}
						if (!rowMatch) {
							test.fail("records details are not matiching for the geoplId: " + geopoliticalId);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"records details are not matiching for the geoplId: " + geopoliticalId, "Fail", "");

						}
						rowMatch = false;

					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithGeopldHolidaysLocales(String testCaseID, String payload) {
		try {

			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			// PostMethod.cntryGraphQLRequest(geopoliticalId,countryCd/*,countryShortName,orgStandardCode,dependentCountryCd,targetDate,endDate*/);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGetQuery(geopoliticalId, targetDate, endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = responseRows.size() - 1; i >= 0; i = i - 1) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
								&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
								&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())
								&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(j + 7).toString())
								&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(j + 8).toString())
								&& getResultDB.get(j + 9).toString().equals(getResponseRows.get(j + 9).toString())
								&& getResultDB.get(j + 10).toString().equals(getResponseRows.get(j + 10).toString())
								&& getResultDB.get(j + 11).toString().equals(getResponseRows.get(j + 11).toString())
								&& getResultDB.get(j + 12).toString().equals(getResponseRows.get(j + 12).toString())
								&& getResultDB.get(j + 13).toString().equals(getResponseRows.get(j + 13).toString())
								&& getResultDB.get(j + 14).toString().equals(getResponseRows.get(j + 14).toString())
								&& getResultDB.get(j + 15).toString().equals(getResponseRows.get(j + 15).toString())) {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");

							test.info("Holiday validation starts:");
							countryHolidayValidation(responsestr, testCaseID, z - 1, targetDate, endDate);

							test.info("Locale validation starts:");
							countryLocaleValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
							z++;
						}

					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithGeopldGeoplAffilTrnslGeoplType(String testCaseID, String payload) {
		try {

			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGetQuery(geopoliticalId, targetDate, endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();

					for (int i = responseRows.size() - 1; i >= 0; i = i - 1) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
								&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
								&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())
								&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(j + 7).toString())
								&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(j + 8).toString())
								&& getResultDB.get(j + 9).toString().equals(getResponseRows.get(j + 9).toString())
								&& getResultDB.get(j + 10).toString().equals(getResponseRows.get(j + 10).toString())
								&& getResultDB.get(j + 11).toString().equals(getResponseRows.get(j + 11).toString())
								&& getResultDB.get(j + 12).toString().equals(getResponseRows.get(j + 12).toString())
								&& getResultDB.get(j + 13).toString().equals(getResponseRows.get(j + 13).toString())
								&& getResultDB.get(j + 14).toString().equals(getResponseRows.get(j + 14).toString())
								&& getResultDB.get(j + 15).toString().equals(getResponseRows.get(j + 15).toString())) {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");

							test.info("Affil Type validation starts:");
							countryAffilTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Trnsl Geopolitical validation starts:");
							countryTrnslGeoplValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Geopolitical Type validation starts:");
							countryGeoTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
							z++;
						}

					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryNoParameters(String testCaseID, String payload) {
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String countryGetQuery = query.countryGetNoParameterQuery(targetDate, endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() >= responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumericCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumericCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharacterCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharacterCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependantCountryCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependantCountryCode"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internationalDialingCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internationalDialingCode"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].landPhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].landPhoneMinimumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMaximumLength"));
						}

						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].mobilePhoneMinimumLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResponseRows.size(); j = j + fields.size()) {
						for (int i = 0; i < getResultDB.size(); i = i + fields.size()) {
							System.out.println("db record   "+getResultDB.get(i));
							if (getResponseRows.get(j).toString().equals(getResultDB.get(i).toString())
									&& getResponseRows.get(j + 1).toString().equals(getResultDB.get(i + 1).toString())
									&& getResponseRows.get(j + 2).toString().equals(getResultDB.get(i + 2).toString())
									&& getResponseRows.get(j + 3).toString().equals(getResultDB.get(i + 3).toString())
									&& getResponseRows.get(j + 4).toString().equals(getResultDB.get(i + 4).toString())
									&& getResponseRows.get(j + 5).toString().equals(getResultDB.get(i + 5).toString())
									&& getResponseRows.get(j + 6).toString().equals(getResultDB.get(i + 6).toString())
									&& getResponseRows.get(j + 7).toString().equals(getResultDB.get(i + 7).toString())
									&& getResponseRows.get(j + 8).toString().equals(getResultDB.get(i + 8).toString())
									&& getResponseRows.get(j + 9).toString().equals(getResultDB.get(i + 9).toString())
									&& getResponseRows.get(j + 10).toString().equals(getResultDB.get(i + 10).toString())
									&& getResponseRows.get(j + 11).toString().equals(getResultDB.get(i + 11).toString())
									&& getResponseRows.get(j + 12).toString().equals(getResultDB.get(i + 12).toString())
									&& getResponseRows.get(j + 13).toString().equals(getResultDB.get(i + 13).toString())
									&& getResponseRows.get(j + 14).toString().equals(getResultDB.get(i + 14).toString())
									&& getResponseRows.get(j + 15).toString().equals(getResultDB.get(i + 15).toString())
									&& getResponseRows.get(j + 16).toString().equals(getResultDB.get(i + 16).toString())
									&& getResponseRows.get(j + 17).toString().equals(getResultDB.get(i + 17).toString())
									&& getResponseRows.get(j + 18).toString().equals(getResultDB.get(i + 18).toString())
									&& getResponseRows.get(j + 19).toString().equals(getResultDB.get(i + 19).toString())
									&& getResponseRows.get(j + 20).toString()
											.equals(getResultDB.get(i + 20).toString())) {
								String[] responseDbFieldValues = { getResultDB.get(i).toString(),
										getResponseRows.get(j).toString(), getResultDB.get(i + 1).toString(),
										getResponseRows.get(j + 1).toString(), getResultDB.get(i + 2).toString(),
										getResponseRows.get(j + 2).toString(), getResultDB.get(i + 3).toString(),
										getResponseRows.get(j + 3).toString(), getResultDB.get(i + 4).toString(),
										getResponseRows.get(j + 4).toString(), getResultDB.get(i + 5).toString(),
										getResponseRows.get(j + 5).toString(), getResultDB.get(i + 6).toString(),
										getResponseRows.get(j + 6).toString(), getResultDB.get(i + 7).toString(),
										getResponseRows.get(j + 7).toString(), getResultDB.get(i + 8).toString(),
										getResponseRows.get(j + 8).toString(), getResultDB.get(i + 9).toString(),
										getResponseRows.get(j + 9).toString(), getResultDB.get(i + 10).toString(),
										getResponseRows.get(j + 10).toString(), getResultDB.get(i + 11).toString(),
										getResponseRows.get(j + 11).toString(), getResultDB.get(i + 12).toString(),
										getResponseRows.get(j + 12).toString(), getResultDB.get(i + 13).toString(),
										getResponseRows.get(j + 13).toString(), getResultDB.get(i + 14).toString(),
										getResponseRows.get(j + 14).toString(), getResultDB.get(i + 15).toString(),
										getResponseRows.get(j + 15).toString(), getResultDB.get(i + 16).toString(),
										getResponseRows.get(j + 16).toString(), getResultDB.get(i + 17).toString(),
										getResponseRows.get(j + 17).toString(), getResultDB.get(i + 18).toString(),
										getResponseRows.get(j + 18).toString(), getResultDB.get(i + 19).toString(),
										getResponseRows.get(j + 19).toString(), getResultDB.get(i + 20).toString(),
										getResponseRows.get(j + 20).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
										"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
										"Response_independentFlag: ", "DB_independentFlag: ",
										"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
										"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
										"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
										"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
										"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
										"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
										"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
										"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
										"DB_internetDomainName: ", "Response_internationalDialingCode: ",
										"DB_internationalDialingCode: ", "Response_landPhoneMaxLengthNumber: ",
										"DB_landPhoneMaxLengthNumber: ", "Response_landPhoneMinLengthNumber: ",
										"DB_landPhoneMinLengthNumber: ", "Response_mobilePhoneMaxLengthNumber: ",
										"DB_mobilePhoneMaxLengthNumber: ", "Response_mobilePhoneMinLengthNumber: ",
										"DB_mobilePhoneMinLengthNumber: ", "Response_effectiveDate: ",
										"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Pass", "");

								test.info("Currency validation starts:");
								countryCurrencyValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
								test.info("OrgStd validation starts:");
								countryOrgStdValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
								/*
								 * test.info("UOM Type validation starts:");
								 * countryUomTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info(
								 * "Geopolitical Type validation starts:");
								 * countryGeoTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Holiday validation starts:");
								 * countryHolidayValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Affil Type validation starts:");
								 * countryAffilTypeValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * //test.info(
								 * "Trnsl Geopolitical validation starts:");
								 * countryTrnslGeoplValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Locale validation starts:");
								 * countryLocaleValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 * test.info("Cntry Dial validation starts:");
								 * countryDialValidation(responsestr,
								 * testCaseID, z - 1, targetDate, endDate);
								 */
								test.info("CountryAdressLabel validation starts:");
								countryAddressValidation(responsestr, testCaseID, z - 1, targetDate, endDate);

								z++;
							}
							/*
							 * else { j = j + fields.size(); //j++; String[]
							 * responseDbFieldValues = {
							 * getResponseRows.get(i).toString(),
							 * getResultDB.get(j).toString(),
							 * getResponseRows.get(i + 1).toString(),
							 * getResultDB.get(j + 1).toString(),
							 * getResponseRows.get(i + 2).toString(),
							 * getResultDB.get(j + 2).toString(),
							 * getResponseRows.get(i + 3).toString(),
							 * getResultDB.get(j + 3).toString(),
							 * getResponseRows.get(i + 4).toString(),
							 * getResultDB.get(j + 4).toString(),
							 * getResponseRows.get(i + 5).toString(),
							 * getResultDB.get(j + 5).toString(),
							 * getResponseRows.get(i + 6).toString(),
							 * getResultDB.get(j + 6).toString(),
							 * getResponseRows.get(i + 7).toString(),
							 * getResultDB.get(j + 7).toString(),
							 * getResponseRows.get(i + 8).toString(),
							 * getResultDB.get(j + 8).toString(),
							 * getResponseRows.get(i + 9).toString(),
							 * getResultDB.get(j + 9).toString(),
							 * getResponseRows.get(i + 10).toString(),
							 * getResultDB.get(j + 10).toString(),
							 * getResponseRows.get(i + 11).toString(),
							 * getResultDB.get(j + 11).toString(),
							 * getResponseRows.get(i + 12).toString(),
							 * getResultDB.get(j + 12).toString(),
							 * getResponseRows.get(i + 13).toString(),
							 * getResultDB.get(j + 13).toString(),
							 * getResponseRows.get(i + 14).toString(),
							 * getResultDB.get(j + 14).toString(),
							 * getResponseRows.get(i + 15).toString(),
							 * getResultDB.get(j + 15).toString(),
							 * getResponseRows.get(i + 16).toString(),
							 * getResultDB.get(j + 16).toString(),
							 * getResponseRows.get(i + 17).toString(),
							 * getResultDB.get(j + 17).toString(),
							 * getResponseRows.get(i + 18).toString(),
							 * getResultDB.get(j + 18).toString(),
							 * getResponseRows.get(i + 19).toString(),
							 * getResultDB.get(j + 19).toString(),
							 * getResponseRows.get(i + 20).toString(),
							 * getResultDB.get(j + 20).toString() }; String[]
							 * responseDbFieldNames = {
							 * "Response_geopoliticalId: ",
							 * "DB_geopoliticalId: ",
							 * "Response_countryNumberCd: ",
							 * "DB_countryNumberCd: ", "Response_countryCd: ",
							 * "DB_countryCd: ", "Response_threeCharCountryCd: "
							 * , "DB_threeCharCountryCd: ",
							 * "Response_independentFlag: ",
							 * "DB_independentFlag: ",
							 * "Response_dependentRelationshipId: ",
							 * "DB_dependentRelationshipId: ",
							 * "Response_dependentCountryCd: ",
							 * "DB_dependentCountryCd: ",
							 * "Response_postalFormatDescription: ",
							 * "DB_postalFormatDescription: ",
							 * "Response_postalFlag: ", "DB_postalFlag: ",
							 * "Response_postalLengthNumber: ",
							 * "DB_postalLengthNumber: ",
							 * "Response_firstWorkWeekDayName: ",
							 * "DB_firstWorkWeekDayName: ",
							 * "Response_lastWorkWeekDayName: ",
							 * "DB_lastWorkWeekDayName: ",
							 * "Response_weekendFirstDayName: ",
							 * "DB_weekendFirstDayName: ",
							 * "Response_internetDomainName: ",
							 * "DB_internetDomainName: ",
							 * "Response_internationalDialingCode: ",
							 * "DB_internationalDialingCode: ",
							 * "Response_landPhoneMaxLengthNumber: ",
							 * "DB_landPhoneMaxLengthNumber: ",
							 * "Response_landPhoneMinLengthNumber: ",
							 * "DB_landPhoneMinLengthNumber: ",
							 * "Response_mobilePhoneMaxLengthNumber: ",
							 * "DB_mobilePhoneMaxLengthNumber: ",
							 * "Response_mobilePhoneMinLengthNumber: ",
							 * "DB_mobilePhoneMinLengthNumber: ",
							 * "Response_effectiveDate: ", "DB_effectiveDate: ",
							 * "Response_expirationDate: ",
							 * "DB_expirationDate: " }; writableResult =
							 * Miscellaneous.geoFieldInputNames(
							 * responseDbFieldValues, responseDbFieldNames);
							 * test.info("Record " + z + " Validation:");
							 * test.pass(writableResult.replaceAll("\n",
							 * "<br />")); ex.writeExcel(fileName, testCaseID,
							 * TestCaseDescription, scenarioType, "NA", "", "",
							 * "", "", writableResult, "Pass", "");
							 * 
							 * test.info("Currency validation starts:");
							 * countryCurrencyValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * test.info("OrgStd validation starts:");
							 * countryOrgStdValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "UOM Type validation starts:");
							 * countryUomTypeValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Geopolitical Type validation starts:");
							 * countryGeoTypeValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Holiday validation starts:");
							 * countryHolidayValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Affil Type validation starts:");
							 * countryAffilTypeValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * //test.info(
							 * "Trnsl Geopolitical validation starts:");
							 * countryTrnslGeoplValidation(responsestr,
							 * testCaseID, z - 1, targetDate, endDate);
							 * test.info("Locale validation starts:");
							 * countryLocaleValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); test.info(
							 * "Cntry Dial validation starts:");
							 * countryDialValidation(responsestr, testCaseID, z
							 * - 1, targetDate, endDate); test.info(
							 * "CountryAdressLabel validation starts:");
							 * countryAddressValidation(responsestr, testCaseID,
							 * z - 1, targetDate, endDate); z++; }
							 */
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryWithDates(String testCaseID, String payload) {
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");

			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			if (Wscode == 200 /* && Wsstatus.equalsIgnoreCase("SUCCESS") */) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				if (geopoliticalId == "" && js.get("data.countries[0].geopoliticalId") != null) {
					geopoliticalId = js.get("data.countries[0].geopoliticalId").toString();
				}
				String countryGetQuery = query.countryGetNoParameterQuery(targetDate, endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = responseRows.size() - 1; i >= 0; i--) {
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryNumberCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryNumberCd"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].countryCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].countryCd"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].threeCharCountryCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].threeCharCountryCd"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].independentFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].independentFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentRelationshipId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentRelationshipId"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].dependentCountryCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].dependentCountryCd"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFormatDescription"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFormatDescription"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalFlag"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalFlag"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].postalLength"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].postalLength"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].firstWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].firstWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].lastWorkWeekDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].lastWorkWeekDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].weekendFirstDayName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].weekendFirstDayName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].internetDomainName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries[" + i + "].internetDomainName"));
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.countries[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.countries[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
								&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
								&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())
								&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(j + 7).toString())
								&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(j + 8).toString())
								&& getResultDB.get(j + 9).toString().equals(getResponseRows.get(j + 9).toString())
								&& getResultDB.get(j + 10).toString().equals(getResponseRows.get(j + 10).toString())
								&& getResultDB.get(j + 11).toString().equals(getResponseRows.get(j + 11).toString())
								&& getResultDB.get(j + 12).toString().equals(getResponseRows.get(j + 12).toString())
								&& getResultDB.get(j + 13).toString().equals(getResponseRows.get(j + 13).toString())
								&& getResultDB.get(j + 14).toString().equals(getResponseRows.get(j + 14).toString())
								&& getResultDB.get(j + 15).toString().equals(getResponseRows.get(j + 15).toString())) {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							test.info("Geopolitical Type validation starts:");
							test.info("UOM Type validation starts:");
							countryUomTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Geopolitical Type validation starts:");
							countryGeoTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Holiday validation starts:");
							countryHolidayValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Affil Type validation starts:");
							countryAffilTypeValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Trnsl Geopolitical validation starts:");
							countryTrnslGeoplValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("OrgStd validation starts:");
							countryOrgStdValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Locale validation starts:");
							countryLocaleValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Cintry Dial validation starts:");
							countryDialValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							test.info("Currency validation starts:");
							countryCurrencyValidation(responsestr, testCaseID, z - 1, targetDate, endDate);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
									getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
									getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
									getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
									getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
									getResultDB.get(j + 11).toString(), getResponseRows.get(j + 12).toString(),
									getResultDB.get(j + 12).toString(), getResponseRows.get(j + 13).toString(),
									getResultDB.get(j + 13).toString(), getResponseRows.get(j + 14).toString(),
									getResultDB.get(j + 14).toString(), getResponseRows.get(j + 15).toString(),
									getResultDB.get(j + 15).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_countryNumberCd: ", "DB_countryNumberCd: ", "Response_countryCd: ",
									"DB_countryCd: ", "Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
									"Response_independentFlag: ", "DB_independentFlag: ",
									"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
									"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
									"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
									"Response_postalFlag: ", "DB_postalFlag: ", "Response_postalLengthNumber: ",
									"DB_postalLengthNumber: ", "Response_firstWorkWeekDayName: ",
									"DB_firstWorkWeekDayName: ", "Response_lastWorkWeekDayName: ",
									"DB_lastWorkWeekDayName: ", "Response_weekendFirstDayName: ",
									"DB_weekendFirstDayName: ", "Response_internetDomainName: ",
									"DB_internetDomainName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
							z++;
						}

					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	private void countryCurrencyValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].currencies");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryCurrencyGetQuery = query.countryCurrencyGraphQLQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryCurrencyGraphQLMethodDbFields();
		List<String> getResultDB = DbConnect.getResultSetFor(countryCurrencyGetQuery, fields, fileName, testCaseID);
//		List<String> getResultDB = new ArrayList<>();
//		try {
//			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//			ResultSet result = stmt.executeQuery(countryCurrencyGetQuery);
//			result.last();
//			result.beforeFirst();
//			String checkNull = null;
//			while (result.next()) {
//				for (int d = 0; d < fields.size(); d++) {
//					checkNull = result.getString(fields.get(d));
//					if (StringUtils.isBlank(checkNull)) {
//						checkNull = "";
//					}
//					getResultDB.add(checkNull.trim());
//				}
//			}
//			stmt.close();
//		} catch (SQLException e) {
//			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
//					"DB Connection Exception: " + e.toString());
//			test.fail("DB connection failed: " + e);
//			Assert.fail("Test Failed");
//		}
		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				/*List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].currencies.geopoliticalId");*/
				List<String> resCurrencyNumCd = null;
				resCurrencyNumCd = js.getList("data.countries[" + k + "].currencies.currencyNumericCode");
				List<String> resCurrencyCd = null;
				resCurrencyCd = js.getList("data.countries[" + k + "].currencies.currencyCode");
				List<String> resMinorUnitCd = null;
				resMinorUnitCd = js.getList("data.countries[" + k + "].currencies.minorUnitCode");
				List<String> resMoneyFormatDesc = null;
				resMoneyFormatDesc = js.getList("data.countries[" + k + "].currencies.moneyFormatDescription");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].currencies.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].currencies.expirationDate");
				for (int i = 0; i < responseRows.size(); i++) {

					/*if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}*/
					if (resCurrencyNumCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resCurrencyNumCd.get(i)));
					}
					if (resCurrencyCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resCurrencyCd.get(i).toString());
					}
					if (resMinorUnitCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resMinorUnitCd.get(i)));
					}
					if (resMoneyFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resMoneyFormatDesc.get(i).toString());
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}

				}
				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					for (int i = 0; i < getResponseRows.size(); i = i + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(i).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(i + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(i + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(i + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(i + 4).toString())
								&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(i + 5).toString())
								/*&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(i + 6).toString())*/) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(i).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(i + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(i + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(i + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(i + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(i + 5).toString(),
									getResultDB.get(j + 5).toString(), /*getResponseRows.get(i + 6).toString(),
									getResultDB.get(j + 6).toString(),*/ };
							String[] responseDbFieldNames = { /*"Response_geoplId: ", "DB_geoplId: ",*/
									"Response_currencyNumberCd: ", "DB_currencyNumberCd: ", "Response_currencyCd: ",
									"DB_currencyCd: ", "Response_minorUnitCd: ", "DB_minorUnitCd: ",
									"Response_moneyFormatDescription: ", "DB_moneyFormatDescription: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.pass(writableResult.replaceAll("\n", "<br />"));
							if (testCaseID != "TC_02") {
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
							}
						} /*
							 * else { String[] responseDbFieldValues = {
							 * getResponseRows.get(j).toString(),
							 * getResultDB.get(j).toString(),
							 * getResponseRows.get(j+1).toString(),
							 * getResultDB.get(j+1).toString(),
							 * getResponseRows.get(j+2).toString(),
							 * getResultDB.get(j+2).toString(),
							 * getResponseRows.get(j+3).toString(),
							 * getResultDB.get(j+3).toString(),
							 * getResponseRows.get(j+4).toString(),
							 * getResultDB.get(j+4).toString(),
							 * getResponseRows.get(j+5).toString(),
							 * getResultDB.get(j+5).toString(),
							 * getResponseRows.get(j+6).toString(),
							 * getResultDB.get(j+6).toString(), }; String[]
							 * responseDbFieldNames = { "Response_geoplId: ",
							 * "DB_geoplId: ", "Response_currencyNumberCd: ",
							 * "DB_currencyNumberCd: ", "Response_currencyCd: ",
							 * "DB_currencyCd: ", "Response_minorUnitCd: ",
							 * "DB_minorUnitCd: ",
							 * "Response_moneyFormatDescription: ",
							 * "DB_moneyFormatDescription: ",
							 * "Response_effectiveDate: ", "DB_effectiveDate: ",
							 * "Response_expirationDate: ",
							 * "DB_expirationDate: " }; writableResult =
							 * Miscellaneous.geoFieldInputNames(
							 * responseDbFieldValues, responseDbFieldNames);
							 * test.fail(writableResult.replaceAll("\n",
							 * "<br />")); ex.writeExcel(fileName, "",
							 * TestCaseDescription, scenarioType, "NA", "", "",
							 * "", "", writableResult, "Fail", "" ); }
							 */
					}
				}
			} else {
				logger.error("Total number of records not matching for Currency record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Currency record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void countryDepnCntryRltspValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		String dependentCountryRelationshipList = js
				.get("data.countries[" + k + "].dependentCountryRelationship.dependentRelationshipId");
		List<String> responseRows = null;
		String dependentCountryRelationship = null;
		if (dependentCountryRelationshipList != null) {
			responseRows = new ArrayList<String>(
					((HashMap<String, String>) js.get("data.countries[" + k + "].dependentCountryRelationship"))
							.values());
			dependentCountryRelationship = js
					.get("data.countries[" + k + "].dependentCountryRelationship.dependentRelationshipId").toString();
		}

		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();

		// ***get query
		String countryDepnCntryRltspGraphQLQuery = query
				.countryDepnCntryRltspGraphQLQuery(dependentCountryRelationship);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryDepnCntryRltspGraphQLMethodDbFields();

		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryDepnCntryRltspGraphQLQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size());
			if (getResultDB.size() == responseRows.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				String resDepnRltspId = null;
				resDepnRltspId = js
						.getString("data.countries[" + k + "].dependentCountryRelationship.dependentRelationshipId");
				String resDepnRltspDesc = null;
				resDepnRltspDesc = js.getString(
						"data.countries[" + k + "].dependentCountryRelationship.dependentRelationshipDescription");

				for (int i = 0; i < responseRows.size() / 2; i++) {

					if (resDepnRltspId == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDepnRltspId);
					}
					if (resDepnRltspDesc == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDepnRltspDesc);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString()

						};
						String[] responseDbFieldNames = { "Response_dependentRelationshipId: ",
								"DB_dependentRelationshipId: ", "Response_dependentRelationshipDescription: ",
								"DB_dependentRelationshipDescription: ",

						};
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(),

						};
						String[] responseDbFieldNames = { "Response_dependentRelationshipId: ",
								"DB_dependentRelationshipId: ", "Response_dependentRelationshipDescription: ",
								"DB_dependentRelationshipDescription: ", };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error(
						"Total number of records not matching for Dependent Country Relationship record between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Dependent Country Relationship record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryStateProvStdValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].stateProvStndList ");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryStateProvStdGraphQLQuery = query.countryStateProvStdGraphQLQuery(geopoliticalId, targetDate,
				endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryStateProvStdGraphQLMethodDbFields();

		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryStateProvStdGraphQLQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}
		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				// logger.info("Total number of records matching for State Prov
				// Std record between DB & Response: "+responseRows.size());
				// test.pass("Total number of records matching for State Prov
				// Std record between DB & Response: "+responseRows.size());
				// ex.writeExcel(fileName, testCaseID, TestCaseDescription,
				// scenarioType, "NA", "", "",
				// "", "", "", "Pass", "Total number of records matching for
				// State Prov Std record between DB & Response:
				// "+responseRows.size()+", below are the test steps for this
				// test case" );
				// List<String> getResponseRows = new ArrayList<>();

				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].stateProvStndList.geopoliticalId");
				List<String> resOrgStdCd = null;
				resOrgStdCd = js.getList("data.countries[" + k + "].stateProvStndList.organisationStandardCode");
				List<String> resStateProvCd = null;
				resStateProvCd = js.getList("data.countries[" + k + "].stateProvStndList.stateProvinceCode");
				List<String> resStateProvNm = null;
				resStateProvCd = js.getList("data.countries[" + k + "].stateProvStndList.stateProvinceName");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].stateProvStndList.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].stateProvStndList.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resOrgStdCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resOrgStdCd.get(i).toString());
					}
					if (resStateProvCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resStateProvCd.get(i).toString());
					}
					if (resStateProvNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resStateProvNm.get(i).toString());
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ",
								"Response_organisationStandardCode: ", "DB_organisationStandardCode: ",
								"Response_stateProvinceCode: ", "DB_stateProvinceCode: ",
								"Response_stateProvinceName: ", "DB_stateProvinceName: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ",
								"Response_organisationStandardCode: ", "DB_organisationStandardCode: ",
								"Response_stateProvinceCode: ", "DB_stateProvinceCode: ",
								"Response_stateProvinceName: ", "DB_stateProvinceName: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for State Prov Std record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for State Prov Std record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryDialValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].countryDialings");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryDialGetQuery = query.countryDialGetQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryDialGraphQLMethodDbFields();

		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryDialGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].countryDialings.geopoliticalId");
				List<String> resIntrnlDialPrefixCd = null;
				resIntrnlDialPrefixCd = js
						.getList("data.countries[" + k + "].countryDialings.internationalDialingPrefixCode");
				List<String> resIntrnlDialCd = null;
				resIntrnlDialCd = js.getList("data.countries[" + k + "].countryDialings.internationalDialingCode");
				List<String> resLandPhnMaxLengthNum = null;
				resLandPhnMaxLengthNum = js
						.getList("data.countries[" + k + "].countryDialings.landPhoneMaximumLengthNumber");
				List<String> resLandPhnMinLengthNum = null;
				resLandPhnMinLengthNum = js
						.getList("data.countries[" + k + "].countryDialings.landPhoneMinimumLengthNumber");
				List<String> resMobilePhnMaxLengthNum = null;
				resMobilePhnMaxLengthNum = js
						.getList("data.countries[" + k + "].countryDialings.mobilePhoneMaximumLengthNumber");
				List<String> resMobilePhnMinLengthNum = null;
				resMobilePhnMinLengthNum = js
						.getList("data.countries[" + k + "].countryDialings.mobilePhoneMinimumLengthNumber");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].countryDialings.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].countryDialings.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resIntrnlDialPrefixCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resIntrnlDialPrefixCd.get(i).toString());
					}
					if (resIntrnlDialCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resIntrnlDialCd.get(i).toString());
					}
					if (resLandPhnMaxLengthNum.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resLandPhnMaxLengthNum.get(i)));
					}
					if (resLandPhnMinLengthNum.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resLandPhnMinLengthNum.get(i)));
					}
					if (resMobilePhnMaxLengthNum.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resMobilePhnMaxLengthNum.get(i)));
					}
					if (resMobilePhnMinLengthNum.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resMobilePhnMinLengthNum.get(i)));
					}

					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}

				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {

					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString().trim())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString().trim())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString().trim())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString().trim())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString().trim())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString().trim())
							&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString().trim())
							&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(j + 7).toString().trim())
							&& getResultDB.get(j + 8).toString().equals(getResponseRows.get(j + 8).toString().trim())) {

						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
								getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
								getResultDB.get(j + 8).toString() };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ",
								"Response_intialDialingPrefixCd: ", "DB_intialDialingPrefixCd: ",
								"Response_intialDialingCd: ", "DB_intialDialingCd: ", "Response_landPhMaxLthNbr: ",
								"DB_landPhMaxLthNbr: ", "Response_landPhMinLthNbr: ", "DB_landPhMinLthNbr: ",
								"Response_moblPhMaxLthNbr: ", "DB_moblPhMaxLthNbr: ", "Response_moblPhMinLthNbr: ",
								"DB_moblPhMinLthNbr: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
								"Response_expirationDate: ", "DB_expirationDate: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");

						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
								getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
								getResultDB.get(j + 8).toString() };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ",
								"Response_intialDialingPrefixCd: ", "DB_intialDialingPrefixCd: ",
								"Response_intialDialingCd: ", "DB_intialDialingCd: ", "Response_landPhMaxLthNbr: ",
								"DB_landPhMaxLthNbr: ", "Response_landPhMinLthNbr: ", "DB_landPhMinLthNbr: ",
								"Response_moblPhMaxLthNbr: ", "DB_moblPhMaxLthNbr: ", "Response_moblPhMinLthNbr: ",
								"DB_moblPhMinLthNbr: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
								"Response_expirationDate: ", "DB_expirationDate: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for Dial record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Dial record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryLocaleValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> getResultDBFinal = new ArrayList<String>();
		List<String> responseRows = js.get("data.countries[" + k + "].locales");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryLocaleGetQuery = query.countryLocaleGraphQLQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryLocaleGraphQLMethodDbFields();

		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryLocaleGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		getResultDBFinal.addAll(getResultDB);

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].locales.geopoliticalId");
				List<String> resLocaleCd = null;
				resLocaleCd = js.getList("data.countries[" + k + "].locales.localeCode");
				List<String> resScriptCd = null;
				resScriptCd = js.getList("data.countries[" + k + "].locales.scriptCode");
				List<String> resLangCd = null;
				resLangCd = js.getList("data.countries[" + k + "].locales.languageCode");
				List<String> resCldrVersDt = null;
				resCldrVersDt = js.getList("data.countries[" + k + "].locales.cldrVersionDate");
				List<String> resCldrVersNm = null;
				resCldrVersNm = js.getList("data.countries[" + k + "].locales.cldrVersionNumber");
				List<String> resDtFullFormatDesc = null;
				resDtFullFormatDesc = js.getList("data.countries[" + k + "].locales.dateFullFormatDescription");
				List<String> resDtLongFormatDesc = null;
				resDtLongFormatDesc = js.getList("data.countries[" + k + "].locales.dateLongFormatDescription");
				List<String> resDtMediumFormatDesc = null;
				resDtMediumFormatDesc = js.getList("data.countries[" + k + "].locales.dateMediumFormatDescription");
				List<String> resDtShortFormatDesc = null;
				resDtShortFormatDesc = js.getList("data.countries[" + k + "].locales.dateShortFormatDescription");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].locales.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].locales.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resLocaleCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resLocaleCd.get(i).toString());
					}
					if (resScriptCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resScriptCd.get(i).toString());
					}
					if (resLangCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resLangCd.get(i).toString());
					}
					if (resCldrVersDt.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resCldrVersDt.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resCldrVersNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resCldrVersNm.get(i).toString());
					}
					if (resDtFullFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDtFullFormatDesc.get(i).toString());
					}
					if (resDtLongFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDtLongFormatDesc.get(i).toString());
					}
					if (resDtMediumFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDtMediumFormatDesc.get(i).toString());
					}
					if (resDtShortFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resDtShortFormatDesc.get(i).toString());
					}

					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				int x = getResultDB.size();
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDBFinal.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDBFinal.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDBFinal.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDBFinal.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDBFinal.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDBFinal.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
							&& getResultDBFinal.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())
							&& getResultDBFinal.get(j + 7).toString().equals(getResponseRows.get(j + 7).toString())
							&& getResultDBFinal.get(j + 8).toString().equals(getResponseRows.get(j + 8).toString())
							&& getResultDBFinal.get(j + 9).toString().equals(getResponseRows.get(j + 9).toString())
							&& getResultDBFinal.get(j + 10).toString().equals(getResponseRows.get(j + 10).toString())
							&& getResultDBFinal.get(j + 11).toString().equals(getResponseRows.get(j + 11).toString()))

					{
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDBFinal.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDBFinal.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDBFinal.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDBFinal.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDBFinal.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDBFinal.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDBFinal.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
								getResultDBFinal.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
								getResultDBFinal.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
								getResultDBFinal.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
								getResultDBFinal.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
								getResultDBFinal.get(j + 11).toString(),

						};
						x += 2;
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_localeCode: ", "DB_localeCode: ", "Response_scrptCode: ", "DB_scrptCode: ",
								"Response_languageCode: ", "DB_languageCode: ", "Response_cldrVersionDate: ",
								"DB_cldrVersionDate: ", "Response_cldrVersionNumber: ", "DB_cldrVersionNumber: ",
								"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
								"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
								"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
								"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
								"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
								"DB_expirationDate: "

						};
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), getResponseRows.get(j + 7).toString(),
								getResultDB.get(j + 7).toString(), getResponseRows.get(j + 8).toString(),
								getResultDB.get(j + 8).toString(), getResponseRows.get(j + 9).toString(),
								getResultDB.get(j + 9).toString(), getResponseRows.get(j + 10).toString(),
								getResultDB.get(j + 10).toString(), getResponseRows.get(j + 11).toString(),
								getResultDB.get(j + 11).toString(),

						};
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_localeCode: ", "DB_localeCode: ", "Response_scrptCode: ", "DB_scrptCode: ",
								"Response_languageCode: ", "DB_languageCode: ", "Response_cldrVersionDate: ",
								"DB_cldrVersionDate: ", "Response_cldrVersionNumber: ", "DB_cldrVersionNumber: ",
								"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
								"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
								"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
								"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
								"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
								"DB_expirationDate: "

						};
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for Locale record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Locale record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryOrgStdValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> getResultDBFinal = new ArrayList<String>();
		List<String> responseRows = null;
		if (js.get("data.countries[" + k + "].countryOrganizationStandards") != null) {
			responseRows = js.get("data.countries[" + k + "].countryOrganizationStandards");
			geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();

			// ***get query
			String countryOrgStdGetQuery = query.countryOrgStdGraphQLQuery(geopoliticalId, targetDate, endDate);
			// ***get the fields needs to be validate in DB
			List<String> fields = ValidationFields.countryOrgStdGraphQLMethodDbFields();

			List<String> getResultDB = DbConnect.getResultSetFor(countryOrgStdGetQuery, fields, fileName, testCaseID);
			/*try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet result = stmt.executeQuery(countryOrgStdGetQuery);
				result.last();
				result.beforeFirst();
				String checkNull = null;
				while (result.next()) {
					for (int d = 0; d < fields.size(); d++) {
						checkNull = result.getString(fields.get(d));
						if (StringUtils.isBlank(checkNull)) {
							checkNull = "";
						}
						getResultDB.add(checkNull.trim());
					}
				}
				stmt.close();
			} catch (SQLException e) {
				ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
						"DB Connection Exception: " + e.toString());
				test.fail("DB connection failed: " + e);
				Assert.fail("Test Failed");
			}*/

			if (getResultDB.size() != 0 && responseRows != null) {

				if (getResultDB.size() == responseRows.size() * fields.size()) {

					List<String> getResponseRows = new ArrayList<String>();

					for (int i = 0; i < responseRows.size(); i++) {

						/*String resGeoplId = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].geopoliticalId");
						String resOrgStdCd1 = js.getString("data.countries[" + k + "].countryOrganizationStandards[" + i
								+ "].organizationStandardCode");*/
						String resCountryFullNm = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryFullName");
						String resCountryShortNm = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryShortName");
						String resOrgStdCd2 = js.getString("data.countries[" + k + "].countryOrganizationStandards[" + i
								+ "].organizationStandardCode");
						String resOrgStdNm = js.getString("data.countries[" + k + "].countryOrganizationStandards[" + i
								+ "].organizationStandardName");
						String resEffDate = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].effectiveDate");
						String resExpDate = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].expirationDate");

						/*if (resGeoplId == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resGeoplId);
						}
						if (resOrgStdCd1 == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resOrgStdCd1);
						}*/
						if (resCountryFullNm == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resCountryFullNm);
						}
						if (resCountryShortNm == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resCountryShortNm);
						}
						if (resOrgStdCd2 == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resOrgStdCd2);
						}
						if (resOrgStdNm == null) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(resOrgStdNm);
						}
						if (resEffDate == null) {
							getResponseRows.add("");
						} else {
							String str = resEffDate;
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (resExpDate == null) {
							getResponseRows.add("");
						} else {
							String str = resExpDate;
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}

					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
					}

					boolean rowMatch = false;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						for (int i = 0; i < getResponseRows.size(); i = i + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(i).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(i + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(i + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(i + 3).toString())
									&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(i + 4).toString())
									&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(i + 5).toString())
									/*&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(i + 6).toString())
									&& getResultDB.get(j + 7).toString()
											.equals(getResponseRows.get(i + 7).toString())*/) {
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(i).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(i + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(i + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(i + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(i + 4).toString(),
										getResultDB.get(j + 4).toString(), getResponseRows.get(i + 5).toString(),
										getResultDB.get(j + 5).toString(), /*getResponseRows.get(i + 6).toString(),
										getResultDB.get(j + 6).toString(), getResponseRows.get(i + 7).toString(),
										getResultDB.get(j + 7).toString(),*/ };
								// x+=2;
								String[] responseDbFieldNames = { /*"Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_organizationStandardCode: ", "DB_organizationStandardCode: ",*/
										"Response_countryFullName: ", "DB_countryFullName: ",
										"Response_countryShortName: ", "DB_countryShortName: ",
										"Response_organizationStandardCode: ", "DB_organizationStandardCode: ",
										"Response_organizationStandardName: ", "DB_organizationStandardName: ",
										"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
										"DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.pass(writableResult.replaceAll("\n", "<br />"));
								if (testCaseID != "TC_02") {
									ex.writeExcel(fileName, "",
											"Contry org std details for the geoplId: " + geopoliticalId, scenarioType,
											"NA", "", "", "", "", writableResult, "Pass", "");
								}
								break;
								// }
							}
							/*
							 * if(!rowMatch) { test.fail(
							 * "Contry org std details are not matiching for the geoplId: "
							 * +geopoliticalId); ex.writeExcel(fileName, "",
							 * TestCaseDescription, scenarioType, "NA", "", "",
							 * "", "",
							 * "Contry org std details are not matiching for the geoplId: "
							 * +geopoliticalId, "Fail", "" );
							 * 
							 * } rowMatch=false;
							 */
						}
					}
				} else {
					logger.error("Total number of records not matching for OrgStd record between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching for OrgStd record between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "",
							"Fail", "");
				}

			} else {
				int resNullCount;
				if (responseRows == null) {
					resNullCount = 0;
				} else {
					resNullCount = responseRows.size();
				}
				if (getResultDB.size() == 0 && resNullCount == 0) {
					test.pass("Response and DB record count is 0, so validation not needed");
					ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
							"Response and DB record count is 0, so validation not needed", "Pass", "");
				} else {
					test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
							+ " is not matching");
					ex.writeExcel(fileName, "",
							TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
									+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
							"Fail", "");
				}

			}
		}
	}

	private void countryOrgStdValidationWithDates(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = null;
		if (js.get("data.countries[" + k + "].countryOrganizationStandards") != null) {
			responseRows = js.get("data.countries[" + k + "].countryOrganizationStandards");
			geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();

			// ***get query
			String countryOrgStdGetQuery = query.countryNoParameterOrgStdGetQuery(geopoliticalId, targetDate, endDate);
			// ***get the fields needs to be validate in DB
			List<String> fields = ValidationFields.countryOrgStdGetMethodDbFields();
			// ***get the result from DB
			List<String> getResultDB = DbConnect.getResultSetFor(countryOrgStdGetQuery, fields, fileName, testCaseID);
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				test.pass("Total number of records matching for OrgStd record between DB & Response: "
						+ responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "", "", "Pass",
						"Total number of records matching for OrgStd record between DB & Response: "
								+ responseRows.size() + ", below are the test steps for this test case");
				List<String> getResponseRows = new ArrayList<>();
				for (int i = 0; i < responseRows.size(); i++) {
					if (StringUtils.isBlank(js
							.getString("data.countries[" + k + "].countryOrganizationStandards[" + i + "].orgStdNm"))) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(js
								.getString("data.countries[" + k + "].countryOrganizationStandards[" + i + "].orgStdNm")
								.trim());
					}
					if (StringUtils.isBlank(js.getString(
							"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryFullName"))) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryFullName")
								.trim());
					}
					if (StringUtils.isBlank(js.getString(
							"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryShortName"))) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].countryShortName")
								.trim());
					}
					if (StringUtils.isBlank(js.getString(
							"data.countries[" + k + "].countryOrganizationStandards[" + i + "].effectiveDate"))) {
						getResponseRows.add("");
					} else {
						String str = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].effectiveDate");
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (StringUtils.isBlank(js.getString(
							"data.countries[" + k + "].countryOrganizationStandards[" + i + "].expirationDate"))) {
						getResponseRows.add("");
					} else {
						String str = js.getString(
								"data.countries[" + k + "].countryOrganizationStandards[" + i + "].expirationDate");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				boolean rowMatch = false;
				for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(e + 4).toString())) {
							rowMatch = true;
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
									getResultDB.get(j + 4).toString(), };
							String[] responseDbFieldNames = { "Response_orgStdNm: ", "DB_orgStdNm: ",
									"Response_countryFullName: ", "DB_countryFullName: ", "Response_countryShortName: ",
									"DB_countryShortName: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
									"Response_expirationDate: ", "DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", "Contry org std details for the geoplId: " + geopoliticalId,
									scenarioType, "NA", "", "", "", "", writableResult, "Pass", "");
							break;
						}
					}
					if (!rowMatch) {
						test.fail("Contry org std details are not matiching for the geoplId: " + geopoliticalId);
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								"Contry org std details are not matiching for the geoplId: " + geopoliticalId, "Fail",
								"");

					}
					rowMatch = false;
				}

			} else {
				logger.error("Total number of records not matching for OrgStd record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for OrgStd record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		}

	}

	private void countryTrnslGeoplValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].translatedGeopoliticals");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryTrnslGeoplGetQuery = query.countryTrnslGeoplGetQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryTrnslGeoplGraphQLMethodDbFields();
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryTrnslGeoplGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {

				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].translatedGeopoliticals.geopoliticalId");
				List<String> resLangCd = null;
				resLangCd = js.getList("data.countries[" + k + "].translatedGeopoliticals.languageCode");
				List<String> resScriptCd = null;
				resScriptCd = js.getList("data.countries[" + k + "].translatedGeopoliticals.scriptCode");
				List<String> resTrnslName = null;
				resTrnslName = js.getList("data.countries[" + k + "].translatedGeopoliticals.translatedName");
				List<String> resVersNum = null;
				resVersNum = js.getList("data.countries[" + k + "].translatedGeopoliticals.versionNumber");
				List<String> resVersDate = null;
				resVersDate = js.getList("data.countries[" + k + "].translatedGeopoliticals.versionDate");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].translatedGeopoliticals.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].translatedGeopoliticals.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {
					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resLangCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resLangCd.get(i).toString());
					}
					if (resScriptCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resScriptCd.get(i).toString());
					}
					if (resTrnslName.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resTrnslName.get(i).toString());
					}
					if (resVersDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resVersDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resVersNum.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resVersNum.get(i).toString());
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				boolean rowMatch = false;
				for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(e + 4).toString())
								&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(e + 5).toString())
								&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(e + 6).toString())
								&& getResultDB.get(j + 7).toString().equals(getResponseRows.get(e + 7).toString())) {
							rowMatch = true;
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
									getResultDB.get(j + 4).toString(), getResponseRows.get(e + 5).toString(),
									getResultDB.get(j + 5).toString(), getResponseRows.get(e + 6).toString(),
									getResultDB.get(j + 6).toString(), getResponseRows.get(e + 7).toString(),
									getResultDB.get(j + 7).toString() };
							String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ",
									"Response_languageCd: ", "DB_languageCd: ", "Response_scrptCd: ", "DB_scrptCd: ",
									"Response_translationName: ", "DB_translationName: ", "Response_versionDate: ",
									"DB_versionDate: ", "Response_versionNumber: ", "DB_versionNumber: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.pass(writableResult.replaceAll("\n", "<br />"));
							if (testCaseID != "TC_02") {
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
							}
							break;
						}
					}
					if (!rowMatch) {
						test.fail("TrnslGeopl details are not matiching for the geoplId: " + geopoliticalId);
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								"TrnslGeopl details are not matiching for the geoplId: " + geopoliticalId, "Fail", "");

					}
					rowMatch = false;
				}

			} else {
				logger.error("Total number of records not matching for TrnslGeopl record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for TrnslGeopl record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryAffilTypeValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);

		List<String> getResultDB = new ArrayList<>();
		List<String> responseRows = js.get("data.countries[" + k + "].geopoliticalAffiliations");
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryAffilTypeGraphQLMethodDbFields();

		for (int x = 0; x < responseRows.size(); x++) {
			geopoliticalId = js.get("data.countries[" + k + "].geopoliticalAffiliations.geopoliticalId[" + x + "]");
			affilTypeId = js.get("data.countries[" + k + "].geopoliticalAffiliations.affilTypeId[" + x + "]")
					.toString();
			String affilTargetDate = js
					.getString("data.countries[" + k + "].geopoliticalAffiliations.effectiveDate[" + x + "]");
			String affilEndDate = js
					.getString("data.countries[" + k + "].geopoliticalAffiliations.expirationDate[" + x + "]");
			String strTargetDate = null, strEndDate = null;
			if (affilTargetDate != null && affilEndDate != null) {
				strTargetDate = affilTargetDate;
				strTargetDate = strTargetDate.substring(0, 10);

				strEndDate = affilEndDate;
				strEndDate = strEndDate.substring(0, 10);
			}

			// ***get query
			String countryAffilTypeGetQuery = query.countryAffilTypeGetQuery(geopoliticalId, strTargetDate, strEndDate);

			try {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet result = stmt.executeQuery(countryAffilTypeGetQuery);
				result.last();
				result.beforeFirst();
				String checkNull = null;
				while (result.next()) {
					for (int d = 0; d < fields.size(); d++) {
						checkNull = result.getString(fields.get(d));
						if (StringUtils.isBlank(checkNull)) {
							checkNull = "";
						}
						getResultDB.add(checkNull.trim());
					}
				}
				stmt.close();
			} catch (SQLException e) {
				ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
						"DB Connection Exception: " + e.toString());
				test.fail("DB connection failed: " + e);
				Assert.fail("Test Failed");
			}
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {

				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].geopoliticalAffiliations.geopoliticalId");
				List<String> resAffilId1 = null;
				resAffilId1 = js.getList("data.countries[" + k + "].geopoliticalAffiliations.affilTypeId");
				List<String> resAffilId2 = null;
				resAffilId2 = js.getList(
						"data.countries[" + k + "].geopoliticalAffiliations.geoPoliticalAffiliatedType.affilTypeId");
				List<String> resAffilCd = null;
				resAffilCd = js.getList(
						"data.countries[" + k + "].geopoliticalAffiliations.geoPoliticalAffiliatedType.affilTypeCode");
				List<String> resAffilNm = null;
				resAffilNm = js.getList(
						"data.countries[" + k + "].geopoliticalAffiliations.geoPoliticalAffiliatedType.affilTypeName");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].geopoliticalAffiliations.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].geopoliticalAffiliations.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resAffilId1.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resAffilId1.get(i)));
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resAffilId2.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resAffilId2.get(i)));
					}
					if (resAffilCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resAffilCd.get(i).toString());
					}
					if (resAffilNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resAffilNm.get(i).toString());
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
							&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString() };
						String[] responseDbFieldNames = { "Response_affilTypeId: ", "DB_affilTypeId: ",
								"Response_geopoliticalId: ", "DB_geopoliticalId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_affilTypeId: ", "DB_affilTypeId: ", "Response_affilTypeCode: ",
								"DB_affilTypeCode: ", "Response_affilTypeName: ", "DB_affilTypeName: ", };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString() };
						String[] responseDbFieldNames = { "Response_affilTypeId: ", "DB_affilTypeId: ",
								"Response_geopoliticalId: ", "DB_geopoliticalId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_affilTypeId: ", "DB_affilTypeId: ", "Response_affilTypeCode: ",
								"DB_affilTypeCode: ", "Response_affilTypeName: ", "DB_affilTypeName: ", };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for AffilTyp record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for AffilTyp record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryHolidayValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].geopoliticalHolidays");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryHolidayGraphQLQuery = query.countryHolidayGraphQLQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryHolidayGraphQLMethodDbFields();
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryHolidayGraphQLQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {

				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].geopoliticalHolidays.geopoliticalId");
				List<String> resHolidayId1 = null;
				resHolidayId1 = js.getList("data.countries[" + k + "].geopoliticalHolidays.holidayId");
				List<String> resHolidayId2 = null;
				resHolidayId2 = js.getList("data.countries[" + k + "].geopoliticalHolidays.holiday.holidayId");
				List<String> resHolidayNm = null;
				resHolidayNm = js.getList("data.countries[" + k + "].geopoliticalHolidays.holiday.holidayName");
				List<String> resHolidayDtParamTxt = null;
				resHolidayDtParamTxt = js
						.getList("data.countries[" + k + "].geopoliticalHolidays.holiday.holidayDateParameterText");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].geopoliticalHolidays.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].geopoliticalHolidays.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resHolidayId1.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resHolidayId1.get(i)));
					}

					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resHolidayId2.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resHolidayId2.get(i)));
					}
					if (resHolidayNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resHolidayNm.get(i).toString());
					}
					if (resHolidayDtParamTxt.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resHolidayDtParamTxt.get(i).toString());
					}

				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}

				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
							&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), };
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_holidayId: ", "DB_holidayId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_holidayId: ", "DB_holidayId: ", "Response_holidayName: ", "DB_holidayName: ",
								"Response_holidayDateParameterText: ", "DB_holidayDateParameterText: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), };
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_holidayId: ", "DB_holidayId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_holidayId: ", "DB_holidayId: ", "Response_holidayName: ", "DB_holidayName: ",
								"Response_holidayDateParameterText: ", "DB_holidayDateParameterText: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for Holiday record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Holiday record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	private void countryUomTypeValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = new ArrayList<>();
		responseRows = js.get("data.countries[" + k + "].geopoliticalUnitOfMeasures");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryUomTypeGetQuery = query.countryUomTypeGraphQLQuery(geopoliticalId, targetDate, endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryUomTypeGraphQLMethodDbFields();

		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryUomTypeGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {

				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.geopoliticalId");
				List<String> resUOMTypeCd1 = null;
				resUOMTypeCd1 = js.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.uomTypeCode");
				List<String> resUOMTypeCd2 = null;
				resUOMTypeCd2 = js
						.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.refUOMType.uomTypeCode");
				List<String> resUOMTypeNm = null;
				resUOMTypeNm = js
						.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.refUOMType.uomTypeName");
				List<String> resUOMTypeDesc = null;
				resUOMTypeDesc = js
						.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.refUOMType.uomTypeDesc");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].geopoliticalUnitOfMeasures.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resUOMTypeCd1.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resUOMTypeCd1.get(i).toString());
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						// int index = str.indexOf("T");
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resUOMTypeCd2.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resUOMTypeCd2.get(i).toString());
					}
					if (resUOMTypeNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resUOMTypeNm.get(i).toString());
					}
					if (resUOMTypeDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resUOMTypeDesc.get(i).toString());
					}
				}

				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())
							&& getResultDB.get(j + 6).toString().equals(getResponseRows.get(j + 6).toString())) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ", "Response_uomTypeCd: ",
								"DB_uomTypeCd: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
								"Response_expirationDate: ", "DB_expirationDate: ", "Response_uomTypeCode: ",
								"DB_uomTypeCode: ", "Response_uomTypeName: ", "DB_uomTypeName: ",
								"Response_uomTypeDesc: ", "DB_uomTypeDesc: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}

					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString(), getResponseRows.get(j + 6).toString(),
								getResultDB.get(j + 6).toString(), };
						String[] responseDbFieldNames = { "Response_geoplId: ", "DB_geoplId: ", "Response_uomTypeCd: ",
								"DB_uomTypeCd: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
								"Response_expirationDate: ", "DB_expirationDate: ", "Response_uomTypeCode: ",
								"DB_uomTypeCode: ", "Response_uomTypeName: ", "DB_uomTypeName: ",
								"Response_uomTypeDesc: ", "DB_uomTypeDesc: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for UOM Type record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for UOM Type record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}
	}

	private void countryGeoTypeValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js
				.get("data.countries[" + k + "].geopoliticalAreas.geoPoliticalType.geopoliticalTypeName");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryGeoTypeNameGraphQLQuery = query.countryGeoTypeNameGraphQLQuery(geopoliticalId, targetDate,
				endDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryGraphQLTypeNameGetMethodDbFields();
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryGeoTypeNameGraphQLQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}

		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].geopoliticalAreas.geopoliticalId");
				List<String> resGeoplTypeId1 = null;
				resGeoplTypeId1 = js.getList("data.countries[" + k + "].geopoliticalAreas.geopoliticalTypeId");
				List<String> resGeoplTypeId2 = null;
				resGeoplTypeId2 = js
						.getList("data.countries[" + k + "].geopoliticalAreas.geoPoliticalType.geopoliticalTypeId");
				List<String> resGeoplTypeNm = null;
				resGeoplTypeNm = js
						.getList("data.countries[" + k + "].geopoliticalAreas.geoPoliticalType.geopoliticalTypeName");
				List<String> resEffDate = null;
				resEffDate = js.getList("data.countries[" + k + "].geopoliticalAreas.effectiveDate");
				List<String> resExpDate = null;
				resExpDate = js.getList("data.countries[" + k + "].geopoliticalAreas.expirationDate");

				for (int i = 0; i < responseRows.size(); i++) {

					if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}
					if (resGeoplTypeId1.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resGeoplTypeId1.get(i)));
					}
					if (resEffDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resEffDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resExpDate.get(i) == null) {
						getResponseRows.add("");
					} else {
						String str = resExpDate.get(i).toString();
						str = str.substring(0, 10);
						getResponseRows.add(str);
					}
					if (resGeoplTypeId2.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resGeoplTypeId2.get(i)));
					}
					if (resGeoplTypeNm.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplTypeNm.get(i).toString());
					}

				}

				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
							&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
							&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
							&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
							&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())
							&& getResultDB.get(j + 5).toString().equals(getResponseRows.get(j + 5).toString())

					) {
						// ***write result to excel
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString()

						};
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_geopoliticalTypeId: ", "DB_geopoliticalTypeId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_geopoliticalTypeId: ", "DB_geopoliticalTypeId: ",
								"Response_geopoliticalTypeName: ", "DB_geopoliticalTypeName: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.pass(writableResult.replaceAll("\n", "<br />"));
						if (testCaseID != "TC_02") {
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						}
					} else {
						String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
								getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
								getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
								getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
								getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
								getResultDB.get(j + 4).toString(), getResponseRows.get(j + 5).toString(),
								getResultDB.get(j + 5).toString()

						};
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_geopoliticalTypeId: ", "DB_geopoliticalTypeId: ", "Response_effectiveDate: ",
								"DB_effectiveDate: ", "Response_expirationDate: ", "DB_expirationDate: ",
								"Response_geopoliticalTypeId: ", "DB_geopoliticalTypeId: ",
								"Response_geopoliticalTypeName: ", "DB_geopoliticalTypeName: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
						test.fail(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "");
					}
				}

			} else {
				logger.error("Total number of records not matching for Geopolitical Type record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Geopolitical Type record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}
	}

	private void countryAdressLabelValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {

		System.out.println("Value of K " + k);
		JsonPath js = new JsonPath(responsestr);

		// List<String> responseRows = js.get("data[" + k + "].addressLabels");

		// HashMap<String, String> data = new HashMap<>();
		Set<String> dataKey = new HashSet<String>();

		// data=js.get("data.countries[" + k + "].addressLabels");
		List<String> data = js.get("data.countries.addressLabels");
		if (data != null) {
			// dataKey=data.keySet();

			int n = dataKey.size();
			System.out.println("Value of n " + n);

			String strArray[] = new String[n];
			strArray = dataKey.toArray(strArray);
			for (int m = 0; m < strArray.length; m++) {

				List<String> responseRows = js.get("data.countries[" + k + "].addressLabels." + strArray[m]);

				// ***get query
				String countryAdressLabelGetQuery = query.AddressLabelsGettQueryCountryAlldata(strArray[m],
						geopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.CountryAddressLabelGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = new ArrayList<>();
				try {
					stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet result = stmt.executeQuery(countryAdressLabelGetQuery);
					result.last();
					result.beforeFirst();
					String checkNull = null;
					while (result.next()) {
						for (int d = 0; d < fields.size(); d++) {
							checkNull = result.getString(fields.get(d));
							if (StringUtils.isBlank(checkNull)) {
								checkNull = "";
							}
							getResultDB.add(checkNull.trim());
						}
					}
					stmt.close();
				} catch (SQLException e) {
					ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
							"DB Connection Exception: " + e.toString());
					test.fail("DB connection failed: " + e);
					Assert.fail("Test Failed");
				}

				if (getResultDB.size() != 0 && responseRows != null) {
					if (getResultDB.size() == responseRows.size() * fields.size()) {
						test.pass("Total number of records matching for Currency record between DB & Response:  "
								+ responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "", "",
								"Pass", "Total number of records matching for Locale record between DB & Response: "
										+ responseRows.size() + ", below are the test steps for this test case");
						List<String> getResponseRows = new ArrayList<>();

						int addressSize = js.get("data.countries[" + k + "].addressLabels.size()");
						int LocalCdSuze = js.get("data.countries[" + k + "].addressLabels." + strArray[m] + ".size()");

						for (int i = 0; i < addressSize; i++) {
							for (int j = 0; j < LocalCdSuze; j++) {

								if (StringUtils.isBlank(js.getString("data.countries.addressLabels." + strArray[m] + "["
										+ i + "].brandAddressLineDescription" + "[" + j + "]"))) {
									getResponseRows.add("");
								} else {
									getResponseRows.add(js.getString("data.countries.addressLabels." + strArray[m] + "["
											+ i + "].brandAddressLineDescription" + "[" + j + "]"));
								}

								if (StringUtils.isBlank(js.getString("data.countries.addressLabels." + strArray[m] + "["
										+ i + "].addressLineNumber" + "[" + j + "]"))) {
									getResponseRows.add("");
								} else {
									getResponseRows.add(js.getString("data.countries.addressLabels." + strArray[m] + "["
											+ i + "].addressLineNumber" + "[" + j + "]"));
								}
								if (StringUtils.isBlank(js.getString("data.countries.addressLabels." + strArray[m] + "["
										+ i + "].applicable" + "[" + j + "]"))) {
									getResponseRows.add("");
								} else {
									String aplli = js.getString("data.countries.addressLabels." + strArray[m] + "[" + i
											+ "].applicable" + "[" + j + "]");
									if (aplli.contains("true"))
										getResponseRows.add("1");
									else
										getResponseRows.add("0");

								}
							}

						}

						if (responseRows.size() == 0) {
							logger.info("0 matching records and there is no validation required");
							test.info("0 matching records and there is no validation required");
						}
						boolean rowMatch = false;
						for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
							for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
								if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
										&& getResultDB.get(j + 1).toString()
												.equals(getResponseRows.get(e + 1).toString())
										&& getResultDB.get(j + 2).toString()
												.equals(getResponseRows.get(e + 2).toString())) {
									rowMatch = true;
									// ***write result to excel
									String[] responseDbFieldValues = { getResultDB.get(j).toString(),
											getResponseRows.get(e).toString(), getResultDB.get(j + 1).toString(),
											getResponseRows.get(e + 1).toString(), getResultDB.get(j + 2).toString(),
											getResponseRows.get(e + 2).toString(), };
									String[] responseDbFieldNames = { "Response_brandAddressLineDescription: ",
											"DB_brandAddressLineDescription: ", "Response_addressLineNumber: ",
											"DB_addressLineNumber:", "Response_applicable: ", "DB_applicable: " };
									writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
											responseDbFieldNames);
									test.pass(writableResult.replaceAll("\n", "<br />"));
									ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
											writableResult, "Pass", "");
									break;
								}

							}
							if (!rowMatch) {
								test.fail("Address Label details are not matiching for the LocalCd: " + strArray[m]);
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										"Address Label details are not matiching for the LocalCd: " + strArray[m],
										"Fail", "");

							}
							rowMatch = false;
						}

					} else {
						logger.error("Total number of records not matching for AddressLabel record between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Total number of records not matching for Address Label record between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "",
								"Fail", "");
					}
				} else {
					int resNullCount;
					if (responseRows == null) {
						resNullCount = 0;
					} else {
						resNullCount = responseRows.size();
					}
					if (getResultDB.size() == 0 && resNullCount == 0) {
						test.pass("Response and DB record count is 0, so validation not needed");
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
								"Response and DB record count is 0, so validation not needed", "Pass", "");
					} else {
						test.fail("Response record count: " + resNullCount + " and DB record count: "
								+ getResultDB.size() + " is not matching");
						ex.writeExcel(fileName, "", TestCaseDescription, scenarioType,
								"NA", "", "", "", "", "Response record count: " + resNullCount
										+ " and DB record count: " + getResultDB.size() + " is not matching",
								"Fail", "");
					}

				}
			}
		} else {
			test.pass("Response and DB record count is 0, so validation not needed");
			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
					"Response and DB record count is 0, so validation not needed", "Pass", "");
		}
	}// CountryAdreessLabelValidation

	private void countryAddressValidation(String responsestr, String testCaseID, int k, String targetDate,
			String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data.countries[" + k + "].addressLabels");
		geopoliticalId = js.get("data.countries[" + k + "].geopoliticalId").toString();
		// ***get query
		String countryAdressLabelGetQuery = query.addressLabelGeoplIdGraphQLQuery(geopoliticalId);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();

		List<String> getResultDB = DbConnect.getResultSetFor(countryAdressLabelGetQuery, fields, fileName, testCaseID);
		/*try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryAdressLabelGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				for (int d = 0; d < fields.size(); d++) {
					checkNull = result.getString(fields.get(d));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					getResultDB.add(checkNull.trim());
				}
			}
			stmt.close();
		} catch (SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
					"DB Connection Exception: " + e.toString());
			test.fail("DB connection failed: " + e);
			Assert.fail("Test Failed");
		}*/
		String resMoneyFormatDesc1;
		if (getResultDB.size() != 0 && responseRows != null) {
			System.out.println("Response rows: " + responseRows.size());
			System.out.println("DB records: " + getResultDB.size() / fields.size());
			if (getResultDB.size() == responseRows.size() * fields.size()) {
				List<String> getResponseRows = new ArrayList<String>();
				/*List<String> resGeoplId = null;
				resGeoplId = js.getList("data.countries[" + k + "].addressLabels.geopoliticalId");*/
				List<String> resCurrencyNumCd = null;
				resCurrencyNumCd = js.getList("data.countries[" + k + "].addressLabels.localeCode");
				List<String> resCurrencyCd = null;
				resCurrencyCd = js.getList("data.countries[" + k + "].addressLabels.addressLineNumber");
				List<String> resMinorUnitCd = null;
				resMinorUnitCd = js.getList("data.countries[" + k + "].addressLabels.brandAddressLineDescription");
				List<String> resMoneyFormatDesc = null;
				resMoneyFormatDesc = js.getList("data.countries[" + k + "].addressLabels.applicable");
				/*
				 * List<String> resEffDate = null; resEffDate =
				 * js.getList("data.countries["+k+
				 * "].addressLabels.effectiveDate"); List<String> resExpDate =
				 * null; resExpDate = js.getList("data.countries["+k+
				 * "].addressLabels.expirationDate");
				 */
				for (int i = 0; i < responseRows.size(); i++) {

					/*if (resGeoplId.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(resGeoplId.get(i).toString());
					}*/
					if (resCurrencyNumCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resCurrencyNumCd.get(i)));
					}
					if (resCurrencyCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resCurrencyCd.get(i)));
					}
					if (resMinorUnitCd.get(i) == null) {
						getResponseRows.add("");
					} else {
						getResponseRows.add(String.valueOf(resMinorUnitCd.get(i)));
					}
					if (resMoneyFormatDesc.get(i) == null) {
						getResponseRows.add("");
					} else {
						if (String.valueOf(resMoneyFormatDesc.get(i)) == "false") {
							resMoneyFormatDesc1 = "0";
						} else {
							resMoneyFormatDesc1 = "1";
						}
						getResponseRows.add(resMoneyFormatDesc1);
					}
					/*
					 * if(resEffDate.get(i)==null) { getResponseRows.add("");
					 * }else { String str = resEffDate.get(i).toString(); //int
					 * index = str.indexOf("T"); str = str.substring(0, 10);
					 * getResponseRows.add(str); } if(resExpDate.get(i)==null) {
					 * getResponseRows.add(""); }else { String str =
					 * resExpDate.get(i).toString(); //int index =
					 * str.indexOf("T"); str = str.substring(0, 10);
					 * getResponseRows.add(str); }
					 */

				}
				if (responseRows.size() == 0) {
					logger.info("0 matching records and there is no validation required");
					test.info("0 matching records and there is no validation required");
				}
				for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
					for (int i = 0; i < getResponseRows.size(); i = i + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(i).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(i + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(i + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(i + 3).toString())
								/*&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(i + 4).toString())*/) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(i).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(i + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(i + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(i + 3).toString(),
									getResultDB.get(j + 3).toString(), /*getResponseRows.get(i + 4).toString(),
									getResultDB.get(j + 4).toString(),*/
									/*
									 * getResponseRows.get(i+5).toString(),
									 * getResultDB.get(j+5).toString(),
									 * getResponseRows.get(i+6).toString(),
									 * getResultDB.get(j+6).toString(),
									 */
							};
							String[] responseDbFieldNames = { /*"Response_geopoliticalID: ", "DB_geopoliticalID: ",*/
									"Response_localeCode: ", "DB_localeCode:", "Response_addressLineNumber: ",
									"DB_addressLineNumber:", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.pass(writableResult.replaceAll("\n", "<br />"));
							if (testCaseID != "TC_02") {
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
							}
						} /*
							 * else { String[] responseDbFieldValues = {
							 * getResponseRows.get(j).toString(),
							 * getResultDB.get(j).toString(),
							 * getResponseRows.get(j+1).toString(),
							 * getResultDB.get(j+1).toString(),
							 * getResponseRows.get(j+2).toString(),
							 * getResultDB.get(j+2).toString(),
							 * getResponseRows.get(j+3).toString(),
							 * getResultDB.get(j+3).toString(),
							 * getResponseRows.get(j+4).toString(),
							 * getResultDB.get(j+4).toString(),
							 * getResponseRows.get(j+5).toString(),
							 * getResultDB.get(j+5).toString(),
							 * getResponseRows.get(j+6).toString(),
							 * getResultDB.get(j+6).toString(), }; String[]
							 * responseDbFieldNames = { "Response_geoplId: ",
							 * "DB_geoplId: ", "Response_currencyNumberCd: ",
							 * "DB_currencyNumberCd: ", "Response_currencyCd: ",
							 * "DB_currencyCd: ", "Response_minorUnitCd: ",
							 * "DB_minorUnitCd: ",
							 * "Response_moneyFormatDescription: ",
							 * "DB_moneyFormatDescription: ",
							 * "Response_effectiveDate: ", "DB_effectiveDate: ",
							 * "Response_expirationDate: ",
							 * "DB_expirationDate: " }; writableResult =
							 * Miscellaneous.geoFieldInputNames(
							 * responseDbFieldValues, responseDbFieldNames);
							 * test.fail(writableResult.replaceAll("\n",
							 * "<br />")); ex.writeExcel(fileName, "",
							 * TestCaseDescription, scenarioType, "NA", "", "",
							 * "", "", writableResult, "Fail", "" ); }
							 */
					}
				}
			} else {
				logger.error("Total number of records not matching for Address Label record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records not matching for Address Label record between DB: "
						+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
			}
		} else {
			int resNullCount;
			if (responseRows == null) {
				resNullCount = 0;
			} else {
				resNullCount = responseRows.size();
			}
			if (getResultDB.size() == 0 && resNullCount == 0) {
				test.pass("Response and DB record count is 0, so validation not needed");
				ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
						"Response and DB record count is 0, so validation not needed", "Pass", "");
			} else {
				test.fail("Response record count: " + resNullCount + " and DB record count: " + getResultDB.size()
						+ " is not matching");
				ex.writeExcel(fileName, "",
						TestCaseDescription, scenarioType, "NA", "", "", "", "", "Response record count: "
								+ resNullCount + " and DB record count: " + getResultDB.size() + " is not matching",
						"Fail", "");
			}

		}

	}

	// ***get the values from test data sheet
	public void testDataFields(String scenarioName, String testCaseId) {
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		try {
			inputData1 = ex.getTestData(scenarioName);
		} catch (IOException e) {
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseId, "", "", "", "", "", "", "", "", "Fail", "Exception: " + e.toString());
			test.fail("Unable to retrieve the test data file/fields");
		}
		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
		geopoliticalId = inputData1.get(testCaseId).get("geopoliticalId");
		countryCode = inputData1.get(testCaseId).get("countryCd");
		threeCharacterCountryCode = inputData1.get(testCaseId).get("threeCharacterCountryCode");
		countryShortName = inputData1.get(testCaseId).get("countryShortName");
		orgStandardCode = inputData1.get(testCaseId).get("orgStandardCode");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}
}
