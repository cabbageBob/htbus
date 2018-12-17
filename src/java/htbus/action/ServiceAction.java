package htbus.action;

import htbus.entity.Page;
import htbus.service.ResourceService;
import htbus.service.Service;
import htbus.util.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.serializer.SerializerFeature;

import net.sf.json.JSONObject;
import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.util.DateUtil;

public class ServiceAction extends DoAction {
	/**
	 * 获取服务列表
	 * @return
	 */
	public Responser getServiceList(){
		Service d = new Service();
		responser.setRtString(com.alibaba.fastjson.JSON.toJSONString(d.getServiceList(params.getParams()), SerializerFeature.WriteMapNullValue));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * 设置服务接口的启用与停用
	 * @return
	 */
	public Responser onoffService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.onoffService(params.getParam("serviceid"),params.getParam("onoff"));
		if(b){
			result.put("success",true);
			result.put("message", "设置成功");
		}else{
			result.put("success",false);
			result.put("message", "设置失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getServiceInfo(){
		Service d = new Service();
		String version = params.getParam("his_version");
		if(version == null){
			responser.setRtString(parseJSON(d.getServiceInfo(params.getParam("serviceid"))));
		}else{
			responser.setRtString(parseJSON(d.getServiceInfo(params.getParam("serviceid"), version)));
		}
		responser.setRtType(JSON);
		return responser;
	}
	
	//获取当前登陆app账号的所有发布出去的接口列表
	public Responser getAppServiceList(){
		ResourceService resource = new ResourceService();
		Service service = new Service();
		Map<String,Object> currentapp = resource.currentAppInfo(TokenUtil.getUsername(request));
		List<Map<String,Object>> list = service.getAppServiceList(currentapp.get("appid").toString());
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	//注册接口
	public Responser regService(){
		Map<String,Object> result = new HashMap<String,Object>();
		String serviceid = new Service().regService(TokenUtil.getToken(request).split("-")[2],params.getParam("servicename"), params.getParam("from_url"), params.getParam("from_request_type"));
		if(serviceid == null){
			result.put("success",false);
			result.put("message", "注册接口失败");
		}else{
			result.put("success",true);
			result.put("message", "注册接口成功");
			result.put("data", serviceid);
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	//检测方法名是否重复
	public Responser checkMethod(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.checkMethod(TokenUtil.getAppid(request),params.getParam("method"),params.getParam("serviceid"));
		if(b){
			result.put("success",true);
			result.put("message", "方法名可使用");
		}else{
			result.put("success",false);
			result.put("message", "方法名重复，请更换其他方法名进行发布");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	//添加服务参数
	public Responser addParam(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.addParam(params.getParam("serviceid"),params.getParam("param"),params.getParam("remark"),params.getParam("type"),params.getParam("sample"));
		if(b){
			result.put("success",true);
			result.put("message", "参数保存成功");
		}else{
			result.put("success",false);
			result.put("message", "参数保存失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	//删除服务参数
	public Responser deleteParam(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.deleteParam(params.getParam("serviceid"),params.getParam("param"));
		if(b){
			result.put("success",true);
			result.put("message", "删除参数成功");
		}else{
			result.put("success",false);
			result.put("message", "删除参数失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	//修改服务接口信息
	public Responser updateService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		JSONObject p = JSONObject.fromObject(params.getParam("jsondata"));
		boolean b = s.updateService(p.getString("serviceid"),p.getString("servicename"),p.getString("method"),p.getString("remark"),
				p.getString("response_type"),p.getString("response_sample"),
				p.getString("cache_effective"),p.getString("from_url"),p.getString("from_request_type"));
		if(b){
			result.put("success",true);
			result.put("message", "修改服务接口成功");
		}else{
			result.put("success",false);
			result.put("message", "修改服务接口失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser publishService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		JSONObject p = JSONObject.fromObject(params.getParam("jsondata"));
		boolean b = s.publishService(p.getString("serviceid"),p.getString("servicename"),p.getString("method"),p.getString("remark"),
				p.getString("response_type"),p.getString("response_sample"),
				p.getString("cache_effective"),p.getString("from_url"),p.getString("from_request_type"), p.getJSONArray("params"));
		if(b){
			result.put("success",true);
			result.put("message", "发布服务接口成功");
		}else{
			result.put("success",false);
			result.put("message", "发布服务接口失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	//删除接口
	public Responser deleteService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.deleteService(params.getParam("serviceid"));
		if(b){
			result.put("success",true);
			result.put("message", "删除服务接口成功");
		}else{
			result.put("success",false);
			result.put("message", "删除服务接口失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser askService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.askService(TokenUtil.getAppid(request),params.getParam("serviceids"));
		if(b){
			result.put("success",true);
			result.put("message", "申请接口成功");
		}else{
			result.put("success",false);
			result.put("message", "申请接口失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getServiceListWithAsk(){
		Service service = new Service();
		Page list = service.getServiceListWithAsk(TokenUtil.getAppid(request), params.getParams());
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getServiceAskList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getServiceAskList();
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser dealServiceAsk(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.dealServiceAsk(params.getParam("askid"),params.getParam("result"));
		if(b){
			result.put("success",true);
			result.put("message", "操作成功");
		}else{
			result.put("success",false);
			result.put("message", "操作失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser hasPowerServiceList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.hasPowerServiceList(params.getParam("appid"),params.getParam("from_appid"));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser noPowerServiceList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.noPowerServiceList(params.getParam("appid"),params.getParam("from_appid"));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser powerService(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.powerService(params.getParam("appid"),params.getParam("power"),params.getParam("serviceids"));
		if(b){
			result.put("success",true);
			result.put("message", "操作成功");
		}else{
			result.put("success",false);
			result.put("message", "操作失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getAskList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getAskList(TokenUtil.getAppid(request));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}

	public Responser getServiceMonitorList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getServiceMonitorList();
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getRequestMonitorList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getRequestMonitorList(params.getParam("tm1"),params.getParam("tm2"));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getRequestMonitorListBySid(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getRequestMonitorList(params.getParam("serviceid"), 
				DateUtil.getDayByOffset(-1,"yyyy-MM-dd HH:mm:ss"), DateUtil.getNow());
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser setServiceCache(){
		Service s = new Service();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.setServiceCache(params.getParam("serviceid"),params.getParam("cache_effective"));
		if(b){
			result.put("success",true);
			result.put("message", "设置成功");
		}else{
			result.put("success",false);
			result.put("message", "设置失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser testService(){
		Service service = new Service();
		String serviceid = params.getParam("bus_service_id");
		Map<String,String> ps = params.getParams();
		ps.remove("bus_service_id");
		Map<String,Object> result = service.testService(serviceid,ps);
		responser.setRtString(parseJSON(result));
		responser.setRtType(TEXT);
		return responser;
	}
	
	public Responser staServiceResourceInfo(){
		Service service = new Service();
		Map<String,Object> map = service.staServiceResourceInfo();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser staErrorServiceList(){
		Service service = new Service();
		List<Map<String,Object>> list = service.staErrorServiceList();
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser staSurportInfo(){
		Service service = new Service();
		Map<String,Object> map = service.staSurportInfo();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser staRequestInfo(){
		Service service = new Service();
		Map<String,Object> map = service.staRequestInfo();
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser staRequestDayLine(){
		Service service = new Service();
		List<Map<String,Object>> list = service.staRequestDayLine(params.getParam("tm1"),params.getParam("tm2"));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getAuthorizedApp(){
		Service service = new Service();
		List<Map<String,Object>> list = service.getAuthorizedApp(params.getParam("serviceid"));
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
}
