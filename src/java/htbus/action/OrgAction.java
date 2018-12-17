package htbus.action;

import java.util.HashMap;
import java.util.Map;

import htbus.service.OrgService;
import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;

public class OrgAction extends DoAction {
	public Responser getXingzhengOrg(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getXingzhengOrg()));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser getDangOrg(){ 
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getDangOrg()));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser getPersonListByOrgid(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getPersonListByOrgid(params.getParam("orgid"))));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser getOrgTree(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getOrgTree()));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser getNewOrgID(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getNewOrgID(params.getParam("pid"))));
		responser.setRtType(TEXT);
		return responser;
	}
	public Responser addOrg(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.addOrg(params.getParam("pid"),params.getParam("orgname"));
		if(b){
			result.put("success",true);
			result.put("message", "添加组织机构成功");
		}else{
			result.put("success",false);
			result.put("message", "添加组织机构失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser updateOrg(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.updateOrg(params.getParam("id"),params.getParam("orgname"));
		if(b){
			result.put("success",true);
			result.put("message", "修改组织机构成功");
		}else{
			result.put("success",false);
			result.put("message", "修改组织机构失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser delOrg(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.delOrg(params.getParam("id"));
		if(b){
			result.put("success",true);
			result.put("message", "删除组织机构成功");
		}else{
			result.put("success",false);
			result.put("message", "删除组织机构失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser updateOrgOrd(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.updateOrgOrd(params.getParam("id1"),params.getParam("id2"));
		if(b){
			result.put("success",true);
			result.put("message", "修改组织机构排序成功");
		}else{
			result.put("success",false);
			result.put("message", "修改组织机构排序失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser checkPersonUid(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.checkPersonUid(params.getParam("uid"));
		if(b){
			result.put("success",true);
			result.put("message", "ID可用");
		}else{
			result.put("success",false);
			result.put("message", "ID重复");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser addPerson(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.addPerson(params.getParam("uid"),
				params.getParam("name"),params.getParam("mobile"),params.getParam("tel")
				,params.getParam("smobile"),params.getParam("mail")
				,params.getParam("sex"),params.getParam("photo")
				,params.getParam("orgids"),params.getParam("roleid"),params.getParam("postid"));
		if(b){
			result.put("success",true);
			result.put("message", "添加人员成功");
		}else{
			result.put("success",false);
			result.put("message", "人员添加失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser updatePerson(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.updatePerson(params.getParam("uid"),
				params.getParam("name"),params.getParam("mobile"),params.getParam("tel")
				,params.getParam("smobile"),params.getParam("mail")
				,params.getParam("sex"),params.getParam("photo")
				,params.getParam("orgids"),params.getParam("roleid"),params.getParam("postid"));
		if(b){
			result.put("success",true);
			result.put("message", "人员信息修改成功");
		}else{
			result.put("success",false);
			result.put("message", "人员信息修改失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	public Responser delPerson(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.delPerson(params.getParam("uid"));
		if(b){
			result.put("success",true);
			result.put("message", "人员删除成功");
		}else{
			result.put("success",false);
			result.put("message", "人员删除失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getPostList(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getPostList(params.getParam("orgid"))));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser addPost(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> a = org.addPost(params.getParam("orgid"),
				params.getParam("postname"));
		if(a!=null && (boolean)a.get("result")){
			result.put("success",true);
			result.put("message", "添加岗位成功");
			result.put("data", a.get("data"));
		}else{
			result.put("success",false);
			result.put("message", "添加岗位失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser updatePost(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.updatePost(params.getParam("postid"),params.getParam("postname"));
		if(b){
			result.put("success",true);
			result.put("message", "修改岗位成功");
		}else{
			result.put("success",false);
			result.put("message", "修改岗位失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser deletePost(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.delPost(params.getParam("postid"));
		if(b){
			result.put("success",true);
			result.put("message", "删除岗位成功");
		}else{
			result.put("success",false);
			result.put("message", "删除岗位失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser getRoleList(){
		OrgService org = new OrgService();
		responser.setRtString(parseJSON(org.getRoleList()));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser addRole(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> a = org.addRole(params.getParam("rolename"));
		if(a!=null && (boolean)a.get("result")){
			result.put("success",true);
			result.put("message", "添加角色成功");
			result.put("data", a.get("data"));
		}else{
			result.put("success",false);
			result.put("message", "添加角色失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser updateRole(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.updateRole(params.getParam("roleid"),params.getParam("rolename"));
		if(b){
			result.put("success",true);
			result.put("message", "修改角色成功");
		}else{
			result.put("success",false);
			result.put("message", "修改角色失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser deleteRole(){
		OrgService org = new OrgService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = org.delRole(params.getParam("roleid"));
		if(b){
			result.put("success",true);
			result.put("message", "删除角色成功");
		}else{
			result.put("success",false);
			result.put("message", "删除角色失败");
		}
		responser.setRtString(parseJSON(result));
		responser.setRtType(JSON);
		return responser;
	}
}
