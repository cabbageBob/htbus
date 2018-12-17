package htbus.action;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import htbus.service.DataService;
import htbus.service.MapService;

public class MapAction extends DoAction{
	/**2018年3月12日下午3:03:46
	 *
	 * @author Jokki
	 * @description arcgis地图服务接口 
	 */
	
	
	/**
	 * @description 获取地图服务器服务资源
	 * @param
	 * @return 
	 */
	public Responser getMapServices(){
		MapService service = new MapService();
		String path = URLDecoder.decode(StringUtils.isBlank(params.getParam("path")) ? "" : params.getParam("path")).replace("/", "%2f");
		responser.setRtString(parseJSON(service.getMapServices(params.getParam("id"), path)));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * @description 获取地图服务器
	 * @param
	 * @return 
	 */
	public Responser getMapServerList(){
		MapService service = new MapService();
		responser.setRtString(parseJSON(service.getMapServerList()));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * @description 根据id获取地图服务器
	 * @param
	 * @return 
	 */
	public Responser getMapServerById(){
		MapService service = new MapService();
		responser.setRtString(parseJSON(service.getMapServerById(params.getParam("id"))));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * @description 添加地图服务器
	 * @param
	 * @return 
	 */
	public Responser addMapServer(){
		MapService service = new MapService();
		Map<String, Object> result = new HashMap<>();
		if(service.addMapServer(params.getParam("map_name"), params.getParam("map_url"), 
				params.getParam("username"), params.getParam("password"))){
			result.put("success", true);
			result.put("message", "添加地图服务器成功");
		}else{
			result.put("success", false);
			result.put("message", "添加地图服务器失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * @description 修改地图服务器
	 * @param
	 * @return 
	 */
	public Responser updateMapServer(){
		MapService service = new MapService();
		Map<String, Object> result = new HashMap<>();
		if(service.updateMapServer(params.getParam("id"), params.getParam("map_name"), params.getParam("map_url"), 
				params.getParam("username"), params.getParam("password"))){
			result.put("success", true);
			result.put("message", "修改地图服务器成功");
		}else{
			result.put("success", false);
			result.put("message", "修改地图服务器失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	/**
	 * @description 删除地图服务器
	 * @param
	 * @return 
	 */
	public Responser deleteMapServer(){
		MapService service = new MapService();
		Map<String, Object> result = new HashMap<>();
		if(service.deleteMapServer(params.getParam("id"))){
			result.put("success", true);
			result.put("message", "删除地图服务器成功");
		}else{
			result.put("success", false);
			result.put("message", "删除地图服务器失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
}
