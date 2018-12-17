package htbus.service;

import htbus.action.ApiAction;
import htbus.cache.DBStatusCache;
import htbus.datasource.AbstractDataSourceInfo;
import htbus.datasource.DataSourceInfo;
import htbus.entity.ExecuteSqlResult;
import htbus.entity.FieldEntity;
import htbus.entity.MatedataTree;
import htbus.util.ConfigUtil;
import htbus.util.DaoUtil;
import htbus.util.ElasticsearchUtil;
import htbus.util.FreeMakerUtil;
import htbus.util.RedisUtil;
import htbus.util.StringUtil;
import htbus.util.TreeBuilder;
import net.sf.ehcache.Element;
import net.sf.ehcache.Cache;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import cn.miao.framework.action.DoAction;
import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.util.DateUtil;
import cn.miao.framework.util.PinyinUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.DateTimeDateFormat;

import com.alibaba.fastjson.JSON;


public class DataService {
	static Logger logger = Logger.getLogger(UserService.class);
	
	/**
	 * elasticsearch 索引
	 */
	private final static String INDEX = "htbus";
	
	/**
	 * elasticsearch 类型
	 */
	private final static String TYPE = "fields";
	
	public final static String DB_STA_KEY = "htbus:DbStaResInfo";
	public final static String DB_MONITOR_KEY = "htbus:DbMonitorResInfo";
	/**
	 * 获取数据库连接状态
	 * @return
	 */
	public static List<Map<String,Object>> getInstanceStatus(){
		List<Map<String,Object>> result = null;
		String sql  = "select instance_id,instance_name,name,ip,port,account,password,dbtype,'instance' nodetype, b.appids manager from db_instance a left join db_app b on a.instance_id=b.dbcode group by instance_id";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Boolean status = false;
		for(Map<String, Object> instance : result){
			try {
				status = DaoUtil.isconnected(instance.get("instance_name"), instance.get("account"), instance.get("password"), 
						instance.get("ip"), instance.get("port"),instance.get("dbtype"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(status) {
				instance.put("status", 1);
			}else {
				instance.put("status", 0);
			}
		}
//		String list = JSON.toJSONString(result);
//		RedisUtil.set("instanceStatus", list);
		return result;
	}
	
	public List<Map<String,Object>> getInstanceList(){
//		Cache cache = DBStatusCache.getCache();
//		Element values = cache.get("dbstatus");
//		@SuppressWarnings("unchecked")
//		List<Map<String, Object>> value = (List<Map<String, Object>>)values.getObjectValue();
		
		List<Map> value = JSON.parseArray(RedisUtil.get("instanceStatus"),Map.class);
		List<Map<String,Object>> result = null;
		String sql  = "select instance_id,instance_name,name,ip,port,account,password,"
				+ " dbtype,'instance' nodetype, b.appids manager, c.remark from db_instance a "
				+ " left join db_app b on a.instance_id=b.dbcode LEFT JOIN db_remark c "
				+ " on a.instance_id like c.id group by instance_id";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
			List<Map<String, Object>> managers = DaoUtil.sysdao().executeQuery("select id, appname from r_app");
			for(Map<String, Object> instance : result){
				if(instance.get("manager") != null){
					StringBuilder builder = new StringBuilder();
					for(String appId : instance.get("manager").toString().split(",")){
						for(Map<String, Object> manager : managers){
							if(appId.equals(manager.get("id").toString())){
								builder.append(",").append(manager.get("appname"));
								break;
							}
						}
					}
					String manager = builder.length() > 0 ? builder.substring(1): null;
					instance.put("manager", manager);
				}

				for(Map instance2 :value ) {
					if(instance2.get("instance_name").equals(instance.get("instance_name"))) {
						
						instance.put("status", instance2.get("status"));
					}
				}
				instance.remove("account");
				instance.remove("password");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}
	
	public Map<String,Object> getInstanceList(String instance_id){
		Map<String,Object> result = null;
		String sql  = "select instance_id,instance_name,name,ip,port,dbtype,'instance' nodetype from db_instance where instance_id=?";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql, new Object[]{instance_id});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	private List<Map<String,Object>> getInstanceList2(){
		List<Map<String,Object>> result = null;
		String sql  = "select instance_id id,instance_name,name label,ip,port,account,password,dbtype,'instance' nodetype from db_instance";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取数据库树状列表
	 * @return
	 */
	public List<Map<String,Object>> getDBTree(){
		List<Map<String,Object>> list = getInstanceList2();
		//Map<String,Integer> dbhash = dbhash();
		for(Map<String,Object> db : list){
			String iid = db.get("id").toString();
			
			//String _id = iid+"-"+db.get("dbname").toString();
			//db.put("id", _id);
			db.put("instance_id", iid);
			db.put("nodetype", "database");
		    //db.put("sta", getStaById(iid));
			/*Integer v = dbhash.get(_id);
			if(v==null) v=1;
			db.put("visible", v);*/
		}
		return list;
	}
	
	private Object getStaById(String iid) throws Exception{
		Object result = 0;
		List<Map<String, Object>> staList = (List<Map<String, Object>>) staDataResourceInfo().get("detail");
		for(Map<String, Object> sta : staList){
			if(iid.equals(sta.get("instance_id"))){
				result = sta.get("sta");
			}
		}
		return result;
	}
	
	/**
	 * 通过SQLSERVER自带的存储过程解析每个数据库的库名和字典完整度
	 * @return
	 */
	public List<Map<String,Object>> analyzeDBTreeByProc(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "CREATE TABLE #temp (dbname VARCHAR (30), sta INT) "
				+"EXEC sp_MSforeachdb 'use ?;INSERT INTO #temp SELECT ''?'', (select COUNT(1) from sys.extended_properties where class<=1 and name=''MS_Description'')*100/((select COUNT(1) from syscolumns a inner join sysobjects d on a.id=d.id and d.xtype=''U'' and d.name<>''dtproperties'')+(select COUNT(1)+1 from sys.tables)) where ''?''   not   in(''master'',''model'',''msdb'',''tempdb'',''northwind'',''pubs'',''ReportServerTempDB'',''ReportServer'')' "
				+"SELECT * FROM #temp order by dbname "
				+"DROP TABLE #temp";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	private Map<String,Integer> dbhash(){
		String sql = "select id,visible from db_label";
		Map<String,Integer> result = new HashMap<String,Integer>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		for(Map<String,Object> map : list){
			
			result.put(map.get("id").toString(),Integer.parseInt(map.get("visible").toString()));
		}
		return result;
	}
	
	/**
	 * 设置数据字典项的可见性
	 * @param id
	 * @param visible
	 * @return
	 */
	public boolean setVisible(String id,int visible){
		boolean result = false;
		String sql = "";
		try {
			if(visible == 1){
				sql = "delete from db_label where id=?";
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{id});
			}else{
				sql = "insert into db_label(id,visible) values(?,?)";
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{id,visible});
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 设置数据库相关项的大段备注文字
	 * @param id
	 * @param remark
	 * @return
	 */
	public boolean setRemark(String id,String remark){
		boolean result = false;
		String sql = "";
		try {
			sql = "replace into db_remark(id,remark) values(?,?)";
			DaoUtil.sysdao().executeSQL(sql, new Object[]{id,remark});
			result = true;
			if(id != null){
				String[] idArray = id.split("\\^");
				if(idArray.length == 2){
					RedisUtil.hset(buildRedisKey(idArray[0], idArray[1]), "remark", remark);
				}else if(idArray.length == 3){
					RedisUtil.hset(buildRedisKey(idArray[0], idArray[1], idArray[2]), "remark", remark);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String,Object> getInstance(String iid){
		Map<String, Object> map=null;
		try {
			map = DaoUtil.sysdao().executeQueryObject("select a.*,b.remark from db_instance a left join db_remark b "
					+ " on a.instance_id like b.id where instance_id=?",new Object[]{iid});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return map;
	}
	
	/**2017年11月14日下午3:44:28
	 * @author Jokki
	 * @description 获取带有登录用户名的实例信息
	 * @param
	 * @return 
	 */
	public Map<String,Object> getInstanceWithUserinfo(String iid){
		Map<String, Object> map=null;
		try {
			map = DaoUtil.sysdao().executeQueryObject("select a.*,b.remark from db_instance a left join db_remark b"
					+ " on a.instance_id like b.id where instance_id=?",new Object[]{iid});
			List<Map<String, Object>> userList = getDaoByIid(iid).executeQuery("select name from master.sys.server_principals where type='S'");
			List<String> users = new ArrayList<>();
			for(Map<String, Object> user : userList){
				users.add(user.get("name").toString());
			}
			map.put("users", users);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return map;
	}
	
	private BaseDao getDaoByIid(String iid){
		Map<String,Object> instance = getInstance(iid);
		if(instance.get("dbtype").toString().equals("MSSQL")){
			String url = "jdbc:sqlserver://ip:port;InstanceName=instance"
					.replace("ip", instance.get("ip").toString())
					.replace("port",instance.get("port").toString())
					.replace("instance",instance.get("instance_name").toString());
			return new BaseDao(
					url, 
					instance.get("account").toString(),
					instance.get("password").toString(),
					"com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}else if(instance.get("dbtype").toString().equals("ORCL")){
			return null;
		}else if(instance.get("dbtype").toString().equals("MYSQL")){
			return null;
		}else{
			return null;	
		}
	}
	
	private BaseDao getDbDao(String iid, String dbname){
		Map<String,Object> instance = getInstance(iid);
		if(instance.get("dbtype").toString().equals("MSSQL")){
			String url = "jdbc:sqlserver://ip:port;databaseName=dbname"
					.replace("ip", instance.get("ip").toString())
					.replace("port",instance.get("port").toString())
					.replace("dbname",dbname);
			return new BaseDao(
					url, 
					instance.get("account").toString(),
					instance.get("password").toString(),
					"com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}else if(instance.get("dbtype").toString().equals("ORCL")){
			return null;
		}else if(instance.get("dbtype").toString().equals("MYSQL")){
			return null;
		}else{
			return null;	
		}
	}
	
	/*public List<Map<String,Object>> getDBlistByIID2(String iid){
		Map<String,Object> instance = getInstance(iid);
		List<Map<String,Object>> dbs = new ArrayList<Map<String,Object>>();
		if(instance!=null){
			String sqlMSSQL = "select  name dbname   from   master..sysdatabases where   name   not   in('master','model','msdb','tempdb','northwind','pubs','ReportServerTempDB','ReportServer')";
			if(instance.get("dbtype").toString().equals("MSSQL")){
				String url = "jdbc:sqlserver://ip:port;InstanceName=instance"
						.replace("ip", instance.get("ip").toString())
						.replace("port",instance.get("port").toString())
						.replace("instance",instance.get("instance_name").toString());
				BaseDao dao = new BaseDao(
						url, 
						instance.get("account").toString(),
						instance.get("password").toString(),
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				try {
					dbs = dao.executeQuery(sqlMSSQL);
					
					Map<String,String> dbremarkhash = dbremarkhash();
					for(Map<String,Object> map : dbs){
						//补充大段备注
						String _id = iid+"-"+map.get("dbname").toString();
						map.put("remark", dbremarkhash.get(_id));
						
						map.put("instance_id", iid);
						
						sqlMSSQL = "SET NOCOUNT ON;use "+map.get("dbname").toString()+";select cast((select COUNT(1)+0.0 from sys.extended_properties where class<=1 and name='MS_Description')*100 / ( (select COUNT(1) from syscolumns a inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties') + (select COUNT(1)+1 from sys.tables) ) as  numeric(5,0)) sta ";
						Map<String,Object> o = dao.executeQueryObject(sqlMSSQL);
						map.put("sta", o.get("sta"));
						
						sqlMSSQL = "SET NOCOUNT ON;use "+map.get("dbname").toString()+";SELECT cast([value] as varchar(500)) [value] from sys.fn_listextendedproperty('MS_Description', NULL, NULL, NULL, NULL, NULL, NULL)";
						o = dao.executeQueryObject(sqlMSSQL);
						if(o!=null && o.get("value")!=null){
							map.put("label", o.get("value"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}else if(instance.get("dbtype").toString().equals("ORCL")){
				
			}else if(instance.get("dbtype").toString().equals("MYSQL")){
				
			}
		}
		
		return dbs;
	}*/
	
	private Map<String,String> dbremarkhash(){
		String sql = "select id,remark from db_remark";
		Map<String,String> result = new HashMap<String,String>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		for(Map<String,Object> map : list){
			
			result.put(map.get("id").toString(),StringUtil.nvl(map.get("remark")));
		}
		return result;
	}

	public List<Map<String,Object>> getDBlistByIID(String iid){
		Map<String,Object> instance = getInstance(iid);
		List<Map<String,Object>> dbs = new ArrayList<Map<String,Object>>();
		if(instance!=null){
			String sqlMSSQL = "select   name dbname   from   master..sysdatabases where   name   not   in('master','model','msdb','tempdb','northwind','pubs','ReportServerTempDB','ReportServer')";
			if(instance.get("dbtype").toString().equals("MSSQL")){
				String url = "jdbc:sqlserver://ip:port;InstanceName=instance"
						.replace("ip", instance.get("ip").toString())
						.replace("port",instance.get("port").toString())
						.replace("instance",instance.get("instance_name").toString());
				BaseDao dao = new BaseDao(
						url, 
						instance.get("account").toString(),
						instance.get("password").toString(),
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				try {
					dbs = dao.executeQuery(sqlMSSQL);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}else if(instance.get("dbtype").toString().equals("ORCL")){
				
			}
			
			try {
				List<Map<String,Object>> list = DaoUtil.sysdao().executeQuery("select * from db_database");
				for(Map<String,Object> map : dbs){
					map.put("instance_id", iid);
					map.put("cnname", "");
					for(Map<String,Object> m : list){
						if(m.get("instance_id").equals(iid) && m.get("dbname").equals(map.get("dbname"))){
							map.put("cnname", m.get("cnname"));
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return dbs;
	}
	
	private AbstractDataSourceInfo dataSourceInfo(String iid){
		Map<String,Object> instance = getInstance(iid);
		return new DataSourceInfo()
				.setDbName(instance.get("instance_name").toString())
				.setDbType(instance.get("dbtype").toString())
				.setIp(instance.get("ip").toString())
				.setPort(instance.get("port").toString())
				.setUsername(instance.get("account").toString())
				.setPassword(instance.get("password").toString())
				.build();
	}
	
	public List<Map<String,Object>> analyzeDBTables(String iid){
		List<Map<String,Object>> tbs = new ArrayList<Map<String,Object>>();
		String setKey = "hb:db:" + iid;
		tbs = RedisUtil.hgetList(setKey, "hb:db:" + iid + ":*");
		if(tbs == null || tbs.isEmpty()){
			AbstractDataSourceInfo dataSourceInfo = dataSourceInfo(iid);
			tbs = dataSourceInfo.getAllTables(dataSourceInfo.getDbName());
			//补充大段备注
			Map<String,String> dbremarkhash = dbremarkhash();
			Map<String, Map<String, String>> tables = new HashMap<>();
			//添加绑定资源类别
			
			for(Map<String,Object> tb:tbs){
				String _id = MessageFormat.format("{0}^{1}",iid,tb.get("tbname").toString());
				tb.put("remark", dbremarkhash.get(_id));
				tables.put(buildRedisKey(iid, tb.get("tbname").toString()), mapObjectToString(tb));
			}
			RedisUtil.hmsetBatch(setKey, tables);
		}
		for(Map<String,Object> tb:tbs){
			tb.put("class", null);
			for(Map<String, Object> mateData : getMateDataByClass("0")){
				if((iid + "-" + tb.get("tbname")).equals(mateData.get("resource_id"))){
					tb.put("class", mateData.get("name"));
					break;
				}
			}
			
			
		}
		return tbs;
	}
	
	private List<Map<String, Object>> getMateDataByClass(String classid){
		List<Map<String, Object>> rList = new ArrayList<>();
		try {
			rList = DaoUtil.sysdao().executeQuery("SELECT f.name,g.resource_id FROM matedata_tree f JOIN matedata_resources g ON g.matedata_code = f.code WHERE g.resource_type = ?", new Object[]{classid});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rList;
	}
	
	private Map<String, String> mapObjectToString(Map<String, Object> map){
		Map<String, String> result = new HashMap<>();
		for(Map.Entry<String, Object> entry : map.entrySet()){
			result.put(entry.getKey(), StringUtil.nvlToBlank(entry.getValue()));
		}
		return result;
	}
	
	public List<Map<String,Object>> getDBTables(String iid,String dbname){
		String sql = "select * from db_table where instance_id=? and dbname=?";
		try {
			return DaoUtil.sysdao().executeQuery(sql, new Object[]{iid,dbname});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Map<String,Object>> analyzeTableFields(String iid, String tbname){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = RedisUtil.hgetList("hb:db:" + iid, "hb:tb:" + iid + ":" + tbname + ":*");
		if(list == null || list.isEmpty()){
			AbstractDataSourceInfo dataSourceInfo = dataSourceInfo(iid);
			list = dataSourceInfo.getTableFields(dataSourceInfo.getDbName(), tbname);
			//补充大段备注
			Map<String,String> dbremarkhash = dbremarkhash();
			Map<String, Map<String, String>> fields = new HashMap<>();
			for(Map<String,Object> field:list){
				String _id = MessageFormat.format("{0}^{1}^{2}",iid, tbname, field.get("field").toString());
				field.put("remark", dbremarkhash.get(_id));
				fields.put(buildRedisKey(iid, tbname, field.get("field").toString()), mapObjectToString(field));
			}
			RedisUtil.hmsetBatch("hb:db:" + iid, fields);
		}
		return list;
	}
	
	public void refreshTableField(){
		List<Map<String, Object>> dbs = getInstanceList();
		for(Map<String, Object> db : dbs){
			String iid = db.get("instance_id").toString();
			//先清空
			RedisUtil.deleteMulti("hb:db:" + iid);
			for(Map<String, Object> tb : analyzeDBTables(iid)){
				analyzeTableFields(iid, tb.get("tbname").toString());
			}
		}
		logger.info("###数据字典刷新成功！");
		
	}
	
	
	public List<Map<String,Object>> getTableField(String iid,String dbname,String tbname){
		String sql = "select * from db_field where instance_id=? and dbname=? and tbname=?";
		try {
			return DaoUtil.sysdao().executeQuery(sql, new Object[]{iid,dbname,tbname});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 添加数据库服务实例
	 * @param name
	 * @param ip
	 * @param port
	 * @param instance_name
	 * @param account
	 * @param password
	 * @param dbtype
	 * @return
	 */

	public boolean addInstance(String name,String ip,String port,String instance_name,String account,String password,String dbtype,String remark){
		boolean result = false;
//		String sql = "DELIMITER $"
//				+ " CREATE TRIGGER addinstance AFTER INSERT"
//				+ " on sys_user for EACH ROW "
//				+ " BEGIN "
//				+ " insert into db_remark(id,remark) select max(instance_id),? from db_instance;"
//				+ "	END $"
//				+ " DELIMITER ;"
//				+ " insert into db_instance(instance_id,name,ip,port,instance_name,account,password,dbtype)"
//				+ " select (max(instance_id)+1),?,?,?,?,?,?,? from db_instance;"
//				+ " DROP TRIGGER addinstance";
		String sqla=" insert into db_instance(instance_id,name,ip,port,instance_name,account,password,dbtype)"
				+ " select (max(instance_id)+1),?,?,?,?,?,?,? from db_instance";
		String sqlb="insert into db_remark(id,remark) select max(instance_id),? from db_instance";
		try {
			if(checkConnection(ip,port,instance_name,account,password,dbtype)){
				result = DaoUtil.sysdao().executeSQL(sqla, new Object[]{name,ip,port,instance_name,account,password,dbtype});
				if(!DaoUtil.sysdao().executeSQL(sqlb,new Object[] {remark})) {
					result=false;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 修改实例信息
	 * @param instance_id
	 * @param name
	 * @param ip
	 * @param port
	 * @param instance_name
	 * @param account
	 * @param password
	 * @param dbtype
	 * @return
	 */
	public boolean updateInstance(String instance_id,String name,String ip,String port,String instance_name,String account,String password,String dbtype,String remark){
		boolean result = false;
		boolean resultb = false;
		String sql = "update db_instance set name=?,ip=?,port=?,instance_name=?,account=?,password=?,dbtype=? where instance_id=?";
		String sqlb = "update db_remark set remark=? where id=?";
		try {
			if(checkConnection(ip,port,instance_name,account,password,dbtype)){
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{name,ip,port,instance_name,account,password,dbtype,instance_id});
				resultb = DaoUtil.sysdao().executeSQL(sqlb,new Object[] {remark,instance_id});
				if(!resultb) {
					result=resultb;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 检测实例是否可连接
	 * @param ip
	 * @param port
	 * @param instance_name
	 * @param account
	 * @param password
	 * @param dbtype
	 * @return
	 */
	public boolean checkConnection(String ip,String port,String instance_name,String account,String password,String dbtype){
		boolean result = false;
		AbstractDataSourceInfo dataSourceInfo = new DataSourceInfo()
				.setDbName(instance_name)
				.setDbType(dbtype)
				.setIp(ip)
				.setPort(port)
				.setUsername(account)
				.setPassword(password)
				.build();
		try {
			result = dataSourceInfo.getBaseDao().checkConnection();
		} catch (SQLException e) {
			result = false;
		}
		return result;
	}
	
	/**
	 * 检测实例是否重复
	 * @param ip
	 * @param port
	 * @param instance_name
	 * @return
	 */
	public boolean checkInstanceRepeate(String ip,String port,String instance_name){
		boolean result = false;
		
		String sql = "select instance_id from db_instance where ip=? and port=? and instance_name=?";
		try {
			List<Map<String,Object>> list = DaoUtil.sysdao().executeQuery(sql, new Object[]{ip,port,instance_name});
			result = list.size()>0?true:false;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 删除实例
	 * @param instance_id
	 * @return
	 */
	public boolean deleteInstance(String instance_id){
		boolean result = false;
		String sql = "delete from db_instance where instance_id = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{instance_id});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取数据库说明书文档下载地址
	 * @param instance_id
	 * @param dbname
	 * @return
	 */
	public String getDatabaseDicdoc(String instance_id,String dbname){
		String result = "";
		String filename = instance_id+"-"+dbname+"数据库说明书.doc";
		File file = new File(ConfigUtil.config("freemaker_output")+filename);
		if(file.exists()){
			result = ConfigUtil.config("freemaker_output_host")+filename;
		}
		return result;
	}
	
	/**
	 * 生成数据库说明书文档
	 * @param instance_id
	 * @param dbname
	 * @return
	 */
	public String createDatabaseDicdoc(String instance_id,String dbname){
		String result = "";
		//准备数据
		Map<String,Object> dicMap = new HashMap<String,Object>();
		dicMap.put("title", dbname);
		List<Map<String,Object>> tbs = analyzeDBTables(instance_id);
		for(Map<String,Object> tb : tbs){
			List<Map<String,Object>> fields = analyzeTableFields(instance_id, tb.get("tbname").toString());
			tb.put("fields", fields);
		}
		dicMap.put("tables", tbs);
		//生成
		try {
			String filename = instance_id+"-"+dbname+"数据库说明书.doc";
			FreeMakerUtil.createDoc("数据库说明书模板.xml", dicMap, filename);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		//获取
		result = getDatabaseDicdoc(instance_id,dbname);
		return result;
	}
	
	/**
	 * 为数据库添加备注
	 * @param instance_id
	 * @param dbname
	 * @param remark
	 * @return
	 */
	public boolean remarkDB(String instance_id,String dbname,String remark){
		boolean result = false;
		
		Map<String,Object> instance = getInstance(instance_id);
		if(instance!=null){
			if(instance.get("dbtype").toString().equals("MSSQL")){
				String sqlMSSQL = "IF ((SELECT COUNT(*) from sys.fn_listextendedproperty('MS_Description',  "
						+"NULL, NULL, NULL, NULL, NULL, NULL)) > 0)  "
						+"EXEC sys.sp_updateextendedproperty @name = N'MS_Description', @value = '"+remark+"' "
						+"ELSE "
						+"EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = '"+remark+"'";
				String url = "jdbc:sqlserver://ip:port;databaseName=dbname"
						.replace("ip", instance.get("ip").toString())
						.replace("port",instance.get("port").toString())
						.replace("dbname",dbname);
				BaseDao dao = new BaseDao(
						url, 
						instance.get("account").toString(),
						instance.get("password").toString(),
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				try {
					result = dao.executeSQL(sqlMSSQL);
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 为数据表添加备注
	 * @param instance_id
	 * @param dbname
	 * @param tablename
	 * @param remark
	 * @return
	 */
	public boolean remarkTable(String instance_id,String dbname,String tablename,String remark){
		AbstractDataSourceInfo dataSourceInfo = dataSourceInfo(instance_id);
		boolean result = dataSourceInfo.remarkTable(tablename, remark);
		RedisUtil.hset(buildRedisKey(instance_id, tablename), "label", remark);
		return result;
	}
	
	/**
	 * 为字段添加备注
	 * @param instance_id
	 * @param dbname
	 * @param tablename
	 * @param filedname
	 * @param remark
	 * @return
	 */
	public boolean remarkField(String instance_id,String dbname,String tablename,String fieldname,String remark, String type, String length){
		AbstractDataSourceInfo dataSourceInfo = dataSourceInfo(instance_id);
		boolean result = dataSourceInfo.remarkField(tablename, fieldname, remark, type, length);
		RedisUtil.hset(buildRedisKey(instance_id, tablename, fieldname), "label", remark);
		return result;
	}
	
	/**
	 * 获取带有某数据库被哪些应用维护管理的应用列表（获取一个应用列表，此列表带有一个属性，这个属性是：针对某数据库是否负责维护isbind）
	 * @param instance_id
	 * @param dbname
	 * @return
	 */
	public List<Map<String,Object>> getAppListWithDBBind(String instance_id){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String dbcode = instance_id;
		String sql = "select a.id appid,a.appname,b.companyname,0 isbind from r_app a left join r_company b on a.companyid = b.id";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
			sql = "select appids from db_app where dbcode=?";
			Map<String,Object> map  = DaoUtil.sysdao().executeQueryObject(sql,new Object[]{dbcode});
			if(map!=null && map.get("appids")!=null){
				String appids = "," + map.get("appids").toString() + ",";
				for(Map<String,Object> app : list){
					if(appids.indexOf(","+app.get("appid").toString()+",")>-1){
						app.put("isbind", 1);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 为某数据库绑定负责维护的应用
	 * @param instance_id
	 * @param dbname
	 * @param appids
	 * @return
	 */
	public boolean bindDBtoApp(String instance_id,String appids){
		boolean result = false;
		String dbcode = instance_id;
		String sql = "replace into db_app(dbcode,appids) values(?,?)";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{dbcode,appids});
			result = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取当前登陆的app所负责的数据库列表
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> getDBListByApp(String appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		String sql = "select Group_concat(dbcode) dbcodes from db_app where POSITION(? IN concat(',',appids,','))>0";
		try {
			Map<String,Object> map = DaoUtil.sysdao().executeQueryObject(sql,new Object[]{","+appid+","});
			if(map!=null && map.get("dbcodes")!=null){
				String dbcodes = ","+map.get("dbcodes").toString()+",";
				List<Map<String,Object>> dbtree = getDBTree();
				for(Map<String,Object> db : dbtree){
					String dbcode = "," + db.get("instance_id").toString() + ",";
					if(dbcodes.indexOf(dbcode) > -1){
						list.add(db);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 统计（解析）某实例下所有数据库的表、字段、完整率情况
	 * @param iid
	 * @return
	 */
	private Map<String,Object> staDBListByIID(String iid){
	    Map<String,Object> instance = getInstance(iid);
		Map<String,Object> dbs = new HashMap<>();
		dbs.put("instance_id", instance.get("instance_id"));
		dbs.put("dbname", instance.get("instance_name"));
		dbs.put("dbremark", instance.get("name"));
		List<Map<String, Object>> tables = analyzeDBTables(iid);
		int fieldCnt = 0, remarkCnt = 0, tableCnt = tables.size(); 
		for(Map<String, Object> table : tables){
			if(table.get("tbname") != null) {
				List<Map<String, Object>> fields = analyzeTableFields(iid, table.get("tbname").toString());
				fieldCnt += fields.size();
				if(!StringUtil.isBlank(table.get("label").toString())){
					remarkCnt ++;
				}
				for(Map<String, Object> field : fields){
					if(!StringUtil.isBlank(field.get("label").toString())){
						remarkCnt ++;
					}
				}
			}
			
		}
			
		int sta = Math.round(((float)remarkCnt * 100 )/ (tableCnt + fieldCnt));
		dbs.put("table_cnt", tableCnt);
		dbs.put("field_cnt", fieldCnt);
		dbs.put("remarkCnt", remarkCnt);
		dbs.put("sta", sta);
		return dbs;
	}
	
	/**
	 * 数据资源统计信息
	 * @return
	 * @throws Exception 
	 */
	public Map<String,Object> staDataResourceInfo() throws Exception{
		List<Map<String,Object>> instancelist = getInstanceList2();
		List<Map<String,Object>> datadetail = new ArrayList<Map<String,Object>>();
		Map<String,Object> result = new HashMap<String,Object>();
		
		int db_sum = instancelist.size(),table_sum = 0,field_sum = 0,sta_avg = 0;
		for(Map<String,Object> instance : instancelist){
			String iid = instance.get("id").toString();
			Map<String,Object> db = staDBListByIID(iid);
				table_sum += (int)db.get("table_cnt");
				field_sum += (int)db.get("field_cnt");
				sta_avg += (int)db.get("sta");
				//db.put("instance_id", iid);	 
				//db.put("instance_name", instance.get("label"));
				datadetail.add(db);
			//db_sum += dbs.size();
		}
		//result.put("instance_sum", instance_sum);
		result.put("db_sum", db_sum);
		result.put("table_sum", table_sum);
		result.put("field_sum", field_sum);
		result.put("sta_avg", sta_avg/db_sum);
		result.put("detail", datadetail);
		RedisUtil.set(DB_STA_KEY, JSON.toJSONString(result), 25 * 60);
		return result;
	}
	/**
	 * 大屏数据资源统计信息
	 * @return
	 * @throws Exception 
	 */
	public Map<String,Object> monitorDataResourceInfo(){
		List<Map<String,Object>> instancelist = getInstanceList2();
		List<Map<String,Object>> datadetail = new ArrayList<Map<String,Object>>();
		Map<String,Object> result = new HashMap<String,Object>();
		
		int db_sum = instancelist.size(),table_sum = 0,field_sum = 0,sta_avg = 0;
		float memory = 0 ;
		for(Map<String,Object> instance : instancelist){
			String iid = instance.get("id").toString();
			Map<String,Object> db = staDBListByIID(iid);
				table_sum += (int)db.get("table_cnt");
				field_sum += (int)db.get("field_cnt");
				sta_avg += (int)db.get("sta");
				memory +=getDbMemory(instance.get("instance_name"),instance.get("account")
							,instance.get("password"),instance.get("ip"),instance.get("port"),instance.get("dbtype"));
				//db.put("instance_id", iid);	 
				//db.put("instance_name", instance.get("label"));
				datadetail.add(db);
			//db_sum += dbs.size();
		}
		//result.put("instance_sum", instance_sum);
		result.put("db_sum", db_sum);
		result.put("table_sum", table_sum);
		result.put("field_sum", field_sum);
		result.put("sta_avg", sta_avg/db_sum);
		result.put("detail", datadetail);
		result.put("memory", memory);
		RedisUtil.set(DB_MONITOR_KEY, JSON.toJSONString(result), 25 * 60);
		return result;
	}
	public float getDbMemory(Object instance_name,Object username,Object password,Object ip,Object port,Object dbtype){
		BaseDao dao = null;
		boolean status =false;
		try {
			status =  DaoUtil.isconnected(instance_name, username, password, ip, port, dbtype);
		} catch (SQLException e2) {
			logger.error(e2);
		}
		float memory = 0.00f;
		if(dbtype.toString().equals("MYSQL")&&status) {
			String url = "jdbc:mysql://"+ip.toString()+":"+port.toString()+"/"+instance_name.toString()
			+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
			dao = new BaseDao(url, username.toString(), password.toString(), "com.mysql.cj.jdbc.Driver");
			String sql="select round(sum(data_length/1024/1024)+sum(INDEX_LENGTH/1024/1024),2) as memory from information_schema.tables where table_schema='"+instance_name+"';";
			String memoryStr = "0.00f";
			try {
				memoryStr = dao.executeQueryObject(sql).get("memory")+"";
			} catch (Exception e) {
				logger.error(e);
			}
			if(memoryStr=="null"||memoryStr.isEmpty()) {
				memory=0.00f;
			}else {
				memory=Float.valueOf(memoryStr);
			}
		}else if(dbtype.toString().equals("MSSQL")&&status) {
			String url="jdbc:sqlserver://"+ip.toString()+":"+port.toString()+";databaseName="+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String sql = "select top 1 convert(int,size) * (8192/1024)/1024 as memory from "+instance_name+".dbo.sysfiles order by fileid";
			String memoryStr = "0.00f";
			try {
				memoryStr = dao.executeQueryObject(sql).get("memory")+"";
			} catch (Exception e) {
				logger.error(e);
			}
			if(memoryStr=="null"||memoryStr.isEmpty()) {
				memory=0.00f;
			}else {
				memory=Float.valueOf(memoryStr);
			}
		}else if(dbtype.toString().equals("ORCAL")&&status) {
			String url="jdbc:oracle:thin:@//"+ip.toString()+":"+port.toString()+"/"+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "oracle.jdbc.driver.OracleDriver");
			String sql = "select round(sum(sum(bytes)/1024/1024)) as memory　from dba_data_files group by tablespace_name";
			String memoryStr = "0.00f";
			try {
				memoryStr = dao.executeQueryObject(sql).get("memory")+"";
			} catch (Exception e) {
				logger.error(e);
			}
			if(memoryStr=="null"||memoryStr.isEmpty()) {
				memory=0.00f;
			}else {
				memory=Float.valueOf(memoryStr);
			}
		}
		return memory;
		
	}
	public int getTotalRows(Object instance_name,Object username,Object password,Object ip,Object port,Object dbtype) {
		BaseDao dao = null;
		boolean status =false;
		int rows = 0;
		try {
			status =  DaoUtil.isconnected(instance_name, username, password, ip, port, dbtype);
		} catch (SQLException e2) {
			logger.error(e2);
		}
		if(dbtype.toString().equals("MYSQL")&&status) {
			String url = "jdbc:mysql://"+ip.toString()+":"+port.toString()+"/"+instance_name.toString()
			+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
			dao = new BaseDao(url, username.toString(), password.toString(), "com.mysql.cj.jdbc.Driver");
			String sql = "select sum(table_rows) table_rows from  information_schema.tables where TABLE_SCHEMA = '"+instance_name+"';";
			try {
				rows = Integer.valueOf(dao.executeQueryObject(sql).get("table_rows")+"");
			} catch (Exception e) {
				logger.error(e);
			}
		}else if(dbtype.toString().equals("MSSQL")&&status) {
			String url="jdbc:sqlserver://"+ip.toString()+":"+port.toString()+";databaseName="+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String sql = "SELECT sum(RowCnt ) table_rows FROM sysindexes i  INNER JOIN sysObjects o  ON (o.id = i.id AND o.xType = 'U') WHERE indid < 2";
			try {
				Long sum = (long) dao.executeQueryObject(sql).get("table_rows");
				rows =sum.intValue() ;
			} catch (Exception e) {
				logger.error(e);
			}
		}else if(dbtype.toString().equals("ORCAL")&&status) {
			String url="jdbc:oracle:thin:@//"+ip.toString()+":"+port.toString()+"/"+instance_name.toString();
			dao=new BaseDao(url, username.toString(), password.toString(), "oracle.jdbc.driver.OracleDriver");
			String sql = "select sum(num_rows) table_rows from user_tables";
			try {
				rows = (int) dao.executeQueryObject(sql).get("table_rows");
			} catch (Exception e) {
				logger.error(e);
				}
		}
		return rows;
	}
	/**
	 * 大屏数据总量变化
	 */
	public void insertTotalData(){
		List<Map<String,Object>> instancelist = getInstanceList2();
		Map<String,Object> result = new HashMap<String,Object>();
		float memory = 0 ;
		int rows = 0;
		for(Map<String,Object> instance : instancelist){
			memory +=getDbMemory(instance.get("instance_name"),instance.get("account")
					,instance.get("password"),instance.get("ip"),instance.get("port"),instance.get("dbtype"));
			rows += getTotalRows(instance.get("instance_name"),instance.get("account")
					,instance.get("password"),instance.get("ip"),instance.get("port"),instance.get("dbtype"));
		}
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String nowStr = formatter.format(now);
		String sql = "insert into total_data(date,row_cnt,memory) values('"+nowStr+"',"+rows+","+memory+")";
		BaseDao dao = DaoUtil.sysdao();
		try {
			dao.executeSQL(sql);
		} catch (Exception e) {
			logger.error(e);
		}
		
	}
	public List<Map<String,Object>> getTotalData(){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		String sql = "select * from total_data order by id desc limit 5";
		try {
			result = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	/**
	 * 大屏数据资源统计信息
	 * @return
	 */
	public Map<String,Object> screenDataResourceInfo(){
		List<Map<String,Object>> instancelist = getInstanceList2();
		List<Map<String,Object>> datadetail = new ArrayList<Map<String,Object>>();
		Map<String,Object> result = new HashMap<String,Object>();
		
		int db_sum = instancelist.size(),table_sum = 0,field_sum = 0,sta_avg = 0;
		for(Map<String,Object> instance : instancelist){
			String iid = instance.get("id").toString();
			Map<String,Object> db = staDBListByIID(iid);
				table_sum += (int)db.get("table_cnt");
				field_sum += (int)db.get("field_cnt");
				sta_avg += (int)db.get("sta");
				
				//db.put("instance_id", iid);	 
				//db.put("instance_name", instance.get("label"));
				datadetail.add(db);
			//db_sum += dbs.size();
		}
		//result.put("instance_sum", instance_sum);
		result.put("db_sum", db_sum);
		result.put("table_sum", table_sum);
		result.put("field_sum", field_sum);
		result.put("sta_avg", sta_avg/db_sum);
		result.put("detail", datadetail);
		RedisUtil.set(DB_STA_KEY, JSON.toJSONString(result), 25 * 60);
		return result;
	}
	public List<Map<String,Object>> analyzeAllTableFields(String iid,String dbname){
		Map<String,Object> instance = getInstance(iid);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(instance!=null){
			String sqlMSSQL = 
					"SELECT a.name field, "
					+"COLUMNPROPERTY( a.id,a.name,'IsIdentity') isidentity, "
					+"(case when (SELECT count(*) FROM sysobjects  "
					+"WHERE (name in (SELECT name FROM sysindexes  "
					+"WHERE (id = a.id) AND (indid in  "
					+"(SELECT indid FROM sysindexkeys  "
					+"WHERE (id = a.id) AND (colid in  "
					+"(SELECT colid FROM syscolumns WHERE (id = a.id) AND (name = a.name)))))))  "
					+"AND (xtype = 'PK'))>0 then 1 else 0 end) iskey, "
					+"b.name type,a.length,  "
					+"a.isnullable,  "
					+"e.text defaultvalue,"
					+"cast(g.[value] as varchar(5000)) remarkt,d.name tbname, "
					+"(select isnull(cast(y.[value] as varchar(5000)),'-') AS tbremark "
                    +"from sys.tables x left join sys.extended_properties y on (x.object_id = y.major_id AND y.minor_id = 0 and y.name='MS_Description') " 
                    +"where x.name<>'dtproperties' and x.name=d.name) tbremark "
					+"FROM  syscolumns a "
					+"left join systypes b on a.xtype=b.xusertype  "
					+"inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' "
					+"left join syscomments e on a.cdefault=e.id  "
					+"left join sys.extended_properties g on a.id=g.major_id AND a.colid=g.minor_id "
					+"left join sys.extended_properties f on d.id=f.class and f.minor_id=0 "
					+"where b.name is not null order by a.id,a.colorder";
			if(instance.get("dbtype").toString().equals("MSSQL")){
				String url = "jdbc:sqlserver://ip:port;databaseName=dbname"
						.replace("ip", instance.get("ip").toString())
						.replace("port",instance.get("port").toString())
						.replace("dbname",dbname);
				BaseDao dao = new BaseDao(
						url, 
						instance.get("account").toString(),
						instance.get("password").toString(),
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				try {
					
					list = dao.executeQuery(sqlMSSQL);
					
					Map<String,Integer> dbhash = dbhash();
					for(Map<String,Object> field:list){
						String _id = MessageFormat.format("{0}-{1}-{2}-{3}",iid,dbname,field.get("field").toString());
						Integer v = dbhash.get(_id);
						if(v==null) v=1;
						field.put("visible", v);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		return list;
	}
	
	/**2017年11月19日下午10:14:30
	 * @description  把所有服务器上的数据库字段信息导入到elasticsearch
	 * @return 
	 * @throws Exception 
	 */
	public void importFieldEntity() throws Exception{
		//需要导入的字段信息
		List<FieldEntity> fields = new ArrayList<>();
		//所有数据库服务器实例
		List<Map<String, Object>> dbList = getInstanceList();
		for(Map<String, Object> db : dbList){
			String instanceId = new String(db.get("instance_id").toString());
			String dbname = new String(db.get("instance_name").toString());
			String dbDescription = new String(db.get("name").toString());
			//获取该实例下的所有数据库
			//List<Map<String, Object>> dbList = getDBlistByIID2(instanceId);
			List<Map<String, Object>> tbList = analyzeDBTables(instanceId);
			for(Map<String, Object> tb : tbList){
				List<Map<String, Object>> fieldList = analyzeTableFields(instanceId, tb.get("tbname").toString());
				for(Map<String, Object> field : fieldList){
					fields.add(new FieldEntity(StringUtil.nvl(field.get("field")), StringUtil.nvl(field.get("label")), instanceId, 
							dbname, dbDescription, StringUtil.nvl(tb.get("tbname")), StringUtil.nvl(tb.get("label"))));	
				}
				
			}
		}
		ElasticsearchUtil.bulkImportFieldEntity(fields, INDEX, TYPE);
		
	}
	
	
	public List<FieldEntity> searchField(String keyword){
		String eql = String.format("{\"query\":{\"bool\":{\"should\":[{\"match\":{\"fieldDescription\":\"%s\"}},{\"match\":{\"field\":\"%s\"}}]}}}",
				keyword, keyword) ;
		
		return ElasticsearchUtil.combinatorialQuery(eql, FieldEntity.class, INDEX, TYPE);
	}
	
	public ExecuteSqlResult executeSQL(String instance_id, String sql, String dbname, String ip, String appid){
		if(instance_id != null && sql != null 
				&& StringUtil.replaceBlank(sql).length() > 1){
			try{
				//先记录执行的sql语句到数据库：tm, appid ,ip, instance_id, dbname, sql
				DaoUtil.sysdao().executeSQL("insert into sql_log(tm, appid, ip, instance_id, dbname, `sql`) values(?,?,?,?,?,?)", 
						new Object[]{DateUtil.getNow(), appid, ip, instance_id, dbname, sql});
				BaseDao dao = dataSourceInfo(instance_id).getBaseDao();
				long begin = 0;
				long end = 0;
				//判断sql语句的类型
				switch(judgeSqlType(sql)){
				case 0: return new ExecuteSqlResult(false, "无效sql语句");
				case 1: List<Map<String, Object>> data = new ArrayList<>();
				        begin = System.currentTimeMillis();
				        data = dao.executeQueryNvl(sql, null);
				        end = System.currentTimeMillis();
				        return new ExecuteSqlResult(true, null, (end - begin) + "ms", 0, data, data.size());
				case 2: begin = System.currentTimeMillis();
				        int cnt = dao.executeSQLReturnCnt(sql, null);
				        end = System.currentTimeMillis();
				        return new ExecuteSqlResult(true, null, (end - begin) + "ms", cnt, null, 0);
				case 3: List<Map<String, Object>> data2 = new ArrayList<>();
		                begin = System.currentTimeMillis();
		                data2 = dao.callProcedure(sql);
		                end = System.currentTimeMillis();
		                return new ExecuteSqlResult(true, null, (end - begin) + "ms", 0, data2, data2.size());
		        default: return new ExecuteSqlResult(false, "无效sql语句"); 
				}
				
			}catch(Exception e){
				return new ExecuteSqlResult(false, e.getMessage());
			}
			
			
		}else{
			return new ExecuteSqlResult(false, "未指定数据库或者空的sql语句");
		}
		
	}
	
	/**2017年12月8日下午3:23:09
	 * @author Jokki
	 * @description 简单判断
	 * @param String
	 * @return Integer
	 */
	private Integer judgeSqlType(String originSql){
		Integer result = 0;
		if(originSql != null && originSql.length() > 4){
			String sql = StringUtil.replaceBlank(originSql).substring(0, 4).toLowerCase();
			if("sele".equals(sql)){
				result = 1;
			}else if("upda".equals(sql) || "dele".equals(sql) 
					|| "inse".equals(sql) || "crea".equals(sql) 
					|| "alte".equals(sql) || "drop".equals(sql) 
					|| "gran".equals(sql) || "revo".equals(sql)
					|| "deny".equals(sql) || "save".equals(sql)
					|| "comm".equals(sql)){
				result = 2;
			}else if("call".equals(sql)){
				result = 3;
			}
		}
		return result;
	}
	
	public Map<String, Object> getMatedataTreeBind() {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String status = "";
		List<MatedataTree> rtlist = new ArrayList<MatedataTree>();
		sql = "select code,name,parent_code,type,ord from matedata_tree order by ord";
		//获取rtlist、status
		try {
			List<MatedataTree> list = DaoUtil.sysdao().executeQuery(sql, null, MatedataTree.class);
			rtlist = new TreeBuilder().buildTree(list);
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", rtlist);
		return rtMap;
	}
	
	public Map<String, Object> getMatedataTree() {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String status = "";
		List<Map<String, Object>> rtlist = new ArrayList<Map<String, Object>>();
		sql = "select code,name,parent_code,type,ord from matedata_tree  order by ord";
		//获取rtlist、status
		try {
			rtlist = DaoUtil.sysdao().executeQuery(sql);
			status = "100";
			for(Map<String,Object> m : rtlist){
				m.put("_s", m.get("name").toString()+PinyinUtil.converterToPinYin(m.get("name").toString()));
			}
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", rtlist);
		return rtMap;
	}
	
	public Map<String, Object> addMatedataClass(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try{
			String code = UUID.randomUUID().toString().replace("-", "");
			sql = "insert into matedata_tree(code,name,parent_code,type,ord) select ?,?,?,?,max(ord)+100 from matedata_tree";
			Boolean result = DaoUtil.sysdao().executeSQL(
				sql,
				new Object[]{code, params.get("name"), params.get("parent_code"), params.get("type")}
			);
			//class_code有唯一索引，返回为false说明有重复class_code
			data = result?"分类添加成功":"分类添加失败，分类编码重复，请使用其他编码";
			status = "100";
		}catch(Exception e){
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> updateMatedata(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try {
			sql = "update matedata_tree set name=? where code=?";
			DaoUtil.sysdao().executeSQL(sql,new Object[]{params.get("name"),params.get("code")});
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> deleteMatedata(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try {
			sql = "delete from matedata_tree where code=? or parent_code=?";
			Boolean result = DaoUtil.sysdao().executeSQL(sql,new Object[]{params.get("code"), params.get("code")});
			sql = "delete from matedata_detail where matedata_code=?";
			DaoUtil.sysdao().executeSQL(sql,new Object[]{params.get("code")});
			data = result ? "分类删除成功" : "分类删除失败，请先清空该分类下的所有元数据再删除分类";
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> dragMatedata(String parent_code, Double prev, Double post, String code) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try {
			if(prev == null){
				prev = 0D;
			}
			//如果间距不够了，加大间距
			if(post != null && post - prev <= 0.01){
				DaoUtil.sysdao().executeSQL("update matedata_tree set ord=ord+100 where ord >= ?", new Object[]{post});
			}
			sql = "update matedata_tree set parent_code=?, ord=? where code=?";
			DaoUtil.sysdao().executeSQL(sql,new Object[]{parent_code, prev + 0.01, code});
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> getMatedataDetail(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String sql = "";
		List<Map<String, Object>> rtlist = new ArrayList<Map<String, Object>>();
		//获取rtlist、status
		try {
			sql = "select * from matedata_detail where matedata_code=?";
			rtlist = DaoUtil.sysdao().executeQuery(sql,new Object[]{params.get("code")});
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htData", rtlist);
		rtMap.put("htStatus", status);
		return rtMap;
	}
	
	public Map<String, Object> addMatedataDetail(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try{
			sql = "insert into matedata_detail(matedata_code,schame_code,schame_name,value) values(?,?,?,?)";
			Boolean result = DaoUtil.sysdao().executeSQL(
				sql,
				new Object[]{params.get("matedata_code"),params.get("schame_code"),params.get("schame_name"),params.get("value")}
			);
			//matedata_code有唯一索引，返回为false说明有重复class_code，matedata_code
			data = result?"元数据添加成功":"元数据添加失败，编码重复，请使用其他编码";
			status = "100";
		}catch(Exception e){
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> updateMatedataDetail(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String data = "NO";
		String sql = "";
		try {			
			sql = "update matedata_detail set value=? where schame_code=? and matedata_code=?";
			DaoUtil.sysdao().executeSQL(sql,new Object[]{params.get("value"),params.get("schame_code"),params.get("matedata_code")});
			status = "100";			
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		rtMap.put("htData", data);
		return rtMap;
	}
	
	public Map<String, Object> delMatedataDetail(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String status = null;
		String sql = "";
		try {			
			sql = "delete from matedata_detail where matedata_code=? and schame_code=?";
			DaoUtil.sysdao().executeSQL(sql,new Object[]{params.get("matedata_code"),params.get("schame_code")});
			status = "100";
		} catch (Exception e) {
			logger.error(sql+":"+e.toString());
			status = "500";
		}
		rtMap.put("htStatus", status);
		return rtMap;
	}
	
	
	/**
	 * @description 添加资源目录的资源
	 * @param code 资源目录id
	 * @param resourceId 资源id 多个id用,分开, 数据库资源id(instance_id-tbname) 服务资源id(service_id)
	 * @param resourceType 资源类型(0:数据库资源 1:服务资源)
	 * @return 
	 */
	public Map<String, Object> addMateDataResources(String code, String resourceId, String resourceType){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "insert into matedata_resources (matedata_code,resource_id, resource_type) values(?,?,?)"
                     +"on duplicate key update matedata_code=values(matedata_code)";
		String[] resourceIds = resourceId.split(",");
		List<Object[]> params = new ArrayList<>();
		boolean status = false;
		for(String id : resourceIds){
			params.add(new Object[]{code, id, resourceType});
		}
		try {
			DaoUtil.sysdao().executeSQLBatch(sql, params);
			status = true;
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		rtMap.put("success", status);
		return rtMap;
	}
	
	public List<Map<String, Object>> getMateDataResources(String code, String resourceType){
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			List<Map<String, Object>> resourceIdList = DaoUtil.sysdao().executeQuery("select resource_id from matedata_resources where matedata_code=? and resource_type=?", new Object[]{code, resourceType});
			if(!resourceIdList.isEmpty()){
				if("0".equals(resourceType)){
					for(Map<String, Object> resourceIdMap : resourceIdList){
						String resourceId = resourceIdMap.get("resource_id").toString();
						int index = resourceId.indexOf("-");
						//String[] resourceId = resourceIdMap.get("resource_id").toString().split("-");
						String instanceId = resourceId.substring(0, index);
						String key = buildRedisKey(instanceId, resourceId.substring(index + 1));
						Map<String, Object> map = getInstanceList(instanceId);
						map.putAll(RedisUtil.hgetall(key));
						result.add(map);
					}
				}else{
					StringBuilder builder = new StringBuilder();
					for(Map<String, Object> resourceIdMap : resourceIdList){
						builder.append(",").append(resourceIdMap.get("resource_id").toString());
					}
					result = new Service().getServiceList(builder.substring(1).toString());
				}
			}
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return result;
	}
	
	public Map<String, Object> getMateDataCode(String resourceId, String resourceType){
		Map<String, Object> rMap = new HashMap<>();
		try{
			rMap = DaoUtil.sysdao().executeQueryObject("select matedata_code from matedata_resources where resource_id=? and resource_type=?", new Object[]{resourceId, resourceType});
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return rMap;
	}
	
	private String buildRedisKey(String instanceId, String tbname){
		return "hb:db:" + instanceId + ":" + tbname;
	}
	
	private String buildRedisKey(String instanceId, String tbname, String field){
		return "hb:tb:" + instanceId + ":" + tbname + ":" + field;
	}
	
	public static void main(String[] args) throws Exception{
//		ApiAction a = 	new ApiAction();
//		a.MonitorsScreen();
		new DataService().insertTotalData();
//		new DataService().addInstance("test", "172.16.35.52", "3306", "htbus", "htbus", "bus@htwater", "MYSQL", "test");
//		new DataService().setRemark("1^2", "remark");;
//		System.out.println(MessageFormat.format("{0}^{1}", "1", "2"));
		//System.out.print(1263/100);
		//new DataService().createDatabaseDicdoc("8", "HTGQ");
		//System.out.println(new DoAction().parseJSON(new DataService().getDBListByApp("4")));
	    //List<Map<String,Object>> list = new DataService().analyzeTableFields("10", "camera_info");
		//new DataService().getMateDataResources("lake", "1");
		//ExecuteSqlResult sql = new DataService().executeSQL("8", "select * from sys_User", "htbus", "localhost", "4");
		//System.out.println(new DataService().checkConnection("172.16.35.8", "3306", "htbus_test", "ht", "ysUU!j3H", "MYSQL"));
		//System.out.println(new DataService().checkConnection("172.16.35.52", "3306", "htbus", "htbus", "bus@htwater", "MYSQL"));
		long t1 = Instant.now().toEpochMilli();
		//new DataService().getMatedataTreeBind();
		System.out.println(new DoAction().parseJSON(new DataService().getInstanceList()));
		System.out.println(Instant.now().toEpochMilli() - t1);
	}


}
