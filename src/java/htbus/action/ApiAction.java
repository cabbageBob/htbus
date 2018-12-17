package htbus.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import htbus.service.DataService;
import htbus.service.Service;
import htbus.util.RedisUtil;
import htbus.util.TokenUtil;
import net.sf.json.JSONObject;

public class ApiAction extends DoAction{
	/**2017年12月13日上午11:45:12
	 * 
	 * @author Jokki
	 * @descriptin 提供外部注册服务的接口
	 *  
	 */
	
	
	public Responser regService(){
		Map<String,Object> result = new HashMap<String,Object>();
		String id = new Service().getAppidByToken(request.getHeader("apptoken"));
		if(id == null){
			result.put("success",false);
			result.put("message", "token值无效");
		}else{
			String serviceid = new Service().regService(id, params.getParam("servicename"), params.getParam("from_url"), params.getParam("from_request_type"));
			if(serviceid == null){
				result.put("success",false);
				result.put("message", "注册接口失败");
			}else{
				result.put("success",true);
				result.put("message", "注册接口成功");
				result.put("data", serviceid);
			}
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser publishService(){
		Map<String,Object> result = new HashMap<String,Object>();
		String id = new Service().getAppidByToken(request.getHeader("apptoken"));
		if(id == null){
			result.put("success",false);
			result.put("message", "token值无效");
		}else{
			Service s = new Service();
			try{
				JSONObject p = JSONObject.fromObject(params.getParam("jsondata"));
				boolean b = s.publishServiceByApi(p.getString("serviceid"),p.getString("servicename"),p.getString("method"),p.getString("remark"),
						p.getString("response_type"),p.getString("response_sample"),
						p.getString("cache_effective"),p.getString("from_url"),p.getString("from_request_type"), p.getJSONArray("params"));
				if(b){
					result.put("success",true);
					result.put("message", "发布服务接口成功");
				}else{
					result.put("success",false);
					result.put("message", "发布服务接口失败");
				}
			}catch(Exception e){
				result.put("success",false);
				result.put("message", e.getMessage());
			}
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser updateService(){
		Map<String,Object> result = new HashMap<String,Object>();
		String id = new Service().getAppidByToken(request.getHeader("apptoken"));
		if(id == null){
			result.put("success",false);
			result.put("message", "token值无效");
		}else{
			Service s = new Service();
			try{
				JSONObject p = JSONObject.fromObject(params.getParam("jsondata"));
				boolean b = s.updateServiceByApi(p.getString("serviceid"),p.getString("servicename"),p.getString("method"),p.getString("remark"),
						p.getString("response_type"),p.getString("response_sample"),
						p.getString("cache_effective"),p.getString("from_url"),p.getString("from_request_type"), p.getJSONArray("params"));
				if(b){
					result.put("success",true);
					result.put("message", "修改服务接口成功");
				}else{
					result.put("success",false);
					result.put("message", "修改服务接口失败");
				}
			}catch(Exception e){
				result.put("success",false);
				result.put("message", e.getMessage());
			}
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser appServiceCount() throws Exception{
		List<Map<String,Object>> list = null;
			list = Service.appServiceCount();
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser platforminfo() throws Exception {
		Map<String,Object> map = Service.platforminfo();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser instanceStatusCount() throws IOException {
		Map<String, Integer> map = Service.instanceStatusCount();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser apiStatusCount() throws Exception {
		Map<String,Object> map = Service.apiStatusCount();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser apiCalledCount() throws Exception {
		List<Map<String,Object>> list = Service.apiCalledCount();
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser MonitorsScreen() throws Exception {
		Map<String,Object> map = new HashMap<>();
		map.put("apiCalledCount", Service.apiCalledCount());
		map.put("apiStatusCount", Service.apiStatusCount());
		map.put("instanceStatusCount", Service.instanceStatusCount());
		map.put("platforminfo", Service.platforminfo());
		map.put("appServiceCount", Service.appServiceCount());
		map.put("apiCountMessage", Service.apiCountMessage2());
		map.put("apiCalledMessage", Service.apiCalledMessage2());
		DataService dataService = new DataService();
		String result = RedisUtil.get(dataService.DB_MONITOR_KEY);
		if(result==null) {
			result=parseJSON(dataService.monitorDataResourceInfo());
		}
		map.put("DataSourceInfo",com.alibaba.fastjson.JSON.toJSON(result));
		map.put("totalData", dataService.getTotalData());
//		map.put("DataSourceInfo", dataService.monitorDataResourceInfo());
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
}
