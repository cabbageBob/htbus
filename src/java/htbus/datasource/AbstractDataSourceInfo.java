package htbus.datasource;

import cn.miao.framework.dao.BaseDao;
import htbus.util.DataSourceUtil;

public abstract class AbstractDataSourceInfo implements DataSourceDictionaryInfo{
	/**2018年5月15日下午8:47:47
	 * 数据库基本信息
	 *
	 * @author Jokki
	 *  
	 */
	
	static{
		DataSourceUtil.init();	
	}
	
	protected String username;
	
	protected String password;
	
	protected String dbType;
	
	protected String dbName;
	
	protected String ip;
	
	protected String port;
	
	protected BaseDao baseDao;
	
	protected AbstractDataSourceSqlConfig dataSourceSqlConfig;
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getDbType(){
		return dbType;
	}
	
	public String getDbName(){
		return dbName;
	}
	
	public String getIp(){
		return ip;
	}
	
	public String getPort(){
		return port;
	}
	
	public BaseDao getBaseDao(){
		return baseDao;
	}
	
	public AbstractDataSourceInfo setUsername(String username){
		this.username = username;
		return this;
	}
	
	public AbstractDataSourceInfo setPassword(String password){
		this.password = password;
		return this;
	}
	
	public AbstractDataSourceInfo setDbType(String dbType){
		this.dbType = dbType;
		this.dataSourceSqlConfig = DataSourceUtil.DataSourceInfoSqlMap.get(DbType.valueOf(dbType));
		return this;
	}
	
	public AbstractDataSourceInfo setIp(String ip){
		this.ip = ip;
		return this;
	}
	
	public AbstractDataSourceInfo setPort(String port){
		this.port = port;
		return this;
	}
	
	public AbstractDataSourceInfo setDbName(String dbName){
		this.dbName = dbName;
		return this;
	}
	
	public AbstractDataSourceInfo build(){
		if(dataSourceSqlConfig != null){
			if(port == null){
				port = dataSourceSqlConfig.defaultPort();
			}
			this.baseDao = new BaseDao(String.format(dataSourceSqlConfig.jdbcUrl(), ip, port, dbName),
					username, password,
					dataSourceSqlConfig.driverClass());
			return this;
		}else{
			throw new DataSourceInfoException("dataSourceSqlConfig为空");
		}
	}
	
}
