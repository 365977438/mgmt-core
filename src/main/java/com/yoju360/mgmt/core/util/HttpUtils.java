package com.yoju360.mgmt.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private static CloseableHttpClient httpClient;
	private static Logger log = LoggerFactory.getLogger(HttpUtils.class);
	
	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);

		httpClient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
	}
	public static final String postRaw(String raw, String url) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);

		httpPost.setEntity(new StringEntity(raw,"UTF-8"));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		
		int status = response.getStatusLine().getStatusCode();//response.getStatusLine().getReasonPhrase()
		
		if (status == 200) {
			try {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				log.debug(url + ": " + result);
				EntityUtils.consume(entity);
				return result;
			} finally {
	            response.close();
	        }
		}
		
		return null;
	}
	
	public static final String post(Map<String, String> params, String url) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<>();
		
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		
		int status = response.getStatusLine().getStatusCode();//response.getStatusLine().getReasonPhrase()
		
		if (status == 200) {
			try {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				log.debug(url + ": " + result);
				EntityUtils.consume(entity);
				return result;
			} finally {
	            response.close();
	        }
		}
		
		return null;
	}

	public static final String get(Map<String, String> params, String url) throws ClientProtocolException, IOException {
		if (params!=null) {
			if (!url.endsWith("?"))
				url += "?";
			
			List<NameValuePair> nvps = new ArrayList<>();
			
			for (String key : params.keySet()) {
				nvps.add(new BasicNameValuePair(key, params.get(key)));
			}
			
			String paramString = URLEncodedUtils.format(nvps, "UTF-8");

		    url += paramString;
		}
		
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		
		int status = response.getStatusLine().getStatusCode();//response.getStatusLine().getReasonPhrase()
		
		if (status == 200) {
			try {
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				log.debug(url + ": " + result);
				EntityUtils.consume(entity);
				response.close();
				return result;
			} finally {
	            response.close();
	        }
		}
		
		return null;
	}
	
	public static void close() throws IOException {
		httpClient.close();
	}
	
	public void finalize() {
		try {
			httpClient.close();
		} catch (IOException e) {
		}
	}
}
