package cn.miao.framework.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.miao.framework.entity.DataSourceObj;
import cn.miao.framework.factory.DaoFactory;
import cn.miao.framework.helper.DatabaseHepler;
import cn.miao.framework.util.Cache;
import cn.miao.framework.util.DateUtil;
import cn.miao.framework.util.StringUtil;
import htbus.util.BeanUtil;

import com.jolbox.bonecp.BoneCPDataSource;
import com.zaxxer.hikari.HikariDataSource;


/**
 * 可支持多数据源xml配置数据源 如果有特殊要求，如事务等，可获取getConnection() 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2011-5-20 下午03:28:23
 * 
 * @see
 */
public class BaseDao {

	Logger logger = Logger.getLogger(this.getClass());
	
	public static String DT_MSSQL="com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public BaseDao(String dsName) {
		if (null == Cache.dataSources) {
			Cache.dataSources = DatabaseHepler.initDataSource();
		}
		DataSourceObj obj = Cache.dataSources.get(dsName);
		if (null == obj) {
			logger.error("数据源不存在！"+dsName);
		}
		this.dsName = dsName;
		url = obj.getDburl();
		username = obj.getUsername();
		password = obj.getPassword();
		className = obj.getClassname();
		maxConn = obj.getMaxConn();
		minConn = obj.getMinConn();
		useCP = obj.getUseCP();
	}
	
	/**
	 * 动态创建数据库链接
	 * @param _url
	 * @param _username
	 * @param _password
	 * @param _className
	 */
	public BaseDao(String _url,String _username,String _password,String _className){
		dsName="";
		url = _url;
		username = _username;
		password = _password;
		className = _className;
		//setDbType(className);
		maxConn = 6;
		minConn = 2;
		useCP = false;
	}

	private String dsName = "";
	private String url = "";
	private String username = "";
	private String password = "";
	private String className = "";
	//private Connection conn = null;
	// private String dbtype = "";
	// 支持多个数据源
	private static Map<String, HikariDataSource> ds = new HashMap<String, HikariDataSource>();
	private int maxConn = 6;
	private int minConn = 2;
	private boolean useCP = false;

	/**
	 * 获取Connection连接
	 * 
	 * @return Connection
	 * @since v 1.0
	 */
	public Connection getConnection() {
		try {
			Connection conn = null;
			Class.forName(className); 	// load the DB driver
			if (useCP) {
			/*//if (useCP) {
				BoneCPDataSource myDS = ds.get(dsName);
				if (null == myDS) { 
					// lazy load connection pool
					ds.put(dsName, new BoneCPDataSource());  // create a new datasource object
					myDS = new BoneCPDataSource();
				}
				myDS.setDisableConnectionTracking(true);
				myDS.setJdbcUrl(url);		// set the JDBC url
				myDS.setUsername(username);				// set the username
				myDS.setPassword(password);				// set the password
				myDS.setMinConnectionsPerPartition(minConn);
				myDS.setMaxConnectionsPerPartition(maxConn);
				myDS.setPartitionCount(3);  // 线程数
				myDS.setCloseConnectionWatch(false);
				//myDS.setReleaseHelperThreads(3);
				myDS.setAcquireIncrement(2);
				//myDS.setStatementReleaseHelperThreads(3);
				myDS.setLazyInit(false);
				myDS.setQueryExecuteTimeLimit(1, TimeUnit.MINUTES);
				conn = myDS.getConnection(); 		// fetch a connection
			//} else {
				// Class.forName(className);
				//conn = DriverManager.getConnection(url, username, password);
			//}
*/				HikariDataSource myDS = ds.get(this.dsName);
                if (myDS == null) {
                    myDS = new HikariDataSource();
                    ds.put(this.dsName,myDS);
                }
                myDS.setJdbcUrl(this.url);
                myDS.setUsername(this.username);
                myDS.setPassword(this.password);
                /*myDS.setMinConnectionsPerPartition(this.minConn);
                myDS.setMaxConnectionsPerPartition(this.maxConn);
                myDS.setPartitionCount(4);
                myDS.setCloseConnectionWatch(false);
                myDS.setAcquireIncrement(3);
                myDS.setStatementsCacheSize(100);
                myDS.setLazyInit(false);
                myDS.setConnectionTimeout(20L, TimeUnit.SECONDS);
                myDS.setQueryExecuteTimeLimit(1L, TimeUnit.MINUTES);
                myDS.setDeregisterDriverOnClose(true);*/
                myDS.setMaximumPoolSize(this.maxConn);
                myDS.setConnectionTimeout(30000);
                myDS.setIdleTimeout(60000);
                myDS.setMaxLifetime(60001);
                myDS.setLoginTimeout(5);
                //myDS.setLeakDetectionThreshold(25000);
                conn = myDS.getConnection();
			}else{
				conn = DriverManager.getConnection(url, username, password);
			}
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭连接
	 * 
	 * @param rs
	 * @param sm
	 * @param conn
	 */
	public void closeDB(Connection conn, PreparedStatement sm, ResultSet rs){
        try
        {
            if(rs != null){
                rs.close();
            }
            if(sm != null){
                sm.close();
            }
            if(conn != null){
                conn.close();
            }
        }
        catch (SQLException e)
        {
            this.logger.info(e.getMessage());
        }
    }
	
	 public void closeDB(Connection conn, PreparedStatement sm){
	        closeDB(conn, sm, null);
	 }

	 public void closeDB(Connection conn){
	        closeDB(conn, null, null);
	 }
	 
	/**
	 * 注意map里面key与sql中?顺序
	 * 
	 * @param sql
	 * @param args
	 * @return void
	 * @since v 1.0
	 */
	public void executeSQLBatch(String sql, List<Object[]> lists) throws Exception {
		PreparedStatement sm = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			conn.setAutoCommit(false);
			for (Object[] objs : lists) {
				sm = conn.prepareStatement(sql);
				if (objs != null) {
					int i = 1;
					for (Object obj : objs) {
						if (obj instanceof Date) {
							sm.setTimestamp(i, new java.sql.Timestamp(
									((java.util.Date) obj).getTime()));
						} else {
							sm.setObject(i, obj);
						}
						i++;
					}
				}
				sm.executeUpdate();
			}
			conn.commit();
			conn.setAutoCommit(true); // 置回原始状态
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB(conn, sm);
		}
	}

	/**
	 * 执行带问号参数的sql update,delete,insert
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public boolean executeSQL(String sql, Object[] args) throws Exception {
		boolean flag = false;
		PreparedStatement sm = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						sm.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						sm.setObject(i + 1, args[i]);
					}
				}
			}
			if (sm.executeUpdate() > 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB(conn, sm);
		}
		return flag;
	}

	public int executeSQLReturnCnt(String sql, Object[] args) throws Exception {
		PreparedStatement sm = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						sm.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						sm.setObject(i + 1, args[i]);
					}
				}
			}
			return sm.executeUpdate();
		} finally {
			closeDB(conn, sm);
		}
	}
	
	/**
	 * 执行 sql update,delete,insert
	 * 
	 * @param sql
	 * @return
	 */
	public boolean executeSQL(String sql) throws Exception {
		return executeSQL(sql, null);
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object[] args) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = null;
		PreparedStatement sm = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						sm.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						sm.setObject(i + 1, args[i]);
					}
				}
			}
			rs = sm.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount();
			while (rs.next()) {
				result = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= colum; i++) {
					// 获取列名
					String columName = metaData.getColumnName(i);
					if (rs.getObject(i) instanceof String) {
						result.put(columName, rs.getObject(i).toString().trim());
					} else {
						result.put(columName, rs.getObject(i));
					}
				}
				// System.out.println(result);
				list.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB(conn, sm, rs);
		}
		return list;
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map<String, Object>> executeQuery(String sql) throws Exception {
		return executeQuery(sql, null);
	}
	
	public <T> List<T> executeQuery(String sql, Object[] args, Class<T> clazz) throws Exception {
		List<T> list = new ArrayList<>();
		Map<String, Object> result = null;
		PreparedStatement sm = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						sm.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						sm.setObject(i + 1, args[i]);
					}
				}
			}
			rs = sm.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount();
			while (rs.next()) {
				result = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= colum; i++) {
					// 获取列名
					String columName = metaData.getColumnName(i);
					if (rs.getObject(i) instanceof String) {
						result.put(columName, rs.getObject(i).toString().trim());
					} else {
						result.put(columName, rs.getObject(i));
					}
				}
				// System.out.println(result);
				list.add(BeanUtil.map2Object(result, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB(conn, sm, rs);
		}
		return list;
	}
	

	/**
	 * 查询一个对象
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String, Object> executeQueryObject(String sql, Object[] args) throws Exception {
		List<Map<String, Object>> lists = executeQuery(sql, args);
		if (null == lists || lists.isEmpty()) {
			return null;
		} else {
			return lists.get(0);
		}
	}

	/**
	 * 查询一个对象
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String, Object> executeQueryObject(String sql) throws Exception {
		return executeQueryObject(sql, null);
	}

	/**
	 * 调用存储过程返回的表
	 * 
	 * @param sql
	 * @param args
	 * @return List<Map<String,Object>>
	 * @since v 1.0
	 */
	public List<Map<String, Object>> callProcedure(String sql, Object[] args) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = null;
		CallableStatement cStatement = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()) {
				conn = getConnection();
			}*/
			conn = getConnection();
			cStatement = conn.prepareCall(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						cStatement.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						cStatement.setObject(i + 1, args[i]);
					}
				}
			}
			rs = cStatement.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount();
			while (rs.next()) {
				result = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= colum; i++) {
					// 获取列名
					String columName = metaData.getColumnName(i);
					result.put(columName, rs.getObject(i));
				}
				list.add(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeDB(conn, cStatement, rs);
		}
		return list;
	}

	/**
	 * 调用存储过程返回的表
	 * 
	 * @param sql
	 * @return List<Map<String,Object>>
	 * @since v 1.0
	 */
	public List<Map<String, Object>> callProcedure(String sql) throws Exception {
		return callProcedure(sql, null);
	}

	/**
	 * 根据表名、字段、条件查询
	 * 
	 * @param tbName
	 * @param fields
	 * @param contditions
	 * @return List<Map<String,Object>>
	 * @since v 1.0
	 */
	public List<Map<String, Object>> query(String tbName, List<String> fields,
			String contditions) throws Exception {
		String sql = "select * from " + tbName;
		if (null != contditions && contditions.length() > 0) {
			sql += " where " + contditions;
		}
		if (null != fields && fields.size() > 0) {
			String fieldsStr = "";
			for (String field : fields) {
				fieldsStr += "," + field;
			}
			sql = sql.replaceAll("*", fieldsStr.substring(1));
		}
		logger.info(sql);
		return executeQuery(sql);
	}

	/**
	 * 根据表名、条件查询所有字段
	 * 
	 * @param tbName
	 * @param condtions
	 * @return List<Map<String,Object>>
	 * @since v 1.0
	 */
	public List<Map<String, Object>> queryAllByCondition(String tbName,
			String condtions) throws Exception {
		return query(tbName, null, condtions);
	}

	/**
	 * 根据表名查询所有
	 * 
	 * @param tbName
	 * @return List<Map<String,Object>>
	 * @since v 1.0
	 */
	public List<Map<String, Object>> queryAll(String tbName) throws Exception {
		return query(tbName, null, null);
	}
	
	/**
	 * 根据id删除
	 * 
	 * @param tbName
	 * @param ids
	 * @return void
	 * @since v 1.0
	 */
	public boolean deleteById(String tbName, Map<String, ?> ids) throws Exception {
		String sql = "delete from " + tbName + " where 0=0 ";
		for (String key : ids.keySet()) {
			if (ids.get(key) instanceof String) {
				sql += " and " + key + "='" + ids.get(key) + "'";
			} else {
				sql += " and " + key + "=" + ids.get(key);
			}
		}
		logger.info(sql);
		try {
			executeSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 更新表
	 * 
	 * @param tbName
	 * @param ids
	 * @param map
	 * @return void
	 * @since v 1.0
	 */
	public Map<String, ?> updateMap(String tbName, Map<String, ?> ids, Map<String, ?> map) throws Exception {
		String sql = "update " + tbName + " set #values# where 0=0 #conditions#";
		String values = "";
		Object object = null;
		// set values
		for (String key : map.keySet()) {
			object = map.get(key);
			if (object instanceof Date) {
				values += "," + key +"='"
						+ DateUtil.date2Str((Date) object,
								"yyyy-MM-dd HH:mm:ss") + "'";
			} else if (object instanceof Integer) {
				values += ","+ key +"=" + object;
			} else if (object instanceof Float) {
				values += ","+ key +"=" + object;
			} else if (object instanceof Double) {
				values += ","+ key +"=" + object;
			} else {
				values += ","+ key +"='" + object + "'";
			}
		}
		// set conditions
		String conditions = "";
		object = null;
		for (String key : ids.keySet()) {
			object = ids.get(key);
			if (object instanceof Date) {
				conditions += " and "+key+"='"
						+ DateUtil.date2Str((Date) object,
								"yyyy-MM-dd HH:mm:ss") + "'";
			} else if (object instanceof Integer) {
				conditions += " and "+key+"=" + object;
			} else if (object instanceof Float) {
				conditions += " and "+key+"=" + object;
			} else if (object instanceof Double) {
				conditions += " and "+key+"=" + object;
			} else {
				conditions += " and "+key+"='" + object + "'";
			}
		}
		String newSql = sql.replaceAll("#values#", values.substring(1)).replaceAll(
				"#conditions#", conditions.substring(1));
		logger.info(newSql);
		try {
			executeSQL(newSql);
			return ids;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 只支持简单数据类型，String、Integer、Date等
	 * 
	 * @param tbName
	 * @param map
	 * @param ids 指定主键名 {"id", "name"}
	 * @return void
	 * @since v 1.0
	 */
	public Map<String, ?> saveMap(String tbName, Map<String, ?> map, String[] ids) throws Exception {
		String sql = "insert into " + tbName + " (#fields#) values(#values#)";
		String fields = "";
		String values = "";
		Object object = null;
		for (String key : map.keySet()) {
			fields += "," + key;
			object = map.get(key);
			if (object instanceof Date) {
				values += ",'"
						+ DateUtil.date2Str((Date) object,
								"yyyy-MM-dd HH:mm:ss") + "'";
			} else if (object instanceof Integer) {
				values += "," + object;
			} else if (object instanceof Float) {
				values += "," + object;
			} else if (object instanceof Double) {
				values += "," + object;
			} else {
				values += ",'" + object + "'";
			}
		}
		String newSql = sql.replaceAll("#fields#", fields.substring(1)).replaceAll(
				"#values#", values.substring(1));
		logger.info(newSql);
		try {
			executeSQL(newSql);
			if (null == ids) {
				// logger.error("没有指定返回主键名");
				return new HashMap<String, String>();
			} else {
				return executeQueryObject("select "+StringUtil.arrJoin(ids, ",")+" from "+tbName+" where id= (SELECT IDENT_CURRENT('"+tbName+"'))");
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 保存返回成功失败
	 * 
	 * @param tbName
	 * @param map
	 * @return boolean
	 * @since v 1.0
	 */
	public boolean saveMap(String tbName, Map<String, ?> map) throws Exception {
		return null == saveMap(tbName, map, null) ? false : true;
	}

	public static void main(String[] args) throws Exception {
		long st = System.currentTimeMillis();
		String ORACLE = "HTOracle";
		BaseDao dao = DaoFactory.getDao(ORACLE);
		List<Map<String, Object>> results = dao.executeQuery("select * from HT_PPTN t where t.tm  > to_date('2014-1-9 17:00:00','yyyy-mm-dd hh24:mi:ss')");
		for (Map<String, Object> map : results) {
			System.out.println(map);
		}
		System.out.println(results.size() + "--total:" + (System.currentTimeMillis() - st));
//		String SQLSERVER1 = "SqlServer1";
//		for (int i = 0; i < 1000; i++) {
//			BaseDao dao = DaoFactory.getDao(SQLSERVER1);
//			Object date = (dao.queryAll("his_service.dbo.tb_pda_core").get(0));
//			if (date instanceof Date) {
//				System.out.println("Date");
//			} else if (date instanceof java.sql.Date) {
//				System.out.println("SQL Date");
//			}
//			System.out.println(date);
//		}
//		String sql = "SELECT rtrim(RB.STCD) as STCD,SB.STNM,RB.VALLEY,RB.RSNM "
//				+ "FROM HT_RAIN_B AS RB LEFT JOIN HT_STBPRP_B AS SB ON RB.ST_ID=SB.ID "
//				+ "WHERE ISRSVR=1 AND ENABLED=1 ORDER BY RB.ORDNO desc";
//		System.out.println(dao.executeQuery(sql));
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("advice", "ds见此");
//		map.put("submit", "管理员");
//		map.put("time", new Date());
//		System.out.println(dao.saveMap("his_service.dbo.tb_advice", map));
//		System.out.println(dao.saveMap("his_service.dbo.tb_advice", map, new String[] {"id", "advice"}));
//		
//		Map<String, Object> ids = new HashMap<String, Object>();
//		ids.put("id", 1);
//		ids.put("inn", "d");
//		// Map<String, Object> map = new HashMap<String, Object>();
//		map.put("advice", "ds见此");
//		map.put("submit", "管理员");
//		map.put("time", new Date());
//		dao.updateMap("tbanem", ids, map);
	}
	
	/**
	 * 检测数据库能否正常连接
	 * @return
	 * @throws SQLException zzj
	 */
	public boolean checkConnection() throws SQLException{
		Boolean result = false;
		Connection conn = null;
		try{
			conn = getConnection();
			if(conn !=null) {
				result = true;
			}
			/*if (null == conn || conn.isClosed() || !conn.isValid(5) ) {
				conn = getConnection();*/
				
				
			/*}else{
				result = false;
			}*/
		}catch(Exception e){
			result = false;
		}finally{
			closeDB(conn);
		}
		return result;
	}
	
	public List<Map<String, Object>> executeQueryNvl(String sql, Object[] args) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = null;
		PreparedStatement sm = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			/*if (null == conn || conn.isClosed()
					|| !conn.isValid(5) ) {
				conn = getConnection();
			}*/
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Date) {
						sm.setTimestamp(i + 1, new java.sql.Timestamp(
								((java.util.Date) args[i]).getTime()));
					} else {
						sm.setObject(i + 1, args[i]);
					}
				}
			}
			rs = sm.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount();
			while (rs.next()) {
				result = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= colum; i++) {
					// 获取列名
					String columName = metaData.getColumnName(i);
					if (rs.getObject(i) instanceof String) {
						result.put(columName, rs.getObject(i).toString().trim());
					} else {
						result.put(columName, rs.getObject(i));
					}
				}
				// System.out.println(result);
				list.add(result);
			}
			
			if(list.isEmpty() || list == null){
				result = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= colum; i++) {
					// 获取列名
					String columName = metaData.getColumnName(i);
					result.put(columName, null);
				}
				// System.out.println(result);
				list.add(result);
			}
		} finally{
			closeDB(conn, sm, rs);
		}		

		return list;
	}
}
