package htbus.service;

import htbus.cache.DBStatusCache;
import htbus.entity.Page;
import htbus.util.DaoUtil;
import htbus.util.RedisUtil;
import htbus.util.StringUtil;
import htbus.util.WebServiceHelper;
import htbus.websocket.MyWebSocket;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.miao.framework.util.DateUtil;
import cn.miao.framework.util.HttpUtil;

public class Service {
	static Logger logger = Logger.getLogger(UserService.class);
	
	/**
	 * 获取数据服务列表
	 * @return
	 */
	public List<Map<String,Object>> getServiceList(){
		List<Map<String,Object>> result = new ArrayList<>();
		String sql = "select (case when cache_effective>0 then 1 else 0 end) iscache, a.serviceid,servicename,CONCAT('/api/',appid,'/',method) path,a.remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective,regtm,appname,from_url,from_request_type,onoff,res_status,berequest_count,a.version current_version,e.version his_version from service a left join r_app b on a.appid=b.id left join (select serviceid,GROUP_CONCAT(version) version from service_version GROUP BY serviceid) e on a.serviceid=e.serviceid where status='publish' order by serviceid";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
			sql = "select param,remark param_remark,type param_type,sample param_sample from service_param where serviceid=?";
			for(Map<String,Object> map : result){
				String serviceid = map.get("serviceid").toString();
				map.put("params", DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid}));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public Page getServiceList(Map<String, String> params){
		List<Map<String,Object>> result = new ArrayList<>();
		String whereCondition = " where status='publish'";
		String orderCondition = "";
		int pageNum = 0, pageSize = 10, total = 0;
		String sql = "select (case when cache_effective>0 then 1 else 0 end) iscache, a.serviceid,servicename,CONCAT('/api/',appid,'/',method) path,a.remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective,regtm,appname,from_url,from_request_type,onoff,res_status,berequest_count,a.version current_version,e.version his_version,h.name class from service a left join r_app b on a.appid=b.id left join (select serviceid,GROUP_CONCAT(version) version from service_version GROUP BY serviceid) e on a.serviceid=e.serviceid "
				+ " left join (SELECT f.name,g.resource_id FROM matedata_tree f JOIN matedata_resources g ON g.matedata_code = f.code "
		        + " WHERE g.resource_type = 1) h on a.serviceid=h.resource_id";
		try {
			if(!StringUtil.isBlank(params.get("appid"))){
				whereCondition += " and appid = " + params.get("appid");
			}
			if(!StringUtil.isBlank(params.get("iscache"))){
				whereCondition += Integer.parseInt(params.get("iscache")) == 1 ? " and cache_effective > 0 " : " and cache_effective = 0 ";
			}
			if(!StringUtil.isBlank(params.get("path"))){
				whereCondition += " and (CONCAT('/api/',appid,'/',method) like '%" + params.get("path") + "%' or servicename like '%" + params.get("path") + "%')";
			}
			if(!StringUtil.isBlank(params.get("serviceid"))){
				whereCondition += " and a.serviceid in (" + params.get("serviceid") + ")";
			}
			if(!StringUtil.isBlank(params.get("serviceidN"))){
				whereCondition += " and a.serviceid not in (" + params.get("serviceidN") + ")";
			}
			if(!StringUtil.isBlank(params.get("onoff"))){
				whereCondition += " and onoff = " + params.get("onoff");
			}
			if(!StringUtil.isBlank(params.get("isClassNull"))){
				if(params.get("isClassNull").toString().equals("0")){
					whereCondition += " and h.name is null ";
				}else if(params.get("isClassNull").toString().equals("1")){
					whereCondition += " and h.name is not null ";
				}
			}
			if(!StringUtil.isBlank(params.get("order"))){
				orderCondition = " order by berequest_count " + params.get("order");
			}
			
			pageNum = params.get("pageNum") == null ? 0 : Integer.parseInt(params.get("pageNum")) - 1;
			pageSize = params.get("pageSize") == null ? 10 : Integer.parseInt(params.get("pageSize"));
			sql = sql + whereCondition + orderCondition + " limit " + pageNum * pageSize + "," + pageSize;
			total = Integer.parseInt(DaoUtil.sysdao().executeQueryObject("select count(*) cnt from service a "
					+ " left join (SELECT f.name,g.resource_id FROM matedata_tree f JOIN matedata_resources g ON g.matedata_code = f.code "
			        + " WHERE g.resource_type = 1) h on a.serviceid=h.resource_id"
			+ whereCondition).get("cnt").toString());
			result = DaoUtil.sysdao().executeQuery(sql);
			sql = "select param,remark param_remark,type param_type,sample param_sample from service_param where serviceid=?";
			for(Map<String,Object> map : result){
				String serviceid = map.get("serviceid").toString();
				map.put("params", DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid}));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new Page(pageNum + 1, pageSize, total, result);
	}
	
	
	/**
	 * 获取数据服务列表
	 * @return
	 */
	public List<Map<String,Object>> getServiceList(String ids){
		List<Map<String,Object>> result = null;
		String sql = "select (case when cache_effective>0 then 1 else 0 end) iscache, a.serviceid,servicename,CONCAT('/api/',appid,'/',method) path,a.remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective,regtm,appname,from_url,from_request_type,onoff,res_status,berequest_count,a.version current_version,e.version his_version from service a left join r_app b on a.appid=b.id left join (select serviceid,GROUP_CONCAT(version) version from service_version GROUP BY serviceid) e on a.serviceid=e.serviceid where status='publish' and a.serviceid in ("
				+ ids
				+ ")";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
			sql = "select param,remark param_remark,type param_type,sample param_sample from service_param where serviceid=?";
			for(Map<String,Object> map : result){
				String serviceid = map.get("serviceid").toString();
				map.put("params", DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 启用停用服务
	 * @param serviceid
	 * @param onoff
	 */
	public boolean onoffService(String serviceid,String onoff){
		boolean result = false;
		String sql = "update service set onoff=? where serviceid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{onoff,serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取接口详细信息
	 * @param serviceid
	 * @return
	 */
	public Map<String,Object> getServiceInfo(String serviceid){
		Map<String,Object> result = new HashMap<String,Object>();
		String sql = "select serviceid, servicename,CONCAT('/api/',appid,'/',method) path,method,a.remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_effective,regtm,appname,from_url,from_request_type,status,onoff from service a left join r_app b on a.appid=b.id where serviceid=?";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql,new Object[]{serviceid});
			sql = "select param,remark param_remark,type param_type,sample param_sample from service_param where serviceid=?";
			result.put("params", DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid}));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取某应用系统注册的服务接口
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> getAppServiceList(String appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select serviceid,servicename,CONCAT('/api/',appid,'/',method) path,from_url from service a left join r_app b on a.appid=b.id where a.appid = ?";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{appid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 注册服务接口
	 * @param appid
	 * @param from_url
	 * @param from_request_type
	 * @return 返回新注册接口的ID
	 */
	public String regService(String appid,String servicename,String from_url,String from_request_type){
		if(from_url != null){
			from_url = StringUtil.replaceBlank(from_url);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		String sql = "insert into service(serviceid,servicename,appid,from_url,from_request_type,regtm,status) select ifnull(max(serviceid)+1,1),?,?,?,?,now(),'reg' from service";
		try {
			boolean b = DaoUtil.sysdao().executeSQL(sql, new Object[]{servicename,appid,from_url,from_request_type});
			if(b){
				sql = "select max(serviceid) serviceid from service";
				result = DaoUtil.sysdao().executeQueryObject(sql);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result.get("serviceid") == null ? null : result.get("serviceid").toString();
	}
	
	/**
	 * 检查方法名是否重复
	 * @param appid
	 * @param method
	 * @return
	 */
	public boolean checkMethod(String appid,String method,String serviceid){
		boolean result = false;
		String sql  = "select serviceid from service where appid=? and method=? and serviceid<>?";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{appid,method,serviceid});
			if(list.size()==0){
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 添加接口参数
	 * @param serviceid
	 * @param param
	 * @param type
	 * @param sample
	 * @return
	 */
	public boolean addParam(String serviceid,String param,String remark,String type,String sample){
		boolean result = false;
		String sql = "insert into service_param(serviceid,param,remark,type,sample) values(?,?,?,?,?)";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{serviceid,param,remark,type,sample});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 删除接口参数
	 * @param serviceid
	 * @param param
	 * @return
	 */
	public boolean deleteParam(String serviceid,String param){
		boolean result = false;
		String sql = "delete from service_param where serviceid=? and param=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{serviceid,param});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 发布/修改服务接口信息
	 * @param serviceid
	 * @param servicename
	 * @param method
	 * @param remark
	 * @param response_type
	 * @param response_sample
	 * @param cache_type
	 * @param cache_job_interval
	 * @param cache_effective
	 * @param from_url
	 * @param from_request_type
	 * @return
	 */
	public boolean publishService(String serviceid,String servicename,String method,String remark,String response_type,String response_sample,String cache_effective,String from_url,String from_request_type, JSONArray paramArray){
		boolean result = false;
		String sql = "update service set status='publish',servicename=?,method=?,remark=?,response_type=?,response_sample=?,from_url=?,from_request_type=?,version=ifnull(version,0)+1 where serviceid=?";
		try {
			//先判断version是否为null,不为空则插入到历史版本
			Object version =DaoUtil.sysdao().executeQueryObject("select version from service where serviceid=?", new Object[]{serviceid}).get("version");
			if(version != null){
				DaoUtil.sysdao().executeSQL("insert into service_version select serviceid,version,regtm,appid,from_url,from_request_type,servicename,method,remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective from service where serviceid=?",
						new Object[]{serviceid});
				List<Object[]> params = new ArrayList<>();
				for(int i =0; i < paramArray.size(); i++){
					params.add(new Object[]{serviceid, paramArray.getJSONObject(i).get("param"), paramArray.getJSONObject(i).get("param_remark"),
							paramArray.getJSONObject(i).get("param_type"), paramArray.getJSONObject(i).get("param_sample"), version});
				}
				DaoUtil.sysdao().executeSQLBatch("insert into service_param_version values(?,?,?,?,?,?)", params);
			}
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{
					servicename,method,remark,response_type,response_sample,
					from_url,from_request_type,serviceid
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public boolean updateService(String serviceid,String servicename,String method,String remark,String response_type,String response_sample,String cache_effective,String from_url,String from_request_type){
		boolean result = false;
		String sql = "update service set servicename=?,method=?,remark=?,response_type=?,response_sample=?,from_url=?,from_request_type=? where serviceid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{
					servicename,method,remark,response_type,response_sample,
					from_url,from_request_type,serviceid
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 删除服务接口
	 * @param serviceid
	 * @return
	 */
	public boolean deleteService(String serviceid){
		boolean result = false;
		String sql = "delete from service where serviceid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql,new Object[]{serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 申请接口
	 * @param appid
	 * @param serviceid_str
	 * @return
	 */
	public boolean askService(String appid,String serviceids){
		boolean result = false;
		String sql = "insert into service_ask(appid,tm,serviceid) select ? appid,now(),serviceid from service where serviceid in ("+serviceids+")";
		try {
			result = DaoUtil.sysdao().executeSQL(sql,new Object[]{appid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * 为某app返回平台能提供的所有服务接口列表，带有审批状态
	 * @param appid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page getServiceListWithAsk(String appid, Map<String, String> params){
		int askstatusExpect = params.get("askstatus") == null ? -1 : Integer.parseInt(params.get("askstatus"));
		Page list = null;
		
		//获取当前app已有的权限（serviceids）
		List<String> hasPower = new ArrayList<>();
		String sql1 = "select GROUP_CONCAT(cast(serviceid as char(10)) SEPARATOR ',') ids from service_power where appid=?";
		Map<String, Object> m1;
		try {
			m1 = DaoUtil.sysdao().executeQueryObject(sql1, new Object[]{appid});
			if(m1!=null && m1.get("ids")!=null){
				hasPower = Arrays.asList(m1.get("ids").toString().split(","));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		//获取当前app正在审批但未被处理的申请（serviceids）
		List<String> asking = new ArrayList<>();
		String sql2 = "select GROUP_CONCAT(cast(serviceid as char(10)) SEPARATOR ',') ids from service_ask where appid=? and result=-1";
		Map<String, Object> m2;
		try {
			m2 = DaoUtil.sysdao().executeQueryObject(sql2, new Object[]{appid});
			if(m2!=null && m2.get("ids")!=null){
				asking = Arrays.asList(m2.get("ids").toString().split(","));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		if(askstatusExpect == 0){
			String serviceidN = asking.size() > 0 ? String.join(",", hasPower) + "," + String.join(",", asking) : String.join(",", hasPower);
			params.put("serviceidN", serviceidN);
			list = getServiceList(params);
		}else if(askstatusExpect == 1){
			if(hasPower.size() > 0){
				params.put("serviceid", String.join(",", hasPower));
			}else{
				params.put("serviceid", "-1");
			}
		}else if(askstatusExpect == 2){
			if(asking.size() > 0){
				params.put("serviceid", String.join(",", asking));
			}else{
				params.put("serviceid", "-1");
			}
		}
		list = getServiceList(params);
		//循环处理list，put审批状态字段
		for(Object map : list.getRecordList()){
			int askstatus = 0;
			String id = ((Map<String, Object>)map).get("serviceid").toString();
			if(hasPower.contains(id)){//说明已经被授权了
				askstatus = 1;
			}else if(asking.contains(id)){//说明正在申请但未被处理
				askstatus = 2;
			}
			((Map<String, Object>)map).put("askstatus", askstatus);
		}
		return list;
	}
	
	/**
	 * 获取所有服务接口申请列表
	 * @return
	 */
	public List<Map<String,Object>> getServiceAskList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.askid,a.appid ask_appid,b.appname ask_appname,a.tm,c.servicename,CONCAT('/api/',c.appid,'/',c.method) path,a.dealtm,a.result,c.remark,d.appname from_appname from service_ask a left join r_app b on a.appid=b.id left join service c on a.serviceid=c.serviceid left join r_app d on c.appid=d.id order by a.tm desc";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	
	/**
	 * 审核应用对接口的申请
	 * @param askid
	 * @param result
	 * @return
	 */
	public boolean dealServiceAsk(String askid,String result){
		boolean b = false;
		String sql = "update service_ask set result = ?,dealtm=now() where askid in ("+askid+") and result = -1";
		String sql2 = "replace into service_power(appid,serviceid) select appid,serviceid from service_ask where askid in ("+askid+")";
		try {
			b = DaoUtil.sysdao().executeSQL(sql,new Object[]{result});
			if(result.equals("1")){
				b = DaoUtil.sysdao().executeSQL(sql2);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			b = false;
		}
		return b;
	}
	
	/**
	 * 获取某app已经被授权使用的接口
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> hasPowerServiceList(String appid,String from_appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		//String sql = "select a.serviceid,servicename,CONCAT('/api/',b.appid,'/',method) path,b.remark,c.appname from_appname from service_power a left join service b on a.serviceid=b.serviceid left join r_app c on b.appid=c.id where a.appid = ?";
		String and = from_appid.equals("all")? "and length(?)>0" : " and b.id=?";
		String sql = "select a.serviceid,a.servicename,CONCAT('/api/',a.appid,'/',a.method) path,a.remark,b.appname from_appname from service a left join r_app b on a.appid=b.id where a.serviceid in (select serviceid from service_power where appid = ?)";
		sql += and;
		
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{appid,from_appid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取某app未被授权的接口列表
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> noPowerServiceList(String appid,String from_appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String and = from_appid.equals("all")? "and length(?)>0" : " and b.id=?";
		String sql = "select a.serviceid,a.servicename,CONCAT('/api/',a.appid,'/',a.method) path,a.remark,b.appname from_appname from service a left join r_app b on a.appid=b.id where status='publish' and a.serviceid not in (select serviceid from service_power where appid = ?)";
		sql += and;
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{appid,from_appid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 为某应用授权或取消授权
	 * @param appid
	 * @param power
	 * @param serviceids
	 * @return
	 */
	public boolean powerService(String appid,String power,String serviceids){
		boolean result = false;
		String sql0 = "delete from service_power where appid=? and serviceid in ("+serviceids+")";
		String sql1 = "insert into service_power(serviceid,appid) select serviceid,? appid from service where serviceid in ("+serviceids+")";
		if(power.equals("0")){
			try {
				result = DaoUtil.sysdao().executeSQL(sql0,new Object[]{appid});
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}else if(power.equals("1")){
			try {
				result = DaoUtil.sysdao().executeSQL(sql1,new Object[]{appid});
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 获取某应用申请的接口处理情况列表
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> getAskList(String appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.askid,a.tm,a.dealtm,a.serviceid,a.result,b.servicename,CONCAT('/api/',b.appid,'/',b.method) path,c.appname from_appname from service_ask a left join service b on a.serviceid=b.serviceid left join r_app c on b.appid=c.id where a.appid = ?";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{appid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取服务监控列表
	 * @return
	 */
	public List<Map<String,Object>> getServiceMonitorList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.serviceid,a.servicename,a.res_status,a.last_cache_tm,a.berequest_count,b.appname from service a left join r_app b on a.appid=b.id where status='publish'";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list; 
	}
	
	/**
	 * 获取某时间段内请求监控列表
	 * @param tm1
	 * @param tm2
	 * @return
	 */
	public List<Map<String,Object>> getRequestMonitorList(String tm1,String tm2){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.tm,b.servicename,c.appname,a.res_status,a.consume from service_log a left join service b on a.serviceid=b.serviceid left join r_app c on b.appid=c.id where tm>=str_to_date(?,'%Y-%m-%d %h:%i:%s') and tm<=str_to_date(?,'%Y-%m-%d %h:%i:%s') order by tm ";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{tm1,tm2});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取某时间段内请求监控列表
	 * @param 
	 * @param tm1
	 * @param tm2
	 * @return
	 */
	public List<Map<String,Object>> getRequestMonitorList(String serviceid, String tm1,String tm2){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.tm,b.servicename,c.appname,a.res_status,a.consume from service_log a left join service b on a.serviceid=b.serviceid left join r_app c on b.appid=c.id where a.serviceid=? and tm>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and tm<=str_to_date(?,'%Y-%m-%d %H:%i:%s') order by tm desc";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid, tm1, tm2});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 设置接口的缓存
	 * @param serviceid
	 * @param interval
	 * @param effective
	 * @return
	 */
	public boolean setServiceCache(String serviceid,String cache_effective){
		boolean result = false;
		String sql = "update service set cache_effective=? where serviceid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{cache_effective,serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 测试接口源端响应
	 * @param serviceid
	 * @param paramstring
	 * @return
	 */
	public Map<String,Object> testService(String serviceid,Map<String,String> params){
		Map<String,Object> result = new HashMap<String,Object>();
		
		long st = System.currentTimeMillis();
		//获取接口信息
		Map<String,Object> service = getServiceInfo(serviceid);
		String method = service.get("method").toString();
		String data = "";
		String type = service.get("from_request_type").toString();
		String from_url = service.get("from_url").toString();
		
		if(from_url.indexOf("?wsdl") > 0){
			String url = from_url.substring(0,from_url.lastIndexOf("/"));
			try{
				data = WebServiceHelper.getInstance(url.replace("?wsdl", "")).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1),params);
			}catch(Exception e){
				data = e.getMessage();
			}
		}else if(from_url.indexOf(".asmx") > 0){
			String url = from_url.substring(0,from_url.lastIndexOf("/"));
			try{
				data = WebServiceHelper.getInstance(url).getResultByMethod(from_url.substring(from_url.lastIndexOf("/")+1), params);
			}catch(Exception e){
				data = e.getMessage();
			}
		}else{
			if(type.equals("POST")){
				try{
					data = HttpUtil.getResponseByApachePOST(paramsToQuerystring(from_url,params));
				}catch(Exception e){
					data = e.getMessage();
				}
			}else{
				try{
					data = HttpUtil.getResponseByApacheGET(paramsToQuerystring(from_url,params));
				}catch(Exception e){
					data = e.getMessage();
				}
			}
		}
		
		int consume = (int)(System.currentTimeMillis() - st);
		
		result.put("consume", consume);
		result.put("response", data);
		
		return result;
	}
	/**
	 * 为HTTP请求组装带有参数的完整请求地址
	 * @param from_url
	 * @param request
	 * @return
	 */
	String paramsToQuerystring(String from_url,Map<String,String> params){
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
			return from_url+"?"+p;
	}
	
	/**
	 * 服务资源统计信息
	 * @return
	 */
	public Map<String,Object> staServiceResourceInfo(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		List<Map<String,Object>> datadetail = new ArrayList<Map<String,Object>>();
		String sql = "select a.id appid,a.appname,(select count(1) from service b where b.appid=a.id and onoff=1 and `status`='publish') service_cnt from r_app a";
		try {
			datadetail = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		sql = "select"
				+" (SELECT count(1) from service where onoff=1 and `status`='publish') service_sum,"
				+" (SELECT count(1) from service where onoff=1 and `status`='publish' and res_status=1) service_normal,"
				+" (SELECT count(1) from service where onoff=1 and `status`='publish' and res_status=0) service_error";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql);
			result.put("app_sum", datadetail.size());
			result.put("detail", datadetail);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 异常接口信息统计
	 * @return
	 */
	public List<Map<String,Object>> staErrorServiceList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.serviceid,a.servicename,a.appid,b.appname,a.res_status,(select tm from service_log where serviceid=a.serviceid order by tm desc limit 1) last_res_tm from service a left join r_app b on a.appid=b.id where a.onoff=1 and a.`status`='publish' and a.res_status=0";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 统计每个应用拥有的权限、累计调用次数、今日日活、最高日活、最高日活日期
	 * @return
	 */
	public Map<String,Object> staSurportInfo(){
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> detail = new ArrayList<Map<String,Object>>();
		String sql = "select a.id appid,a.appname,ifnull(b.cnt,0) power_cnt,ifnull(c.cnt,0) request_sum,ifnull(d.cnt,0)today_sum,ifnull(e.cnt,0) maxday_sum,e.tm maxday_date from r_app a left join (select a.appid, count(a.appid) cnt from service_power a left join service b on a.serviceid=b.serviceid where b.status='publish'  GROUP BY appid) b on a.id=b.appid "
                    + "left join (select appid,count(*)cnt from service_log GROUP BY appid)c on a.id=c.appid "
				    + "left join (select appid,count(*)cnt from service_log where tm>CURDATE()GROUP BY appid )d on a.id=d.appid "
                    + "left join (select appid, max(cnt) cnt,tm from(select appid ,concat(year(tm),'-',case when length(month(tm))=1 then concat('0',month(tm)) else month(tm) end,'-',case when length(day(tm))=1 then concat('0',day(tm)) else day(tm) end) tm,"
                    + "count(*) cnt from service_log group by appid,concat(year(tm),'-',case when length(month(tm))=1 then concat('0',month(tm)) else month(tm) end,'-',case when length(day(tm))=1 then concat('0',day(tm)) else day(tm) end) order by appid,cnt desc)a group by appid) e on a.id=e.appid";
		try {
			detail = DaoUtil.sysdao().executeQuery(sql);
			Map<String,Object> maxapp = new HashMap<String,Object>();
			int max = -1;
			for(Map<String,Object> app : detail){
				int temp = Integer.parseInt(app.get("request_sum").toString());
				if(temp>max){
					maxapp = app;
					max = temp;
				}
			}
			result.put("app_sum", detail.size());
			result.put("max_appname", maxapp.get("appname"));
			result.put("max_request", maxapp.get("request_sum"));
			result.put("detail", detail);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 统计接口调用情况，总请求次数、今日请求次数、今日被请求最频繁的前三个接口
	 * @return
	 */
	public Map<String,Object> staRequestInfo(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		String sql = "select (select count(1) from service_log) request_sum,(select count(1) from service_log where tm >= curdate()) today_sum";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql);
			
			sql = "select y.servicename,x.* from"
					+" (select serviceid,count(1) request_cnt from service_log where tm >= curdate() group by serviceid) x"
					+" join service y on x.serviceid=y.serviceid order by x.request_cnt desc LIMIT 3";
			result.put("top3", DaoUtil.sysdao().executeQuery(sql));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 统计某时间段服务的日调用过程
	 * @return
	 */
	public List<Map<String,Object>> staRequestDayLine(String tm1,String tm2){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select tm,count(1) cnt from"
				+" (select DATE_FORMAT(tm,'%Y-%c-%d') tm from service_log where tm >= DATE_FORMAT(?,'%Y-%c-%d %h:%i:%s') and tm < DATE_FORMAT(?,'%Y-%c-%d %h:%i:%s')) x"
				+" group by tm"
				+" order by tm ";
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{tm1,tm2});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 记录服务接口调用日志
	 * @param appid
	 * @param serviceid
	 * @param auth
	 * @param consume
	 * @param res_remark
	 * @param res_status
	 */
	public static void log(String appid,int serviceid,int auth,int consume,String res_remark,int res_status){
		String sql = "insert into service_log(appid,serviceid,auth,consume,res_remark,res_status,tm) values((select id from r_app where token = ?),?,?,?,?,?,now())";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{appid,serviceid,auth,consume,res_remark,res_status});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录服务接口状态
	 * @param serviceid
	 * @param status
	 */
	public static void status(int serviceid,int status){
		String sql = "update service set res_status=? where serviceid=?";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{status,serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录接口被调用次数总数+1
	 * @param serviceid
	 */
	public static void count(int serviceid){
		String sql = "update service set berequest_count = berequest_count+1 where serviceid=?";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录接口最近的缓存时间
	 * @param serviceid
	 */
	public static void setCacheTime(int serviceid){
		String sql = "update service set last_cache_tm=now() where serviceid=?";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static List<Map<String,Object>> appServiceCount() throws Exception{
		List<Map<String,Object>> list = null;
		String sql = "select a.id, a.appname,b.service_provide,c.service_use FROM r_app a LEFT JOIN "
					+"(select COUNT(appid) service_provide,appid FROM service GROUP BY appid ) b "
					+"ON a.id=b.appid "
					+"LEFT JOIN (select COUNT(appid) service_use,appid from  service_power GROUP BY appid)c "
					+"ON b.appid=c.appid";
		list = DaoUtil.sysdao().executeQuery(sql);
		return list;
	}
	
	/**
	 * 平台信息
	 * @throws Exception
	 */
	public static Map<String,Object> platforminfo() throws Exception {
//		String tmsql = "select tm as initialtm_begintm from platform"; 
//		String dbsql = "select count(1)  from db_instance";
//		String sesql = "select count(1) from service";
//		String apsql = "select count(1) from r_app";
		Map<String,Object> map = null;
		String sql = "select tm as initialtm,b.ins instance_num,b.app app_num,b.se service_num "
					+"from platform_begintm a JOIN (select count(1) ins, e.app app,e.se se from "
					+"db_instance  join (select count(1) se,d.app app from service c JOIN "
					+"(select count(1) app from r_app) d )e) b";
		map = DaoUtil.sysdao().executeQueryObject(sql);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String initialtm = map.get("initialtm").toString();
		Long inittosecond = LocalDateTime.parse(initialtm, formatter).toEpochSecond(ZoneOffset.of("+8"));
	    Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
	    long day = (now-inittosecond)/86400;
	    map.put("day", day);
	    map.remove("initialtm");
	    return map;

	}
	
	/**
	 * 数据库状态统计
	 * @throws IOException
	 */
	public static Map<String,Integer> instanceStatusCount() throws IOException {
		int normal =0;
		int abnormal =0;
		Map<String,Integer> map = new HashMap<>();
//		Cache cache = DBStatusCache.getCache();
//		Element values = cache.get("dbstatus");
//		@SuppressWarnings("unchecked")
//		List<Map<String, Object>> value = (List<Map<String, Object>>)values.getObjectValue();
		List<Map> value = JSON.parseArray(RedisUtil.get("instanceStatus"),Map.class);
		for(Map stu:value) {
			if((Integer)stu.get("status")==1) {
				normal++;
			}else {
				abnormal++;
			}
		}
		map.put("normal",normal);
		map.put("abnormal",abnormal);
		return map;
	}
	
	/**
	 * 近30天服务调用次数
	 * @throws Exception
	 */
	public static List<Map<String,Object>> apiCalledCount() throws Exception {
		List<Map<String,Object>> result = null;
		String sql = " select lefttable.date,IFNULL(righttable.n,'0') num from(SELECT "
				+" DATE_FORMAT(date_sub(date_add(CURDATE(),INTERVAL -1 DAY),interval @i:=@i+1 day),'%m-%d')" 
				+" as date from service_log,(select @i:= -1) t limit 30)"
				+" as lefttable LEFT JOIN(SELECT DATE_FORMAT(tm,'%m-%d') ss,count(tm) n"
				+" FROM service_log where DATE_SUB(date_add(CURDATE(),INTERVAL -1 DAY), INTERVAL 29"
				+" DAY) <= date(tm) GROUP BY ss) righttable ON lefttable.date=righttable.ss";
		result =DaoUtil.sysdao().executeQuery(sql);
		return result;
	}
	
	/**
	 * api状态统计
	 * @throws Exception
	 */
	public static Map<String,Object> apiStatusCount() throws Exception {
		List<Map<String,Object>> result = null;
		Integer normal = 0;
		Map<String, Object> map = new HashMap<>();
		String sql = "SELECT res_status,count(res_status) num FROM service_log GROUP BY res_status";
		result =DaoUtil.sysdao().executeQuery(sql);
		for(Map<String, Object> st : result){
			if((Integer)st.get("res_status")==0) {
				map.put("unnormal", st.get("num"));
			}else {
				Integer num = Integer.valueOf(st.get("num").toString());
				 normal=num+normal;
			}
			map.put("normal",normal);
		}
		return map;
	}
	
	/**
	 * 服务调用监控
	 * @param from_appid
	 * @param res_status
	 * @param method
	 * @throws Exception
	 */
	public static void apiCalledMessage(String from_appid,int res_status,String method) throws Exception {
		String sql = "select appname from r_app where id=?";
		Map<String,Object> map = DaoUtil.sysdao().executeQueryObject(sql, new Object[]{from_appid});
		if(res_status==1) {
			map.put("res_status", 1);
		}else if(res_status==2) {
			map.put("res_status", 2);
		}else {
			map.put("res_status", 0);
		}
		map.put("appname", map.get("appname"));
		map.put("method", method);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
		String nowStr = formatter.format(now);
		map.put("tm", nowStr);
		JSONObject json = JSONObject.fromObject(map);
		String message =json.toString();
		MyWebSocket.sendMessage(message);
	}
	/**
	 * 服务调用次数
	 * @throws Exception
	 */
	public static void apiCountMessage() throws Exception {
		Map<String,Object> map = new HashMap<>();
		String todaysql="SELECT count(*) today FROM service_log where TO_DAYS(tm)=TO_DAYS(NOW())";
		String weeksql ="SELECT count(*) week FROM service_log WHERE YEARWEEK(date_format(tm,'%Y-%m-%d')) = YEARWEEK(now())";
		String sql = "SELECT count(*) amount FROM service_log";
		Map<String,Object> m1 = DaoUtil.sysdao().executeQueryObject(todaysql) ;
		Map<String,Object> m2 = DaoUtil.sysdao().executeQueryObject(weeksql) ;
		Map<String,Object> m3 = DaoUtil.sysdao().executeQueryObject(sql) ;
		map.putAll(m1);
		map.putAll(m2);
		map.putAll(m3);
		JSONObject json = JSONObject.fromObject(map);
		String message =json.toString();
		MyWebSocket.sendMessage(message);
	}
	/*
	 * 初始化服务信息
	 */
	public static Map<String,Object> apiCountMessage2() throws Exception{
		Map<String,Object> map = new HashMap<>();
		String todaysql="SELECT count(*) today FROM service_log where TO_DAYS(tm)=TO_DAYS(NOW())";
		String weeksql ="SELECT count(*) week FROM service_log WHERE YEARWEEK(date_format(tm,'%Y-%m-%d')) = YEARWEEK(now())";
		String sql = "SELECT count(*) amount FROM service_log";
		Map<String,Object> m1 = DaoUtil.sysdao().executeQueryObject(todaysql) ;
		Map<String,Object> m2 = DaoUtil.sysdao().executeQueryObject(weeksql) ;
		Map<String,Object> m3 = DaoUtil.sysdao().executeQueryObject(sql) ;
		map.putAll(m1);
		map.putAll(m2);
		map.putAll(m3);
		return map;
	}
	public static List<Map<String,Object>> apiCalledMessage2() throws Exception{
		List<Map<String,Object>> list = new ArrayList<>();
		String logsql = "SELECT a.tm,a.res_status,b.method,c.appname from service_log a"
				+" LEFT JOIN (select * from service) b on a.serviceid=b.serviceid "
				+" LEFT JOIN (SELECT * from r_app) c ON b.appid = c.id where c.appname " 
				+" is NOT null AND b.method is NOT null ORDER BY a.logid DESC LIMIT 4";
		list= DaoUtil.sysdao().executeQuery(logsql);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd HH:mm");
		for(Map<String,Object> s:list) {
			String tm = s.get("tm").toString();
			int i = tm.indexOf(".");
			String tm2 = tm.substring(0, i);
			s.replace("tm", format.format(LocalDateTime.parse(tm2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
		}
		return list;
	}
	public static String apiMessageOnopen() throws Exception {
		List<Map<String,Object>> list = new ArrayList<>();
		Map<String,Object> map1 = new HashMap<>();
		String todaysql="SELECT count(*) today FROM service_log where TO_DAYS(tm)=TO_DAYS(NOW())";
		String weeksql ="SELECT count(*) week FROM service_log WHERE YEARWEEK(date_format(tm,'%Y-%m-%d')) = YEARWEEK(now())";
		String amountsql = "SELECT count(*) amount FROM service_log";
		String logsql = "SELECT a.tm,a.res_status,b.method,c.appname from service_log a"
				+" LEFT JOIN (select * from service) b on a.serviceid=b.serviceid"
				+"LEFT JOIN (SELECT * from r_app) c ON b.appid = c.id ORDER BY a.logid DESC LIMIT 4";
		Map<String,Object> m1 = DaoUtil.sysdao().executeQueryObject(todaysql) ;
		Map<String,Object> m2 = DaoUtil.sysdao().executeQueryObject(weeksql) ;
		Map<String,Object> m3 = DaoUtil.sysdao().executeQueryObject(amountsql) ;
		list= DaoUtil.sysdao().executeQuery(logsql);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd HH:mm");
		for(Map<String,Object> s:list) {
			String tm = s.get("tm").toString();
			s.replace("tm", format.format(LocalDateTime.parse(tm, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
		}
		map1.putAll(m1);
		map1.putAll(m2);
		map1.putAll(m3);
		list.add(map1);
		JSONObject json = JSONObject.fromObject(list);
		String message =json.toString();
//		MyWebSocket.sendMessage(message);
		return message;
	}
	
	/**
	 * 整合status和count方法，接口被请求时记录总数+1和响应状态
	 * @param serviceid
	 * @param status
	 
	public static void berequest(int serviceid,int status){
		String sql = "update service set res_status=?,berequest_count = berequest_count+1 where serviceid=?";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{status,serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}*/
	
	public List<Map<String, Object>> getAuthorizedApp(String serviceid){
		List<Map<String, Object>> rList = new ArrayList<>();
		try {
			rList = DaoUtil.sysdao().executeQuery("select a.appid,b.appname from service_power a left join r_app b on a.appid=b.id where a.serviceid=? ", new Object[]{serviceid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return rList;
	}
	
	/**
	 * 根据版本获取接口详细信息
	 * @param serviceid
	 * @param version
	 * @return
	 */
	public Map<String,Object> getServiceInfo(String serviceid, String his_version){
		Map<String,Object> result = new HashMap<String,Object>();
		String sql = "select serviceid,version, servicename,CONCAT('/api/',appid,'/',method) path,method,a.remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective,regtm,appname,from_url,from_request_type from service_version a left join r_app b on a.appid=b.id where serviceid=? and version=?";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql,new Object[]{serviceid, his_version});
			sql = "select param,remark param_remark,type param_type,sample param_sample from service_param_version where serviceid=? and version=?";
			result.put("params", DaoUtil.sysdao().executeQuery(sql, new Object[]{serviceid, his_version}));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	} 
	
	public String getAppidByToken(String token){
		if(token == null){
			return null;
		}
		String id = null;
		try {
			Map<String, Object> map = DaoUtil.sysdao().executeQueryObject("select id from r_app where token=?", new Object[]{token});
			if(token == null || map == null || map.get("id") == null){
				return id;
			}else{
				id = map.get("id").toString();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return id;
	}
	
	public boolean publishServiceByApi(String serviceid,String servicename,String method,String remark,String response_type,String response_sample,String cache_effective,String from_url,String from_request_type, JSONArray paramArray){
		boolean result = false;
		String sql = "update service set status='publish',servicename=?,method=?,remark=?,response_type=?,response_sample=?,from_url=?,from_request_type=?,version=ifnull(version,0)+1 where serviceid=?";
		try {
			//先判断version是否为null,不为空则插入到历史版本
			Map<String, Object> map = DaoUtil.sysdao().executeQueryObject("select version from service where serviceid=?", new Object[]{serviceid});
			if(map == null){
				return result;
			}
			Object version = map.get("version");
			if(version != null){
				DaoUtil.sysdao().executeSQL("insert into service_version select serviceid,version,regtm,appid,from_url,from_request_type,servicename,method,remark,request_type,response_type,response_sample,cache_type,cache_job_interval,cache_job_url,cache_effective from service where serviceid=?",
						new Object[]{serviceid});
				DaoUtil.sysdao().executeSQL("insert into service_param_version select serviceid,param,,remark,type,sample,'" + version + "' from service_param where serviceid=?",
						new Object[]{serviceid});
			}
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{
					servicename,method,remark,response_type,response_sample,
					from_url,from_request_type,serviceid
			});
			List<Object[]> params = new ArrayList<>();
			for(int i =0; i < paramArray.size(); i++){
				params.add(new Object[]{serviceid, paramArray.getJSONObject(i).get("param"), paramArray.getJSONObject(i).get("param_remark"),
						paramArray.getJSONObject(i).get("param_type"), paramArray.getJSONObject(i).get("param_sample")});
			}
			DaoUtil.sysdao().executeSQL("delete from service_param where serviceid=?", new Object[]{serviceid});
			DaoUtil.sysdao().executeSQLBatch("replace into service_param values(?,?,?,?,?)", params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public boolean updateServiceByApi(String serviceid,String servicename,String method,String remark,String response_type,String response_sample,String cache_effective,String from_url,String from_request_type, JSONArray paramArray){
		boolean result = false;
		String sql = "update service set servicename=?,method=?,remark=?,response_type=?,response_sample=?,from_url=?,from_request_type=? where serviceid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{
					servicename,method,remark,response_type,response_sample,
					from_url,from_request_type,serviceid
			});
			List<Object[]> params = new ArrayList<>();
			for(int i =0; i < paramArray.size(); i++){
				params.add(new Object[]{serviceid, paramArray.getJSONObject(i).get("param"), paramArray.getJSONObject(i).get("param_remark"),
						paramArray.getJSONObject(i).get("param_type"), paramArray.getJSONObject(i).get("param_sample")});
			}
			DaoUtil.sysdao().executeSQL("delete from service_param where serviceid=?", new Object[]{serviceid});
			DaoUtil.sysdao().executeSQLBatch("replace into service_param values(?,?,?,?,?)", params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public static void main2(String[] args){
		Map<String,String> params = new HashMap<String,String>();
		//params.put("Path", "");
		//String result = WebServiceHelper.getInstance("http://api.qgj.cn/webapi/profile.asmx").getResultByMethod("ListOrg",params);
		params.put("ty", "ir1");
		List<Map<String,Object>> result = new Service().getRequestMonitorList("1",DateUtil.getDayByOffset(-1, "yyyy-MM-dd HH:mm:ss"), DateUtil.getNow());
		//boolean result = new Service().getAskList("5,6,7","1");
		System.out.print(result);
	}
	
	public static void main(String[] args){
		Map<String, String> params = new HashMap<>();
		params.put("pageNum", "1");
		params.put("pageSize", "10");
		//params.put("path", "searchSLGC");
		params.put("askstatus", "2");
		params.put("isClassNull", "1");
		System.out.println(JSON.toJSONString("{\"serviceid\":\"48\",\"method\":\"regService\",\"remark\":\"注册服务返回serviceid\",\"response_type\":\"JSON\",\"response_sample\":\"{\"message\":\"注册接口成功\",\"data\": \"50\",\"success\": true}\",\"cache_effective\":\"0\",\"servicename\":\"注册服务\",\"from_url\":\"http://172.16.35.50:8080/bus/regService!api\",\"from_request_type\":\"POST\",\"params\":[{\"param\":\"servicename\",\"param_remark\":\"服务名\",\"param_type\":\"string\",\"param_sample\":\"服务1\"},{\"param\":\"from_url\",\"param_remark\":\"来源地址\",\"param_type\":\"string\",\"param_sample\":\"http://demo.htwater.net:8080/shanhong/searchSLGC!PUBLIC\"}]}", SerializerFeature.PrettyFormat));
		long start = Instant.now().toEpochMilli() ;
		System.out.println(JSON.toJSONString(new Service().getServiceList(params), SerializerFeature.WriteMapNullValue));
		System.out.println(Instant.now().toEpochMilli() - start);
		count(1);
		//System.out.print("?wsdl/getUser".substring("?wsdl/getUser".lastIndexOf("/")+1));
	}
}
