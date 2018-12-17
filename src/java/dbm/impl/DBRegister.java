package dbm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;
import dbm.helper.DBReorgHelper;

public class DBRegister {
	public BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public List<Map<String, Object>> getDBSourceList() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select a.id,a.`name`,a.dbremark,b.dbtype_name,a.dbport,c.server_name,c.server_remark,d.`status` from dbsource a left join dbtype b on a.dbtype = b.dbtype left join dbserver c on a.server_ip = c.server_ip left join db_monitor_r d on a.id = d.dbsource_id";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public Map<String, Object> checkDBConnect(String id) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "NO";
		String ceshi = "NO";
		String sql = "";
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			sql = "select a.dbname,a.dbtype,a.server_ip,a.dbport,a.username,a.password,b.urltpl,b.driverclass,a.orcl_sid from dbsource a left join dbtype b on a.dbtype=b.dbtype where a.id='"+id+"';";
			params = sysdao.executeQueryObject(sql);
			String ip = params.get("server_ip").toString();
			String port = params.get("dbport").toString();
			String dbname = params.get("dbname").toString();
			String url = "";
			if(params.get("dbtype").toString().equals("ORCL")){
				url = params.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", params.get("orcl_sid").toString());
			}else{
			    url = params.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", dbname);
			}
			BaseDao my = new BaseDao(
					url, 
					params.get("username").toString(), 
					params.get("password").toString(), 
					params.get("driverclass").toString());
			long start = System.currentTimeMillis(); 
			Boolean test = my.checkConnection();
			long end = System.currentTimeMillis();
			int tm = (int)(end-start)/1000;
			data = test?"连接成功":"连接失败";
			ceshi = data;
			if(tm<=5&&test==true){
				data="正常";
			}else if(tm>5&&tm<=60&&test==true){
				data="响应慢";
			}else if(tm>60||test==false){
				data="无响应";
			}; 
			sql = "update db_monitor_r  set status='"+data+"' where dbsource_id='"+id+"'";
			data = sysdao.executeSQL(sql)?"YES":"NO";
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", ceshi);
		return rtMap;
	}
	public List<Map<String, Object>> getDBServerList(){
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select id,server_ip,server_name,server_remark from dbserver";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
		
	}
	public List<Map<String, Object>> getDBTypeList(){
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select id,dbtype,dbtype_name,driverclass,urltpl from dbtype";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public Map<String, Object> addDBSource(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			if(params.get("dbtype").equals("ORCL")){
				sql = "insert into dbsource(name,dbname,dbremark,dbtype,server_ip,dbport,username,password,orcl_sid) values(?,?,?,?,?,?,?,?,?)";
				sysdao.executeSQL(sql,
					    new Object[]{params.get("name"),params.get("dbname"),params.get("dbremark")
						,params.get("dbtype"),params.get("server_ip"),params.get("dbport"),params.get("username"),params.get("password"),params.get("sid")});
				sql = "select max(id) id from dbsource";
				String id = sysdao.executeQueryObject(sql).get("id").toString();
				sql = "insert into db_monitor_r(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values (?,?,?,?,?,?,?,?)";
				String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				sysdao.executeSQL(sql,new Object[]{id,time,"待测试","0","0","0","0","0"});
				sql = "insert into db_monitor_his(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values (?,?,?,?,?,?,?,?)";
				data=sysdao.executeSQL(sql,new Object[]{id,time,"待测试","0","0","0","0","0"})?"Success":"Defeat";
			}else{
			    sql = "insert into dbsource(name,dbname,dbremark,dbtype,server_ip,dbport,username,password) values(?,?,?,?,?,?,?,?)";
			    sysdao.executeSQL(sql,
					    new Object[]{params.get("name"),params.get("dbname"),params.get("dbremark")
						,params.get("dbtype"),params.get("server_ip"),params.get("dbport"),params.get("username"),params.get("password")});
			    sql = "select max(id) id from dbsource";
				String id = sysdao.executeQueryObject(sql).get("id").toString();
			    sql = "insert into db_monitor_r(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values (?,?,?,?,?,?,?,?)";
				String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				sysdao.executeSQL(sql,new Object[]{id,time,"待测试","0","0","0","0","0"});
				sql = "insert into db_monitor_his(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values (?,?,?,?,?,?,?,?)";
				data = sysdao.executeSQL(sql,new Object[]{id,time,"待测试","0","0","0","0","0"})?"Success":"Defeat";
			}
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> getDBSource(String id) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		try{
			sql = "select id,name,dbname,dbremark,dbtype,server_ip,dbport,username,password,orcl_sid sid from dbsource where id='"+id+"';";
			rtMap = sysdao.executeQueryObject(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> editDBSource(Map<String, String> params){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql ="";	
		String data ="Defeat";
		try{
			if(params.get("dbtype").equals("ORCL")){
				sql = "update dbsource set name=?,dbname=?,dbremark=?,dbtype=?,server_ip=?,dbport=?,username=?,password=?,orcl_sid=? where id=?";
				data = sysdao.executeSQL(sql,new Object[]{params.get("name"),params.get("dbname"),params.get("dbremark")
						,params.get("dbtype"),params.get("server_ip"),params.get("dbport"),params.get("username"),params.get("password"),params.get("sid"),params.get("id")})?"Success":"Defeat";
			}else{
			sql = "update dbsource set name=?,dbname=?,dbremark=?,dbtype=?,server_ip=?,dbport=?,username=?,password=? where id=?";
			data = sysdao.executeSQL(sql,new Object[]{params.get("name"),params.get("dbname"),params.get("dbremark")
					,params.get("dbtype"),params.get("server_ip"),params.get("dbport"),params.get("username"),params.get("password"),params.get("id")})?"Success":"Defeat";
			}
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> delDBSource(String id) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data ="Defeat" ;
		try{
			sql = "delete from dbsource where id=?";
			sysdao.executeSQL(sql,new Object[]{id});
			sql ="delete from db_monitor_r where dbsource_id=?;";
			sysdao.executeSQL(sql,new Object[]{id});
			sql = "delete from db_monitor_his where dbsource_id=?";
			data = sysdao.executeSQL(sql,new Object[]{id})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> addDBServer(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			sql = "insert into dbserver(server_ip,server_name,server_remark) values(?,?,?)";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("server_ip"),params.get("server_name"),params.get("server_remark")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> editDBServer(Map<String, String> params){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql ="";	
		String data ="Defeat";
		try{
			sql = "update dbserver set server_ip=?,server_name=?,server_remark=? where id=?";
			data = sysdao.executeSQL(sql,new Object[]{params.get("server_ip"),params.get("server_name"),params.get("server_remark"),params.get("id")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> delDBServer(String id){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data = "Defeat";
		try{
			sql = "delete from dbserver where id=?";
			data = sysdao.executeSQL(sql, new Object[]{id})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> addDBType(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			sql = "insert into dbtype(dbtype,dbtype_name,driverclass,urltpl) values(?,?,?,?)";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("dbtype"),params.get("dbtype_name"),params.get("driverclass"),params.get("urltpl")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> editDBType(Map<String, String> params){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql ="";	
		String data ="Defeat";
		try{
			sql = "update dbtype set dbtype=?,dbtype_name=?,driverclass=?,urltpl=? where id=?";
			data = sysdao.executeSQL(sql,new Object[]{params.get("dbtype"),params.get("dbtype_name"),params.get("driverclass"),params.get("urltpl"),params.get("id")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public static void main(String[] args){
		Map<String,String>params = new HashMap<String, String>();
		params.put("instance_name","MSSQLSERVER");
		params.put("name", "ceshi");
		params.put("ip","127.0.0.1");
		params.put("port", "1433");
		params.put("account", "sa");
		params.put("password", "maoqiao");
		params.put("dbtype", "MSSQL");
		new DBRegister().addInstance(params);
		//new DBRegister().delInstance("7");
	}
	public Map<String, Object> addInstance(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			String dbtype = params.get("dbtype");
			String url = "" ;
			if(dbtype.equals("MSSQL")){
				url = "jdbc:sqlserver://ip:port;InstanceName=dbname".
						replace("ip", params.get("ip")).
						replace("port",params.get("port")).
						replace("dbname", params.get("instance_name"));
				BaseDao my = new BaseDao(
						url, 
						params.get("account"),
						params.get("password"),
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				//创建登录触发器
				sql = "IF db_id('LoginInfo') is null CREATE database LoginInfo;";
			    my.executeSQL(sql);
			    DBReorgHelper.logger.info("MSSQL数据库已创建");
				sql = " USE LoginInfo;"+
				      " IF OBJECT_ID('login_history','U') is null"+
				      " CREATE TABLE login_history(FACT_ID bigint IDENTITY(1,1) primary key,SESSION_ID int,LOGIN_NAME nvarchar(30),LOGIN_TIME datetime,[HOST_NAME] nvarchar(18),ip_address nvarchar(15));"+
				      " IF EXISTS(select 1 from sys.server_triggers where name = 'login_history_trigger')"+
				      " DROP TRIGGER login_history_trigger ON ALL SERVER;";
			    my.executeSQL(sql);
			    DBReorgHelper.logger.info("MSSQL登录表已创建");
			    sql = " CREATE TRIGGER login_history_trigger"+
				      " ON ALL SERVER with execute as 'sa'"+
				      " FOR LOGON"+
				      " AS"+
				      " BEGIN"+
				      " IF ORIGINAL_LOGIN() NOT LIKE 'NT AUTHORITY%' AND"+
				      " ORIGINAL_LOGIN() NOT LIKE 'NT SERVICE%'"+
				      " BEGIN"+
				      " INSERT INTO LoginInfo..login_history"+
				      " VALUES(EVENTDATA().value('(/EVENT_INSTANCE/SPID)[1]', 'int'),ORIGINAL_LOGIN(),GETDATE(),host_name(),EVENTDATA().value('(/EVENT_INSTANCE/ClientHost)[1]', 'nvarchar(15)'));"+
				      "END;"+
				      "END;";
			    my.executeSQL(sql);
			    DBReorgHelper.logger.info("MSSQL登录触发器已创建");
				//创建ddl触发器
				sql="USE LoginInfo;"+
				    " IF OBJECT_ID('ddl_history','U') is null"+
                    " CREATE TABLE ddl_history(FACT_ID bigint IDENTITY(1,1) primary key,Post_Time datetime,LOGIN_NAME nvarchar(30),[HOST_NAME] nvarchar(18),DabaseName nvarchar(18),[Schema] nvarchar(18),[Object] nvarchar(18),[TSQL] nvarchar(max),[Event] nvarchar(18),[XmlEvent] xml);"+
				    " IF EXISTS(select 1 from sys.server_triggers where name = 'DDL_history_trigger')"+
                    " DROP TRIGGER DDL_history_trigger ON ALL SERVER;";
				my.executeSQL(sql);
			    DBReorgHelper.logger.info("MSSQL DDL表已创建");
				sql=" CREATE TRIGGER [DDL_history_trigger]"+
                    " ON ALL SERVER  with execute as 'sa'"+
                    " FOR DDL_SERVER_LEVEL_EVENTS,DDL_DATABASE_LEVEL_EVENTS AS"+ 
                    " BEGIN"+
                    " SET NOCOUNT ON;"+
                    " DECLARE @data XML;"+
                    " DECLARE @schema sysname;"+
                    " DECLARE @object sysname;"+
                    " DECLARE @eventType sysname;"+
                    " SET @data = EVENTDATA();"+
                    " INSERT INTO[LoginInfo]..[ddl_history]([Post_Time],[LOGIN_NAME],[HOST_NAME],[DabaseName],[Schema],[Object],[TSQL],[Event],[XmlEvent])"+
                    " VALUES(GETDATE(),@data.value('(/EVENT_INSTANCE/LoginName)[1]', 'nvarchar(18)'),host_name(),@data.value('(/EVENT_INSTANCE/DatabaseName)[1]', 'nvarchar(18)'),@data.value('(/EVENT_INSTANCE/SchemaName)[1]', 'nvarchar(18)'),@data.value('(/EVENT_INSTANCE/ObjectName)[1]', 'nvarchar(18)'),@data.value('(/EVENT_INSTANCE/TSQLCommand)[1]', 'nvarchar(max)'),@data.value('(/EVENT_INSTANCE/EventType)[1]', 'nvarchar(18)'),@data);"+
                    "END;";
				my.executeSQL(sql);
			    DBReorgHelper.logger.info("MSSQL ddl触发器已创建");
			}else if(dbtype.equals("ORCL")){
				url = "jdbc:oracle:thin:@//ip:port/dbname".replace("ip", params.get("ip")).replace("port",params.get("port")).replace("dbname", params.get("instance_name"));
				BaseDao my = new BaseDao(
						url, 
						params.get("account"),
						params.get("password"),
						"oracle.jdbc.OracleDriver");
				sql = "CREATE TABLE SYSTEM.LOGIN_HISTORY(SESSION_ID  NUMBER(8,0) NOT NULL,LOGIN_ON_TIME DATE,USER_IN_DB VARCHAR2(50),MACHINE VARCHAR2(50),IP_ADDRESS VARCHAR2(20),RUN_PROGRAM VARCHAR2(50))PCTFREE 10 PCTUSED 40 MAXTRANS 255 TABLESPACE SYSTEM STORAGE(INITIAL 64K MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) NOCACHE LOGGING";
				my.executeSQL(sql);
			    DBReorgHelper.logger.info("ORACLE登录表已创建");
				sql=  "CREATE OR REPLACE TRIGGER SYS.LOGIN_HISTORY_TRIGGER AFTER LOGON ON DATABASE BEGIN"+
                      " INSERT INTO SYSTEM.LOGIN_HISTORY(session_id,login_on_time,user_in_db,machine,ip_address,run_program)"+
                      " SELECT AUDSID,sysdate,sys.login_user,machine,SYS_CONTEXT('USERENV','IP_ADDRESS'),program"+
                      " FROM v$session WHERE AUDSID=USERENV('SESSIONID') and AUDSID<>'0';"+
                      " END;";
				my.executeSQL(sql);
			    DBReorgHelper.logger.info("ORACLE登录触发器已创建");
                sql = "CREATE SEQUENCE DDL_SEQ minvalue 1 maxvalue 9999999999 start with 1 increment by 1 cache 20";
                my.executeSQL(sql);
			    DBReorgHelper.logger.info("ORACLE自增序列已创建");
                sql = "CREATE TABLE SYSTEM.DDL_HISTORY (fact_id NUMBER(10) primary key,opertime date,ip VARCHAR2 (20),hostname VARCHAR2 (30),operation VARCHAR2 (30),object_type VARCHAR2 (30),object_name VARCHAR2 (30),sql_stmt CLOB,username VARCHAR2 (30))";
                my.executeSQL(sql);
			    DBReorgHelper.logger.info("ORACLE DDL表已创建");
                sql = "CREATE OR REPLACE TRIGGER sys.ddl_history_trigger AFTER DDL ON DATABASE DECLARE PRAGMA AUTONOMOUS_TRANSACTION ; n NUMBER ; stmt CLOB := NULL ; sql_text ora_name_list_t ;"+
                      "BEGIN n := ora_sql_txt (sql_text);FOR i IN 1 ..n LOOP stmt := stmt || sql_text (i);END LOOP;"+ 
                      "INSERT INTO SYSTEM.DDL_HISTORY (fact_id,opertime,ip,hostname,operation,object_type,object_name,sql_stmt,username)"+
                      " select DDL_SEQ.NEXTVAL,SYSDATE,SYS_CONTEXT ('userenv', 'ip_address'),SYS_CONTEXT ('userenv', 'terminal'),ora_sysevent,ora_dict_obj_type,ora_dict_obj_name,stmt,USER from dual;"+ 
                      "COMMIT;"+
                      "END;";
				my.executeSQL(sql);
			    DBReorgHelper.logger.info("ORACLE DDL触发器已创建");
			}
			sql = "insert into db_instance(instance_name,name,ip,port,account,password,dbtype) values(?,?,?,?,?,?,?)";
			sysdao.executeSQL(sql,
					new Object[]{params.get("instance_name"),params.get("name"),params.get("ip"),params.get("port"),params.get("account"),params.get("password"),dbtype});
			String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			sql = "select max(instance_id) id from db_instance";
			String id = sysdao.executeQueryObject(sql).get("id").toString();
			sql = "insert into dbi_monitor_r(instance_id,tm,sqlutz,otherutz,sysutz,allmemory,availmemory,sqlmemory,todaylogin,todayddl) values(?,?,?,?,?,?,?,?,?,?)";
			data = sysdao.executeSQL(sql, 
					new Object[]{id,time,"0","0","0","0","0","0","0","0"})?"Success":"Defeat";
			sql = "insert into dbi_monitor_his(instance_id,tm,sqlutz,otherutz,sysutz,allmemory,availmemory,sqlmemory) values(?,?,?,?,?,?,?,?)";
			data = sysdao.executeSQL(sql, 
					new Object[]{id,time,"0","0","0","0","0","0"})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> updateInstance(Map<String, String> params){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data = "Defeat";
		try {
			sql = "update db_instance set instance_name=?,name=?,ip=?,port=?,account=?,password=? where instance_id=?";
			data = sysdao.executeSQL(sql, new Object[]{params.get("instance_name"),params.get("name"),
					params.get("ip"),params.get("port"),params.get("account"),params.get("password"),params.get("instance_id")})?"Success":"Defeat";
			rtMap.put("result",data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
		
	}
	public Map<String, Object> delInstance(String instance_id){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql= "";
		String data = "Defeat";
		try {
			sql = "select * from db_instance where instance_id=?";
			Map<String,Object> map = sysdao.executeQueryObject(sql,new Object[]{instance_id});
			if(map.get("dbtype").toString().equals("MSSQL")){
		    sql = "drop trigger [login_history_trigger] on all server;drop trigger [DDL_history_trigger] on all server;";
			String url = "jdbc:sqlserver://ip:port;InstanceName=dbname".replace("ip", map.get("ip").toString()).replace("port",map.get("port").toString()).replace("dbname", map.get("instance_name").toString());
			BaseDao my = new BaseDao(
					url, 
					map.get("account").toString(),
					map.get("password").toString(),
					"com.microsoft.sqlserver.jdbc.SQLServerDriver");
			my.executeSQL(sql);
			DBReorgHelper.logger.info("MSSQL 登录触发器已删除,DDL触发器已删除");
			}else if(map.get("dbtype").toString().equals("ORCL")){
					String url = "jdbc:oracle:thin:@//ip:port/dbname".replace("ip", map.get("ip").toString()).replace("port",map.get("port").toString()).replace("dbname", map.get("instance_name").toString());
					BaseDao my = new BaseDao(
							url, 
							map.get("account").toString(),
							map.get("password").toString(),
							"oracle.jdbc.OracleDriver");
					sql = "drop trigger SYS.LOGIN_HISTORY_TRIGGER";
					my.executeSQL(sql);
					DBReorgHelper.logger.info("ORACLE 登录触发器已删除");
					sql = "drop trigger SYS.DDL_HISTORY_TRIGGER";
					my.executeSQL(sql);
					DBReorgHelper.logger.info("ORACLE DDL触发器已删除");
					sql = "DROP TABLE SYSTEM.LOGIN_HISTORY";
					my.executeSQL(sql);
					DBReorgHelper.logger.info("ORACLE 登录表已删除");
					sql = "DROP TABLE SYSTEM.DDL_HISTORY";
					my.executeSQL(sql);
					DBReorgHelper.logger.info("ORACLE DDL表已删除");
					sql = "DROP SEQUENCE DDL_SEQ";
					my.executeSQL(sql);
					DBReorgHelper.logger.info("ORACLE自增序列已删除");
			}
			sql = "delete from db_instance where instance_id=?";
			sysdao.executeSQL(sql,new Object[]{instance_id});
			sql = "delete from dbi_monitor_r where instance_id=?";
			sysdao.executeSQL(sql,new Object[]{instance_id});
			sql = "delete from dbi_monitor_his where instance_id=?";
			data = sysdao.executeSQL(sql,new Object[]{instance_id})?"Success":"Defeat";
			rtMap.put("result",data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
}
