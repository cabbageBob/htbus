package dbm.util;


import java.text.MessageFormat;


import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.endec.MD5;
import cn.miao.framework.factory.DaoFactory;
import dbm.impl.DataSource;
import dbm.impl.User;

public class Test {	
	public static void main(String args[]){
		String tpl =  "abc''{0}''{1}{2}";
		System.out.println(MessageFormat.format(tpl, 1,2,3));
		System.out.println(new User().getUser());
		String s="admin";
		System.out.println(MD5.getMD5ofStr(s, 3));
		String classname ="com.mysql.jdbc.Driver"; 
	    String url="jdbc:mysql://localhost:3306/dbm";
		//String classname = "oracle.jdbc.OracleDriver";
		//String url ="jdbc:oracle:thin:@//192.168.100.70:1521/ORCL";
		//String user ="htuser";
		//String password="htwater";
	   String user="root"; 
		String password="maoqiao"; 
		BaseDao dao = new BaseDao(url,user,password,classname);
		/*s = "select log_time from dbm_monitor.accesslog";
		System.out.println(dao.executeQueryObject(s).get("log_time"));*/
		String sql = "exec xp_readerrorlog 0,1,N'Login',N'for user','2016-10-29 00:00:00','2016-10-29 23:59:59',N'DESC'";//登陆失败的日志
		String sql2 = "SELECT a.[session_id],a.[login_time],a.[host_name],a.[original_login_name],b.[client_net_address]"+
                      " FROM MASTER.sys.dm_exec_sessions a INNER JOIN MASTER.sys.dm_exec_connections b ON a.session_id=b.session_id"+ 
                      " where a.[login_time]<='2016-11-02'and a.[login_time]>= '2016-11-01 00:00:00' Order by a.[login_time] desc";//当前连接（会话）的登陆情况
		//String sql3 = "select * from ::fn_trace_gettable('C:\\Program Files\\Microsoft SQL Server\\MSSQL10.MSSQLSERVER\\MSSQL\\Log\\MSSQLSERVER.trc',0) ";
		//String sql4 = "declare @rc int; declare @TraceID int; declare @maxfilesize bigint; set @maxfilesize = 500;exec @rc = sp_trace_create @TraceID output, 2, N'C:\\Program Files\\Microsoft SQL Server\\MSSQL10.MSSQLSERVER\\MSSQL\\Log\\MSSQLSERVER', @maxfilesize, NULL,@filecount = 2; declare @on bit; set @on = 1;exec sp_trace_setevent @TraceID, 14, 1, @on exec sp_trace_setevent @TraceID, 14, 9, @on ;exec sp_trace_setevent @TraceID, 14, 6, @on;exec sp_trace_setevent @TraceID, 14, 8, @on exec sp_trace_setevent @TraceID, 14, 10, @on ;exec sp_trace_setevent @TraceID, 14, 14, @on;exec sp_trace_setevent @TraceID, 14, 11, @on exec sp_trace_setevent @TraceID, 14, 12, @on ;exec sp_trace_setevent @TraceID, 14, 15, @on;exec sp_trace_setevent @TraceID, 14, 35, @on declare @intfilter int declare @bigintfilter bigint exec sp_trace_setfilter @TraceID, 6, 0, 7, N'NETWORK SERVICE';exec sp_trace_setfilter 2, 10, 0, 7, N'Microsoft SQL Server JDBC Driver' exec sp_trace_setstatus @TraceID, 1 ";
	    String sql3 = "sp_trace_setstatus  @traceid = 2  ,  @status = 2";
		/*List<Map<String, Object>> list = dao.executeQuery(sql3);
		if(list!=null){
		System.out.println(list.size());
		}else{
		int a= 0;
		     System.out.println(a);
		}*/
	   /* sql= "insert into dbserver(server_ip,server_name,server_remark) values('1','1','2');SELECT MAX(id)id from dbserver";
	    	// "//select LAST_INSERT_ID() id";
	    System.out.println(dao.executeQuery(sql).get(0).get("id"));*/
	    int i;
	    int x;
	    String ip= null;
	    String input1 = null;
	    String input2 = null;
	    ip = "126.168.1.1";
	    i = ip.indexOf('.');
	    x = ip.indexOf('.',i+1);
	    input1 = ip.substring(0,i);
	    input2 = ip.substring(i+1, x);
	    
	    System.out.println("the  input1 is "+input1);
	    System.out.println("the input2 is "+input2);
	}
}
