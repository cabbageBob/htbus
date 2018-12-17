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
import dbm.impl.Monitor;

public class DBReorgHelper {
	public static Logger logger = Logger.getLogger(DBReorgHelper.class);

	/**
	 * 需要入库
	 */
	public static final String isimpY = "1";//"需要入库"
	
	/**
	 * 不需要不入库
	 */
	public static final String isimpN = "0";//"不需要不入库"
	
	/**
	 * 缺失站点
	 */
	public static final String ismissY = "1";//"缺失站点"
	
	/**
	 * 非缺失站点
	 */
	public static final String ismissN = "0";//"不为缺失站点"
	
	/**
	 * 连续
	 */
	public static final String serialY = "1";//"连续"
	
	/**
	 * 不连续
	 */
	public static final String serialN = "0";//"不连续"
	
	/**
	 * 不检查
	 */
	public static final String noSerial = "-1";//"不检查"

	/**
	 * 分离数据库
	 */
	public static final String detachDbSql = "use master;exec sp_detach_db 'mdfname'";
	
	
	/**
	 * 附加数据库
	 */
	public static final String attachDbSql = "use master;exec sp_attach_db 'mdfname','path/mdffname','path/ldffname' ";
	
	/**
	 * 查找日表中 不同stcd记录数
	 */
	public static final String daySql = "select stcd,dt tm from tbname where stcd=? order by tm asc";
	
	/**
	 * 查找旬表中 不同stcd记录数
	 */
	public static final String tenSql = "select stcd,ptbgdt tm from tbname where stcd=? order by tm asc";
	
	/**
	 * 查找月表中 不同stcd记录数
	 */
	public static final String mmSql = "select stcd,yr yyyy,mth mm from tbname where stcd=? order by mm asc";
	
	/**
	 * 查找年表中 不同stcd记录数
	 */
	public static final String yyyySql = "select stcd,yr yyyy from tbname where stcd=?";

	/**
	 * 查找不同的泥沙类型
	 */
	public static final String diffSdtpSql = "select distinct sdtp from tbname where stcd=?";
	
	/**
	 * 查找表中 Z 水位记录数
	 */
	public static final String zCntSql = "select stcd,count(stcd) strcdcnt from tbname where z is not  null group by stcd order by stcd";
	
	/**
	 * 查找表中 Q  流量记录数
	 */
	public static final String qCntSql = "select stcd,count(stcd) strcdcnt from tbname where q is not  null group by stcd order by stcd";

	/**
	 * 查找表中 S  含沙记录数
	 */
	public static final String sCntSql = "select stcd,count(stcd) strcdcnt from tbname where s is not  null group by stcd order by stcd";

	/**
	 * 查找表中不同站点各自记录数语句
	 */
	public static final String diffStRcdCntSql = "select stcd,count(stcd) strcdcnt from tbname group by stcd order by stcd";
	
	/**
	 * 查找ms数据库中所有表中的记录总数语句
	 */
	public static final String sumRcdSql = "select object_name (i.id) tbnm,rows as rowcnt from sysindexes i inner join sysobjects o ON (o.id = i.id AND o.xType = 'U') where indid < 2 order by tbnm";
	
	/**
	 * 查找表中不同站点的个数语句
	 */
	public static final String diffStCntSql = "select count(distinct stcd) stcnt from tbname";
	
	/**
	 * 查找表中所有数据
	 */
	public static final String alllistSql = "select * from tbname";
	/**
	 * 查找表中所有数据
	 */
	public static final String destiSql = "select top 1 * from tbname";
	
	/**
	 * 存放需要检查的表信息 tbid、tbnm、tbtype、tb_check_type、tbdes
	 */
	public static List<Map<String,Object>> checkTbList = new ArrayList<>();
	
	/**
	 * 存放表的检查规则  tbtype、tb_rcdcnt、tbtype_des
	 */
	public static List<Map<String,Object>> tbRuleList = new ArrayList<>();
	
	/**
	 * 存放所有站点信息
	 */
	public static List<Map<String,Object>> allStList = new ArrayList<>();


	/**
	 * 所有局-站点配置信息，这是基表
	 */
	public static List<Map<String, Object>> baseStList = new ArrayList<>();
	
	/**
	 * 所有接入的数据库连接信息
	 */
	public static List<Map<String, Object>> dbConnList = new ArrayList<>();
	
	public static int daycnt,tencnt,mcnt,ycnt;
	
