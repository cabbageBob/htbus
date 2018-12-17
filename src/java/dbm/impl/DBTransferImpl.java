package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.miao.framework.action.DoAction;
import dbm.helper.DBTransferHelper;
import dbm.helper.DBTransferUtil;

public class DBTransferImpl {
	DBTransferHelper dbTHelper = new DBTransferHelper();
	
	public static void main(String[] args) {
		
		
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().getDbDetailByDbid("1")));//mssql
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().getDbDetailByDbid("3")));//oracle
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().getDbDetailByDbid("3")));//mysql
		
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().getInsertRule()));
		
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().createTask("d041f90a-99c6-11e6-a17e-005056931899", "task1", "task1测试")));
		
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().setUpTask("1", "1", "2", "HT_GATE", "", "", "4", "5", "7")));
		//System.out.println(new DoAction().parseJSON(new DBTransferImpl().setUpTask("2", "1", "3", "pm_stations", "HT_VIEW_RAIN2,HT_VIEW_RAIN3,HT_VIEW_RAIN4,HT_VIEW_RAINDAY", "", "5", "2", "7")));
		
		System.out.println(new DoAction().parseJSON(new DBTransferImpl().runTask("1")));
		
		System.exit(0);
	}
	 
	//获取所有任务
	public List<Map<String, Object>> getTaskByUser(String userid){
		return DBTransferUtil.getDataFromDbTrans(
				"{call getTaskByUser(?)}",new Object[]{userid});
	}
	
	//获取数据库表、视图、存储过程详情
	public Map<String, Object> getDbDetailByDbid(String dbid){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("alltables",dbTHelper.executeDBsql(dbid,1,null));
		rtMap.put("allviews", dbTHelper.executeDBsql(dbid,2,null));
		rtMap.put("allpros", dbTHelper.executeDBsql(dbid,3,null));
		return rtMap;
	}
	
	//转换规则获取
	public Map<String, Object> getInsertRule(){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		List<Map<String, Object>> ruleList = DBTransferUtil.getDataFromDbTrans(
				"{call getInsertRule()}",null);
		if( null==ruleList || ruleList.size()==0 )
			return rtMap;
		for( Map<String, Object> map:ruleList ){
			//insert_ruleid, insert_rule_type, insert_rule_cnt, insert_rule_des
			if( !rtMap.containsKey(map.get("rule_type_des").toString()) ){
				rtMap.put(map.get("rule_type_des").toString(),"");
			}
		}
		Set<String> keyset = rtMap.keySet();
		for( String key:keyset ){
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for( Map<String, Object> map:ruleList ){
				if( map.get("rule_type_des").equals(key) ){
					Map<String, Object> tmpmap = new HashMap<String, Object>();
					tmpmap.putAll(map);
					tmpmap.remove("rule_type_des");
					list.add(tmpmap);
				}
			}
			rtMap.put(key, list);
		}
		return rtMap;
	}
	
	//创建任务,创建成功返回 该条任务详情，失败返回 null
	public Map<String, Object> createTask(String userid,String taskname,String taskdes){
		List<Map<String, Object>> createList = DBTransferUtil.getDataFromDbTrans(
				"{call createTask(?,?,?)}",new Object[]{userid,taskname,taskdes});
		if( null==createList || createList.size()==0 )
			return null;
		return createList.get(0);
	}

	//设置任务，包括：保存任务详情、保存源数据库、保存目标数据库。返回内容 getTaskDetail
	//高级菜单中，保存源、目标数据表、字段之类的信息暂时不做。 20161114 已做
	public Map<String, Object> setUpTask(String taskid, String dbsid, String dbtid, String tbnames, String viewnames, String pronames,
			String ruleidcreate, String ruleidinsert, String ruleiddrror) {
		//设置任务源、目标库、规则，并绑定源、目标库
		List<Map<String, Object>> createTaskList = DBTransferUtil.getDataFromDbTrans(
				"{call setUpTask(?,?,?,?,?,?)}",new Object[]{taskid,dbsid,dbtid,
						ruleidcreate,ruleidinsert,ruleiddrror});
		if( null==createTaskList || createTaskList.size()==0 )
			return null;
		//表、视图、存储过程个数
		int tbselectedcnt = (null==tbnames||tbnames.length()==0)?0:tbnames.split(",").length;
		int viewselectedcnt = (null==viewnames||viewnames.length()==0)?0:viewnames.split(",").length;
		int proselectedcnt = (null==pronames||pronames.length()==0)?0:pronames.split(",").length;
		
		//存储源、目标表结构
		String stbids = "",ttbids="";
		if( tbselectedcnt>0 ){
			Map<String, String> stTBIDMap =  dbTHelper.setUpDefaultTB(taskid,dbsid, dbtid, tbnames);
			stbids = stTBIDMap.get("source_tbids");
			ttbids = stTBIDMap.get("target_tbids");
		}

		//setUpSrcDB	
		String alltbs = dbTHelper.formatListValue(
				dbTHelper.executeDBsql(dbsid,1,null), "tbname");
		String allviews = dbTHelper.formatListValue(
				dbTHelper.executeDBsql(dbsid,2,null), "viewname");
		String allpros = dbTHelper.formatListValue(
				dbTHelper.executeDBsql(dbsid,3,null), "proname");

		List<Map<String, Object>> setUpSrcDBList = DBTransferUtil.getDataFromDbTrans(
				"{call setUpSrcDB(?,?,?,?,?,?,?,?,?,?,?)}",new Object[]{dbsid,taskid,
						tbselectedcnt,viewselectedcnt,proselectedcnt,
						alltbs,tbnames,allviews,viewnames,allpros,pronames});//此处使用 stbids 代替 tbnames,避免tbnames重复?
		
		//setUpTarDB
		/*alltbs = DBTransferHelper.formatListValue(
				DBTransferHelper.executeDBsql(dbtid,1,null), "tbname");
		allviews = DBTransferHelper.formatListValue(
				DBTransferHelper.executeDBsql(dbtid,2,null), "viewname");
		allpros = DBTransferHelper.formatListValue(
				DBTransferHelper.executeDBsql(dbtid,3,null), "proname");*/
		List<Map<String, Object>> setUpTarDBList = DBTransferUtil.getDataFromDbTrans(
				"{call setUpTarDB(?,?,?,?,?,?,?,?,?,?,?)}",new Object[]{dbtid,taskid,
						tbselectedcnt,viewselectedcnt,proselectedcnt,
						alltbs,tbnames,allviews,viewnames,allpros,pronames});//此处使用 ttbids 代替 tbnames?

		//更新任务状态为可运行
		try {
			boolean updateTaskStatus = DBTransferUtil.executeSqlByDbTrans(
					"{call updateTaskUserEnabled(?,?,?)}",new Object[]{
							createTaskList.get(0).get("userid"),taskid,"1"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getTaskDetail(taskid);
	}
	
	//获取任务详情
	public Map<String, Object> getTaskDetail(String taskid) {
		List<Map<String, Object>> taskdetailList = DBTransferUtil.getDataFromDbTrans(
				"{call getTaskDetail(?)}",new Object[]{taskid});
		if( null==taskdetailList || taskdetailList.size()==0 )
			return null;
		return taskdetailList.get(0);
	}
	
	//运行任务
	public Map<String, Object> runTask(String taskid) {
		return dbTHelper.runTask(taskid);
	}
	
	//删除任务
	public boolean deleteTask(String taskids) {
		boolean result = false;
		try {
			result = DBTransferUtil.executeSqlByDbTrans("{call deleteTask(?)}",new Object[]{taskids});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Object saveAdvSetting(String taskid, String stbid, 
			String ttbid,String sfields,String sfieldtypes, String tfields, 
			String tfieldtypes, String tfieldvalues, String wherecondition) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAdvSetting(String taskid, String dbsid, String dbtid, String param4, String param5) {
		// TODO Auto-generated method stub
		return null;
	}

}
