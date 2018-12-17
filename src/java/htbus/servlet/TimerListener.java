package htbus.servlet;

import htbus.service.DataService;
import htbus.service.Service;
import htbus.service.UserService;
import htbus.util.DataSourceUtil;
import htbus.util.RedisUtil;
import htbus.util.StringUtil;
import htbus.util.WebServiceHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import cn.miao.framework.util.HttpUtil;

/**
 * Application Lifecycle Listener implementation class TimerListener
 *
 */
public class TimerListener implements ServletContextListener {
	
	//public static int timercnt = 24*60 - 1;
	
	/**
	 * 共享数据，只能有一个线程能写改数据，但可以有多个线程同时读该数据
	 */
	private static List<Map<String,Object>> service_list = new ArrayList<Map<String,Object>>();
	
	/**
	 * 如果缓存的剩余生存时间小于这个时间，将重新刷新缓存(秒)
	 */
	private static long REFRESH_TIME = 60;
	
	/**
	 * 读写锁
	 */
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	private Logger logger = Logger.getLogger(TimerListener.class);
    /**
     * Default constructor. 
     */
    public TimerListener() {
    	super();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	//初始化数据资源SQL
    	DataSourceUtil.init();
    	
        ScheduledExecutorService service = Executors.newScheduledThreadPool(6);
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间  
        service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try{
					writeServiceList();
				}catch(Exception e){
					logger.error(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
        
        service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
            	try{
            		//处理定时缓存
                	dealServiceCacheJob();
				}catch(Exception e){
					logger.error(e);
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
        
        
        service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
            	try{
            		//刷新elasticsearch字段信息
                	refreshFields();
                	//刷新数据资源表和字段信息
                	new DataService().refreshTableField();
				}catch(Exception e){
					logger.error(e);
				}
			}
		}, 0, 1, TimeUnit.HOURS);
        
        service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
            		//处理巡查接口状态：每天一次，发生在晚上0点
                dealCheckServiceStatus();
			}
		}, 0, 1, TimeUnit.MINUTES);
        
        service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
            	try{
            		//刷新数据资源统计信息：每天一次，发生在晚上1点
            		refreshStaDataResourceInfo();
				}catch(Exception e){
					logger.error(e);
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
        
    }
    
    private List<Map<String, Object>> getServiceList(){
    	rwl.readLock().lock();//上读锁，其他线程只能读不能写
    	try{
    		return service_list;
    	}finally{
    		rwl.readLock().unlock();
    	}
    }
    
    private void writeServiceList(){
    	rwl.writeLock().lock();//上写锁，不允许其他线程读也不允许写
    	//查询到所有接口
		service_list = new Service().getServiceList();
		logger.info(Thread.currentThread().getName() + ": 服务信息刷新成功!");
		rwl.writeLock().unlock();
    }
	
    //处理定时缓存
    private void dealServiceCacheJob(){
    	ExecutorService es = Executors.newFixedThreadPool(5);
    	try{
    		for(final Map<String,Object> service : getServiceList()){
        		es.submit(new Runnable() {
    				@Override
    				public void run() {
    					try{
    						//int interval = (int)service.get("cache_job_interval");
    			    		int effective = (int)service.get("cache_effective");
    			    		if(effective>0){
    			    			Long ttl = RedisUtil.ttl(service.get("path").toString());
    			    			if( ttl != -1 && (ttl == null || ttl < REFRESH_TIME)){
    			    				String data = proxy(service);
    			    				RedisUtil.set(service.get("path").toString(), data, effective);
    			    				logger.info(service.get("path") + ": 缓存刷新成功");
    			    				Service.setCacheTime(Integer.valueOf(service.get("serviceid").toString()));
    			    			}
    			    		}
    					}catch(Exception e){
    						logger.info("##############################");
    						logger.info(service.get("path") + ": 缓存刷新失败");
    						logger.error(e);
    						logger.info("##############################");
    					}
    				}
    			});
        	}
    	}finally{
    		es.shutdown();
    	}
    }
    
    //处理巡查接口状态：每天一次，发生在晚上0点
    private void dealCheckServiceStatus(){
    	Calendar cal=Calendar.getInstance();
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	int minute = cal.get(Calendar.MINUTE); 
    	if(hour == 0 && minute == 0){
    		for(Map<String,Object> service : getServiceList()){
    			try{
    				int res_status = 0;
            		
            		String from_url = service.get("from_url").toString();
            		int serviceid = (int)service.get("serviceid");
            		String type = service.get("from_request_type").toString();
            		Map<String,String> params = getParams(service);
            		
            		if(from_url.indexOf("?wsdl") > 0){
        				String url = from_url.substring(0,from_url.lastIndexOf("?"));
        				WebServiceHelper.getInstance(url).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1),params);
        				res_status = 1;
        			}else if(from_url.indexOf(".asmx") > 0){
    					String url = from_url.substring(0,from_url.lastIndexOf("/"));
    					WebServiceHelper.getInstance(url).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1), params);
    					res_status = 1;
    				}else{
        				if(type.equals("POST")){
        					HttpUtil.getResponseByApachePOST(paramsToQuerystring(from_url,params));
        					res_status = 1;
        				}else{
       						HttpUtil.getResponseByApacheGET(paramsToQuerystring(from_url,params));
       						res_status = 1;
        				}
        			}
            		Service.status(serviceid, res_status);
            		logger.info(service.get("path") + ": 巡查接口状态成功!");
    			}catch(Exception e){
    				logger.info(service.get("path") + ": 巡查接口状态失败!");
    				logger.error(e.getMessage(), e);
    			}
        		
        	}
    	}
    }
    
    Map<String,String> getParams(Map<String,Object> service){
    	List<Map<String,Object>> list = (List<Map<String,Object>>)service.get("params");
    	if(list.size()>0){
    		Map<String,String> params = new HashMap<String,String>();
    		for(Map<String,Object> map : list){
    			params.put(map.get("param").toString(), StringUtil.nvlToBlank(map.get("param_sample")));
    		}
    		return params;
    	}else{
    		return null;
    	}
    }
    
    /**
	 * 为HTTP请求组装带有参数的完整请求地址
	 * @param from_url
	 * @param request
	 * @return
	 */
	String paramsToQuerystring(String from_url,Map<String,String> params){
		String result = "";
		String p = "";
			if (null == params || params.isEmpty()) {
				return from_url;
			}
			StringBuffer stringBuffer = new StringBuffer();
			
			for(String key : params.keySet()){
				String value = params.get(key);
				
				stringBuffer.append("&");
				stringBuffer.append(key);
				stringBuffer.append("=");
				stringBuffer.append(null == value ? "" : value);
			}

			p = (null==stringBuffer || stringBuffer.length()==0)?"":stringBuffer.substring(1);
			return result = from_url+"?"+ p;
	}
    
    private String proxy(Map<String,Object> service) throws Exception{
    	String result = "";
		
		String from_url = StringUtil.replaceBlank(service.get("from_url").toString());
		String type = service.get("from_request_type").toString();
		Map<String,String> params = getParams(service);
		if(from_url.indexOf("?wsdl") > 0){
			String url = from_url.substring(0,from_url.lastIndexOf("?"));
				result = WebServiceHelper.getInstance(url).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1),params);
		}else if(from_url.indexOf(".asmx") > 0){
			String url = from_url.substring(0,from_url.lastIndexOf("/"));
			WebServiceHelper.getInstance(url).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1), params);
		}else{
			if(type.equals("POST")){
				result = HttpUtil.getResponseByApachePOST(paramsToQuerystring(from_url,params));
			}else{
				result = HttpUtil.getResponseByApacheGET(paramsToQuerystring(from_url,params));
			}
		}
    	return result;
    }
    
    private void refreshFields() throws Exception{
		new DataService().importFieldEntity();
		logger.info(Thread.currentThread().getName() + ": elasticsearch字段信息刷新成功!");
    }
    
    public void refreshStaDataResourceInfo(){
    	Calendar cal=Calendar.getInstance();
    	int hour = cal.get(Calendar.HOUR_OF_DAY);
    	int minute = cal.get(Calendar.MINUTE); 
    	if(hour == 1 && minute == 0){
    		try {
				new DataService().staDataResourceInfo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		logger.info(Thread.currentThread().getName() + ": 数据资源统计信息刷新成功!");
    	}
		
	}
}
