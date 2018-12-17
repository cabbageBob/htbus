package htbus.datasource;

import java.util.List;
import java.util.Map;

public interface DataSourceDictionaryInfo {
	/**2018年5月15日下午9:12:52
	 * 数据资源信息
	 * @author Jokki
	 *  
	 */
	List<Map<String, Object>> getAllTables(String dbname);
	
	List<Map<String, Object>> getTableFields(String dbname, String tableName);
	
	Map<String, Object> getDbCount();
	
	boolean remarkTable(String tbname, String remark);
	
	boolean remarkField(String tbname, String field, String remark, String type, String length);
}
