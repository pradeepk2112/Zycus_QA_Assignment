package com.zycus.qa.interview.functional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Zycus_QA_AssignmentTest_NegativeCases extends Zycus_QA_AssignmentTest {
	Zycus_QA_AssignmentTest obj_assignmentTest = new Zycus_QA_AssignmentTest();

	// bad method been passed
	@Test
	public void testMethodNotAllowed() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException{
		customerInfo.put("first_name", "eden");
		customerInfo.put("last_name", "hazard");
		customerInfo.put("ssn_number", "902345671"); 
		customerInfo.put("date_of_birth", "315532800");
		// using PUT instead of POST to add a new customer
		CloseableHttpResponse response = restUtils.raiseFalseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo);
		obj_assignmentTest.delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(bad_method, response.getStatusLine().toString());
	}

	// URI will be provided incorrectly
	@Test
	public void testInvalidResource() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		customerInfo.put("first_name", "eden");
		customerInfo.put("last_name", "hazard");
		customerInfo.put("ssn_number", "902345671"); 
		customerInfo.put("date_of_birth", "315532800");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,"");
		delayPublish();
		// testing with api given incorrectly
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(invalid_Resource, response.getStatusLine().toString());
		String readLine;
		String responseBody = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		if ((response.getStatusLine().getStatusCode() == 405)) {
			while (((readLine = br.readLine()) != null)) {
				responseBody += "\n" + readLine;
			}
		}
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
		Iterator<String> itr = jsonObject.keys();
		while(itr.hasNext()){
			String key = itr.next();
			if(key.equals("name")){
				softAssert.assertEquals("INVALID_RESOURCE_ID", jsonObject.get(key));
				break;
			}
		}
	}

	// The below test case validates for 500 Internal server Error, this can be achieved by passing in a bad request payload
	// passing a invalid request payload while creating customer & validating the restError and error code 1001
	@Test(testName="")
	public void testClientSideError() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		customerInfo.put("first_name", "eden");
		customerInfo.put("last_name", "hazard");
		customerInfo.put("ssn_number", "902345671"); 
		customerInfo.put("date_of_birth", "315532800");
		customerInfo.put("weird_input", "xyz");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,"");
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(client_error, response.getStatusLine().toString());
		// Reading the response JSON message if the response is 200 or 201 created
		String readLine;
		String responseBody = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		if (response.getStatusLine().getStatusCode() == 201) {
			while (((readLine = br.readLine()) != null)) {
				responseBody += "\n" + readLine;
			}
		}
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
		JSONArray customers = (JSONArray)jsonObject.get("restError");
		for(Object customer : customers){
			if(customer instanceof JSONObject){
				softAssert.assertEquals(errorCode,((JSONObject) customer).get("errorCode"));
			}
		}
	}

	// passing in the same ssn_number as to the customer created initially
	@Test(testName="")
	public void testDuplicateCustomer() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		customerInfo.put("first_name", "jimmy");
		customerInfo.put("last_name", "anderson");
		customerInfo.put("ssn_number", customerInfo.get("ssn_number")); 
		customerInfo.put("date_of_birth", "315532800");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,"");
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(expected_response, response.getStatusLine().toString());
		// Reading the response JSON message if the response is 200 or 201 created
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
		JSONArray customers = (JSONArray)jsonObject.get("customerInfo");
		for(Object customer : customers){
			if(customer instanceof JSONObject){
				softAssert.assertEquals(duplicate_Customer,((JSONObject) customer).get("person_ID"));
			}
		}
	}

	@Test(testName="")
	public void testEditReqHeader() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		customerInfo.put("first_name", "vincent");
		customerInfo.put("last_name", "kompany");
		customerInfo.put("ssn_number", customerInfo.get("ssn_number")); 
		customerInfo.put("date_of_birth", "315532833");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,false,"");
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(remove_req_header, response.getStatusLine().toString());
	}

	@Test(testName="")
	public void testUnsupportedMediaType(ITestResult result) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		String methodName = result.getMethod().getMethodName();
		customerInfo.put("first_name", "vincent");
		customerInfo.put("last_name", "kompany");
		customerInfo.put("ssn_number", customerInfo.get("ssn_number")); 
		customerInfo.put("date_of_birth", "315532833");
		CloseableHttpResponse response = restUtils.raiseNewCustomer(environment, port, Constant.createCustomerURI, customerInfo,true,true,methodName);
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(unsupported_Media_Type, response.getStatusLine().toString());
		String readLine;
		String responseBody = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		if (response.getStatusLine().getStatusCode() == 415) {
			while (((readLine = br.readLine()) != null)) {
				responseBody += "\n" + readLine;
			}
		}
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
		softAssert.assertEquals(unsupported_Media_Type_name, jsonObject.get("name").toString());
		softAssert.assertEquals(unsupported_Media_Type_message, jsonObject.get("message").toString());

	}

	@Test(testName="")
	public void testBadRequest() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		CloseableHttpResponse response = restUtils.getCustomerInfo(environment, port, Constant.getCustomerURI+"?customer_id="+newCustomer.get("person_id"),false,"");
		delayPublish();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(bad_Request, response.getStatusLine().toString());

	}

	// this test case is making a ftp call to the service instead of http or https, no response state will be returned
	// here response will take time, as its been hit from a different protocol, so that's why delay publish is in a loop
	@Test(expectedExceptions=NullPointerException.class)
	public void testNoResponse(ITestResult result) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException, IOException, ParseException{
		String methodName = result.getMethod().getMethodName();
		CloseableHttpResponse response = restUtils.getCustomerInfo(environment, port, Constant.getCustomerURI+"?customer_id="+newCustomer.get("person_id"),true,methodName);
		int i=0;
		while(i<=2){
			delayPublish();
			i++;
		}
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertNull(response.getStatusLine().toString());

	}


}
