package htbus.datasource;

public class OracleDataSourceSql extends AbstractDataSourceSqlConfig{

	public static final OracleDataSourceSql INSTANCE = new OracleDataSourceSql();
	
	private OracleDataSourceSql() {
	}
	
	@Override
	public String allTablesSql(String dbname) {
		return "select a.TABLE_NAME \"tbname\",nvl(a.COMMENTS,'-') \"label\",round(nvl2(a.COMMENTS, (nvl(b.cnt,0)+1)/(c.cnt+1), nvl(b.cnt,0)/(c.cnt+1))*100, 0) \"sta\""
               +"from user_tab_comments a "
               +"left join (select TABLE_NAME, count(*)cnt "
               +"from user_col_comments  where COMMENTS IS not null group by table_name)b on a.table_name=b.table_name "
               +"left join (select TABLE_NAME, count(*)cnt "
               +"from user_col_comments group by table_name)c on a.table_name=c.table_name order by \"tbname\";";
	}

	@Override
	public String tableFieldsSql(String dbname, String tbname) {
		return "select a.COLUMN_NAME \"field\",0 \"isidentity\", nvl2(b.column_name,1,0) \"iskey\",a.DATA_TYPE \"type\",DATA_LENGTH \"length\", decode(NULLABLE,'N',0,'Y',1) \"isnullable\",DATA_DEFAULT \"defaultvalue\",c.COMMENTS \"label\" "
               +"from user_tab_columns a "
               +"left join (select col.column_name from user_constraints con,user_cons_columns col "
               +"where con.constraint_name=col.constraint_name and con.constraint_type='P' "
               +"and col.table_name='"
               + tbname
               + "') b on a.column_name=b.column_name "
               +"left join user_col_comments c on a.column_name=c.column_name and c.table_name='"
               + tbname
               + "' where a.Table_Name='"
               + tbname
               + "' order by \"field\";";
	}

	@Override
	public String dbCountSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String driverClass() {
		return "oracle.jdbc.OracleDriver";
	}

	@Override
	public String jdbcUrl() {
		return "jdbc:oracle:thin:@//%s:%s/%s";
	}

	@Override
	public String defaultPort() {
		return "1521";
	}

	@Override
	public String alterTableCommentSql(String tbname, String remark) {
		return new StringBuilder()
				.append("Comment on table ")
				.append(tbname)
				.append(" is '")
				.append(remark).append("'").toString();
	}

	@Override
	public String alterFieldCommentSql(String tbname, String field, String remark, String type, String length) {
		return new StringBuilder()
				.append("Comment on column ")
				.append(tbname)
				.append(".")
				.append(field)
				.append(" is '")
				.append(remark).append("'").toString();
	}
	
	
}
