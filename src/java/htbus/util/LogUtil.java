package htbus.util;


public class LogUtil {
	public static void log(String user,String logtype,String logmsg){
		String sql = "insert into sys_log(user,logtm,logtype,logmsg) values(?,now(),?,?)";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{user,logtype,logmsg});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void log(String user,String logtype,String logmsg,String tag1,String tag2){
		String sql = "insert into sys_log(user,logtm,logtype,logmsg,tag1,tag2) values(?,now(),?,?,?,?)";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{user,logtype,logmsg,tag1,tag2});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void syserr(Object obj,String msg){
		String user = "system";
		String logtype = "error";
		String logmsg = obj.getClass().getName()+":"+Thread.currentThread().getStackTrace()[2].getMethodName()+":"+msg;
		
		String sql = "insert into sys_log(user,logtm,logtype,logmsg) values(?,now(),?,?)";
		try {
			DaoUtil.sysdao().executeSQL(sql, new Object[]{user,logtype,logmsg});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
	}
}
