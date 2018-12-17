package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.hjwImpl;

public class hjwAction extends DoAction{
	//这里是黄靖威在后期为完善模块功能需要的一些接口
	hjwImpl hjw= new hjwImpl();
	public Responser addTreeGroup(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.addTreeGroup(params.getParam("name"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser updateTreeGroup(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.updateTreeGroup(params.getParam("ids"),params.getParam("id"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser updateGroupName(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.updateGroupName(params.getParam("name"),params.getParam("id"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser deleteTreeGroup(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.deleteTreeGroup(params.getParam("id"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser orderTree(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.orderTree(params.getParam("ordno"),params.getParam("id"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser addMAccess(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(hjw.addMAccess(params.getParam("access_tm"),session.getAttribute("token").toString(),params.getParam("name"))));
		return responser;
	}
}
