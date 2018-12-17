package dbm.util;

import dbm.impl.DataSource;

public class LogUtil {
	public static void log(String user,String logtype,String logmsg){
		String sql = "insert into sys_log(user,logtm,logtype,logmsg) values(?,now(),?,?)";
		try {
			DataSource.sysdao().executeSQL(sql, new Object[]{logtype,logmsg});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void log(String user,String logtype,String logmsg,String tag1,String tag2){
		String sql = "insert into sys_log(user,logtm,logtype,logmsg,tag1,tag2) values(?,now(),?,?,?,?)";
		try {
			DataSource.sysdao().executeSQL(sql, new Object[]{user,logtype,logmsg,tag1,tag2});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