	static{
		try {
			//allStList = getDataFromSxj2015("select * from hy_stsc_a",null);
			refreshList();
			//Monitor.refreshAlert();
			for( Map<String, Object> tbruleMap:tbRuleList ){
				String tbtype = tbruleMap.get("tbtype").toString();
				String tb_rcdcnt = (null==tbruleMap.get("tb_rcdcnt"))?"0":tbruleMap.get("tb_rcdcnt").toString();
				switch (tbtype.toUpperCase()) {
					case "C": daycnt = Integer.parseInt(tb_rcdcnt);break;
					case "D": tencnt = Integer.parseInt(tb_rcdcnt);break;
					case "E": mcnt = Integer.parseInt(tb_rcdcnt);break;
					case "F": ycnt = Integer.parseInt(tb_rcdcnt);break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void refreshList(){
		baseStList = getDataFromReorg("{call getReorgStInfo()}",null);
		dbConnList = getDataFromReorg("{call getConnByDbid(?)}",new Object[]{null});
		checkTbList = getDataFromReorg("{call getTbCheck()}",null);
		tbRuleList = getDataFromReorg("{call getTbRcdCnt()}",null);
	}
	
	/**
	 * @description 从数据库 sxj2015 中获取 list 数据
	 * @date 2016年11月14日下午3:44:39
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String,Object>> getDataFromSxj2015(String sqls,Object[]args){
		BaseDao daoDbmReorg = DaoFactory.getDao("sxj2015");
		List<Map<String,Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			if( null== args ){
				rtlist = daoDbmReorg.executeQuery(sqls);
			}else{
				rtlist = daoDbmReorg.executeQuery(sqls,args);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listToLowCase(rtlist);
	}
	
	/** 
	 * @description 在 数据库整编总库 中执行 附加、分离语句
	 * @date 2016年11月14日下午3:45:56
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static boolean executeSqlByZK(String sqls,Object[]args) throws Exception{
		boolean result = false;
		BaseDao daoDbmReorg = DaoFactory.getDao("localhost");
		result = daoDbmReorg.executeSQL(sqls);
		return result;
	}
	
	/**
	 * @description 从数据库 dbm_reorganize 中获取 list 数据
	 * @date 2016年11月14日下午3:44:39
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String,Object>> getDataFromReorg(String sqls,Object[]args){
		BaseDao daoDbmReorg = DaoFactory.getDao("dbmreorganize");
		List<Map<String,Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			if( null== args ){
				rtlist = daoDbmReorg.executeQuery(sqls);
			}else{
				rtlist = daoDbmReorg.executeQuery(sqls,args);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listToLowCase(rtlist);
	}
	
	/** 
	 * @description 在 数据库 dbm_reorganize 中执行 dml 语句
	 * @date 2016年11月14日下午3:45:56
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 */
	public static boolean executeSqlByReorg(String sqls,Object[]args) throws Exception{
		BaseDao daoDbmReorg = DaoFactory.getDao("dbmreorganize");
		return daoDbmReorg.executeSQL(sqls, args);
	}
	
	/**
	 * 批量入库
	 */
	public static void executeSQLBatch(String sqls,List<Object[]> listo) throws Exception{
		BaseDao daoDbmReorg = DaoFactory.getDao("dbmreorganize");
		daoDbmReorg.executeSQLBatch(sqls, listo);
	}
	
	/** 
	 * @description 在 数据库 dbm_reorganize 中执行 dml 语句
	 * @date 2016年11月14日下午3:45:56
	 * @author lxj
	 * @param sqls
	 * @param args
	 * @return
	 */
	public static List<Map<String,Object>> callProByReorg(String sqls,Object[]args){
		List<Map<String, Object>> rtList = null;
		try {
			rtList = getDataFromReorg(sqls,args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
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

	public static int getRuleRcdCnt(String tbtype) {
		int result = 0 ;
		switch (tbtype.toUpperCase()) {
			case "C":case "C4": result = daycnt ;break;
			case "D":case "D4": result = tencnt ;break;
			case "E":case "E4": result = mcnt ;break;
			case "F":case "F4": result = ycnt ;break;
			default : result = 0 ;break;
		}
		return result;
	}
	
	public static String getStnm(String stcd){
		String result = "";
		for( Map<String, Object> allStMap:baseStList ){
			if(stcd.equals(allStMap.get("stcd").toString())){
				result = (null==allStMap.get("stnm"))?"":allStMap.get("stnm").toString();
				break;
			}
		}
		return result;
	}
	
	public static String getCheckTbnm(String tbid){
		String result = "";
		for( Map<String, Object> tbMap:checkTbList ){
			if(tbid.equals(tbMap.get("tbid").toString())){
				result = (null==tbMap.get("tbnm"))?"":tbMap.get("tbnm").toString();
				break;
			}
		}
		return result;
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
	 * @description 根据数据库id 获取数据库所有连接信息
	 * @date 2016年11月14日下午3:50:37
	 * @author lxj
	 * @param dbid
	 * @return
	 */
	public static Map<String, Object> getDaoMapByDbid(String dbid){
		Map<String, Object> rtMap = null;
		dbConnList.clear();
		dbConnList = getDataFromReorg("{call getConnByDbid(?)}",new Object[]{null});
		for( Map<String, Object> connMap:dbConnList ){
			if( connMap.get("id").toString().equals(dbid) ){
				rtMap = connMap;
				break;
			}
		}
		return rtMap;
	}

	public static String getKeyStrs(List<Map<String, Object>> list,String key) {
		String result = "";
		if( list==null || list.isEmpty() ) return result;
		if( !list.get(0).containsKey(key) ) return result;
		for( Map<String, Object> map:list ){
			result += (result.length()==0)?"'"+map.get(key).toString()+"'":"',"+map.get(key).toString()+"'";
		}
		return result;
	}
}
