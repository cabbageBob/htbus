package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.funcMenuImpl;

public class funcMenuAction extends DoAction {
	funcMenuImpl funcmenu=new funcMenuImpl();
	public Responser getUserInfo() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.getUserInfo(session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser editUserInfo() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.editUserInfo(params.getParams())));
		return responser;
	}
	
	public Responser checkUserPsd() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.checkUserPsd(session.getAttribute("token").toString(),params.getParam("psd"))));
		return responser;
	}
	
	public Responser updateUserPsd() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.updateUserPsd(params.getParam("psd"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser getWarnInfo() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.getWarnInfo()));
		return responser;
	}
	
	public Responser editWarnInfo() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.editWarnInfo(params.getParams())));
		return responser;
	}
	
	public Responser getLoginLog() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(funcmenu.getLoginLog(params.getParams())));
		return responser;
	}
	
	public Responser addLoginLog() {
		responser.setRtType(JSON);
		String ip=getRequestIp();
		responser.setRtString(parseJSON(funcmenu.addLoginLog(params.getParams(),ip)));
		return responser;
	}
}