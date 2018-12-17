package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.DBRegister;
import dbm.impl.DBmanage;

public class DBmanageAction extends DoAction{
	DBmanage dbmanage= new DBmanage();
	public Responser getAllDB(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.getAllDB()));
		return responser;
	}
	
	public Responser addDB(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.addDB(params.getParams())));
		return responser;
	}
	public Responser updateDB(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.updateDB(params.getParams())));
		return responser;
	}
	public Responser delDB(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.delDB(params.getParams())));
		return responser;
	}
	public Responser getDBService(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.getDBService()));
		return responser;
	}
	public Responser addDBService(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.addDBService(params.getParams())));
		return responser;
	}
	public Responser updateDBService(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbmanage.updateDBService(params.getParams())));
		return responser;
	}
	
}
