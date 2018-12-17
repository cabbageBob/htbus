package htbus.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import htbus.entity.GenerateTokenRequestBody;
import htbus.util.DaoUtil;
import htbus.util.RedisUtil;
import htbus.util.StringUtil;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapService {
	/**2018年3月8日下午5:10:01
	 *
	 * @author Jokki
	 *  
	 */
	private Logger logger = Logger.getLogger(this.getClass());
	
	private String URL_SPILT = "/";
	
	private String MAP_SERVER_KEY = "MAP_SERVER_TOKEN_";
	
	private String MAP_THUMBNAIL_REST_URL = "/rest/services/";
	
	/**
	 * @description 地图服务器列表
	 * @param
	 * @return 
	 */
	public List<Map<String, Object>> getMapServerList(){
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			result = DaoUtil.sysdao().executeQuery("select id, map_name from map_server");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * @description 根据id获取地图服务器信息
	 * @param
	 * @return 
	 */
	public Map<String, Object> getMapServerById(String id){
		Map<String, Object> result = new HashMap<>();
		try {
			result = DaoUtil.sysdao().executeQueryObject("select * from map_server where id=?", new Object[]{id});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public boolean addMapServer(String map_name, String map_url, String username, String password){
		boolean result = false;
		try {
			result = DaoUtil.sysdao().executeSQL("insert into map_server (map_name, map_url, username, password) values(?,?,?,?)", new Object[]{map_name, map_url, username, password});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public boolean updateMapServer(String id, String map_name, String map_url, String username, String password){
		boolean result = false;
		try {
			result = DaoUtil.sysdao().executeSQL("update map_server set map_name=?,map_url=?,username=?,password=? where id=?", new Object[]{map_name, map_url, username, password, id});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public boolean deleteMapServer(String id){
		boolean result = false;
		try {
			result = DaoUtil.sysdao().executeSQL("delete from map_server  where id=?", new Object[]{id});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public Map<String, Object> generateToken(String baseUrl, String username, String password, String id){
		
		Map<String, Object> result = new HashMap<>();
		if(baseUrl == null){
			logger.error("地图服务地址不能为空");
		}else{
			GenerateTokenRequestBody requestBody = new GenerateTokenRequestBody();
			requestBody.setUsername(username);
			requestBody.setPassword(password);
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(baseUrl)
					.client(shildSSL())
					.addConverterFactory(GsonConverterFactory.create()).build();
			MapRestService service = retrofit.create(MapRestService.class);
			try {
				Call<Map<String, Object>> call = service.generateToken(requestBody.getUsername(), requestBody.getPassword(), 
						requestBody.getF(), requestBody.getClient(), requestBody.getExpiration());
				result = call.execute().body();
				if("error".equals(result.get("status"))){
					return null;
				}else{
					RedisUtil.psetExAt(MAP_SERVER_KEY + id, result.get("token").toString(), Long.parseLong(result.get("expires").toString()));
				}
			} catch (Exception e) {
				logger.error("token生成失败", e);
			}
		}
		return result;
	}
	
	
	public Map<String, Object> getMapServices(String id, String path){
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> mapServer = getMapServerById(id);
		String baseUrl = StringUtil.nvl(mapServer.get("map_url")) + URL_SPILT;
		String token = RedisUtil.get(MAP_SERVER_KEY + id);
		if(token == null){
			String username = StringUtil.nvl(mapServer.get("username"));
			String password = StringUtil.nvl(mapServer.get("password"));
			if(generateToken(baseUrl, username, password, id) != null)
			token = StringUtil.nvl(generateToken(baseUrl, username, password, id).get("token"));
		}
		if(StringUtils.isBlank(token)){
			result.put("status", "error");
			result.put("message", "token生成失败");
			return result;
		}
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(shildSSL())
				.addConverterFactory(GsonConverterFactory.create()).build();
		MapRestService service = retrofit.create(MapRestService.class);
		Call<Map<String, Object>> call = service.getMapServices(path, "json", token);
		try {
			result = call.execute().body();
			if("error".equals(result.get("status")) && ((List<String>) result.get("messages")).contains("Token Expired.") ){
				result = getMapServices(id, path);
			}
			if(path.indexOf('.') > 0 && path.indexOf("MapServer") > 0){
				StringBuilder sb = new StringBuilder();
				sb.append(StringUtil.nvl(mapServer.get("map_url"))).append(MAP_THUMBNAIL_REST_URL).append(path.replace("%2f", "/").replace(".", "/"))
				.append("?f=jsapi&token=").append(token);
				result.put("mapThumbnail", sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
		
		
	}
	
	
	
	/**
	 * @description 屏蔽https验证
	 * @param
	 * @return 
	 */
	public OkHttpClient shildSSL(){
		OkHttpClient sClient = new OkHttpClient();
		SSLContext sc = null;
		try{
			sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
			}}, new SecureRandom());
		}catch (Exception e) {
			e.printStackTrace();
		}
		HostnameVerifier hv1 = new HostnameVerifier() {
			
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		String workerClassName="okhttp3.OkHttpClient";
		try{
			Class workClass = Class.forName(workerClassName);
			Field hostnameVerifier = workClass.getDeclaredField("hostnameVerifier");
			hostnameVerifier.setAccessible(true);
			hostnameVerifier.set(sClient, hv1);
			Field sslSocketFactory = workClass.getDeclaredField("sslSocketFactory");
			sslSocketFactory.setAccessible(true);
			sslSocketFactory.set(sClient, sc.getSocketFactory());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sClient;
	}
	
	
	public static void main(String[] args) {
		//System.out.println(new MapService().getMapServices("1", "%2f"));
		System.out.println(new MapService().getMapServices("2", "BX_Layers.MapServer"));
	}
}
