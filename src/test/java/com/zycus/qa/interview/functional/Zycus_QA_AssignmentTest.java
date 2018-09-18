package com.zycus.qa.interview.functional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.zycus.qa.interview.util.RestAPIUtils;


public class Zycus_QA_AssignmentTest extends Constant {
	RestAPIUtils restUtils = new RestAPIUtils();
	Map<String, String> newCustomer= new HashMap<String, String>();
	Map<String, String> getCustomer= new HashMap<String, String>();
	Map<String, String> customerInfo = new HashMap<String, String>();
	Long timestamp = System.currentTimeMillis() / 1000L;
	public static Logger logger=null;
	// Enum Declaration for Rest API methods
	enum APIMethodType
	{
		POST,
		PUT,
		PATCH,
		GET
	};
	// Declaring the properties to run from the command line, here user can specify any inputs in the VM args,
	// eg: 1st Param has to be the name, 2nd param will be the value
	// to specify the port number of the application in the VM args, name has to be appPort, value will be 15463
	public final Integer port = Integer.valueOf(System.getProperty("appPort","15463"));
	public String environment = System.getProperty("appStage","staging2t.qa.zycus.com");

	public void delayPublish() {
		try {
			logger.info("Delay 10 seconds");
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
		}
	}


	@Test(dependsOnMethods={"isResponseValidJSONTest"})
	public void createCustomerTest() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, org.json.simple.parser.ParseException{
		String customer_Id = null;
		// Assuming 4 mandatory fields for customer ID creation, 
		// date of birth i am taking in UNIX time stamp format
		// ssn will be unique for all the customers and this will be the composite key along with customer ID to have the
		// customer details saved in db
		customerInfo.put("first_name", "eden");
		customerInfo.put("last_name", "hazard");
		customerInfo.put("ssn_number", "902345671"); 
		customerInfo.put("date_of_birth", "315532800");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,"");
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(Constant.expected_response, response.getStatusLine().toString().contains("201 Created"));
		// Reading the response JSON message if the response is 201 created
		String readLine;
		String responseBody = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		if (response.getStatusLine().getStatusCode() == 201) {
			while (((readLine = br.readLine()) != null)) {
				responseBody += "\n" + readLine;
			}
		}
		// on adding a successfully customer, the response should contain a "User registration successful" message, 
		// if the response is 200 while creating user, then the response body should contain "User already registered"
		// Assuming response id of the below format, & validating the content, "message": "User registration successful" if it is present

		/*{
	    "customerInfo": [
	             {
	                 "personId": 2345678,
	                 "country": "AUS",
	                 "message": "User registration successful"
	             }
	         ]
	     }*/

		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
		JSONArray customers = (JSONArray)jsonObject.get("customerInfo");
		for(Object customer : customers){
			if(customer instanceof JSONObject){
				softAssert.assertEquals(((JSONObject) customer).get("message"),userReg);
			}
		}
		//here, in the above jsonObject reference, the response message as a Json Object will be stored
		// m assuming customer_id will be one of the Json Element in the customer response
		if(jsonObject.get("customer_id")!=null){
			customer_Id = (String) jsonObject.get("customer_id");
		}
		// the reason behind putting the customer_Id to a map is that, for other tests it can be used as a global reference,
		// i can use the map reference for the next set of tests to retrieve the customer id
		newCustomer.put("person_id", customer_Id);
	}

	@Test(dependsOnMethods={"createCustomerTest","isResponseValidJSONTest"})
	public void getCustomerTest() throws KeyManagementException, ParseException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, org.json.simple.parser.ParseException{
		// Get method, adding the customer id which got created from the above customer test method
		// adding it from the map reference
		CloseableHttpResponse response = restUtils.getCustomerInfo(environment, port, Constant.getCustomerURI+"?customer_id="+newCustomer.get("person_id"),true);
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(expected_response, response.getStatusLine().toString().contains("200 OK"));
		String readLine;
		String responseBody = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		if (response.getStatusLine().getStatusCode() == 200) {
			while (((readLine = br.readLine()) != null)) {
				responseBody += "\n" + readLine;
			}
		}
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
		// validating if the response fetched from the GET request is matching with the details provided while adding customer
		softAssert.assertEquals(jsonObject.get("first_name"), customerInfo.get("first_name"));
		softAssert.assertEquals(jsonObject.get("last_name"), customerInfo.get("last_name"));
		softAssert.assertEquals(jsonObject.get("ssn_number"), customerInfo.get("ssn_number"));
		softAssert.assertEquals(jsonObject.get("date_of_birth"), customerInfo.get("date_of_birth"));

	}

	@Test(priority=0)
	public void isResponseValidJSONTest() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException{
		customerInfo.put("first_name", "eden");
		customerInfo.put("last_name", "hazard");
		customerInfo.put("ssn_number", "902345671"); 
		customerInfo.put("date_of_birth", "315532800");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,"");
		delayPublish();
		Assert.assertTrue(isJSONValid(response.toString()));
		// validating response header
		Header[] headers = response.getAllHeaders();
		for(Header header : headers){
			if(header.getName().contains("Content-Type")&&(header.getValue().contains("application/json"))){
				Assert.assertTrue(header.getName().contains("Content-Type")&&(header.getValue().contains("application/json"))); 
				break;
			}
		}

	}

	private boolean isJSONValid(String responseString) {
		try {
			new JSONObject(responseString);
		} catch (JSONException ex) {
			try {
				new JSONArray(responseString);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}


}
