package com.zycus.qa.interview.util;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RestAPIUtils {
	Long timestamp_str = System.currentTimeMillis() / 1000L;

	public CloseableHttpResponse raiseNewCustomer(String stageName, int port, String URI, Map<String, String> customerInfoMap, boolean status, boolean flag) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		HttpPost request;
		CloseableHttpResponse response = null; 
		// JSON request payload, assumption, 6 params -> 2 are hardcoded as they aren't mandate fields, 4 fields of customer info
		// are retrieved from map object
		String reqBody =  "{\"first_name\":\""+customerInfoMap.get("first_name")+"\",\"marital_status\":\"SINGLE\",\"last_name\":\""+customerInfoMap.get("last_name")+"\",\"ssn\":\""+customerInfoMap.get("ssn_number")+"\",\"date_of_birth\":\""+customerInfoMap.get("date_of_birth")+"\",\"place_of_birth\":\"AUS\"}";
		System.out.println("endpoint : "+stageName+":"+port+URI);
		System.out.println("request Body : "+reqBody);
		if(status=true)
			request = createHttpPostRequest(stageName, port, URI);	
		else
			request = createWrongHttpPostRequest(stageName, port, URI+"addingrandom");	
		if(flag)
			response = publishToPostAPI(request, reqBody);
		else
			response = publishToPostAPI(request, reqBody,flag);
		System.out.println("Status:"+response.getStatusLine().toString());
		HttpEntity entity = response.getEntity();
		System.out.println("Response:"+EntityUtils.toString(entity));
		return response;
	}

	private HttpPost createHttpPostRequest(String appEndPoint, int appPort, String URI) throws URISyntaxException  {
		HttpPost request = new HttpPost(new URI("https://"+appEndPoint+":"+appPort+URI));
		return request;	
	}

	private HttpPost createWrongHttpPostRequest(String appEndPoint, int appPort, String URI) throws URISyntaxException  {
		HttpPost request = new HttpPost(new URI("https://"+appEndPoint+":"+appPort+URI));
		return request;	
	}

	@SuppressWarnings("deprecation")
	private static CloseableHttpResponse publishToPostAPI(HttpPost request, String requestbody) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {
		// Adding SSLContextBuilder & SSLConnectionSocketFactory to make https call & handling certificate issues
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustStrategy(){
			public boolean isTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();
		StringEntity params =new StringEntity(requestbody);
		request.setEntity(params);
		request.addHeader("content-type", "application/json");
		CloseableHttpResponse res=httpclient.execute(request);
		System.out.println(res);
		return res;
	}

	@SuppressWarnings("deprecation")
	private static CloseableHttpResponse publishToPostAPI(HttpPost request, String requestbody, boolean flag) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {
		// Adding SSLContextBuilder & SSLConnectionSocketFactory to make https call & handling certificate issues
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustStrategy(){
			public boolean isTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();
		StringEntity params =new StringEntity(requestbody);
		request.setEntity(params);
		CloseableHttpResponse res=httpclient.execute(request);
		System.out.println(res);
		return res;
	}

	public CloseableHttpResponse getCustomerInfo(String stageName, int port, String URI) throws URISyntaxException, ParseException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException
	{
		HttpGet request = createHttpGetRequest(stageName, port, URI);
		CloseableHttpResponse response = publishToGetAPI(request);
		System.out.println("Status:"+response.getStatusLine().toString());
		HttpEntity entity = response.getEntity();
		System.out.println("Response:"+EntityUtils.toString(entity));
		return response;
	}

	private static HttpGet createHttpGetRequest(String appEndPoint, int appPort, String URI) throws URISyntaxException{
		HttpGet request = new HttpGet(new URI("https://"+appEndPoint+":"+appPort+URI));
		return request;		
	}

	@SuppressWarnings("deprecation")
	private static CloseableHttpResponse publishToGetAPI(HttpGet request) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustStrategy(){
			public boolean isTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();
		request.addHeader("content-type", "application/json");
		CloseableHttpResponse res=httpclient.execute(request);
		System.out.println(res);
		return res;
	}

	public CloseableHttpResponse raiseFalseNewCustomer(String stageName, int port, String URI, Map<String, String> customerInfoMap) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException
	{
		// JSON request payload, assumption, 6 params -> 2 are hardcoded as they aren't mandate fields, 4 fields of customer info
		// are retrieved from map object
		String reqBody =  "{\"first_name\":\""+customerInfoMap.get("first_name")+"\",\"marital_status\":\"SINGLE\",\"last_name\":\""+customerInfoMap.get("last_name")+"\",\"ssn\":\""+customerInfoMap.get("ssn_number")+"\",\"date_of_birth\":\""+customerInfoMap.get("date_of_birth")+"\",\"place_of_birth\":\"AUS\"}";
		System.out.println("endpoint : "+stageName+":"+port+URI);
		System.out.println("request Body : "+reqBody);
		HttpPut request = createHttpPutRequest(stageName, port, URI);	
		CloseableHttpResponse response = publishToPutAPI(request, reqBody);
		System.out.println("Status:"+response.getStatusLine().toString());
		HttpEntity entity = response.getEntity();
		System.out.println("Response:"+EntityUtils.toString(entity));
		return response;
	}

	private HttpPut createHttpPutRequest(String appEndPoint, int appPort, String URI) throws URISyntaxException  {
		HttpPut request = new HttpPut(new URI("https://"+appEndPoint+":"+appPort+URI));
		return request;	
	}

	@SuppressWarnings("deprecation")
	private static CloseableHttpResponse publishToPutAPI(HttpPut request, String requestbody) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustStrategy(){
			public boolean isTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();
		StringEntity params =new StringEntity(requestbody);
		request.setEntity(params);
		request.addHeader("content-type", "application/json");
		CloseableHttpResponse res=httpclient.execute(request);
		System.out.println(res);
		return res;
	}

}
