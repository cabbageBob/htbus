package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.DBTransferImpl;

/**
 * @description  数据转换
 * @date 2016年11月7日下午3:56:45
 * @author lxj
 */
public class DBTransferAction extends DoAction{
	
	DBTransferImpl dbTImpl =new DBTransferImpl();

	//根据用户获取用户任务表
	public Responser getTaskByUser(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.getTaskByUser(params.getParam("userid"))));
		return responser;
	}
	
	//获取数据库表、视图、存储过程详情
	public Responser getDbDetailByDbid(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.getDbDetailByDbid(params.getParam("dbid"))));
		return responser;
	}
	
	//转换规则获取
	//目前支持三种规则的设置：数据插入方式、对象创建方式、遇到错误处理方式
	public Responser getInsertRule(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.getInsertRule()));
		return responser;
	}
	
	//创建任务,创建成功返回 该条任务详情，失败返回 null
	public Responser createTask(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.createTask(params.getParam("userid"),
				params.getParam("taskname"),params.getParam("taskdes"))));
		return responser;
	}
	
	//设置任务,设置成功返回 该条任务详情，失败返回 null
	//参数 ruleidcreate\ruleidinsert\ruleiddrror 形式返回
	public Responser setUpTask(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.setUpTask(
				params.getParam("taskid"), params.getParam("dbsid"), params.getParam("dbtid"),
				params.getParam("tbnames"),params.getParam("viewnames"),
				params.getParam("pronames"), params.getParam("ruleidcreate"),
				params.getParam("ruleidinsert"),params.getParam("ruleiddrror"))));
		return responser;
	}
	
	//获取任务详情
	public Responser getTaskDetail(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.getTaskDetail(params.getParam("taskid"))));
		return responser;
	}
	
	//运行任务
	public Responser runTask(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.runTask(params.getParam("taskid"))));
		return responser;
	}
	
	//删除任务
	public Responser deleteTask(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.deleteTask(params.getParam("taskids"))));
		return responser;
	}
	
	//保存高级设置(筛选字段、条件筛选记录)
	public Responser saveAdvSetting(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.saveAdvSetting(params.getParam("taskid"),
				params.getParam("stbid"),params.getParam("ttbid"),
				params.getParam("sfields"),params.getParam("sfieldtypes"),params.getParam("tfields"),
				params.getParam("tfieldtypes"),params.getParam("tfieldvalues"),
				params.getParam("wherecondition"))));
		return responser;
	}
	
	//获取高级设置(筛选字段、条件筛选记录)
	public Responser getAdvSetting(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbTImpl.getAdvSetting(params.getParam("taskid"),
				params.getParam("dbsid"),params.getParam("dbtid"),
				params.getParam("tbsid"),params.getParam("tbtid"))));
		return responser;
	}
}
