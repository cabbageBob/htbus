package htbus.action;

import java.util.Map;

import htbus.service.UserService;
import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;

public class PublicAction extends DoAction {
	public Responser login(){
		UserService u = new UserService();
		Map<String,Object> map = u.login(params.getParam("username"), params.getParam("userpassword"));
		
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser applogin(){
		UserService u = new UserService();
		Map<String,Object> map = u.applogin(params.getParam("username"), params.getParam("userpassword"));
		
		responser.setRtString(parseJSON(map));
		responser.setRtType(JSON);
		return responser;
	}
	
	public Responser test(){
		responser.setRtString("结果正确："+params.getParam("p"));
		responser.setRtType(TEXT);
		return responser;
	}
}
