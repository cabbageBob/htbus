package dbm.action;


import dbm.impl.DataSource;
import dbm.impl.Platform;
import dbm.entity.LoginToken;
import dbm.service.SSOService;


import java.util.HashMap;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.endec.RSA;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.factory.ServiceFactory;
import cn.miao.framework.util.StringUtil;

public class ManageAction extends DoAction {
	public Responser getMenus() {
		Platform platform = new Platform();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(platform.getMenus(session.getAttribute("token").toString())));
		return responser;
	}
	public Responser getPublicKey() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(RSA.getPublicKeyMap()));
		return responser;
	}
	public Responser testDataSource() {
		DataSource datasource = new DataSource();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(datasource.testDataSource(params.getParams())));
		return responser;
	}
	SSOService service = (SSOService) ServiceFactory.getService("ssoimpl");
	public Responser login() {
		Map<String, String> rtMap = new HashMap<String, String>();
		responser.setRtType(JSON);
		String userName = (null==params.getParam("userName"))?"":params.getParam("userName");
		String password = (null==params.getParam("password"))?"":params.getParam("password");
		String app = params.getParam("app");
		String type = params.getParam("type"); // sso / login
		// 需要将16进制转为十进制，再转byte
		if (password.length() < 60) {
			rtMap.put("login", "failed");
			responser.setRtString(parseJSON(rtMap));
			return responser;
		}
		// !这里后面重点处理---开始
		password = RSA.decrypt(password); // 解密后会出现空的情况，即无法解密，需要再次登录
		if (0 == password.length()) {
			try {
				password = RSA.decrypt(password);
			} catch (Exception e) {
				password = "";
			}
		}
		// ---!这里后面重点处理---结束
		Map<String, Object> checkResult = service.login(userName, password);
		if (null == checkResult) {
			rtMap.put("login", "failed");
		} else {
			LoginToken token = new LoginToken();
			token.setRealName(checkResult.get("realname").toString());
			token.setDepartment(StringUtil.parseNull(checkResult
					.get("department")));
			token.setUserName(userName);
			token.setUtype(checkResult.get("utype").toString());
			session.setAttribute("token", token);
			session.setAttribute("type", token.getUtype());
			rtMap.put("login", "success");
			
			rtMap.put("redirectUrl", "admin/index.html");
			app = "Platform"; // 用于记录到数据库中
			
		}
		responser.setRtString(parseJSON(rtMap));
		return responser;
	}
	public Responser logout() {
		session.invalidate();
		responser.setRtType(TEXT);
		responser.setRtString("login.html");
		return responser;
	}
	public Responser userIsExist() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(service.userIsExist(params.getParam("username"))));
		return responser;
	}
}
