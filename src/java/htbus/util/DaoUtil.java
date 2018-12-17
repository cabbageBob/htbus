package htbus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;

public class DaoUtil {
	public static BaseDao sysdao(){
		return DaoFactory.getDao("htbus");
	}
	public static BaseDao htgqdao() {
		return DaoFactory.getDao("htgq");
	}

	public static Boolean isconnected(Object instance_name,Object username,Object password,Object ip,Object port,Object dbtype) throws SQLException {
		BaseDao dao = null;
		if(dbtype.toString().equals("MYSQL")) {
		String url = "jdbc:mysql://"+ip.toString()+":"+port.toString()+"/"+instance_name.toString()
		+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
		dao = new BaseDao(url, username.toString(), password.toString(), "com.mysql.cj.jdbc.Driver");
		return dao.checkConnection();
		}else if(dbtype.toString().equals("MSSQL")) {
			String url="jdbc:sqlserver://"+ip.toString()+":"+port.toString()+";databaseName="+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return dao.checkConnection();
		}else if(dbtype.toString().equals("ORCAL")) {
			String url="jdbc:oracle:thin:@//"+ip.toString()+":"+port.toString()+"/"+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "oracle.jdbc.driver.OracleDriver");
			return dao.checkConnection();
		}
		return null;
	}
}
