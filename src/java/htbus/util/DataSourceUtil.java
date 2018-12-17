package htbus.util;

import java.util.HashMap;
import java.util.Map;

import htbus.datasource.AbstractDataSourceSqlConfig;
import htbus.datasource.DbType;
import htbus.datasource.MysqlDataSource;
import htbus.datasource.OracleDataSourceSql;
import htbus.datasource.SqlServerDataSourceSql;

public class DataSourceUtil {
	/**2018年5月16日下午2:40:52
	 * 
	 * 数据资源工具类
	 * @author Jokki
	 *  
	 */
	public static Map<DbType, AbstractDataSourceSqlConfig> DataSourceInfoSqlMap = new HashMap<>();
	
	public static final String REDIS_DB_KEY_PREFIX = "hb:db";
	
	public static final String REDIS_DB_ALLTB_PATTERN = "hb:db:*";
	
	public static void init(){
		DataSourceInfoSqlMap.put(DbType.MSSQL, SqlServerDataSourceSql.INSTANCE);
		DataSourceInfoSqlMap.put(DbType.MYSQL, MysqlDataSource.INSTANCE);
		DataSourceInfoSqlMap.put(DbType.ORACLE, OracleDataSourceSql.INSTANCE);
	}
}
