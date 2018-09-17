package com.zycus.qa.interview.functional;

public class Constant {

	public final static String createCustomerURI = "/v1/saas/createcustomer";
	public final static String getCustomerURI = "/v1/saas/saas/getcustomer";
	public final static String expected_response = "200 OK";
	public final static String userReg = "User registration successful";
	public final static long errorCode = 1001;
	public final static String duplicate_Customer = "Already user exists. Not allowed";
	public final static String remove_req_header = "406 Not acceptable";
	public final static String client_error = "500 Internal Server Error";
	public final static String invalid_Resource = "404 Not Found";
	public final static String bad_method = "405 Method Not Allowed";

}
