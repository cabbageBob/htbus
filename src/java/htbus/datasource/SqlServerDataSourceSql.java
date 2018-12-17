package htbus.datasource;

import java.util.HashMap;
import java.util.Map;

public class SqlServerDataSourceSql extends AbstractDataSourceSqlConfig{

	public static final SqlServerDataSourceSql INSTANCE = new SqlServerDataSourceSql();
	
	private SqlServerDataSourceSql(){
	}
	
	@Override
	public String allTablesSql(String dbname) {
		return "select x.name AS tbname,isnull(cast(y.[value] as varchar(5000)),'-') AS label, "
				+"cast(((select COUNT(1)+0.0 from sys.extended_properties a left join sys.sysobjects b on a.major_id=b.id where class=1 and a.name='MS_Description' and b.name=x.name)*100 "
				+"/ "
				+"( "
				+"(select COUNT(1)+1 from syscolumns a "
				+"inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' where d.name=x.name) "
				+")) as numeric(5,0)) sta "
				+"from sys.tables x left join sys.extended_properties y on (x.object_id = y.major_id AND y.minor_id = 0 and y.name='MS_Description') "
				+"where x.name<>'dtproperties'";
	}

	@Override
	public String tableFieldsSql(String dbname, String tbname) {
		return "SELECT a.name field, "
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
				+"isnull(cast(g.[value] as varchar(5000)),'-') label "
				+"FROM  syscolumns a "
				+"left join systypes b on a.xtype=b.xusertype  "
				+"inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' "
				+"left join syscomments e on a.cdefault=e.id  "
				+"left join sys.extended_properties g on a.id=g.major_id AND a.colid=g.minor_id "
				+"left join sys.extended_properties f on d.id=f.class and f.minor_id=0 "
				+"where b.name is not null "
				+"and d.name= '"
				+tbname
				+"' "
				+"order by a.id,a.colorder";
	}

	@Override
	public String dbCountSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String driverClass() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	@Override
	public String jdbcUrl() {
		return "jdbc:sqlserver://%s:%s;databaseName=%s";
	}

	@Override
	public String defaultPort() {
		return "1433";
	}

	@Override
	public String alterTableCommentSql(String tbname, String remark) {
		return "IF ((SELECT COUNT(*) from sys.fn_listextendedproperty('MS_Description',  "
				+"'SCHEMA', N'dbo',  "
				+"'TABLE', N'"+tbname+"',  "
				+"NULL, NULL)) > 0)  "
				+"EXEC sp_updateextendedproperty @name = N'MS_Description', @value = '"+remark+"' "
				+", @level0type = 'SCHEMA', @level0name = N'dbo' "
				+", @level1type = 'TABLE', @level1name = N'"+tbname+"' "
				+"ELSE "
				+"EXEC sp_addextendedproperty @name = N'MS_Description', @value = '"+remark+"' "
				+", @level0type = 'SCHEMA', @level0name = N'dbo' "
				+", @level1type = 'TABLE', @level1name = N'"+tbname+"'";
	}

	@Override
	public String alterFieldCommentSql(String tbname, String field, String remark, String type, String length) {
		return "IF ((SELECT COUNT(*) from sys.fn_listextendedproperty('MS_Description',  "
				+"'SCHEMA', N'dbo',  "
				+"'TABLE', N'"+tbname+"',  "
				+"'COLUMN', N'"+field+"')) > 0)  "
				+"EXEC sp_updateextendedproperty @name = N'MS_Description', @value = '"+remark+"' "
				+", @level0type = 'SCHEMA', @level0name = N'dbo' "
				+", @level1type = 'TABLE', @level1name = N'"+tbname+"' "
				+", @level2type = 'COLUMN', @level2name = N'"+field+"' "
				+"ELSE "
				+"EXEC sp_addextendedproperty @name = N'MS_Description', @value = '"+remark+"' "
				+", @level0type = 'SCHEMA', @level0name = N'dbo' "
				+", @level1type = 'TABLE', @level1name = N'"+tbname+"' "
				+", @level2type = 'COLUMN', @level2name = N'"+field+"'";
	}
	
}
