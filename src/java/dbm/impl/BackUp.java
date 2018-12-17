package dbm.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;

import cn.miao.framework.dao.BaseDao;
import dbm.util.DBUtil;
import dbm.util.QuartzManager;

public class BackUp {
	
	public List<Map<String,Object>> getAllPlan(){
		String sql = "select a.*,b.dbname,b.name from bak_plan a left join dbsource b on a.dbid = b.id where isdel=0";
		try {
			return DataSource.sysdao().executeQuery(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 创建计划
	 * @param dbid 数据库ID
	 * @param folder 存储文件夹，精确至数据库ID和全量或增量
	 * @param type 0全量 1增量
	 * @return
	 * @throws Exception 
	 */
	public Boolean addPlan(String dbid,String folder,String type,String rule) throws Exception{
		String sql = "insert into bak_plan(createtm,dbid,folder,type,rule) values(now(),?,?,?,?);";
		Boolean result = false;
		try {
			result = DataSource.sysdao().executeSQL(sql,new String[]{dbid,folder,type,rule});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createFolder(dbid,folder);
		//createFolder(folder+"//full//");
		//createFolder(folder+"//different//");
		return result;
	}
	/**
	 * 启动计划
	 * @param planid
	 * @return
	 */
	public static Boolean startPlan(String planid){
		Boolean result = false;
		try{
			QuartzManager.removeJob(planid);//如果已经启动了先删掉
			
			String sql = "select * from bak_plan where planid=?";
			Map<String,Object> plan = DataSource.sysdao().executeQueryObject(sql, new Object[]{planid});
			JobDataMap datamap = new JobDataMap();// (JobDataMap)plan;
			datamap.put("planid", plan.get("planid"));
			datamap.put("type", plan.get("type"));
			datamap.put("dbid", plan.get("dbid"));
			datamap.put("folder", plan.get("folder"));
			datamap.put("rule", plan.get("rule"));
			QuartzManager.addJob(planid, BackUpJob.class, plan.get("rule").toString(), datamap);
			result = true;
			
			String sql_start = "update bak_plan set status=1 where planid=?";
			try {
				DataSource.sysdao().executeSQL(sql_start, new Object[]{planid});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(Exception e){
			
		}
		return result;
	}
	/**
	 * 停用计划
	 * @param planid
	 * @return
	 */
	public Boolean stopPlan(String planid){
		try{
			QuartzManager.removeJob(planid);
			String sql_start = "update bak_plan set status=0 where planid=?";
			DataSource.sysdao().executeSQL(sql_start, new Object[]{planid});
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 获取备份执行日志
	 * @param planid
	 * @return
	 */
	public List<Map<String,Object>> getPlanLog(String planid){
		String sql = "select a.*,b.dbid,c.name from sys_log a left join bak_plan b on a.tag1=b.planid left join dbsource c on b.dbid=c.id where a.logtype='backup' and a.tag1=?";
		try {
			return DataSource.sysdao().executeQuery(sql, new Object[]{planid});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取历史备份文件列表
	 * @param planid
	 * @return
	 */
	public List<Map<String,Object>> getBackFiles(String planid){
		String sql = "select a.*,b.name from bak_plan_file a left join dbsource b on a.dbid=b.id where planid=?";
		try {
			return DataSource.sysdao().executeQuery(sql, new Object[]{planid});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 启动所有计划，在tomcat启动时执行一次
	 * @return
	 */
	public static Boolean startAllPlan(){
		try{
			String sql = "select * from bak_plan where status=1 and isdel=0";
			List<Map<String,Object>> list = DataSource.sysdao().executeQuery(sql);
			for(Map<String,Object> plan : list){
				startPlan(plan.get("planid").toString());
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Boolean deletePlan(String planid){
		String sql = "update bak_plan set isdel=1 where planid=?";
		try {
			return DataSource.sysdao().executeSQL(sql, new Object[]{planid});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void createFolder(String dbid,String folder) throws Exception{
//		File uploadFile = new File(folder);
//		if (!uploadFile.exists()) {
//			uploadFile.mkdirs();
//		}
		//folder = folder.replaceAll("/", "//");
		BaseDao dao = DBUtil.getDBDao(dbid);
		String sql = "EXEC sp_configure 'show advanced options', 1; "
				+"RECONFIGURE WITH OVERRIDE; "
				+"EXEC sp_configure 'xp_cmdshell', 1; "
				+"RECONFIGURE WITH OVERRIDE; "
				+"EXEC xp_cmdshell 'mkdir "+folder+"'; ";
		dao.executeQuery(sql);
	}

	/**还原备份文件
	 * @param bakfileid
	 * @return
	 */
	public Map<String,String> restoreBack(String bakfileid){
		//记录正在还原
		String sql_recover_start = "insert into bak_recover(bakfileid,tm,status) values(?,now(),'还原中');";
		try {
			DataSource.sysdao().executeSQL(sql_recover_start, new Object[]{bakfileid});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		int recoverid = 0;
		String sql_recover_id = "select max(id) id from bak_recover;";
		try {
			Map<String,Object> rid = DataSource.sysdao().executeQueryObject(sql_recover_id);
			if(rid!=null){
				recoverid = Integer.parseInt(rid.get("id").toString());
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		String sql ="select a.dbid,a.filepath,a.type,a.planid,a.createtm,b.*,c.driverclass,c.urltpl,d.folder from bak_plan_file a left join dbsource b on a.dbid=b.id left join dbtype c on b.dbtype=c.dbtype left join bak_plan d on a.planid=d.planid where a.bakfileid=?";
		Map<String,String> map = new HashMap<String,String>();
		String result = "Defeat";
		try{
			Map<String,Object> mjmap = DataSource.sysdao().executeQueryObject(sql,new Object[]{bakfileid});
			String dbname = mjmap.get("dbname").toString();
			String filepath = mjmap.get("filepath").toString();
			String type = mjmap.get("type").toString();
			BaseDao dao = DBUtil.getDBDao(mjmap.get("urltpl").toString(),mjmap.get("server_ip").toString()
					,mjmap.get("dbport").toString(),
					dbname ,
					mjmap.get("username").toString() ,
					mjmap.get("password").toString() ,
					mjmap.get("driverclass").toString());
			Date now = new Date();
			String tm = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
			String filename = mjmap.get("folder").toString() + dbname + "_" + tm +"_restore"+ ".bak";
			sql = type.equals("0")?"backup database dbname to disk='".replace("dbname", dbname)+filename+"' with init":"backup database dbname to disk='".replace("dbname", dbname)+filename.replace("different", "full")+"' with init";
			dao.executeSQL(sql);
			String str_tm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
			String file_sql = "insert into bak_plan_file(planid,dbid,createtm,filepath,type) values(?,?,?,?,?)";
			DataSource.sysdao().executeSQL(file_sql,new Object[]{
					mjmap.get("planid"),
					mjmap.get("dbid").toString(),
					str_tm,
					filename,
					"0"
			});
			if(type.equals("0")){
				String sql1 ="USE master;DECLARE @spid int DECLARE CUR CURSOR FOR SELECT spid FROM sysprocesses WHERE   dbid=(select top 1 dbid from master..sysprocesses  where db_name(dbid)='"+dbname+"')OPEN CUR FETCH NEXT FROM CUR INTO @spid WHILE @@FETCH_STATUS = 0 "+
		    		      " BEGIN EXEC ('KILL ' + @spid )FETCH  NEXT  FROM CUR INTO @spid END CLOSE CUR DEALLOCATE CUR;"+
	                      " RESTORE DATABASE ["+dbname+"]"+
	                      " FROM  DISK = '"+filepath+"' WITH ";
				sql = "RESTORE FILELISTONLY FROM  DISK = '"+filepath+"'";
				List<Map<String,Object>> list1 = dao.executeQuery(sql);
				//sql = "select filename from "+dbname+".dbo.sysfiles";
				//List<Map<String,Object>> list2 = dao.executeQuery(sql);
				for(int i=0;i<list1.size();i++){
				String logicalname = list1.get(i).get("LogicalName").toString();
				String path = list1.get(i).get("PhysicalName").toString();
			    sql1 = sql1+" MOVE N'"+logicalname+"' TO N'"+path+"'," ;
				}
				sql1 = sql1 +" RECOVERY,  NOUNLOAD,  REPLACE,  STATS = 10";
			    dao.executeSQL(sql1);
			    result = "Success";
			}else if(type.equals("1")){
				//差异备份需要先还原一个完整备份，但NORECOVERY，然后再还原差异备份文件
				String sql2 = "select filepath from bak_plan_file where createtm<=? and dbid=? and filepath like '%full%' and filepath not like '%restore%' ORDER BY createtm desc LIMIT 1";
			    String createtm = mjmap.get("createtm").toString();
			    String dbid = mjmap.get("dbid").toString();
				String filepath2 = DataSource.sysdao().executeQueryObject(sql2,new Object[]{createtm,dbid}).get("filepath").toString();
				sql2 = "RESTORE FILELISTONLY FROM  DISK = '"+filepath2+"'";
				List<Map<String,Object>> list1 = dao.executeQuery(sql2);
				sql2 = "use master;"+
						  " DECLARE @spid int DECLARE CUR CURSOR FOR SELECT spid FROM sysprocesses WHERE   dbid=(select top 1 dbid from master..sysprocesses  where db_name(dbid)='"+dbname+"')OPEN CUR FETCH NEXT FROM CUR INTO @spid WHILE @@FETCH_STATUS = 0 "+
						  " BEGIN EXEC ('KILL ' + @spid )FETCH  NEXT  FROM CUR INTO @spid END CLOSE CUR DEALLOCATE CUR;"+
			              " RESTORE DATABASE ["+dbname+"]"+
			              " FROM  DISK = '"+filepath2+"' WITH ";
				for(int i=0;i<list1.size();i++){
				String logicalname = list1.get(i).get("LogicalName").toString();
				String path = list1.get(i).get("PhysicalName").toString();
				//sql2 = "select filename from "+dbname+".dbo.sysfiles";
				//String datapath2 = dao.executeQuery(sql2).get(0).get("filename").toString();
				//String logpath2 = dao.executeQuery(sql2).get(1).get("filename").toString();
		        sql2 = sql2 +" MOVE N'"+logicalname+"' TO N'"+path+"',";        
				}
				sql2 = sql2+ " NORECOVERY,  NOUNLOAD,  REPLACE,  STATS = 10;"+
			              " restore database ["+dbname+"] from disk='"+filepath+"';";
				dao.executeSQL(sql2);
				result = "Success";
			}
		}catch (Exception e) {
			//记录失败
			String sql_recover_err = "update bak_recover set status='失败',msg=? where id = ?";
			try {
				DataSource.sysdao().executeSQL(sql_recover_err, new Object[]{e.getMessage(),recoverid});
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		map.put("result", result);
		
		//记录成功
		String sql_recover_end = "update bak_recover set status='成功' where id = ?";
		try {
			DataSource.sysdao().executeSQL(sql_recover_end, new Object[]{recoverid});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取所有数据库恢复日志
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getRecoverList() throws Exception{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select  c.dbname, a.bakfileid,b.filepath,a.tm,a.status,a.msg from bak_recover a left join bak_plan_file b on a.bakfileid=b.bakfileid left join dbsource c on b.dbid=c.id order by a.id desc";
		list = DataSource.sysdao().executeQuery(sql);
		return list;
	}
}
