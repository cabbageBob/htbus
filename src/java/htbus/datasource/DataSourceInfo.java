package htbus.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import htbus.servlet.TimerListener;

public class DataSourceInfo extends AbstractDataSourceInfo{

	private final Logger logger = Logger.getLogger(TimerListener.class);
	@Override
	public List<Map<String, Object>> getAllTables(String dbname) {
		List<Map<String, Object>> tables = new ArrayList<>();
		try {
			tables = baseDao.executeQuery(dataSourceSqlConfig.allTablesSql(dbname));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tables;
	}

	@Override
	public List<Map<String, Object>> getTableFields(String dbname, String tableName) {
		List<Map<String, Object>> fields = new ArrayList<>();
		try {
			String sql = dataSourceSqlConfig.tableFieldsSql(dbname, tableName);
			fields = baseDao.executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return fields;
	}

	@Override
	public Map<String, Object> getDbCount() {
		Map<String, Object> map = new HashMap<>();
		try {
			map = baseDao.executeQueryObject(dataSourceSqlConfig.dbCountSql());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return map;
	}
	
	@Override
	public boolean remarkTable(String tbname, String remark) {
		boolean result = false;
		try{
			result = baseDao.executeSQL(dataSourceSqlConfig.alterTableCommentSql(tbname, remark));
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public boolean remarkField(String tbname, String field, String remark, String type, String length) {
		boolean result = false;
		try{
			result = baseDao.executeSQL(dataSourceSqlConfig
					.alterFieldCommentSql(tbname, field, remark, type, length));
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		AbstractDataSourceInfo datasourceInfo = new DataSourceInfo().setDbType("MSSQL").setIp("192.168.100.4")
				.setUsername("sa").setPassword("htwater1,nbsl").setDbName("HTGQ").build();
		List list = datasourceInfo.getAllTables("HTGQ");
		System.out.println("");
	}

	

}
