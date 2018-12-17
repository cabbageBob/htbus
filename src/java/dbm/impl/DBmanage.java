package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.util.PinyinUtil;
import dbm.util.DBUtil;

public class DBmanage {
	public List<Map<String, Object>> getAllDB() {
		String sql = "select * from db_connect";
		List<Map<String, Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			rtlist = DataSource.sysdao().executeQuery(sql);
		for(Map<String,Object> m : rtlist){
			m.put("_s", String.valueOf(m.get("dbname"))+PinyinUtil.converterToPinYin(String.valueOf(m.get("dbname")))
					+String.valueOf(m.get("dbremark"))+PinyinUtil.converterToPinYin(String.valueOf(m.get("dbremark"))));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtlist;
	}
	
	public Map<String, Object> addDB(Map<String, String>params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "insert into db_connect(dsid,dbname,dburl,ip,username,password,db_type,port,service_name,service_remark,dbremark,driverclass,SID"
						+ " ) values (?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.get("dsid"),params.get("dbname"),params.get("dburl")
						,params.get("ip"),params.get("username"),params.get("password")
						,params.get("db_type"),params.get("port"),params.get("service_name"),params.get("service_remark")
						,params.get("dbremark"),params.get("driverclass")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	/**
	 * 更新db_connect
	 * @param 
	 * @return
	 */
	public Map<String, Object> updateDB(Map<String, String>params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "UPDATE db_connect set dsid=?,dbname=?,dburl=?,ip=?,username=?,password=?,db_type=?,port=?,service_name=?,service_remark=?,dbremark=?,driverclass=?,SID=? WHERE id=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.get("dsid"),params.get("dbname"),params.get("dburl")
					,params.get("ip"),params.get("username"),params.get("password")
					,params.get("db_type"),params.get("port"),params.get("service_name"),params.get("service_remark")
					,params.get("dbremark"),params.get("driverclass"),params.get("SID"),params.get("id")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	/**
	 * 删除db_connect指定数据
	 * @param 
	 * @return
	 */
	public Map<String, Object> delDB(Map<String, String>params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "Delete from db_connect WHERE id=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.get("id")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public List<Map<String, Object>> getDBService() {
		String sql = "select * from serviceqi";
		List<Map<String, Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			rtlist = DataSource.sysdao().executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtlist;
	}
	
	public Map<String, Object> addDBService(Map<String, String>params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "insert into serviceqi(ip,service_name,service_remark"
						+ " ) values (?,?,?)";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.get("ip"),params.get("service_name"),params.get("service_remark")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	/**
	 * 更新serviceqi
	 * @param 
	 * @return
	 */
	public Map<String, Object> updateDBService(Map<String, String>params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "UPDATE serviceqi set ip=?,service_name=?,service_remark=? WHERE id=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.get("ip"),params.get("service_name"),params.get("service_remark"),params.get("id")});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public List<Map<String,Object>> getSourceTree(String type,String uname){
		//先获取所有的数据资源
		//List<Map<String,Object>> list_dbs
		String sql_dbs = "select a.id,CONCAT(a.name,'(',a.dbremark,')') name,a.name dbname,a.dbname dbcode,a.server_ip,a.dbremark,(case b.status when '正常' then 'green' when '响应慢' then 'yellow' else 'red' end) iconSkin from dbsource a left join db_monitor_r b on a.id=b.dbsource_id";
		List<Map<String, Object>> list_dbs_all = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list_grp = new ArrayList<Map<String,Object>>();
		try {
			list_dbs_all = DataSource.sysdao().executeQuery(sql_dbs);
		//获取当前用户权限
		String sql_power = "select permission from sys_user where uname=?";
		Map<String,Object> power = DataSource.sysdao().executeQueryObject(sql_power, new Object[]{uname});
		String str_power = ","+power.get("permission").toString()+",";
		//根据权限过滤
		List<Map<String,Object>> list_dbs = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> db : list_dbs_all){
			if(str_power.indexOf( ","+db.get("id").toString()+"," )>-1){
				list_dbs.add(db);
			}
		}
		//再根据type获取分组

		String sql_grp = "";
		if(type.equals("storage")){
			sql_grp = "select id,concat(server_name,'(',server_remark,')') name,server_ip,server_name,server_remark from dbserver";
			list_grp = DataSource.sysdao().executeQuery(sql_grp);
			for(Map<String,Object> grp : list_grp){
				String server_ip = grp.get("server_ip").toString();
				List<Map<String,Object>> children = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> db : list_dbs){
					String dbip = db.get("server_ip").toString();
					if(server_ip.equals(dbip)){
						children.add(db);
					}
				}
				grp.put("children", children);
			}
		}else if(type.equals("custom")){//自定义分组
			sql_grp = "select id,group_name name,group_name,dbsource_ids,ordno from dbgroup where uname=? order by ordno";
			list_grp = DataSource.sysdao().executeQuery(sql_grp,new Object[]{uname});
			
			for(Map<String,Object> map_grp : list_grp){
				map_grp.put("isParent", true);
				String ids = ","+map_grp.get("dbsource_ids").toString()+",";
				List<Map<String,Object>> children = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> db : list_dbs){
					if(ids.indexOf(","+db.get("id").toString()+",")>-1){
						children.add(db);
						db.put("isout", true);
					}
				}
				
				map_grp.put("children", children);
			}
			//此时还有很多数据库不属于任何分组，怎么办？
			Map<String,Object> map_orther = new HashMap<String,Object>();
			map_orther.put("id", "orther");
			map_orther.put("name", "其他数据库");
			List<Map<String,Object>> map_orther_children = new ArrayList<Map<String,Object>>();
			for(Map<String,Object> db : list_dbs){
				Object isout = db.get("isout");
				if(isout == null){//如果没被标记，则...
					map_orther_children.add(db);
				}
			}
			map_orther.put("children", map_orther_children);
			list_grp.add(map_orther);
		}else{//按名称所有
			Map<String,Object> map_all = new HashMap<String,Object>();
			map_all.put("id", "root");
			map_all.put("name", "所有已注册的数据库");
			map_all.put("children", list_dbs);
			list_grp.add(map_all);
		}
		//各位父节点是否应该颜色区分一下孩子们的状态呢？
		for(Map<String,Object> map : list_grp){
			List<Map<String,Object>> children = (List<Map<String,Object>>)map.get("children");
			int cnt = children.size();
			if(cnt == 0){
				map.put("iconSkin", "green");
				continue;
			}
			int red = 0,yellow = 0,green = 0;
			for(Map<String,Object> db : children){
				String color = db.get("iconSkin").toString();
				if("red".equals(color)) red = red +1;
				if("yellow".equals(color)) yellow = yellow+1;
				if("green".equals(color)) green = green +1;
			}
			if(red == cnt){
				map.put("iconSkin", "red");
			}else if(green == cnt){
				map.put("iconSkin", "green");
			}else{
				map.put("iconSkin", "yellow");
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list_grp;
	}
	
	public List<Map<String,Object>> getMonitorError(){
		String sql = "select * from monitor_error order by tm desc limit 8;";
		//String sql = "select  a.dbsource_id id,b.name,a.status,a.connect_tm,a.tm from db_monitor_his a left join dbsource b on a.dbsource_id=b.id where status<>'正常'  ORDER BY tm DESC limit 3";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DataSource.sysdao().executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getDBSizeInfo(String dbsource_ids){
		if(dbsource_ids == null || dbsource_ids.equals("")) dbsource_ids="all";
		
		//先获取所有数据库状态信息
		List<Map<String,Object>> all = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql_all = "select id,name,dbsize from dbsource a left join db_monitor_r b on a.id=b.dbsource_id";
		try {
			all = DataSource.sysdao().executeQuery(sql_all);
		//取出自己需要的
		if(dbsource_ids.equals("all")){
			list=all;
		}else{
			dbsource_ids = "," + dbsource_ids +",";
			for(Map<String,Object> map : all){
				if(dbsource_ids.indexOf(","+map.get("id").toString()+",") > -1){
					list.add(map);
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getDBSizeLine(String dbsource_id,String tm1,String tm2){
		String sql = "select tm,dbsize from db_monitor_his where dbsource_id=? and tm>=? and tm<=?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DataSource.sysdao().executeQuery(sql,new Object[]{dbsource_id,tm1,tm2});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getRowcountInfo(String dbsource_ids){
		if(dbsource_ids == null || dbsource_ids.equals("")) dbsource_ids="all";
		
		//先获取所有数据库状态信息
		List<Map<String,Object>> all = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql_all = "select id,name,rowcount from dbsource a left join db_monitor_r b on a.id=b.dbsource_id";
		try {
			all = DataSource.sysdao().executeQuery(sql_all);
		//取出自己需要的
		if(dbsource_ids.equals("all")){
			list=all;
		}else{
			dbsource_ids = "," + dbsource_ids +",";
			for(Map<String,Object> map : all){
				if(dbsource_ids.indexOf(","+map.get("id").toString()+",") > -1){
					list.add(map);
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getRowcountLine(String dbsource_id,String tm1,String tm2){
		String sql = "select tm,rowcount from db_monitor_his where dbsource_id=? and tm>=? and tm<=?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DataSource.sysdao().executeQuery(sql,new Object[]{dbsource_id,tm1,tm2});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getConnectcountInfo(String dbsource_ids){
		if(dbsource_ids == null || dbsource_ids.equals("")) dbsource_ids="all";
		
		//先获取所有数据库状态信息
		List<Map<String,Object>> all = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql_all = "select id,name,connect_count from dbsource a left join db_monitor_r b on a.id=b.dbsource_id";
		try {
			all = DataSource.sysdao().executeQuery(sql_all);
		//取出自己需要的
		if(dbsource_ids.equals("all")){
			list=all;
		}else{
			dbsource_ids = "," + dbsource_ids +",";
			for(Map<String,Object> map : all){
				if(dbsource_ids.indexOf(","+map.get("id").toString()+",") > -1){
					list.add(map);
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Map<String,Object>> getConnectcountLine(String dbsource_id,String tm1,String tm2){
		String sql = "select tm,connect_count from db_monitor_his where dbsource_id=? and tm>=? and tm<=?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DataSource.sysdao().executeQuery(sql,new Object[]{dbsource_id,tm1,tm2});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Map<String,Object> excuteSQLByDBID(String dbid,String sql){
		Map<String,Object> result = new HashMap<String,Object>();
		//0.参数容错
		if(dbid.trim().length()<1 || dbid==null || sql.trim().length()<1 || sql==null){
			result.put("success", false);
			result.put("message", "必须选中有效的数据库，并编写有效的SQL语句。");
			return result;
		}
		try{//封装结果包含rows\data\time\success\message
			long begin = System.currentTimeMillis();
			BaseDao dao = DBUtil.getDBDao(dbid);
			List<Map<String,Object>> data = dao.executeQuery(sql);
			long end = System.currentTimeMillis();
			
			result.put("data",data);
			result.put("rows", data.size());
			result.put("success", true);
			result.put("time", end - begin);//毫秒
			
		}catch(Exception e){
			result.put("message", e.toString());
			result.put("success", false);
		}
		return result;
	}
	
	public static void main(String[] orgs){
		//Map<String,Object> data = excuteSQLByDBID("1", "select top2 * from ST_PPTN_R");
		System.out.println("ok");
	}
}
 