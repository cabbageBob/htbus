package dbm.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;

/**
 * @description 存放所有数据迁移静态数据,以及直接从静态数据中获取的方法
 * @date 2016年11月14日下午2:56:47
 * @author lxj
 */
public class DBTransferUtil {
	/**
	 * 英文单双引号 double quotation marks
	 */
	public static String dqm = "\"";
	/**
	 * 空格
	 */
	public static String bks = " ";
	/**
	 * 分号
	 */
	public static String fenhao = ";";
	/**
	 * 左圆括号 openParenthesis
	 */
	public static String openP = "(";
	/**
	 * 右圆括号 closedParenthesis
	 */
	public static String closedP = ")";
	
	/**
	 * null 字符串
	 */
	public static String nullF = "NULL";
	
	/**
	 * not null 字符串
	 */
	public static String notNullF = "NOT NULL";
	
	/**
	 * mssql 数据库	dbtype_id=1
	 * 字段名：field_type_id,field_type_name,dbtype_id,field_type_length,sql_field_enabled,mysql_field_enabled,oracle_field_enabled
	 */
	public static List<Map<String, Object>> mssqlFieldList = getDataFromDbTrans(
			"{call getFieldTypeByDbTypeID(?)}", new Object[]{1});
	
	/**
	 *  oracle 数据库	dbtype_id=2
	 *  字段名：field_type_id,field_type_name,dbtype_id,field_type_length,sql_field_enabled,mysql_field_enabled,oracle_field_enabled
	 */
	public static List<Map<String, Object>> oracleFieldList = getDataFromDbTrans(
			"{call getFieldTypeByDbTypeID(?)}", new Object[]{2});
	
	/**
	 *  mysql 数据库	dbtype_id=3
	 *  字段名：field_type_id,field_type_name,dbtype_id,field_type_length,sql_field_enabled,mysql_field_enabled,oracle_field_enabled
	 */
	public static List<Map<String, Object>> mysqlFieldList = getDataFromDbTrans(
			"{call getFieldTypeByDbTypeID(?)}", new Object[]{3});
	
	/**
	 * 所有接入的数据库连接信息
	 */
	public static List<Map<String, Object>> dbConnList = getDataFromDbTrans(
			"{call getConnByDbid(?)}",new Object[]{null});
	
	List<Map<String, Object>> ruleList = getDataFromDbTrans(
			"{call getInsertRule()}",null);
	
	static String sqlcnt = "select count(1) cnt from tbname";
	static Logger logger = Logger.getLogger(DBTransferUtil.class);
	
	/**
	 * @description 从数据库dbm中获取 list 数据
	 * @date 2016年11月14日下午3:44:39
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String,Object>> getDataFromDbm(String sqls,Object[]args) throws Exception{
		BaseDao daoDbmTransfer = DaoFactory.getDao("dbmdb");
		List<Map<String,Object>> rtlist = new ArrayList<Map<String,Object>>();

		if( null== args ){
			rtlist = daoDbmTransfer.executeQuery(sqls);
		}else{
			rtlist = daoDbmTransfer.executeQuery(sqls,args);
		}

		return listToLowCase(rtlist);
	}
	
	/** 
	 * @description 在 数据库 dbm 中执行 dml 语句
	 * @date 2016年11月14日下午3:45:56
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 */
	public static boolean executeSqlByDbm(String sqls,Object[]args) throws Exception{
		BaseDao daoDbm = DaoFactory.getDao("dbmdb");
		return daoDbm.executeSQL(sqls, args);
	}
	
	/**
	 * @description 在数据库 dbm_transfer 中获取 list 数据
	 * @date 2016年11月14日下午3:47:10
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String,Object>> getDataFromDbTrans(String sqls,Object[]args){
		BaseDao daoDbmTransfer = DaoFactory.getDao("dbmtransfer");
		List<Map<String,Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			if( null== args ){
					rtlist = daoDbmTransfer.executeQuery(sqls);
			}else{
				rtlist = daoDbmTransfer.executeQuery(sqls,args);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listToLowCase(rtlist);
	}

	/**
	 * @description 在数据库 dbm_transfer 中执行 dml 语句
	 * @date 2016年11月14日下午3:47:44
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 */
	public static boolean executeSqlByDbTrans(String sqls,Object[]args) throws Exception{
		BaseDao daoDbm = DaoFactory.getDao("dbmtransfer");
		return daoDbm.executeSQL(sqls, args);
	}
	
