package dbm.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionException;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;

public class CheckAll {
	public static BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public static void  checkAllDB() {
		String data1 = "NO";
		String data2 = "NO";
		String sql = "";
		List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
		try {
			sql = "select a.id,a.name,a.dbname,a.dbtype,a.server_ip,a.dbport,a.username,a.password,b.urltpl,b.driverclass,a.orcl_sid from dbsource a left join dbtype b on a.dbtype=b.dbtype";
			params = sysdao.executeQuery(sql);
			for(Map<String,Object> map : params){
			String id = map.get("id").toString();	
			String ip = map.get("server_ip").toString();
			String port = map.get("dbport").toString();
			String dbname = map.get("dbname").toString();
			String username = map.get("username").toString();
			String password = map.get("password").toString();
			String dbclass = map.get("driverclass").toString();
			String url = "";
			String status = "";
			String sql1 = "";
			String sql2 = "";
			String sql3 = "";
			String sql4 = "";
			if(map.get("dbtype").toString().equals("ORCL")){
				url = map.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", map.get("orcl_sid").toString());
				sql1 = "select count(*) from v$process";
				sql2 = "select sum(num_rows) from user_tables";
				sql3 = "select round(sum(sum(bytes)/1024/1024)) as MB　from dba_data_files group by tablespace_name";
				sql4 = "select sum(bytes)/1024/1024 as MB from v$log";			
			}else if(map.get("dbtype").toString().equals("MSSQL")){
			    url = map.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", dbname);
			    sql1 = "SELECT COUNT(1) FROM Master.dbo.SYSPROCESSES WHERE DBID IN ( SELECT  DBID FROM  Master.dbo.SYSDATABASES WHERE NAME='"+dbname+"')";
			    sql2 = "SELECT sum(RowCnt ) FROM sysindexes i  INNER JOIN sysObjects o  ON (o.id = i.id AND o.xType = 'U') WHERE indid < 2 ";
			    sql3 = "select top 1 convert(int,size) * (8192/1024)/1024 from "+dbname+".dbo.sysfiles order by fileid";
			    sql4 = "select top 1 convert(int,size) * (8192/1024)/1024 from "+dbname+".dbo.sysfiles order by fileid desc";
			}else if(map.get("dbtype").toString().equals("MYSQL")){
				url = map.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", dbname);
				sql1 = "select COUNT(*) from information_schema.processlist where DB='"+dbname+"';";
				sql2 = "select sum(table_rows) from  information_schema.tables where TABLE_SCHEMA = '"+dbname+"';";
				sql3 = "select round(sum(data_length/1024/1024)+sum(INDEX_LENGTH/1024/1024),2) as data from information_schema.tables where table_schema='"+dbname+"';";
				sql4 = "SELECT VARIABLE_VALUE/1024/1024 FROM information_schema.global_variables where VARIABLE_NAME='innodb_log_file_size'";
			}
			BaseDao my = new BaseDao(
					url, 
					username,
					password,
					dbclass
            );
			long start = System.currentTimeMillis(); 
			Boolean test = my.checkConnection();
			long end = System.currentTimeMillis(); 
			int tm = (int)(end-start)/1000;
			int alerttm = Integer.parseInt(Monitor.alertInfo.get("con_tm").toString());
			if(tm<=alerttm&&test==true){
				status="正常";
			}else if(tm>alerttm&&tm<=60&&test==true){
				status="响应慢";
				sql = "";
			}else if(tm>60||test==false){
				status="无响应";
			}
			if((status.equals("正常"))||(status.equals("响应慢")))
			{
			String connect_cnt = getSQLResult(url,dbclass,username,password,sql1);
			String row_cnt = getSQLResult(url,dbclass,username,password,sql2);
			String dbsize = getSQLResult(url,dbclass,username,password,sql3);
			String logsize = getSQLResult(url,dbclass,username,password,sql4);
			String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String sql5 = "update db_monitor_r set tm=?,status=?,connect_tm=?,dbsize=?,logsize=?,rowcount=?,connect_count=? where dbsource_id=?";
			data1 = sysdao.executeSQL(sql5,new Object[]{time,status,tm,dbsize,logsize,row_cnt,connect_cnt,id})?"YES":"NO";
			String sql6 = "insert into db_monitor_his(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values(?,?,?,?,?,?,?,?)";
			data2 = sysdao.executeSQL(sql6, new Object[]{id,time,status,tm,dbsize,logsize,row_cnt,connect_cnt})?"YES":"NO";
			if(status.equals("响应慢")){
				sysdao.executeSQL(Monitor.insertAlert,new Object[]{time,map.get("name").toString()+"响应慢"});
			}else if(Integer.parseInt(connect_cnt)>= Integer.parseInt(Monitor.alertInfo.get("con_num").toString())){
				sysdao.executeSQL(Monitor.insertAlert,new Object[]{time,map.get("name").toString()+"连接数超出警戒"});
			}
			}
			else if(status.equals("无响应")){
				String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String sql5 = "update db_monitor_r set tm=?,status=?,connect_tm=?,dbsize=?,logsize=?,rowcount=?,connect_count=? where dbsource_id=?";
				data1 = sysdao.executeSQL(sql5,new Object[]{time,status,tm,"0","0","0","0",id})?"YES":"NO";
				String sql6 = "insert into db_monitor_his(dbsource_id,tm,status,connect_tm,dbsize,logsize,rowcount,connect_count) values(?,?,?,?,?,?,?,?)";
				data2 = sysdao.executeSQL(sql6, new Object[]{id,time,status,tm,"0","0","0","0"})?"YES":"NO";
				sysdao.executeSQL(Monitor.insertAlert,new Object[]{time,map.get("name").toString()+"无响应"});
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new CheckAll().checkAllDB();
	}

	
	//获得单个的sql查询数据 类似连接数等
	public static String getSQLResult(String url,String classname,String user,String pass,String sql){
		String result="";
		try{
		Class.forName(classname).newInstance(); 
		Connection conn= DriverManager.getConnection(url,user,pass);
        Statement  stmt =conn.createStatement();
        //执行SQL语句
        ResultSet rs=stmt.executeQuery(sql);
        while(rs.next()){
        	String a =rs.getString(1);
        	result = a ;
        }
        conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result ;
	}
	public static void  getCpuMemory(){
		String sql = "";
		List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
		try{
			sql = "select a.instance_id,a.instance_name,a.name,a.ip,a.port,a.account,a.password,a.dbtype,b.driverclass,b.urltpl from db_instance a LEFT JOIN dbtype b ON a.dbtype=b.dbtype ";
			params = sysdao.executeQuery(sql);
			for(Map<String,Object> map : params){
			String id = map.get("instance_id").toString();	
			String ip = map.get("ip").toString();
			String port = map.get("port").toString();
			String isname = map.get("instance_name").toString();
			String username = map.get("account").toString();
			String password = map.get("password").toString();
			String dbclass = map.get("driverclass").toString();
			String urltpl = map.get("urltpl").toString();
			String dbtype = map.get("dbtype").toString();
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
			String cputm = "";
			float cpusql = 0;
			int sqlmem = 0;
			if(my.checkConnection()){
				if(dbtype.equals("MSSQL")){
				sql = "DECLARE @ts_now BIGINT"+ 
                      " SELECT @ts_now = cpu_ticks / CONVERT(FLOAT, (cpu_ticks / ms_ticks))  FROM sys.dm_os_sys_info"+    
                      " SELECT top (1) DATEADD(ms, -1 * (@ts_now - [TIMESTAMP]), GETDATE()) AS MonitorTime,SQLProcessUtilization,SystemIdle,100 - SystemIdle - SQLProcessUtilization AS OtherProcessUtilization"+ 
                      " FROM (SELECT record.value('(./Record/@id)[1]', 'int') AS record_id,record.value('(./Record/SchedulerMonitorEvent/SystemHealth/SystemIdle)[1]', 'int') AS SystemIdle,"+ 
                      " record.value('(./Record/SchedulerMonitorEvent/SystemHealth/ProcessUtilization)[1]', 'int') AS SQLProcessUtilization,TIMESTAMP"+  
                      " FROM (SELECT TIMESTAMP, CONVERT(XML, record) AS record FROM sys.dm_os_ring_buffers WHERE ring_buffer_type = N'RING_BUFFER_SCHEDULER_MONITOR'"+  
                      " AND record LIKE '%<SystemHealth>%') AS x) AS y ORDER BY record_id DESC  ";
				Map<String,Object> cpuMap =  my.executeQueryObject(sql);
				cputm = cpuMap.get("MonitorTime").toString();
				cpusql = Float.parseFloat(cpuMap.get("SQLProcessUtilization").toString());
				String cpusys = cpuMap.get("SystemIdle").toString();
				String cpuotr = cpuMap.get("OtherProcessUtilization").toString();
				sql = "SELECT [total_physical_memory_kb]/1024 as [Physical Memory_MB],[available_physical_memory_kb]/1024 as [Available_Memory_MB] FROM sys.dm_os_sys_memory";
				Map<String,Object> memMap = my.executeQueryObject(sql);
				String allmem = memMap.get("Physical Memory_MB").toString();
				String availmem = memMap.get("Available_Memory_MB").toString();
				sql = "SELECT cntr_value/1024 as Mem_MB FROM sys.dm_os_performance_counters WHERE counter_name = 'Total Server Memory (KB)'";
				sqlmem = Integer.parseInt(my.executeQueryObject(sql).get("Mem_MB").toString());
				String sql2 = "update dbi_monitor_r set tm=?,sqlutz=?,otherutz=?,sysutz=?,allmemory=?,availmemory=?,sqlmemory=? where instance_id=?";
				sysdao.executeSQL(sql2,new Object[]{cputm,cpusql,cpuotr,cpusys,allmem,availmem,sqlmem,id});
				sql2 = "insert into dbi_monitor_his(instance_id,tm,sqlutz,otherutz,sysutz,allmemory,availmemory,sqlmemory) values(?,?,?,?,?,?,?,?)";
				sysdao.executeSQL(sql2,new Object[]{id,cputm,cpusql,cpuotr,cpusys,allmem,availmem,sqlmem});
				}else if(dbtype.equals("ORCL")){
					sql = "select END_TIME,VALUE from v$sysmetric where (METRIC_NAME LIKE '%Host CPU Utilization (%)%' OR METRIC_NAME LIKE 'CPU Usage Per Sec' OR METRIC_NAME LIKE 'Host CPU Usage Per Sec') AND rownum < 4";
					List<Map<String, Object>> cpulist =  my.executeQuery(sql);
					cputm = cpulist.get(0).get("END_TIME").toString();
					float hostcpu = Float.parseFloat(cpulist.get(0).get("VALUE").toString());
					float dbtm = Float.parseFloat(cpulist.get(1).get("VALUE").toString());
					float hosttm = Float.parseFloat(cpulist.get(2).get("VALUE").toString());
					cpusql =  dbtm/hosttm*hostcpu;
					float cpuotr = hostcpu-cpusql;
					float cpusys = 100-hostcpu;
					sql = "select round(VALUE/1024/1024,0) ALLMEMORY from v$osstat WHERE STAT_NAME LIKE 'PHYSICAL_MEMORY_BYTES'";
					String allmem = my.executeQueryObject(sql).get("ALLMEMORY").toString();
					sql = "select ((SELECT ROUND(sum(value)/1024/1024,0) from v$sga)+(select ROUND(value/1024/1024,0) from v$pgastat where name='total PGA allocated')) AS sqlmem FROM DUAL";
					sqlmem = Integer.parseInt(my.executeQueryObject(sql).get("SQLMEM").toString());
					String sql2 = "update dbi_monitor_r set tm=?,sqlutz=?,otherutz=?,sysutz=?,allmemory=?,sqlmemory=? where instance_id=?";
					sysdao.executeSQL(sql2,new Object[]{cputm,cpusql,cpuotr,cpusys,allmem,sqlmem,id});
					sql2 = "insert into dbi_monitor_his(instance_id,tm,sqlutz,otherutz,sysutz,allmemory,sqlmemory) values(?,?,?,?,?,?,?)";
					sysdao.executeSQL(sql2,new Object[]{id,cputm,cpusql,cpuotr,cpusys,allmem,sqlmem});
				}else if(dbtype.equals("MYSQL")){
					
				}
				if(cpusql>=Float.parseFloat(Monitor.alertInfo.get("cpu").toString())){
					sysdao.executeSQL(Monitor.insertAlert,new Object[]{cputm,map.get("name").toString()+"CPU占用率过高"});
				}else if(sqlmem>=Integer.parseInt(Monitor.alertInfo.get("memory").toString())*1024){
					sysdao.executeSQL(Monitor.insertAlert,new Object[]{cputm,map.get("name").toString()+"内存占用过高"});
				}
			}else{
				String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String sql2 = "update dbi_monitor_r set tm=?,sqlutz=?,otherutz=?,sysutz=?,allmemory=?,availmemory=?,sqlmemory=? where instance_id=?";
				sysdao.executeSQL(sql2,new Object[]{time,"0","0","0","0","0","0",id});
				sql2 = "insert into dbi_monitor_his(instance_id,tm,sqlutz,otherutz,sysutz,allmemory,availmemory,sqlmemory) values(?,?,?,?,?,?,?,?)";
				sysdao.executeSQL(sql2,new Object[]{id,time,"0","0","0","0","0","0"});
				
			}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void  getTodayLoginAndDDL(){
		String sql = "";
		List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
		try{
			sql = "select a.instance_id,a.instance_name,a.ip,a.port,a.account,a.password,a.dbtype,b.driverclass from db_instance a LEFT JOIN dbtype b ON a.dbtype=b.dbtype ";
			params = sysdao.executeQuery(sql);
			for(Map<String,Object> map : params){
			String id = map.get("instance_id").toString();	
			String ip = map.get("ip").toString();
			String port = map.get("port").toString();
			String isname = map.get("instance_name").toString();
			String username = map.get("account").toString();
			String password = map.get("password").toString();
			String dbclass = map.get("driverclass").toString();
			String dbtype = map.get("dbtype").toString();
			String url ="";
			if(dbtype.equals("MSSQL")){
			    url = "jdbc:sqlserver://"+ip+":"+port+";instanceName="+isname;
			}else if (dbtype.equals("ORCL")){
				url = "jdbc:oracle:thin:@//"+ip+":"+port+"/"+isname;
			}
			BaseDao my = new BaseDao(
				url, 
				username,
				password,
				dbclass);
			if(my.checkConnection()){
				if(dbtype.equals("MSSQL")){
				String time=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				String tm1 = time +" 00:00:00";
				String tm2 = time +" 23:59:59";
				sql = "select COUNT(*) cnt from Logininfo..login_history where LOGIN_TIME>=? and LOGIN_TIME<=?";
				Map<String,Object> mjMap =  my.executeQueryObject(sql,new Object[]{tm1,tm2});
				sql = "select count(*)cnt from Logininfo..DDL_history WHERE Post_Time >=? and Post_Time <=?";
				Map<String,Object> mjMap2 =  my.executeQueryObject(sql,new Object[]{tm1,tm2});
				String sql2 = "update dbi_monitor_r set todaylogin=?,todayddl=? where instance_id=?";
				sysdao.executeSQL(sql2,new Object[]{mjMap.get("cnt").toString(),mjMap2.get("cnt").toString(),id});
				}else if(dbtype.equals("ORCL")){
					String time=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					String tm1 = time +" 00:00:00";
					String tm2 = time +" 23:59:59";
					sql = "select COUNT(*) cnt from SYSTEM.LOGIN_HISTORY where LOGIN_ON_TIME>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and LOGIN_ON_TIME<=to_date(?, 'yyyy-mm-dd hh24:mi:ss')";
					Map<String,Object> mjMap =  my.executeQueryObject(sql,new Object[]{tm1,tm2});
					sql = "select count(*)cnt from SYSTEM.DDL_HISTORY WHERE OPERTIME >=to_date(?,'yyyy-mm-dd hh24:mi:ss') and OPERTIME <=to_date(?, 'yyyy-mm-dd hh24:mi:ss')";
					Map<String,Object> mjMap2 =  my.executeQueryObject(sql,new Object[]{tm1,tm2});
					String sql2 = "update dbi_monitor_r set todaylogin=?,todayddl=? where instance_id=?";
					sysdao.executeSQL(sql2,new Object[]{mjMap.get("CNT").toString(),mjMap2.get("CNT"),id});
				}
			}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
