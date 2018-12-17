/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;






import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * HTTP的工具类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2010 2010-9-27 下午01:26:12
 * 
 * @see
 */
public class HttpUtil {

	/**
	 * post 发请求
	 * 
	 * @param urlString
	 * @param sendMsg
	 *            version, ...
	 * @return void
	 * @since v 1.0
	 */
	public static void sendMessage(String urlString, Map<String, String> sendMsg) {
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8000);
			// conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setDefaultUseCaches(false);
			OutputStream out = conn.getOutputStream();
			out.write(getParams(sendMsg).getBytes());
			out.flush();
			out.close();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			// String content = new String(sb);
			br.close();
			conn.disconnect();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String request(String urlString, Map<String, String> params) {
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8000);
			// conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setDefaultUseCaches(false);
			OutputStream out = conn.getOutputStream();
			out.write(getParams(params).getBytes());
			out.flush();
			out.close();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
			conn.disconnect();
			return sb.toString();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 组装param
	 * 
	 * @param sendMsg
	 * @return String
	 * @since v 1.0
	 */
	public static String getParams(Map<String, String> sendMsg) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String key : sendMsg.keySet()) {
			stringBuffer.append("&");
			stringBuffer.append(key);
			stringBuffer.append("=");
			stringBuffer.append(sendMsg.get(key));
		}
		return stringBuffer.substring(1);
	}

	/**
	 * 根据url地址获取response，并转成String
	 * 
	 * @param urlString
	 * @return String
	 * @since v 1.0
	 */
	public static String getResponseFromWeb(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		String rtString = "";
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8000);
			conn.setReadTimeout(6000);
			conn.setDoInput(true);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());
				// 为输出创建BufferedReader
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = "";
				// 使用循环来读取获得的数据
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				in.close();
				conn.disconnect();
				rtString = stringBuffer.toString();
			} else {
				rtString = "Connection Error. HTTP ResponseCode:"
						+ conn.getResponseCode();
			}
		} catch (SocketTimeoutException e) {
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return rtString;
	}

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
					.getContent(),"UTF-8");
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtString;
	}

	public static String getResponseByApachePOST(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		String rtString = "";
		HttpClient httpclient = new DefaultHttpClient();
		// Content-Type: text/html; charset=GB2312
		httpclient.getParams().setParameter("Content-Type",
				"text/html;charset=GBK");
		// Prepare a request object
		HttpPost httpPost = new HttpPost(urlString);
		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtString;
	}

	/**
	 * 根据url地址获取response，并转成String 只支持"UTF-8"
	 * 
	 * @param urlString
	 * @return String
	 * @since v 1.0
	 */
	public static String getResponseFromWebByGET(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		String rtString = "";
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8000);
			conn.setReadTimeout(6000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream(), "UTF-8");
				// 为输出创建BufferedReader
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = "";
				// 使用循环来读取获得的数据
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				in.close();
				conn.disconnect();
				rtString = stringBuffer.toString();
			} else {
				rtString = "Connection Error. HTTP ResponseCode:"
						+ conn.getResponseCode();
			}
		} catch (SocketTimeoutException e) {
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return rtString;
	}

	/**
	 * 根据url地址获取xml document
	 * 
	 * @param urlString
	 * @return String
	 * @since v 1.0
	 */
	public static Document getXmlFromWeb(String urlString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Document document = null;
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(8000);
			conn.setReadTimeout(10000);
			conn.setDoInput(true);
			documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(conn.getInputStream());
			conn.disconnect();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return document;
	}

	/**
	 * 测试图片是否存在
	 * 
	 * @param path
	 * @return boolean
	 * @since v 1.0
	 */
	public static boolean tryConnection(String path) {
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.getInputStream();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			conn.disconnect();
		}
	}

	/**
	 * 检测网络情况
	 * 
	 * @return boolean true 可用 false 不可用
	 * @since v 1.0
	 */
	public static boolean serverIsAvailable(String urlStr) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			// 使用HttpURLConnection打开连接
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setDoInput(true);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				return true;
			}
		} catch (SocketTimeoutException e2) {
			e2.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}
		return false;
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 */
	public static InputStream getInputStreamFromURL(String urlStr) {
		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			inputStream = urlConn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	public static boolean downloadFile(String urlString, String path,
			String fileName) {
		// set the download URL, a url that points to a file on the internet
		// this is the file to be downloaded
		URL url;
		try {
			url = new URL(urlString);
			// create the new connection
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// and connect!
			urlConnection.connect();
			// set the path where we want to save the file
			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			File file = new File(path + "\\" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			// this will be used to write the downloaded data into the file we
			// created
			FileOutputStream fileOutput = new FileOutputStream(file);

			// this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			urlConnection.getContentLength();
			// variable to store total downloaded bytes
			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			// used to store a temporary size of the
			// buffer
			// now, read through the input buffer and write the contents to the
			// file
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				// add the data in the buffer to the file in the file output
				// stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				// add up the size so we know how much is downloaded
				// this is where you would do something to report the prgress,
				// like this maybe
				// updateProgress(downloadedSize, totalSize);
			}
			// close the output stream when done
			fileOutput.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 获取session
	 * 
	 * @param urlString
	 * @return String
	 * @since v 1.0
	 */
	public static String getLoginSession(String urlString) {
		String sessionId = "";
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(24000);
			conn.setReadTimeout(24000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				Map<String, List<String>> map = conn.getHeaderFields();
				// 得到Cookie的所有内容，包括SESSIONID，在进行下次提交的时候 直接把这个Cookie的值设到头里头就行了
				// 淡然只得到SESSIONID也很简单的 ，但是有时候Set-Cookie的值有几个的
				List<String> list = (List<String>) map.get("Set-Cookie");
				if (list.size() == 0 || list == null) {
					return null;
				}
				StringBuilder builder = new StringBuilder();
				for (String str : list) {
					sessionId = builder.append(str).toString();
				}
				conn.disconnect();
				return sessionId;
			}
		} catch (SocketTimeoutException e) {
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return sessionId;
	}

	/**
	 * 根据url地址获取response，并转成String 只支持"UTF-8"
	 * 
	 * @param urlString
	 * @return String
	 * @since v 1.0
	 */
	public static String getResponseWithSession(String urlString,
			String sessionId) {
		StringBuffer stringBuffer = new StringBuffer();
		String rtString = "";
		try {
			URL url = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(24000);
			conn.setReadTimeout(24000);
			conn.setRequestMethod("GET");
			conn.addRequestProperty("Cookie", sessionId);
			conn.setDoInput(true);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream(), "UTF-8");
				// 为输出创建BufferedReader
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = "";
				// 使用循环来读取获得的数据
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				in.close();
				conn.disconnect();
				rtString = stringBuffer.toString();
			} else {
				rtString = "Connection Error. HTTP ResponseCode:"
						+ conn.getResponseCode();
			}
		} catch (SocketTimeoutException e) {
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return rtString;
	}

	



	
	/**
	 * 组装param
	 * 
	 * @param sendMsg
	 * @return String
	 * @since v 1.0
	 */
	public static String formatParams(Map<String, String> params) {
		if (null == params || params.isEmpty()) {
			return "";
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (String key : params.keySet()) {
			stringBuffer.append("&");
			stringBuffer.append(key);
			stringBuffer.append("=");
			stringBuffer.append(params.get(key));
		}
		return "?" + stringBuffer.substring(1);
	}

    /**
     * 序列化请求参数
     * @param querystring
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static Map<String, String> parseParams(String querystring) throws UnsupportedEncodingException, MalformedURLException {
    	URL url = new URL("http://bus.htwater.net?"+querystring);
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