	/**
	 * @description list中 key 值 全转小写
	 * @date 2016年11月14日下午3:48:17
	 * @author lxj
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> listToLowCase(List<Map<String, Object>> list) {
		if( null==list || list.size()==0 )
			return list;
		List<Map<String, Object>> rtList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map :list){
			Map<String, Object> tmpmap =  new HashMap<String, Object>();
			Set<String> keyset = map.keySet();
			Iterator<String> iterator = keyset.iterator();
			while(iterator.hasNext()) {
				 String key = iterator.next().toString();
				 tmpmap.put(key.toLowerCase(), map.get(key));
			 }
			rtList.add(tmpmap);
		}
		return rtList;
	}

	/**
	 * @description 根据数据库id 获取数据库所有连接信息
	 * @date 2016年11月14日下午3:50:37
	 * @author lxj
	 * @param dbid
	 * @return
	 */
	public static Map<String, Object> getDaoMapByDbid(String dbid){
		Map<String, Object> rtMap = null;
		for( Map<String, Object> connMap:dbConnList ){
			if( connMap.get("id").toString().equals(dbid) ){
				rtMap = connMap;
				break;
			}
		}
		return rtMap;
	}
	
	/**
	 * @description 根据 dbid 获取 dao
	 * @date 2016年11月14日下午4:10:45
	 * @author lxj
	 * @param dbid
	 * @return
	 */
	public static BaseDao getDaoByDbid(String dbid){
		Map<String, Object> dbMap = getDaoMapByDbid(dbid);
		if( null==dbMap ) return null;
		String _username = dbMap.get("username").toString();
		String _password = dbMap.get("password").toString();
		String _className = dbMap.get("driver_class").toString();
		String _url = dbMap.get("url_tpl").toString()
				.replace("ip", dbMap.get("server_ip").toString())
				.replace("port", dbMap.get("dbport").toString())
				.replace("dbname", dbMap.get("dbname").toString());
		return new BaseDao(_url, _username, _password, _className);
	}
	
	/**
	 * @description 根据 dbid、tbnm 获取数据表中记录总数
	 * @date 2016年11月14日下午4:11:07
	 * @author lxj
	 * @param dbid
	 * @param tbnm
	 * @return
	 * @throws Exception 
	 */
	public static int getTbRecordCnt(String dbid, String tbnm) throws Exception {
		int result = 0;
		BaseDao daosdb = getDaoByDbid(dbid);
		Map<String, Object> cntmap = daosdb.executeQueryObject(sqlcnt.replace("tbname", tbnm));
		if( null!=cntmap )
			result = Integer.parseInt(cntmap.get("cnt").toString());
		return result;
	}

	/**
	 * @description 根据 dbid 获取 数据库类型
	 * @date 2016年11月14日下午4:18:16
	 * @author lxj
	 * @param dbid
	 * @return -1 不存在  1 mssql; 2 oracle; 3 mysql
	 */
	public static int getDbTypeByDbid(String dbid){
		Map<String, Object> dbMap = getDaoMapByDbid(dbid);
		if( null==dbMap ) return -1;
		return Integer.parseInt( dbMap.get("dbtype_id").toString() ); 
		
	}
	
	/**
	 * @description 根据 dbid 获取 对应的所有字段类型
	 * @date 2016年11月14日下午4:18:16
	 * @author lxj
	 * @param dbid
	 * @return
	 */
	public static List<Map<String, Object>> getFiledTypeByDbid(int dbtype_id){
		List<Map<String, Object>> rtList = null;
		switch( dbtype_id ){
			case 1:
				rtList = mssqlFieldList;break;
			case 2:
				rtList = oracleFieldList;break;
			case 3:
				rtList = mysqlFieldList;break;
		}
		return rtList;
	}

