package htbus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import htbus.entity.FieldEntity;


/**2017年11月17日上午10:48:35
 * @author Jokki
 * @description
 */
/**2017年11月17日上午10:50:27
 * @author Jokki
 * @description
 */
public class ElasticsearchUtil {
	/**2017年11月16日下午6:31:57
	 *
	 * @author Jokki
	 */
	private static final Logger logger = Logger.getLogger(ElasticsearchUtil.class);
	/**
	 * elasticsearch节点1的地址
	 */
	private static String ELASTICSEARCH_NODE1_URL = ConfigUtil.config("elasticsearch_node1_url");
	
	/**
	 * elasticsearch节点1的端口
	 */
	private static Integer ELASTICSEARCH_NODE1_PORT = Integer.parseInt(ConfigUtil.config("elasticsearch_node1_port"));
	
	
	/**
	 * elasticsearch bulk api format noid
	 */
	private static String BULK_FORMAT_NOID = "{\"%s\" : {\"_index\" : \"%s\", \"_type\" : \"%s\"}}%n";
	
	/**
	 * elasticsearch bulk api format with id
	 */
	private static String BULK_FORMAT_ID = "{\"%s\" : {\"_index\" : \"%s\", \"_type\" : \"%s\", \"_id\" : \"%s\"}}%n";
	
	/**
	 * bulk api
	 */
	private final static String BULK_API = "_bulk";
	
	/**
	 * search api
	 */
	private final static String SEARCH_API = "_search";
	/**
	 * 换行符
	 */
	private final static String NEWLINES = "\n";
	/**
	 * 斜杠
	 */
	private final static String SLASH = "/";
	
	/**2017年11月17日下午3:10:55
	 * @description 批量导入(随机ID)
	 * @param list(对象数组)
	 * @param index(索引)
	 * @param type(类型)
	 * @return 
	 */
	public static <T> Boolean bulkImport(List<T> list, String index, String type) throws IOException{
		String path = SLASH + index + SLASH + type + SLASH + BULK_API;
		RestClient client = RestClient.builder(new HttpHost(ELASTICSEARCH_NODE1_URL, 
				ELASTICSEARCH_NODE1_PORT, "http")).build();
		String actionMetaData = String.format(BULK_FORMAT_NOID, "index", index, type);
		List<String> bulkData = new ArrayList<>();  //a list of your documents in JSON strings
		for(T t : list){
			bulkData.add(JSON.toJSONString(t));
		}
		StringBuilder bulkRequestBody = new StringBuilder();
		for(String bulkItem : bulkData){
			bulkRequestBody.append(actionMetaData);
			bulkRequestBody.append(bulkItem);
			bulkRequestBody.append(NEWLINES);
		}
		HttpEntity entity = new NStringEntity(bulkRequestBody.toString(), ContentType.APPLICATION_JSON);
		try{
			Map<String, String> emptyMap = Collections.emptyMap();
			Response response = client.performRequest("POST", path, emptyMap, entity);
			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		}catch(Exception e){
			return false;
		}finally{
			client.close();
		}
	}
	
	/**2017年11月17日下午3:12:14
	 * @description (异步)定制化导入,固定ID
	 * @param
	 * @return 
	 */
	public static void bulkImportFieldEntity(List<FieldEntity> list, String index, String type) throws Exception{
		String path = SLASH + index + SLASH + type + SLASH + BULK_API;
		final RestClient client = RestClient.builder(new HttpHost(ELASTICSEARCH_NODE1_URL, 
				ELASTICSEARCH_NODE1_PORT, "http")).build();
		Map<String, String> bulkData = new HashMap<>();  //a list of your documents in JSON strings
		for(FieldEntity t : list){
			bulkData.put(t.getField() + t.getTbname() + t.getDbname() + t.getInstanceId(), JSON.toJSONString(t));
		}
		StringBuilder bulkRequestBody = new StringBuilder();
		for(String key : bulkData.keySet()){
			String indexMetaData = String.format(BULK_FORMAT_ID, "index", index, type, key);
			bulkRequestBody.append(indexMetaData);
			bulkRequestBody.append(bulkData.get(key));
			bulkRequestBody.append(NEWLINES);
		}
		HttpEntity entity = new NStringEntity(bulkRequestBody.toString(), ContentType.APPLICATION_JSON);
		Map<String, String> emptyMap = Collections.emptyMap();
		ResponseListener responseListener = new ResponseListener() {
				@Override
				public void onSuccess(Response arg0) {
					try {
						logger.info("数据字典导入成功");
						//System.out.println(EntityUtils.toString(arg0.getEntity()));
					} finally{
						try {
							client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				@Override
				public void onFailure(Exception arg0) {
					logger.info("数据字典导入失败");
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		client.performRequestAsync("POST", path, emptyMap, entity, responseListener);
		//return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}
	
	/**2017年11月17日下午4:25:01
	 * @description 多条件查询
	 * @param eql
	 * @return 
	 */
	public static <T> List<T> combinatorialQuery(String eql, Class<T> classT, String index, String type){
		String path = SLASH + index + SLASH + type + SLASH + SEARCH_API;
		RestClient client = RestClient.builder(new HttpHost(ELASTICSEARCH_NODE1_URL, 
				ELASTICSEARCH_NODE1_PORT, "http")).build();
		try {
			HttpEntity entity = new NStringEntity(eql, ContentType.APPLICATION_JSON);
			Map<String, String> params = new HashMap<>();
			params.put("size", "10000");
			Response response = client.performRequest("GET", path, params, entity);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = response.getEntity();
				return getSource(httpEntity, classT);
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}finally{
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private static <T> List<T> getSource(HttpEntity entity, Class<T> clazz) throws IOException, ParseException {
		 Args.notNull(entity, "Entity");
		InputStream instream = entity.getContent();
		List<T> list = new ArrayList<>();
		if (instream == null) {
			return list;
		}
		try {
			JSONObject jsonObject = JSON.parseObject(instream, JSONObject.class);
			JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
			for(int i = 0;i < jsonArray.size();i++){
				JSONObject source = jsonArray.getJSONObject(i).getJSONObject("_source");
				T t = JSON.parseObject(source.toJSONString(), clazz);
				list.add(t);
			}
		} finally {
			instream.close();
		}
		return list;
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		final RestClient restClient = RestClient.builder(new HttpHost(ELASTICSEARCH_NODE1_URL, 
				ELASTICSEARCH_NODE1_PORT, "http")).build();
		Response response = restClient.performRequest("GET", "/", Collections.singletonMap("pretty", "true"));
		System.out.println(EntityUtils.toString(response.getEntity()));
		//Thread.sleep(10000L);
		restClient.close();
		/*System.out.println(JSON.toJSONString(combinatorialQuery("{\"query\":{\"bool\":{\"should\":[{\"match\":{\"fieldDescription\":\"站\"}},{\"match\":{\"field\":\"stcd\"}}]}}}", 
				FieldEntity.class, "test1", "fieldSearch"), SerializerFeature.WriteMapNullValue));*/
		FieldEntity entity = new FieldEntity("stcd", null, "1", "HTGQ", "工情库", "DZ_ZM", "闸门基本信息");
		List<FieldEntity> list = new ArrayList<>();
		list.add(entity);
		System.out.println(bulkImport(list, "test1", "fieldSearch"));
	}
	
}
