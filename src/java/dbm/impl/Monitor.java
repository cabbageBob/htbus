package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;


public class Monitor {
	public Map<String, Object> getMonitorInfo( String dbsource_ids,String uname){
		Map<String,Object> rtMap = new HashMap<String, Object>();
		BaseDao sysdao1 =  DaoFactory.getDao("dbmdb");
		try{
		String sql_all = "select * from(select a.id,a.name,b.status,b.dbsize,b.logsize,b.rowcount,b.connect_count,c.connect_count max_connect,e.min_connect from dbsource a left join db_monitor_r b on a.id=b.dbsource_id left join db_monitor_his c on a.id=c.dbsource_id LEFT JOIN (SELECT * from(select dbsource_id,connect_count min_connect from db_monitor_his ORDER BY connect_count)f  GROUP BY f.dbsource_id) e on a.id=e.dbsource_id ORDER BY max_connect desc ) d GROUP BY d.id";
		List<Map<String,Object>> all = sysdao1.executeQuery(sql_all);
		//dbsource_ids : 1,2,3   all   null
		//List<String> lst= new ArrayList<String>();
		if(dbsource_ids.equals("all")||dbsource_ids.equals(null)){
			/*for(Map<String,Object> map : all){
				lst.add(map.get("id").toString());
			}
			dbsource_ids = StringUtils.join(lst.toArray(),",");*/
			BaseDao sysdao2 =  DaoFactory.getDao("dbmdb");
			String sql_power = "select permission from sys_user where uname=?";
			Map<String,Object> power = sysdao2.executeQueryObject(sql_power, new Object[]{uname});
			dbsource_ids = power.get("permission").toString();
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String[] dbid = dbsource_ids.split(",");
		List<String> mList = new ArrayList<String>();
		for(String did : dbid){
			mList.add(did);
		}
		for(Map<String,Object> map : all){
			if(mList.contains(map.get("id").toString())){
				list.add(map);
			}
		}
		Map<String,Object> dbinfo = new HashMap<String, Object>();
		int a = 0;
		int b =0 ;
		int c =0;
		int d = 0;
		int e =0;
		int f =0;
		double g = 0;
		double h =0;
		for(Map<String,Object> map:list){
			if(map.containsValue("正常")){
				a = a+1;
			}
			else if(map.containsValue("响应慢")){
				b = b+1;
			}else {
				c = c+1;
			}
			d = d+Integer.parseInt(map.get("connect_count").toString());
			e = e+Integer.parseInt(map.get("max_connect").toString());
			f = f+Integer.parseInt(map.get("min_connect").toString());
			g = g+ Double.valueOf(map.get("dbsize").toString());
			if(Double.valueOf(map.get("dbsize").toString())>=h){
				h = Double.valueOf(map.get("dbsize").toString());
			}
		}
		dbinfo.put("all_cnt", list.size() );
		dbinfo.put("normal_cnt", a);
		dbinfo.put("slow_cnt", b);
		dbinfo.put("no_cnt", c);
		Map<String,Object> connect_info = new HashMap<String, Object>();
		connect_info.put("all_cnt", d);
		connect_info.put("max_cnt", e);
		connect_info.put("min_cnt", f);
		Map<String,Object> request_info = new HashMap<String, Object>();
		List<String> request_top = new ArrayList<String>();
		request_info.put("today", "30154");
		request_info.put("month", "1023578");
		BaseDao sysdao3 =  DaoFactory.getDao("dbmdb");
		String sql_top = "select b.name from (select dbsource_id from db_monitor_r where dbsource_id IN ("+dbsource_ids+") order by connect_count desc limit 3 ) a left join dbsource b on a.dbsource_id=b.id ";
		List<Map<String,Object>> toplist = sysdao3.executeQuery(sql_top);
		for(Map<String,Object>map:toplist){
		request_top.add(map.get("name").toString());
		}
		Map<String,Object> dbsize = new HashMap<String, Object>();
		dbsize.put("all_size", g);
		dbsize.put("max_size", h);
		List<String>maxd = new ArrayList<String>();
		for(Map<String,Object> map:list){
			if(map.get("dbsize").toString().equals(String.valueOf(h))){
				String maxdb = map.get("name").toString();
				maxd.add(maxdb);
			}
		}
		dbsize.put("max_db", maxd);
		rtMap.put("dbinfo", dbinfo);
		rtMap.put("connect_info", connect_info);
		rtMap.put("request_info", request_info);
		rtMap.put("request_top",request_top);
		rtMap.put("dbsize", dbsize);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public List<Map<String, Object>> getAllInstanceInfo(){
		List<Map<String, Object>> mjList = new ArrayList<Map<String, Object>>();
		String sql ="";
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try{
			sql = "select a.instance_id,a.instance_name,a.name,a.ip,a.port,a.account,a.password,b.sqlutz,b.sqlmemory from db_instance a left join dbi_monitor_r b on a.instance_id=b.instance_id";
			mjList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mjList;
	}
	public List<Map<String,Object>> getCpuLine(String instance_id,String tm1,String tm2){
		String sql = "select tm,sqlutz from dbi_monitor_his where instance_id=? and tm>=? and tm<=?";
		List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try {
			list = sysdao.executeQuery(sql,new Object[]{instance_id,tm1,tm2});
		}catch(NullPointerException e){
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<Map<String,Object>> getMemoryLine(String instance_id,String tm1,String tm2){
		String sql = "select tm,sqlmemory from dbi_monitor_his where instance_id=? and tm>=? and tm<=?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try {
			list = sysdao.executeQuery(sql,new Object[]{instance_id,tm1,tm2});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public Map<String,Object> getInstanceInfoById(String instance_id){
		Map<String,Object> mjMap = new HashMap<String, Object>();
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		String sql = "select sqlutz,otherutz,sysutz,allmemory,availmemory,sqlmemory,todaylogin,todayddl from dbi_monitor_r where instance_id=?";
		try {
			mjMap = sysdao.executeQueryObject(sql,new Object[]{instance_id});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mjMap;
	}
	public  List<Map<String,Object>> getLoginDetail(String instance_id,String tm1, String tm2,String ip1,String ip2){
		List<Map<String,Object>> mjList = new ArrayList<Map<String, Object>>();
		Map<String,Object> params = new HashMap<String, Object>();
		String sql = "";
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try{
		sql = "select a.instance_name,a.ip,a.port,a.account,a.password,b.dbtype,b.driverclass,b.urltpl from db_instance a LEFT JOIN dbtype b ON a.dbtype=b.dbtype where a.instance_id =?";
		params = sysdao.executeQueryObject(sql,new Object[]{instance_id});	
		String ip = params.get("ip").toString();
		String port = params.get("port").toString();
		String isname = params.get("instance_name").toString();
		String username = params.get("account").toString();
		String password = params.get("password").toString();
		String dbclass = params.get("driverclass").toString();
		String urltpl = params.get("urltpl").toString();
		String dbtype = params.get("dbtype").toString();
		String url = "";
		if(dbtype.equals("MSSQL")){
		    url = urltpl.replace("ip", ip).replace("port", port).replace("DatabaseName", "InstanceName").replace("dbname", isname);
		}else if (dbtype.equals("ORCL")){
			url = urltpl.replace("ip", ip).replace("port", port).replace("dbname", isname);
		}else if (dbtype.equals("MYSQL")){
			url = urltpl.replace("ip", ip).replace("port", port).replace("dbname", isname);
		}
		BaseDao my = new BaseDao(
			url, 
			username,
			password,
			dbclass);
		if(my.checkConnection()){
			long ipint1 = ipToLong(ip1);
			long ipint2 = ipToLong(ip2);
			if(dbtype.equals("MSSQL")){
		        sql = "select SESSION_ID,LOGIN_NAME,LOGIN_TIME,HOST_NAME,ip_address IP_ADDRESS from LoginInfo..login_history where (LOGIN_TIME BETWEEN ? and ? and (cast(parsename(IP_ADDRESS, 4) as bigint)*256*256*256+cast(parsename(IP_ADDRESS, 3) as bigint)*256*256+cast(parsename(IP_ADDRESS, 2) as bigint)*256+cast(parsename(IP_ADDRESS, 1) as bigint)) between ? and ? )";
		        mjList = my.executeQuery(sql, new Object[]{tm1,tm2,ipint1,ipint2});
			}else if(dbtype.equals("ORCL")){
				sql = "select SESSION_ID,USER_IN_DB LOGIN_NAME,LOGIN_ON_TIME LOGIN_TIME,MACHINE HOST_NAME,IP_ADDRESS FROM SYSTEM.LOGIN_HISTORY WHERE (LOGIN_ON_TIME BETWEEN to_date(?,'yyyy-mm-dd hh24:mi:ss') and to_date(?, 'yyyy-mm-dd hh24:mi:ss')and (cast(regexp_substr(IP_ADDRESS,'\\d+',1,1) as bigint)*256*256*256+cast(regexp_substr(IP_ADDRESS,'\\d+',1,2) as bigint)*256*256+cast(regexp_substr(IP_ADDRESS,'\\d+',1,3) as bigint)*256+cast(regexp_substr(IP_ADDRESS,'\\d+',1,4) as bigint)) between ? and ? )";
			    mjList = my.executeQuery(sql, new Object[]{tm1,tm2,ipint1,ipint2});
			}else if(dbtype.equals("MYSQL")){
				/*sql = "select ip from t_ip where inet_aton(ip) between inet_aton(?) and inet_aton(?)";
			    mjList = my.executeQuery(sql, new Object[]{tm1,tm2,ip1,ip2});*/
			}
		}else{
			Map<String, Object> mjMap = new HashMap<String, Object>();
			mjMap.put("result", "数据库实例暂时无法连接");
			mjList.add(mjMap);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mjList;
	}
	public static long ipToLong(String ip){
		String[] ipchar = ip.replace(".",",").split(",");
		long a1 =  Long.parseLong(ipchar[0]);
		long b1 =  Long.parseLong(ipchar[1]);
		long c1 =  Long.parseLong(ipchar[2]);
		long d1 =  Long.parseLong(ipchar[3]);
		long iplong = a1*256*256*256+b1*256*256+c1*256+d1;
		return iplong;
	}
	public  List<Map<String,Object>> getDDLDetail(String instance_id,String tm1, String tm2){
		List<Map<String,Object>> mjList = new ArrayList<Map<String, Object>>();
		Map<String,Object> params = new HashMap<String, Object>();
		String sql = "";
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try{
		sql = "select a.instance_name,a.ip,a.port,a.account,a.password,b.dbtype,b.driverclass,b.urltpl from db_instance a LEFT JOIN dbtype b ON a.dbtype=b.dbtype where a.instance_id ='"+instance_id+"'";
		params = sysdao.executeQueryObject(sql);	
		String ip = params.get("ip").toString();
		String port = params.get("port").toString();
		String isname = params.get("instance_name").toString();
		String username = params.get("account").toString();
		String password = params.get("password").toString();
		String dbclass = params.get("driverclass").toString();
		String urltpl = params.get("urltpl").toString();
		String dbtype = params.get("dbtype").toString();
		String url = "";
		if(dbtype.equals("MSSQL")){
		    url = urltpl.replace("ip", ip).replace("port", port).replace("DatabaseName", "InstanceName").replace("dbname", isname);
		}else if (dbtype.equals("ORCL")){
			url = urltpl.replace("ip", ip).replace("port", port).replace("dbname", isname);
		}else if (dbtype.equals("MYSQL")){
			url = urltpl.replace("ip", ip).replace("port", port).replace("dbname", isname);
		}
		BaseDao my = new BaseDao(
			url, 
			username,
			password,
			dbclass);
		if(my.checkConnection()){
			if(dbtype.equals("MSSQL")){
		        sql = "select Post_Time,LOGIN_NAME,HOST_NAME,DabaseName,TSQL,Event from LoginInfo..ddl_history where Post_Time>=? and Post_Time<=?";
		        mjList = my.executeQuery(sql, new Object[]{tm1,tm2});
			}else if(dbtype.equals("ORCL")){
				sql = "select OPERTIME,USERNAME,IP,HOSTNAME,OPERATION,OBJECT_TYPE,OBJECT_NAME,SQL_STMT FROM SYSTEM.DDL_HISTORY WHERE OPERTIME>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and OPERTIME<=to_date(?,'yyyy-mm-dd hh24:mi:ss')";
			    mjList = my.executeQuery(sql, new Object[]{tm1,tm2});
			}
		}else{
			Map<String, Object> mjMap = new HashMap<String, Object>();
			mjMap.put("result", "数据库实例暂时无法连接");
			mjList.add(mjMap);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mjList;
	}
	public  List<Map<String,Object>> getCMDetail(String instance_id,String tm1, String tm2){
		List<Map<String,Object>> mjList = new ArrayList<Map<String, Object>>();
		String sql = "";
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try{
		sql = "select tm,sqlutz,sysutz,availmemory,sqlmemory from dbi_monitor_his where instance_id=? and tm>=? and tm<=?";
		mjList = sysdao.executeQuery(sql, new Object[]{instance_id,tm1,tm2});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mjList;
	}
	public  List<Map<String,Object>> getConnDetail(String dbsource_id,String tm1, String tm2){
		List<Map<String,Object>> mjList = new ArrayList<Map<String, Object>>();
		String sql = "";
		BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		try{
		sql = "select * from db_monitor_his where dbsource_id=? and tm>=? and tm<=?";
		mjList = sysdao.executeQuery(sql, new Object[]{dbsource_id,tm1,tm2});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mjList;
	}
	public static void main(String[] args) {
		//refreshAlert();
		Monitor.ipToLong("255.255.255.255");
		System.exit(0);
	}
	public static Map<String, Object> alertInfo = new HashMap<String, Object>();
	public static final String insertAlert = "insert into monitor_error values(?,?)";
	public static void refreshAlert(){
		try{
			BaseDao sysdao =  DaoFactory.getDao("dbmdb");
		String sql = "select * from warn_set";
		alertInfo = sysdao.executeQueryObject(sql);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	static{
		refreshAlert();
	}
}
