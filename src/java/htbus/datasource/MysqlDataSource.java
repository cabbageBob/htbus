package htbus.datasource;

import org.apache.commons.lang.StringUtils;

public class MysqlDataSource extends AbstractDataSourceSqlConfig{

	public static final MysqlDataSource INSTANCE = new MysqlDataSource();
	
	private MysqlDataSource() {
	}
	
	@Override
	public String allTablesSql(String dbname) {
		return "select trim(c.table_name) tbname,if(c.table_comment='', '-',c.table_comment) label,"
              +"round(if(c.table_comment='', ifnull(b.cnt,0)/(a.cnt+1), (ifnull(b.cnt,0)+1)/(a.cnt+1))*100,0) sta "
              +"from information_schema.TABLES c "
              +"left join "
              +"(select table_name, count(*)cnt from information_schema.COLUMNS where TABLE_SCHEMA='"
              + dbname
              + "' group by TABLE_NAME) a on c.table_name=a.table_name "
              +"left join "
              +"(select table_name, count(*) cnt from information_schema.COLUMNS where TABLE_SCHEMA='"
              + dbname
              + "' and COLUMN_COMMENT <>'' group by table_name)b on c.table_name=b.table_name "
              +"where c.TABLE_SCHEMA='"
              + dbname
              + "' order by tbname";
	}

	@Override
	public String tableFieldsSql(String dbname, String tbname) {
		return "select trim(COLUMN_NAME) field,0 isidentity,if(COLUMN_KEY='PRI',1,0)iskey,trim(DATA_TYPE) type,"
				+ "SUBSTR(COLUMN_TYPE,LOCATE('(',COLUMN_TYPE)+1, LOCATE(')',COLUMN_TYPE)-LOCATE('(',COLUMN_TYPE)-1) length,if(IS_NULLABLE='NO',0,1) isnullable,"
				+ "trim(COLUMN_DEFAULT) defaultvalue,if(COLUMN_COMMENT='','-',COLUMN_COMMENT) label from information_schema.COLUMNS where TABLE_SCHEMA='"
				+ dbname
				+ "' and TABLE_NAME='"
				+ tbname
				+ "'";
	}

	@Override
	public String dbCountSql() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String driverClass() {
		return "com.mysql.cj.jdbc.Driver";
	}

	@Override
	public String jdbcUrl() {
		// TODO Auto-generated method stub
		return "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&allowMultiQueries=true&useSSL=false&serverTimezone=UTC";
	}

	@Override
	public String defaultPort() {
		return "3306";
	}

	@Override
	public String alterTableCommentSql(String tbname, String remark) {
		return new StringBuilder()
				.append("alter table ")
				.append(tbname)
				.append(" comment '")
				.append(remark).append("'").toString();
	}

	@Override
	public String alterFieldCommentSql(String tbname, String field, String remark, String type, String length) {
		type = StringUtils.isBlank(length) ? type : type + "(" + length + ")";
		return new StringBuilder()
				.append("alter table ")
				.append(tbname)
				.append(" modify column ")
				.append(type)
				.append(" comment '")
				.append(remark).append("'").toString();
	}
}
