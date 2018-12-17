package dbm.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.miao.framework.dao.BaseDao;
import dbm.util.DBUtil;
import dbm.util.LogUtil;

public class BackUpJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDataMap plan = arg0.getMergedJobDataMap();
		String sql = "select a.id dbid,a.name,a.dbtype,a.dbname,a.server_ip,a.dbport,a.username,a.password,b.driverclass,b.urltpl,b.tpl_bak_full,b.tpl_bak_different from dbsource a left join dbtype b on a.dbtype=b.dbtype where a.id=?";
		Map<String, Object> db;
		try {
			db = DataSource.sysdao().executeQueryObject(sql, new Object[]{plan.get("dbid").toString()});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		//获取类型全量还是增量
		String type = plan.get("type").toString();
		String bak_sql = "";
		Date now = new Date();
		String tm = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
		String filename = plan.get("folder").toString() + db.get("dbname").toString() + "_" + tm + ".bak";
		if(type.equals("0")){//全量备份
			bak_sql = db.get("tpl_bak_full").toString();
		}else if(type.equals("1")){//差异备份
			bak_sql = db.get("tpl_bak_different").toString();
		}
		String msg = "";
		String str_result = "";
		try{
			BaseDao dao = DBUtil.getDBDao(db.get("urltpl").toString(),db.get("server_ip").toString()
					,db.get("dbport").toString(),
					db.get("dbname").toString() ,
					db.get("username").toString() ,
					db.get("password").toString() ,
					db.get("driverclass").toString());
			
			dao.executeSQL(
					bak_sql.replace("dbname", db.get("dbname").toString())
					.replace("filename", filename)
					);
			msg="备份执行成功";
			str_result = "success";
			
			//记录备份文件
			String str_tm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
			String file_sql = "insert into bak_plan_file(planid,dbid,createtm,filepath,type) values(?,?,?,?,?)";
			DataSource.sysdao().executeSQL(file_sql,new Object[]{
					plan.get("planid"),
					db.get("dbid").toString(),
					str_tm,
					filename,
					plan.get("type").toString()
			});
		}catch(Exception e){
			msg="备份执行失败，"+e.getMessage();
			str_result = "faild";
		}
		//记录操作日志
		LogUtil.log("system","backup", msg, plan.get("planid").toString(), str_result);
	}
}
