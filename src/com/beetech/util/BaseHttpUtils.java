package com.beetech.util;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.common.base.Charsets;

public class BaseHttpUtils {
	
	private static RequestConfig defaultRequestConfig = RequestConfig.custom()
		    .setSocketTimeout(30000)
		    .setConnectTimeout(30000)
		    .setConnectionRequestTimeout(60000)
		    .setStaleConnectionCheckEnabled(true)
		    .build();
	
	private	static CloseableHttpClient httpClient = HttpClients.custom()
		    .setDefaultRequestConfig(defaultRequestConfig)
		    .build();
	
	
	public static String httpClient(String url) throws IOException {
		HttpGet req = null;
		CloseableHttpResponse httpResponse = null;
		try {
			req = new HttpGet(url);

			httpResponse = httpClient.execute(req);
			HttpEntity entity = httpResponse.getEntity();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String retStr = EntityUtils.toString(entity, Charsets.UTF_8);
				EntityUtils.consume(entity);
				return retStr;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(httpResponse != null){
				httpResponse.close();
			}
		}
		return null;
	}
}
