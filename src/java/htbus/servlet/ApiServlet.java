package htbus.servlet;

import htbus.service.Service;
import htbus.service.UserService;
import htbus.util.DaoUtil;
import htbus.util.RedisUtil;
import htbus.util.WebServiceHelper;
import htbus.websocket.MyWebSocket;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.util.HttpUtil;

/**
 * Servlet implementation class ApiServlet
 */
public class ApiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(UserService.class);
	final static ListeningExecutorService leservice = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApiServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProxy(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProxy(request,response);
	}
	
	protected void doProxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long st = System.currentTimeMillis();
			
		//从地址中找到方法名、提供者编号、token
		String uri = request.getRequestURI();
		String method = uri.substring(uri.lastIndexOf("/")+1);
		String from_appid = uri.substring(uri.lastIndexOf("/api/")+5,uri.lastIndexOf("/"));
		String token = request.getHeader("apptoken");
		String res_remark = "";//记录接口响应是否正常（文字描述，用于记录到日志）
		int res_status = 0;//记录接口响应状态是否正常 1正常 0不正常（从缓存里取数据不算数的啊，这个状态用来标记这个接口目前好坏）
		int serviceid=0;
		//验证权限
		int auth = checkPower(token,from_appid,method);
		if(auth>0){
			//获取接口信息
			Map<String,Object> service = getServiceInfo(from_appid, method);
			String response_type = service.get("response_type").toString();
			serviceid = (int)service.get("serviceid");
			String data = "";
			//判断是否需要从缓存读取
			int effective = (int)service.get("cache_effective");
			if(effective>0){//从缓存读取 key的拼接规则：from_appid.method 比如 4.getRain
				String key = "/api/" + from_appid +"/"+method;
				data = RedisUtil.get(key);
				res_remark = data == null ?"缓存服务器响应异常或者缓存过期":"缓存服务器响应正常";//第一个判断结果永远不会被记录
				//缓存为空
				if(data == null){
					//设置对某个key只允许一个线程查询数据和写缓存，其他线程等待
					//设置3min的超时，防止del失败，且防止缓存过期后一直不能刷新
					if(RedisUtil.setnx(key + "_mutex", "1", 3 * 60) == 1){//设置成功
						String type = service.get("from_request_type").toString();
						String from_url = service.get("from_url").toString();
						if(from_url.indexOf("?wsdl") > 0){
							String url = from_url.substring(0,from_url.lastIndexOf("?"));
							String originMethod = from_url.substring(from_url.lastIndexOf("/")+1);
							Map<String,String> params = getParams(request);
							try{
								data = WebServiceHelper.getInstance(url).getResultByMethod(originMethod, params);
								res_remark = "源端响应正常";
								res_status = 1;
							}catch(Exception e){
								res_remark = "源端响应异常";
							}
						}else if(from_url.indexOf(".asmx") > 0){
							String url = from_url.substring(0,from_url.lastIndexOf("/"));
							String originMethod = from_url.substring(from_url.lastIndexOf("/")+1);
							Map<String,String> params = getParams(request);
							try{
								data = WebServiceHelper.getInstance(url).getResultByMethod(originMethod, params);
								res_remark = "源端响应正常";
								res_status = 1;
							}catch(Exception e){
								res_remark = "源端响应异常";
							}
						}else{
							if(type.equals("POST")){
								try{
									data = HttpUtil.getResponseByApachePOST(getUrl(from_url,request));
									res_remark = "源端响应正常";
									res_status = 1;
								}catch(Exception e){
									res_remark = "源端响应异常";
								}
							}else{
								try{
									data = HttpUtil.getResponseByApacheGET(getUrl(from_url,request));
									res_remark = "源端响应正常";
									res_status = 1;
								}catch(Exception e){
									res_remark = "源端响应异常";
								}
							}
							
						}
						RedisUtil.set(key, data, effective);
						Service.setCacheTime(serviceid);
						RedisUtil.del(key + "_mutex");
					}else{ //这个时候代表同时的其他线程已经重新刷新了缓存，重试获取缓存
						try {
							Thread.sleep(50L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						data = RedisUtil.get(key);
					}
					
					//此时应该标记一下这个接口目前状态，因为被调用了一次收到了反馈：代理请求时的状态
					Service.status(serviceid, res_status);
				}else{
					res_status = 2;//缓存没问题，请求时正常返回数据了，但并不代表源端接口没问题，所以标记为2
				}
			}else if( effective==0){//直接读取，代理：不需要缓存
				String type = service.get("from_request_type").toString();
				String from_url = service.get("from_url").toString();
				
				if(from_url.indexOf("?wsdl") > 0){
					String url = from_url.substring(0,from_url.lastIndexOf("?"));
					Map<String,String> params = getParams(request);
					try{
						data = WebServiceHelper.getInstance(url).getResultByMethod(method,params);
						res_remark = "源端响应正常";
						res_status = 1;
					}catch(Exception e){
						res_remark = "源端响应异常";
					}
				}else if(from_url.indexOf(".asmx") > 0){
					String url = from_url.substring(0,from_url.lastIndexOf("/"));
					String originMethod = from_url.substring(from_url.lastIndexOf("/")+1);
					Map<String,String> params = getParams(request);
					try{
						data = WebServiceHelper.getInstance(url).getResultByMethod(originMethod, params);
						res_remark = "源端响应正常";
						res_status = 1;
					}catch(Exception e){
						res_remark = "源端响应异常";
					}
				}else{
					if(type.equals("POST")){
						try{
							data = HttpUtil.getResponseByApachePOST(getUrl(from_url,request));
							res_remark = "源端响应正常";
							res_status = 1;
						}catch(Exception e){
							res_remark = "源端响应异常";
						}
					}else{
						try{
							data = HttpUtil.getResponseByApacheGET(getUrl(from_url,request));
							res_remark = "源端响应正常";
							res_status = 1;
						}catch(Exception e){
							res_remark = "源端响应异常";
						}
					}
					
					//此时应该重新设置缓存，注意缓存时要把缓存有效期加上
					//int cache_effective = (int)service.get("cache_effective");
					/*if(cache_effective>0){
						RedisUtil.set("/api/" + from_appid + "/" + method, data, cache_effective);
						//此时应该记录一下最近缓存时间
						Service.setCacheTime(serviceid);
					}*/
				}
				//此时应该标记一下这个接口目前状态，因为被调用了一次收到了反馈：代理请求时的状态
				Service.status(serviceid, res_status);

			}
			
			if (response_type.equals("JSON")) {
				response.setContentType("application/json");
			} else if (response_type.equals("XML")) {
				response.setContentType("text/xml");
			} else if (response_type.equals("TEXT")) {
				response.setContentType("text/html");
			} else if (response_type.equals("NONE")) {
				
			}
			response.getWriter().print(data);
		}else if(auth == -1){
			response.sendError(400,"不存在此接口");
			res_remark = method;
		}else if(auth == -2){
			Map<String,Object> service = getServiceInfo(from_appid, method);
			serviceid = (int)service.get("serviceid");
			response.sendError(401,"此接口未启用或者未发布");
			res_remark = "此接口未启用或者未发布";
		}else{
			Map<String,Object> service = getServiceInfo(from_appid, method);
			serviceid = (int)service.get("serviceid");
			response.sendError(401,"无权调用此接口");
			res_remark = "权限验证失败";
		}
		
		int consume = (int)(System.currentTimeMillis() - st);
		leservice.execute(new Apiparam(uri,method,from_appid,token,res_remark,res_status,serviceid,auth,consume));
		
		//记录该接口被调用总次数+1，其实可以从log里统计出来某接口调用总次数，不过会很慢，
		//所以没次被请求时就+1，以后可以定时的通过log去校正这个值
		Service.count(serviceid);
	}
	class Apiparam implements Runnable{
		String uri;
		String method;
		String from_appid ;
		String token ;
		String res_remark;
		int res_status ;
		int serviceid;
		int auth;
		int consume;
		public Apiparam(String uri, String method, String from_appid, String token, String res_remark, int res_status,
				int serviceid,int auth,int consume) {
			super();
			this.uri = uri;
			this.method = method;
			this.from_appid = from_appid;
			this.token = token;
			this.res_remark = res_remark;
			this.res_status = res_status;
			this.serviceid = serviceid;
			this.auth =auth;
			this.consume=consume;
		}
		@Override
		public void run() {
			//记录客户端请求该接口的日志
			Service.log(token, serviceid, auth, consume, res_remark,res_status);
			try {//服务调用监控
				if(serviceid==0) {
					logger.error("不存在此接口");
				}else {
					Service.apiCalledMessage(from_appid,res_status,method);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * 为SOAP组装参数map
	 * @param request
	 * @return
	 */
	Map<String,String> getParams(HttpServletRequest request){
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> pnames = request.getParameterNames();
		while(pnames.hasMoreElements()) {
			String name = (String) pnames.nextElement();
			String value = request.getParameter(name);
			map.put(name, null == value ? "" : value);
		}
		return map;
	}
	
	/**
	 * 为HTTP请求组装带有参数的完整请求地址
	 * @param from_url
	 * @param request
	 * @return
	 */
	String getUrl(String from_url,HttpServletRequest request){
		String p = "";
		Map<String, String[]> params = request.getParameterMap();
			if (null == params || params.isEmpty()) {
				return from_url;
			}
			StringBuffer stringBuffer = new StringBuffer();
			
			Enumeration<?> pnames = request.getParameterNames();
			while(pnames.hasMoreElements()) {
				String name = (String) pnames.nextElement();
				String value = request.getParameter(name);
				
				stringBuffer.append("&");
				stringBuffer.append(name);
				stringBuffer.append("=");
				stringBuffer.append(null == value ? "" : value);
			}

			p = (null==stringBuffer || stringBuffer.length()==0)?"":stringBuffer.substring(1);
			return from_url+"?"+p;
	}
	
	/**
	 * 获取接口的全部信息
	 * @param from_appid
	 * @param method
	 * @return
	 */
	Map<String,Object> getServiceInfo(String from_appid,String method){
		String sql = "select * from service where appid=? and method=?";
		Map<String,Object> map = null;
		try {
			map = DaoUtil.sysdao().executeQueryObject(sql, new Object[]{from_appid,method});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 请求认证
	 * @param apptoken
	 * @param from_appid
	 * @param method
	 * @return 0无权限 -1无接口 >1通过
	 */
	int checkPower(String apptoken,String from_appid,String method){
		int result = 0;
		String sql = "select serviceid,onoff,status from service where appid=? and method=? ";
		List<Map<String, Object>> list1;
		try {
			list1 = DaoUtil.sysdao().executeQuery(sql,new Object[]{from_appid,method});
			
			if(list1.size()==0){ 
				result = -1;//根本就没这个接口
			}else{
				sql = "select * from service_power where appid=(select id from r_app where r_app.token=?) and serviceid = (select serviceid from service where appid=? and method=?)";
				List<Map<String, Object>> list = DaoUtil.sysdao().executeQuery(sql, new Object[]{apptoken,from_appid,method});
				result = list.size();//0无权限 >1有权限（有可能数据设计的不好或程序问题，同一个接口为某应用授权了两次）
				//验证是否接口是否启用或者发布
				if(result > 0){
					if(!"1".equals(String.valueOf(list1.get(0).get("onoff"))) 
							|| !"publish".equals(String.valueOf(list1.get(0).get("status")))){
						result = -2;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
}
