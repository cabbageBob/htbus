package dbm.action;

import java.util.List;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.factory.ServiceFactory;
import dbm.impl.DataSource;
import dbm.impl.User;
import dbm.service.SSOService;
import dbm.util.LogUtil;

public class UserAction extends DoAction {
	User user = new User();
	public Responser getGroup() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getGroup()));
		return responser;
	}
	public Responser getGroup2() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getGroup2()));
		return responser;
	}
	public Responser getGroup3() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getGroup3()));
		return responser;
	}
	public Responser getRole() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getRole()));
		return responser;
	}
	public Responser getData() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getData()));
		return responser;
	}
	public Responser getUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getUser()));
		return responser;
	}
	public Responser getLoginUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getLoginUser(params.getParam("uname"))));
		return responser;
	}
	public Responser addUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.addUser(params.getParams())));
		return responser;
	}
	public Responser addPer() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.addPer(params.getParams())));
		return responser;
	}
	public Responser delUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.delUser(params.getParams())));
		return responser;
	}
	public Responser freezeUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.freezeUser(params.getParams())));
		return responser;
	}
	public Responser editUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.editUser(params.getParams())));
		return responser;
	}
	public Responser editUser2() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.editUser2(params.getParams())));
		return responser;
	}
	public Responser updateTime() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.updateTime(params.getParams())));
		return responser;
	}
	public Responser userIsExist(){
		SSOService service = (SSOService) ServiceFactory.getService("ssoimpl");
		responser.setRtType(JSON);
		boolean isExist = service.userIsExist(params.getParam("username"));
		responser.setRtString("{\"isExist\": "+ isExist +"}");
		return responser;
	}
	public Responser giveUser() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.giveUser(params.getParams())));
		return responser;
	}
	public Responser giveUser2() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.giveUser2(params.getParams())));
		return responser;
	}
	public Responser giveIp() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.giveIp(params.getParam("uname"))));
		return responser;
	}
	public Responser givelimit() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.givelimit(params.getParams())));
		return responser;
	}
	public Responser getPermission() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getPermission(params.getParam("uname"))));
		return responser;
	}
	public Responser getPermission2() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(user.getPermission2(params.getParam("uname"))));
		return responser;
	}
	/**
	 * 获取当前登陆用户信息
	 * @return
	 */
	public Responser getCurrentUser(){
		responser.setRtType(JSON);
		String sql="select uid,uname,realname,utype,`group` gid from sys_user where uname=?";
		try {
			Map<String,Object> user = DataSource.sysdao().executeQueryObject(sql,new Object[]{session.getAttribute("token").toString()});
			responser.setRtString(parseJSON(user));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responser;
	}
	
	/**
	 * 从客户端记录用户关键的数据库操作日志
	 * @return
	 */
	public Responser userActionLog(){
		LogUtil.log(
				session.getAttribute("token").toString(), 
				params.getParam("logtype"), 
				params.getParam("msg"), 
				params.getParam("msg1")==null?"":params.getParam("msg1"), 
				params.getParam("msg2")==null?"":params.getParam("msg2")
		);
		responser.setRtType(NONE);
		return responser;
	}
	
	public Responser getActionLog() throws Exception{
		String sql = "select a.*,b.logname from sys_log a left join sys_log_type b on a.logtype=b.logtype where user<>'system' and logtm>? and logtm<?";
		List<Map<String,Object>> list = DataSource.sysdao().executeQuery(sql,new Object[]{params.getParam("tm1"),params.getParam("tm2")});
		responser.setRtString(parseJSON(list));
		responser.setRtType(JSON);
		return responser;
	}
}
