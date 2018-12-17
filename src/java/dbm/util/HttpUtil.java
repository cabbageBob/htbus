package dbm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtil {
	public static String getResponseByApacheGET(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		String rtString = "";
		HttpClient httpclient = new DefaultHttpClient();
		// Prepare a request object
		HttpGet httpget = new HttpGet(urlString);
		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Examine the response status
			InputStreamReader in = new InputStreamReader(response.getEntity()
					.getContent(), "UTF-8");
			// 为输出创建BufferedReader
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = "";
			// 使用循环来读取获得的数据
			while (((inputLine = buffer.readLine()) != null)) {
				stringBuffer.append(inputLine);
			}
			in.close();
			rtString = stringBuffer.toString();
		} catch (ClientProtocolException e) {
			
		} catch (IOException e) {
			
		}
		return rtString;
	}
}
