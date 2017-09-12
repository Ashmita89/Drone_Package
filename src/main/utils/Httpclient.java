package main.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Httpclient {
public static String callClient(String url) throws IOException {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	HttpGet httpGet = new HttpGet(url);
	CloseableHttpResponse response = httpclient.execute(httpGet);
	String responseBody;
	try {
	    //System.out.println(response.getStatusLine());
		StatusLine statusLine=response.getStatusLine();
		if (statusLine.getStatusCode() >= 300) {
			    throw new HttpResponseException(statusLine.getStatusCode(),statusLine.getReasonPhrase());
		}
	    HttpEntity entity = response.getEntity();
	    if (entity == null) {
	        return (null);
	    } 
	    responseBody=EntityUtils.toString(entity,"UTF-8");
	    // and ensure it is fully consumed
	    EntityUtils.consume(entity);
	} finally {
	    response.close();
	}
	return responseBody;
	
}
}
