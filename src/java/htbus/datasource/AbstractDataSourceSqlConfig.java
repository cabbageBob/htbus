package htbus.datasource;

public abstract class AbstractDataSourceSqlConfig {
	
	/**
	 * 获取数据库所有表的SQL
	 * @return 
	 */
	public abstract String allTablesSql(String dbname);
	
	/**
	 * 获取表所有字段的SQL
	 * @return 
	 */
	public abstract String tableFieldsSql(String dbname, String tbname);
	
	/**
	 * 获取数据库所有记录数的SQL
	 * @return 
	 */
	public abstract String dbCountSql();
	
	
	/**
	 * 修改表备注
	 * @param tbname
	 * @return 
	 */
	public abstract String alterTableCommentSql(String tbname, String remark);
	
	/**
	 * 修改字段备注
	 * @param tbname
	 * @param field
	 * @return 
	 */
	public abstract String alterFieldCommentSql(String tbname, String field, String remark, String type, String length);
	
	public abstract String driverClass();
	
	public abstract String jdbcUrl();
	
	public abstract String defaultPort();
}
