package htbus.action;

import htbus.service.ResourceService;
import htbus.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;

public class ResourceAction extends DoAction {
	public Responser getCompanyList(){
		ResourceService service = new ResourceService();
		responser.setRtString(parseJSON(service.getCompanyList()));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser addCompany(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.addCompany(
				params.getParam("companyname"),
				params.getParam("keeper"),
				params.getParam("mobile")
				);
		if(b){
			result.put("success",true);
			result.put("message", "添加开发商成功");
		}else{
			result.put("success",false);
			result.put("message", "添加开发商失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser updateCompany(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.updateCompany(
				params.getParam("id"),
				params.getParam("companyname"),
				params.getParam("keeper"),
				params.getParam("mobile")
				);
		if(b){
			result.put("success",true);
			result.put("message", "修改开发商成功");
		}else{
			result.put("success",false);
			result.put("message", "修改开发商失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;	
	}
	
	public Responser deleteCompany(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.deleteCompany(
				params.getParam("id")
				);
		if(b){
			result.put("success",true);
			result.put("message", "删除开发商成功");
		}else{
			result.put("success",false);
			result.put("message", "删除开发商失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;	
	}
	
	public Responser getAppList(){
		ResourceService service = new ResourceService();
		responser.setRtString(parseJSON(service.getAppList(
				)));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser addApp(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.addApp(
				params.getParam("appname"),
				params.getParam("companyid"),
				params.getParam("url"),
				params.getParam("ip"),
				params.getParam("domain"),
				params.getParam("account"),
				params.getParam("password"),
				params.getParam("outip"),
				params.getParam("remark")
				);
		if(b){
			result.put("success",true);
			result.put("message", "添加应用系统成功");
		}else{
			result.put("success",false);
			result.put("message", "添加应用系统失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;	
	}
	
	public Responser updateApp(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.updateApp(
				params.getParam("id"),
				params.getParam("appname"),
				params.getParam("companyid"),
				params.getParam("url"),
				params.getParam("ip"),
				params.getParam("domain"),
				params.getParam("account"),
				params.getParam("password"),
				params.getParam("remark")
				);
		if(b){
			result.put("success",true);
			result.put("message", "修改应用系统成功");
		}else{
			result.put("success",false);
			result.put("message", "修改应用系统失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;	
	}
	
	public Responser deleteApp(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.deleteApp(
				params.getParam("id")
				);
		if(b){
			result.put("success",true);
			result.put("message", "删除应用系统成功");
		}else{
			result.put("success",false);
			result.put("message", "删除应用系统失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;	
	}
	
	//获取当前登陆应用账号的应用信息
	public Responser currentAppInfo(){
		ResourceService service = new ResourceService();
		String appaccount = TokenUtil.getUsername(request);
		responser.setRtString(parseJSON(service.currentAppInfo(appaccount)));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser updateAppPassword(){
		String username = TokenUtil.getUsername(request);
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.updateAppPassword(username,params.getParam("oldpassword"),params.getParam("newpassword"));
		if(b){
			result.put("success",true);
			result.put("message", "密码修改成功");
		}else{
			result.put("success",false);
			result.put("message", "密码修改失败，请核对原始密码是否正确");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;
	}
	
	public Responser getAllFileList(){
		ResourceService service = new ResourceService();
		responser.setRtString(parseJSON(service.getAllFileList()));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser getAppFileList(){
		ResourceService service = new ResourceService();
		String appaccount = TokenUtil.getAppid(request);
		responser.setRtString(parseJSON(service.getAppFileList(appaccount)));
		responser.setRtType(JSON);
		return responser;	
	}
	
	public Responser deleteAppFile(){
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.deleteAppFile(params.getParam("fileid"));
		if(b){
			result.put("success",true);
			result.put("message", "文档删除成功");
		}else{
			result.put("success",false);
			result.put("message", "文档删除失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;
	}
	
	public Responser addAppFile(){
		String appid = TokenUtil.getAppid(request);
		ResourceService service = new ResourceService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = service.addAppFile(appid,params.getParam("filepath"),params.getParam("filename"),params.getParam("tag"),
				params.getParam("remark"));
		if(b){
			result.put("success",true);
			result.put("message", "添加文档成功");
		}else{
			result.put("success",false);
			result.put("message", "添加文档失败");
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;
	}
}
