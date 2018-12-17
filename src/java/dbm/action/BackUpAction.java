package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.BackUp;

public class BackUpAction extends DoAction {
	public Responser getAllPlan(){
		BackUp backup = new BackUp();
		responser.setRtType(JSON);
		responser.setRtString(
				 parseJSON(backup.getAllPlan())
				);
		return responser;
	} 
	public Responser addPlan() throws Exception{
		BackUp backup = new BackUp();
		responser.setRtType(TEXT);
		Boolean result = backup.addPlan(params.getParam("dbid"), params.getParam("folder"), params.getParam("type"), params.getParam("rule"));
		responser.setRtString(result?"true":"false");
		return responser;
	}
	public Responser getBackupLog(){
		BackUp backup = new BackUp();
		responser.setRtType(JSON);
		responser.setRtString(
				parseJSON(backup.getPlanLog(params.getParam("planid")))
				);
		return responser;
	}
	public Responser getBackFiles(){
		BackUp backup = new BackUp();
		responser.setRtType(JSON);
		responser.setRtString(
				parseJSON(backup.getBackFiles(params.getParam("planid")))
				);
		return responser;
	}
	public Responser startPlan(){
		BackUp backup = new BackUp();
		responser.setRtType(TEXT);
		Boolean result = backup.startPlan(params.getParam("planid"));
		responser.setRtString(result?"yes":"false");
		return responser;
	}
	public Responser stopPlan(){
		BackUp backup = new BackUp();
		responser.setRtType(TEXT);
		Boolean result = backup.stopPlan(params.getParam("planid"));
		responser.setRtString(result?"yes":"false");
		return responser;
	}
	public Responser deletePlan(){
		BackUp backup = new BackUp();
		responser.setRtType(TEXT);
		Boolean result = backup.deletePlan(params.getParam("planid"));
		responser.setRtString(result?"yes":"false");
		return responser;
	}
	public Responser restoreBack(){
		BackUp backup = new BackUp();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(backup.restoreBack(params.getParam("bakfileid"))));
		return responser;
	}
	
	public Responser getRecoverList() throws Exception{
		BackUp backup = new BackUp();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(backup.getRecoverList()));
		return responser;
	}
}
