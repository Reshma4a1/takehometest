package com.vlocity.qe;

import java.util.logging.Level;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author reshma shaik
 *
 * This class verifies elements on the wikipedia homepage.
 */
public class WikipediaTest {

	private Logger log = LoggerFactory.getLogger(WikipediaTest.class);

	private WebDriver driver;
	private ElementFinder finder;

	@BeforeClass
	public void setup() {

		/*
		 * If the following driver version doesn't work with your Chrome version
		 * see https://sites.google.com/a/chromium.org/chromedriver/downloads
		 * and update it as needed.
		 */

		WebDriverManager.chromedriver().version("74.0.3729.6").setup();
		driver = new ChromeDriver();
		finder = new ElementFinder(driver);
		driver.get("https://www.wikipedia.org/");
		driver.manage().window().maximize();
	  }
	
	
	/*
	  * @Description : method designed for checking we are in wikipeida page.
	  * @Variable sloganClass : class name for header "The Free Encyclopedia"
	  * @Variable slogan : web element for header "The Free Encyclopedia"
	  * @statement Assert : To validate the actual and expected output for the header "The Free Encyclopedia"
	  * 
	  */

	@Test
	public void sloganPresent() {

		String sloganClass = "localized-slogan";
		WebElement slogan = finder.findElement(By.className(sloganClass));

		Assert.assertNotNull(slogan, String.format("Unable to find slogan div by class: %s", sloganClass));

		log.info("Slogan text is {}", slogan.getText());

		Assert.assertEquals(slogan.getText(), "The Free Encyclopedia");
	}
	

	/*
	  * Description : method designed for checking the languages which will get data from data provider
	  * @param : languagedata
	  * @Variable language : Common xpath for all the languages
	  * @Variable element : web element for the languages
	  * @statement Assert : To validate the actual and expected output for the langauges data
	  * 
	  */
	

	@Test(dataProvider = "languages")
	public void validate_languages(String languagedata) {
		String language = "//strong[text()='" + languagedata + "']";

		WebElement languageelement = finder.findElement(By.xpath(language));

		Assert.assertNotNull(languageelement,String.format("Unable to find element  by xapth: %s", language));
		log.info("Language text is {}", languageelement.getText());

		Assert.assertEquals(languageelement.getText(), languagedata);

	}

	/*
	  * Description : method designed for checking status code after clicking on languages ,which will get data from data provider
	  * @param : languagesdata
	  * @Variable languages : Common xpath for all the languages
	  * @RestAssured : For getting the status code of the response
	  * @statement Assert : To validate the actual and expected output status code
	  * 
	  */

	@Test(dataProvider = "languages")
	public void validate_languages_statuscode(String languagesdata) {
		String languages = "//strong[text()='" + languagesdata + "']";
		finder.findElement(By.xpath(languages)).click(); 
		log.info("Clicked on " +languagesdata +" successfully" );
		String url = driver.getCurrentUrl();
		
		/*ChromeOptions options = new ChromeOptions();
		DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, options);
		LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		LogEntries logs = driver.manage().logs().get("PERFORMANCE");
		System.out.println(logs);*/
		
		Response resp = RestAssured.get(url);
		int code = resp.getStatusCode();
		Assert.assertEquals(code, 200);
		System.out.println("Verified status code for : " +languagesdata );
		driver.navigate().back();
		

	}
	
	/*
	  * Description : method designed to close the browser
	  * 
	  */
	

	@AfterClass
	public void closeBrowser() {

		if (driver != null) {
			driver.close();
			log.info("Closed the browser");
		}
	}

	
	/*
	  * Description : method designed using data provider annotation to provide the data to the methods
	  * 
	  */
	@DataProvider(name = "languages")
	public Object[][] provideData() {
		return new Object[][] { { "English" }, { "Español" } };
	}

}
