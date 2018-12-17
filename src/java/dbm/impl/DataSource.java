package dbm.impl;


import java.util.HashMap;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;

public class DataSource {
	//public static BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public static BaseDao sysdao(){
		return DaoFactory.getDao("dbmdb");
	}
	public static Map<String,Object> getDataSource(String dsid){
		return null;
	}
	
	public static BaseDao getDao(String dsid) {
	    BaseDao dao = new BaseDao("");
		try {
			Map<String, Object> datasource= sysdao().executeQueryObject("select * from datasource where dsid = ?", new Object[]{dsid});
		 dao = new BaseDao(
				datasource.get("dslink").toString(), 
				datasource.get("username").toString(), 
				datasource.get("password").toString(), 
				datasource.get("driverclass").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dao;
	}
	/**
	 * @param params
	 * @return
	 * 数据库连接测试
	 */
	public Map<String, Object> testDataSource(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "NO";
		String sql = "";
		//List<Map<String, Object>> rtlist = new ArrayList<Map<String, Object>>();
		try {
			String dbtype = params.get("dbtype");
			String ip = params.get("server_ip");
		    String port = params.get("dbport");
		    String dbname = params.get("dbname");
		    String sid = params.get("sid");
		    String user = params.get("username");
		    String pass = params.get("password");
		    String url ="";
			sql = "select driverclass,urltpl from dbtype where dbtype='"+dbtype+"';";
			Map<String, Object>map = sysdao().executeQueryObject(sql);
			if(dbtype.equals("ORCL")){
				url = map.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", sid);
			}else{
			    url = map.get("urltpl").toString().replace("ip",ip).replace("port",port).replace("dbname", dbname);
			}
			BaseDao my = new BaseDao(
					url, 
					user, 
					pass, 
					map.get("driverclass").toString());
			Boolean test = my.checkConnection();
			data = test?"连接成功":"连接失败";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
}
