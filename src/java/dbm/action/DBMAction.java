package dbm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.helper.DBTransferHelper;
import dbm.impl.DBRegister;
import dbm.impl.DBmanage;
import dbm.impl.Monitor;

public class DBMAction extends DoAction{
	DBRegister dbr = new DBRegister();
	public Responser getDBSourceList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.getDBSourceList()));
		return responser;
	}
	public Responser getDBServerList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.getDBServerList()));
		return responser;
	}
	public Responser getDBTypeList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.getDBTypeList()));
		return responser;
	}
	public Responser addDBSource(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.addDBSource(params.getParams())));
		return responser;
	}
	public Responser getDBSource(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.getDBSource(params.getParam("id"))));
		return responser;
	}
	public Responser editDBSource(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.editDBSource(params.getParams())));
		return responser;
	}
	public Responser delDBSource(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.delDBSource(params.getParam("id"))));
		return responser;
	}
	public Responser addDBServer(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.addDBServer(params.getParams())));
		return responser;
	}
	public Responser editDBServer(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.editDBServer(params.getParams())));
		return responser;
	}
	public Responser delDBServer(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.delDBServer(params.getParam("id"))));
		return responser;
	}
	public Responser addDBType(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.addDBType(params.getParams())));
		return responser;
	}
	public Responser editDBType(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.editDBType(params.getParams())));
		return responser;
	}
	public Responser addInstance(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.addInstance(params.getParams())));
		return responser;
	}
	public Responser updateInstance(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.updateInstance(params.getParams())));
		return responser;
	}
	public Responser delInstance(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.delInstance(params.getParam("instance_id"))));
		return responser;
	}
	public Responser checkDBConnect(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbr.checkDBConnect(params.getParam("id"))));
		return responser;
	}
	
	public Responser getSourceTree(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getSourceTree(params.getParam("type"),session.getAttribute("token").toString())));
		return responser;
	}
	
	public Responser getMonitorError(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getMonitorError()));
		return responser;
	}
	
	public Responser getDBSizeInfo(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getDBSizeInfo( params.getParam("dbsource_ids")  )));
		return responser;
	}
	
	public Responser getDBSizeLine(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getDBSizeLine( params.getParam("dbsource_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	
	public Responser getRowcountInfo(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getRowcountInfo( params.getParam("dbsource_ids")  )));
		return responser;
	}
	
	public Responser getRowcountLine(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getRowcountLine( params.getParam("dbsource_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	
	public Responser getConnectcountInfo(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getConnectcountInfo( params.getParam("dbsource_ids")  )));
		return responser;
	}
	
	public Responser getConnectcountLine(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(parseJSON(dbm.getConnectcountLine( params.getParam("dbsource_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	public Responser getMonitorInfo (){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getMonitorInfo(params.getParam("dbsource_ids"),session.getAttribute("token").toString())));
		return responser;
	}
	public Responser getAllInstanceInfo(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getAllInstanceInfo()));
		return responser;
	}
	public Responser getCpuLine(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getCpuLine( params.getParam("instance_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	public Responser getMemoryLine(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getMemoryLine( params.getParam("instance_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	public Responser getInstanceInfoById(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getInstanceInfoById(params.getParam("instance_id"))));
		return responser;
	}
	public Responser getLoginDetail(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getLoginDetail( params.getParam("instance_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				,params.getParam("ip1")
				,params.getParam("ip2")
				)));
		return responser;
	}
	public Responser getDDLDetail(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getDDLDetail( params.getParam("instance_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	public Responser getCMDetail(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getCMDetail( params.getParam("instance_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	public Responser getConnDetail(){
		Monitor a = new Monitor();
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(a.getConnDetail( params.getParam("dbsource_id")
				,params.getParam("tm1")
				,params.getParam("tm2")
				)));
		return responser;
	}
	
	/**
	 * 获取表的字段信息 dbid tbname
	 * @return
	 */
	public Responser getFieldInfoOfTable(){
		responser.setRtType(JSON);
		List<Map<String,Object>> list = new DBTransferHelper().executeDBsql(
				params.getParam("dbid"), 
				4, 
				params.getParam("tbname"));
		responser.setRtString(parseJSON(list));
		return responser;
	}
	
	/**
	 * 执行SQL语句
	 * @return
	 */
	public Responser excuteSQLByDBID(){
		responser.setRtType(JSON);
		DBmanage dbm = new DBmanage();
		responser.setRtString(
				parseJSON(
						dbm.excuteSQLByDBID(
								params.getParam("dbid")
								,params.getParam("sql")
						)
				)
		);
		return responser;
	}

	public static void main(String[] args){
		List<Map<String,Object>> list = new DBTransferHelper().executeDBsql(
				"1", 
				4, 
				"ST_PPTN_R");
		list=new ArrayList<Map<String,Object>>();
	}
}