	public static String getTarFiled(int sdbtypeid, int tdbtypeid, String sftype, int flen, int fprec, int fscal) {
		String result = "";
		List<Map<String, Object>> srcFieldList = getFiledTypeByDbid(sdbtypeid);
		List<Map<String, Object>> tarFieldList = getFiledTypeByDbid(tdbtypeid);
		for( Map<String, Object> sfMap:srcFieldList ){
			if( sfMap.get("field_type_name").toString().toLowerCase().equals(sftype.toLowerCase()) ){
				int sftypelen = Integer.parseInt(sfMap.get("field_type_length").toString());
				int tftypelen = 0;
				String tfEnabled = getTarFEnabled(tdbtypeid); 
				String [] tftypes = sfMap.get(tfEnabled).toString().split(",");
				String tftype = "";
				String precscal = "";
				switch( sdbtypeid ){
				case 1:
					switch( tdbtypeid ){
						case 1://mssql->mssql
							tftype = tftypes[0];
							tftypelen = sftypelen;
							switch( tftypelen ){
								case 0: precscal = "";break;
								case 1: precscal = "("+ (fprec<0?"max":fprec) +")";break;
								case 2: precscal = "("+ fprec +","+ fscal +")";break;
							}
							result = tftype + bks + precscal ;
							break;
						case 2://mssql->oracle 
							tftype = tftypes[0];
							tftypelen = getFieldTypeLen(tftype, tdbtypeid);
							if( sftype.toLowerCase().equals("float") ){
								precscal = "("+ fprec +")";
							}else {
								if( sftype.toLowerCase().contains("varchar") || sftype.toLowerCase().contains("nvarchar") ){
									if( flen<0 ){
										tftype = tftypes[1];
										tftypelen = getFieldTypeLen(tftype, tdbtypeid);
										switch( tftypelen ){
										case 0: precscal = "";break;
										case 1: precscal = "("+ (fprec<0?"max":fprec) +")";break;
										case 2: precscal = "("+ fprec +","+ fscal +")";break;
										}
									}else
										precscal = "("+ fprec +")";
								}else{
									switch( tftypelen ){
									case 0: precscal = "";break;
									case 1: precscal = "("+ fprec +")";break;
									case 2: precscal = "("+ fprec +","+ fscal +")";break;
									}
								}
							}
							result = tftype + bks + precscal ;
							break;
						case 3://mssql->mysql
							tftype = tftypes[0];
							tftypelen = getFieldTypeLen(tftype, tdbtypeid);
							switch( tftypelen ){
							case 0: precscal = "";break;
							case 1: precscal = "("+ fprec +")";break;
							case 2: precscal = "("+ fprec +","+ fscal +")";break;
							}
							result = tftype + bks + precscal ;
							break;
					};
					break;
				case 2:
					break;
				case 3:
					break;
				}
				break;
			}
		}
		return result;
	}
	
	public static int getFieldTypeLen(String ftype,int dbtypeid){
		int result = 0;
		List<Map<String, Object>> fieldList = getFiledTypeByDbid(dbtypeid);
		for( Map<String, Object> fMap:fieldList ){
			if( fMap.get("field_type_name").toString().toLowerCase().equals(ftype.toLowerCase()) ){
				result = Integer.parseInt(fMap.get("field_type_length").toString());
				break;
			}
		}
		return result;
	}

	private static String getTarFEnabled(int tdbtype_id) {
		switch( tdbtype_id ){
		case 1:
			return "sql_field_enabled";
		case 2:
			return "oracle_field_enabled";
		case 3:
			return "mysql_field_enabled";
		default: return "";
		}
	}

	/**
	 * @description 构造字段详细信息
	 * @date 2016年11月15日下午2:38:59
	 * @author lxj
	 * @param tdbtypeid
	 * @param fname
	 * @param tftype 包含字段类型 字段长度、精度等信息
	 * @param fcom
	 * @param isnull
	 * @return
	 */
	public static String createFFullName(int tdbtypeid, String fname, String tftype, String fcom, String isnull) {
		String result = "";
		String nullstr = ("1".equals(isnull)?nullF:notNullF);
		String commentstr = fcom;//字段备注暂不处理
		switch( tdbtypeid ){
		case 1:
			result = fname +bks+ tftype +bks+ nullstr;break;
		case 2:
			result = dqm+ fname +dqm+bks+ tftype +bks+ nullstr;break;
		case 3:
			result = fname +bks+ tftype +bks+ nullstr;break;
		}
		return result;
	}
}
