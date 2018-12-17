package dbm.util;

import java.util.HashMap;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import dbm.impl.DataSource;

public class DBUtil {
	public static String getDBUrl(String dbid){
		String sql_db = "select a.dbname,a.dbtype,a.server_ip,a.dbport,a.username,a.password,b.urltpl,b.driverclass from dbsource a left join dbtype b on a.dbtype=b.dbtype";
		Map<String, Object> db = new HashMap<String, Object>();
		String tpl = "";
		try {
			db = DataSource.sysdao().executeQueryObject(sql_db);
		tpl = db.get("urltpl").toString();
		tpl=tpl.replace("ip", db.get("server_ip").toString())
		.replace("port", db.get("dbport").toString())
		.replace("dbname", db.get("dbname").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tpl;
	}
	public static BaseDao getDBDao(String dbid) throws Exception{
		String sql_db = "select a.dbname,a.dbtype,a.server_ip,a.dbport,a.username,a.password,b.urltpl,b.driverclass from dbsource a left join dbtype b on a.dbtype=b.dbtype where a.id=?";
		Map<String, Object> db = DataSource.sysdao().executeQueryObject(sql_db,new Object[]{dbid});
		
		String tpl = db.get("urltpl").toString();
		tpl = tpl.replace("ip", db.get("server_ip").toString())
		.replace("port", db.get("dbport").toString())
		.replace("dbname", db.get("dbname").toString());
		
		BaseDao dao = new BaseDao(tpl,
				db.get("username").toString(),
				db.get("password").toString(),
				db.get("driverclass").toString());
		
		return dao;
	}
	public static BaseDao getDBDao(String url,String server_ip,
			String dbport,String dbname,String username,
			String password,String driverclass){
		String tpl = url;
		tpl = tpl.replace("ip", server_ip)
		.replace("port", dbport)
		.replace("dbname", dbname);
		
		BaseDao dao = new BaseDao(tpl,
				username,
				password,
				driverclass);
		
		return dao;
	}
}
