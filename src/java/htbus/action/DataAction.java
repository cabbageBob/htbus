package htbus.action;

import htbus.service.DataService;
import htbus.util.RedisUtil;
import htbus.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;

public class DataAction extends DoAction {
	
	//获取实例列表
	public Responser getInstanceList(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getInstanceList()));
		responser.setRtType(JSON);
		return responser;
	}
	
	//根据实例ID获取数据库列表
	/*public Responser getDBlistByIID(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getDBlistByIID2(params.getParam("instance_id"))));
		responser.setRtType(JSON);
		return responser;
	}*/
	
	//根据数据库名和实例ID获取数据库内所有表信息
	public Responser getDBTables(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getDBTables(
				params.getParam("instance_id"),
				params.getParam("dbname")
				)));
		responser.setRtType(JSON);
		return responser;
	}
	
	
	//根据表ID获取表字典
	public Responser getTableField(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getTableField(
				params.getParam("instance_id"),
				params.getParam("dbname"),
				params.getParam("tbname")
				)));
		responser.setRtType(JSON);
		return responser;
	}
	
	//===========================================
	
	public Responser getDBTree(){
		String cache = RedisUtil.get("dbtree");
		if(cache == null){
			DataService d = new DataService();
			String str = parseJSON(d.getDBTree());
			responser.setRtString(str);
			RedisUtil.set("dbtree",str,10);
		}else{
			responser.setRtString(cache);
		}
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser analyzeDBTree(){//重新解析
		DataService d = new DataService();
		String str = parseJSON(d.getDBTree());
		RedisUtil.set("dbtree",str,10);
		responser.setRtString(str);
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser setVisible(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.setVisible(params.getParam("id"),Integer.parseInt(params.getParam("visible")));
		if(b){
			result.put("success",true);
			result.put("message", "设置成功");
			RedisUtil.del("dbtree");
		}else{
			result.put("success",false);
			result.put("message", "设置失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	} 
	
	public Responser setRemark(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.setRemark(params.getParam("id"),params.getParam("remark"));
		if(b){
			result.put("success",true);
			result.put("message", "设置成功");
			RedisUtil.del("dbtree");
		}else{
			result.put("success",false);
			result.put("message", "设置失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	} 
	
	//根据数据库名和实例ID获取数据库内所有表信息
	public Responser analyzeDBTables(){
		DataService d = new DataService();
		responser.setRtString(com.alibaba.fastjson.JSON.toJSONString((d.analyzeDBTables(
				params.getParam("instance_id")
				)), SerializerFeature.WriteMapNullValue));
		responser.setRtType(JSON);
		return responser;
	}
	
	//根据表ID获取表字典
	public Responser analyzeTableFields(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.analyzeTableFields(
				params.getParam("instance_id"),
				params.getParam("tbname")
				)));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getInstance(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getInstanceWithUserinfo(params.getParam("instance_id"))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser addInstance(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.checkInstanceRepeate(params.getParam("ip"),
				params.getParam("port"),
				params.getParam("instance_name"));
		if(!b){
			b = s.addInstance(
					params.getParam("name"),
					params.getParam("ip"),
					params.getParam("port"),
					params.getParam("instance_name"),
					params.getParam("account"),
					params.getParam("password"),
					params.getParam("dbtype"),
					params.getParam("remark"));
					
			if(b){
				result.put("success",true);
				result.put("message", "添加实例成功");
			}else{
				result.put("success",false);
				result.put("message", "添加实例失败");
			}
		}else{
			result.put("success",false);
			result.put("message", "该实例已被注册");
		}
		
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser updateInstance(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.updateInstance(
				params.getParam("instance_id"),
				params.getParam("name"),
				params.getParam("ip"),
				params.getParam("port"),
				params.getParam("instance_name"),
				params.getParam("account"),
				params.getParam("password"),
				params.getParam("dbtype"),
				params.getParam("remark"));
		if(b){
			result.put("success",true);
			result.put("message", "修改实例成功");
		}else{
			result.put("success",false);
			result.put("message", "修改实例失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser deleteInstance(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = s.deleteInstance(
				params.getParam("instance_id"));
		if(b){
			result.put("success",true);
			result.put("message", "删除实例成功");
		}else{
			result.put("success",false);
			result.put("message", "删除实例失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getDatabaseDicdoc(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		String url = s.getDatabaseDicdoc(
				params.getParam("instance_id")
				,params.getParam("dbname"));
		if(url.length()>0){
			result.put("success",true);
			result.put("message", "文档已生成请直接下载");
		}else{
			result.put("success",false);
			result.put("message", "文档不存在请先生成文档");
		}
		result.put("docurl", url);
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser createDatabaseDicdoc(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		String url = s.createDatabaseDicdoc(
				params.getParam("instance_id")
				,params.getParam("dbname"));
		if(url.length()>0){
			result.put("success",true);
			result.put("message", "文档已生成请直接下载");
		}else{
			result.put("success",false);
			result.put("message", "服务器错误，文档生成失败");
		}
		result.put("docurl", url);
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser remarkDB(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = false;
		b = s.remarkDB(
				params.getParam("instance_id")
				,params.getParam("dbname")
				,params.getParam("remark"));
		if(b){
			result.put("success",true);
			result.put("message", "备注成功");
		}else{
			result.put("success",false);
			result.put("message", "备注失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser remarkTable(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = false;
		b = s.remarkTable(
				params.getParam("instance_id")
				,params.getParam("dbname")
				,params.getParam("tablename")
				,params.getParam("remark"));
		if(b){
			result.put("success",true);
			result.put("message", "备注成功");
		}else{
			result.put("success",false);
			result.put("message", "备注失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser remarkField(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = false;
		b = s.remarkField(
				params.getParam("instance_id")
				,params.getParam("dbname")
				,params.getParam("tablename")
				,params.getParam("filedname")
				,params.getParam("remark")
				,params.getParam("type"),
				params.getParam("length"));
		if(b){
			result.put("success",true);
			result.put("message", "备注成功");
		}else{
			result.put("success",false);
			result.put("message", "备注失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getAppListWithDBBind(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getAppListWithDBBind(params.getParam("instance_id"))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser bindDBtoApp(){
		DataService s = new DataService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = false;
		b = s.bindDBtoApp(
				params.getParam("instance_id")
				,params.getParam("appids"));
		if(b){
			result.put("success",true);
			result.put("message", "绑定成功");
		}else{
			result.put("success",false);
			result.put("message", "绑定失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getDBListByApp(){
		DataService d = new DataService();
		responser.setRtString(parseJSON(d.getDBListByApp(TokenUtil.getAppid(request))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser staDataResourceInfo() throws Exception{
		DataService dbservice = new DataService();
		String result = RedisUtil.get(DataService.DB_STA_KEY);
		if(result == null){
			result = parseJSON(dbservice.staDataResourceInfo());
		}
		responser.setRtString(result);
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser searchField(){
		DataService dbservice = new DataService();
		responser.setRtString(parseJSON(dbservice.searchField(params.getParam("keyword"))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser refreshFields(){
		DataService dbservice = new DataService();
		Map<String, String> map = new HashMap<>();
		try {
			dbservice.importFieldEntity();
			map.put("result", "success");
		} catch (Exception e) {
			map.put("result", "fail");
		}
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser executeSQL(){
		responser.setRtString(parseJSON(new DataService().executeSQL(params.getParam("instance_id"), 
				 params.getParam("sql"), params.getParam("dbname"), getRequestIp(), TokenUtil.getAppid(request))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser checkDb(){
		DataService dbservice = new DataService();
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("success", dbservice.checkConnection(params.getParam("ip"), params.getParam("port"), 
					params.getParam("instance_name"), params.getParam("account"), 
					params.getParam("password"), params.getParam("dbtype")));
		} catch (Exception e) {
			map.put("result", false);
		}
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getMatedataTree(){
		DataService resources = new DataService();
		responser.setRtString(parseJSON(resources.getMatedataTree()));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getMatedataTreeBind(){
		DataService resources = new DataService();
		responser.setRtString(parseJSON(resources.getMatedataTreeBind()));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser addMatedataClass(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.addMatedataClass(params.getParams())));
		return responser;
	}
	
	public Responser updateMatedata(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.updateMatedata(params.getParams())));
		return responser;
	}
	
	public Responser deleteMatedata(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.deleteMatedata(params.getParams())));
		return responser;
	}
	
	public Responser dragMatedata(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		Double prev = params.getParam("prev") == null ? null : Double.parseDouble(params.getParam("prev"));
		Double post = params.getParam("post") == null ? null : Double.parseDouble(params.getParam("post"));
		responser.setRtString(parseJSON(resources.dragMatedata(params.getParam("parent_code"), prev,
				post, params.getParam("code"))));
		return responser;
	}
	
	public Responser getMatedataDetail(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.getMatedataDetail(params.getParams())));
		return responser;
	}
	
	public Responser addMatedataDetail(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.addMatedataDetail(params.getParams())));
		return responser;
	}
	
	public Responser updateMatedataDetail(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.updateMatedataDetail(params.getParams())));
		return responser;
	}
	
	public Responser delMatedataDetail(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.delMatedataDetail(params.getParams())));
		return responser;
	}
	
	public Responser addMateDataResources(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.addMateDataResources(params.getParam("code"), params.getParam("resourceId"), params.getParam("resourceType"))));
		return responser;
	}
	
	public Responser getMateDataResources(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.getMateDataResources(params.getParam("code"), params.getParam("resourceType"))));
		return responser;
	}
	
	public Responser getMateDataCode(){
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.getMateDataCode(params.getParam("resourceId"), params.getParam("resourceType"))));
		return responser;
	}
	public Responser monitorDataResourceInfo() {
		DataService resources = new DataService();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(resources.monitorDataResourceInfo()));
		return responser;
	}
}
