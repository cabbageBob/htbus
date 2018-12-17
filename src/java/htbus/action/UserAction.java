package htbus.action;

import java.util.HashMap;
import java.util.Map;

import htbus.service.UserService;
import htbus.util.TokenUtil;
import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;

public class UserAction extends DoAction {
	/*
	 * 获取用户权限菜单
	 */
	public Responser getUserMenu(){
		String username = TokenUtil.getUsername(request);
		UserService u = new UserService();
		responser.setRtString(parseJSON(u.getUserMenu(username)));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser updatePassword(){
		String username = TokenUtil.getUsername(request);
		UserService u = new UserService();
		Map<String,Object> result = new HashMap<String,Object>();
		boolean b = u.updatePassword(username,params.getParam("oldpassword"),params.getParam("newpassword"));
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
}
