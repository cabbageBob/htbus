package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;

public class ZBImpl {
	public BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public List<Map<String, Object>> getAllZBTask(){
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			sql = "select * from zb_task";
			rtList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}
	public List<Map<String,Object>>getZBTaskAllYear(){
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			sql = "select distinct yyyy from zb_task";
			rtList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}
	public List<Map<String,Object>>getZBReportByTaskid(String taskid ){
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			sql = "select a.id,a.program_tbnm,a.stcnt,a.targetrow,a.realrow,a.errstcnt,b.tbdes from zb_report a left join zb_program b on a.program_tbnm=b.tbnm where a.taskid=?";
			rtList = sysdao.executeQuery(sql,new Object[]{taskid});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}
	public List<Map<String, Object>> getZBTaskByGid(String gid){
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			sql = "select * from zb_task where gid=?";
			rtList = sysdao.executeQuery(sql,new Object[]{gid});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}
	public List<Map<String, Object>> getNewReportByGid(String gid){
		List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			sql = "select M.*,M.strow*M.stcnt targetrow FROM(select a.tbnm,a.tbdes,a.strow, (select COUNT(stcd) from zb_stinfo b where b.gid=? and b.obitmcd like concat('%',a.tb_check_type,'%')) stcnt from zb_program a) M";
			rtList = sysdao.executeQuery(sql,new Object[]{gid});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}
	public Map<String,Object> addZBTask(String gids,String year){
		String sql ="";
		String result="Defeat";
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String[] allgid = gids.split(",");
			for(int i=0;i<allgid.length;i++){
				String gid = allgid[i];
				sql = "select gname from sys_group where gid=?";
				String taskname=year+"-"+sysdao.executeQueryObject(sql, new Object[]{gid}).get("gname").toString();
				sql = "insert into zb_task(taskname,gid,yyyy) values(?,?,?);";
				sysdao.executeSQL(sql, new Object[]{taskname,gid,year});		
				sql = "select max(taskid) taskid from zb_task;";
				String taskid = sysdao.executeQueryObject(sql).get("taskid").toString();
				//sql = "select M.*,M.strow*M.stcnt targetrow FROM(select a.tbnm,a.tbdes,a.strow, (select COUNT(stcd) from zb_stinfo b where b.gid=? and b.obitmcd like concat('%',a.tb_check_type,'%')) stcnt from zb_program a) M";
				//List<Map<String,Object>> list = sysdao.executeQuery(sql);
				String sql2 = "insert into zb_report(program_tbnm,stcnt,targetrow) select M.tbnm,M.stcnt,M.strow*M.stcnt targetrow FROM(select a.tbnm,a.tbdes,a.strow, (select COUNT(stcd) from zb_stinfo b where b.gid=? and b.obitmcd like concat('%',a.tb_check_type,'%')) stcnt from zb_program a) M";
				sysdao.executeSQL(sql2,new Object[]{gid});
				sql2 = "update zb_report set taskid=? where taskid is null";
				result = sysdao.executeSQL(sql2,new Object[]{taskid})?"Success":"Defeat";
			}	
		}catch (Exception e) {
			e.printStackTrace();
		}
		map.put("result", result);
		return map;
	}
	public Map<String,Object> promiseZBTask(String taskid){
		String sql = "";
		String data = "Defeat";
		Map<String,Object> map =new HashMap<String,Object>();
		try {
			sql = "update zb_task set promise=?,status=? where taskid=?";
			data = sysdao.executeSQL(sql, new Object[]{"2016数据库质量保证书.pdf","已保证",taskid})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		map.put("result", data);
		return map;
	}
	public List<Map<String,Object>> detailReport(String tbnm,String stcnt){
		String sql = "";
		List<Map<String,Object>> map =new ArrayList<Map<String,Object>>();
		try {
			sql = "select stcd,stnm,(select strow from zb_program where tbnm=?) datarow, '连续' as isline,'-' as nodate from zb_stinfo a limit "+stcnt;
			map = sysdao.executeQuery(sql, new Object[]{tbnm});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public Map<String,Object> replyZBTask(String taskid,String reply,String status){
		String sql = "";
		String data = "Defeat";
		Map<String,Object> map =new HashMap<String,Object>();
		try {
			sql = "update zb_task set status=?,reply=? where taskid=?";
			data = sysdao.executeSQL(sql, new Object[]{status,reply,taskid})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		map.put("result", data);
		return map;
	}
	public static void main(String[] args) {
		new ZBImpl().addZBTask("0020020026015","2018");
		System.exit(0);
	}
}
