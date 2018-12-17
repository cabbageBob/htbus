package dbm.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.factory.DaoFactory;
import dbm.impl.DBReorgImpl;
import dbm.impl.DataSource;
import dbm.impl.ZBImpl;
import dbm.util.ComparatorMap;
import dbm.util.ComparatorPinYin;

public class ZBAction extends DoAction {
	ZBImpl zbi = new ZBImpl();
	public Responser getAllZBTask() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.getAllZBTask()));
		return responser;
	}
	public Responser getZBTaskAllYear() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.getZBTaskAllYear()));
		return responser;
	}
	public Responser getZBReportByTaskid() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.getZBReportByTaskid(params.getParam("taskid"))));
		return responser;
	}
	public Responser getZBTaskByGid() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.getZBTaskByGid(params.getParam("gid"))));
		return responser;
	}

	public Responser excuseZBTask(){
		String sql = "update zb_task set excuse=? where taskid=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.getParam("excuse"),params.getParam("taskid")});
			responser.setRtString("1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responser.setRtString("0");
		}
		responser.setRtType(TEXT);
		return responser;
	}

	public Responser getNewReportByGid() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.getNewReportByGid(params.getParam("gid"))));
		return responser;
	}
	public Responser addZBTask() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.addZBTask(params.getParam("gids"),params.getParam("year"))));
		return responser;
	}

	/**
	 * 统计数据完整性（按照原目标数量，全部填写正常）
	 * @return
	 */
	public Responser doZBReport(){
		String sql = "update zb_task set mdf='data2016.mdf',ldf='data2016.ldf',status='已提交' where taskid=?";
		String sql2 = "update zb_report set realrow=targetrow,errstcnt=0 where taskid=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{params.getParam("taskid")});
			DataSource.sysdao().executeSQL(sql2,new Object[]{params.getParam("taskid")});
			responser.setRtString("1");
		} catch (Exception e) {
			responser.setRtString("0");
			e.printStackTrace();
		}
		responser.setRtType(JSON);
		return responser;
	}
	public Responser promiseZBTask() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.promiseZBTask(params.getParam("taskid"))));
		return responser;
	}
	public Responser detailReport() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.detailReport(params.getParam("tbnm"),params.getParam("stcnt"))));
		return responser;
	}
	public Responser replyZBTask() {
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(zbi.replyZBTask(params.getParam("taskid"),params.getParam("reply"),params.getParam("status"))));
		return responser;
	}
	
	/**
	 * 根据gid返回测站
	 * @return
	 */
	public Responser getStation(){
		responser.setRtType(JSON);
		String sql = "select * from zb_stinfo where gid=?";
		try {
			List<Map<String,Object>> list = DataSource.sysdao().executeQuery(sql,new Object[]{params.getParam("gid")});
			responser.setRtString(parseJSON(list));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responser.setRtString("error");
		}
		
		return responser;
	}
	/**
	 * 根据gid返回测站
	 * @return
	 */
	public Responser getStation2() throws Exception{
		String gid = params.getParam("gid");
		String year = params.getParam("year");
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		BaseDao dao1 = DaoFactory.getDao("dbmreorganize");
		BaseDao dao2 = DaoFactory.getDao("stbase");
		//1.获取本地列表
		String sql1 = "select * from sys_reorg_stinfo where deleted='0' and gid=? and yyyy = ?";
		List<Map<String,Object>> list1 = dao1.executeQuery(sql1,new Object[]{gid,year});
		//2.获取测站管理系统里的列表
		String sql2 = "{call getSTList(?)}";
		List<Map<String,Object>> list2 = dao2.executeQuery(sql2,new Object[]{gid});
		//2.5获取沿革，更新测站管理系统里的列表
				String sql3 = "{call getSTYG(?)}";
				List<Map<String, Object>> list3 = new ArrayList<Map<String,Object>>();
				try {
					list3 = dao2.executeQuery(sql3,new Object[]{year});
					for(Map<String,Object> map_yg : list3){
						Map<String,Object> map = getMapFromList(list2,"stcd",map_yg.get("stcd").toString());
						if(map!=null){
							map.put("stct", map_yg.get("sttp"));
							map.put("obitmcd", map_yg.get("obitmcd"));
						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		//3.得到最全的STCD和站名列表
		Set<Map<String,Object>> stnms = new HashSet<Map<String,Object>>();
		for(Map<String,Object> map : list1){
			Map<String, Object> midmap = new HashMap<String, Object>();
			if(map.get("stnm")!=null){
			midmap.put("stcd",map.get("stcd"));
			midmap.put("stnm", map.get("stnm"));
			}else{
				midmap.put("stcd", map.get("stcd"));
				midmap.put("stnm", "null");
			}
			stnms.add(midmap);
		}
		for(Map<String,Object> map : list2){
			Map<String, Object> midmap = new HashMap<String, Object>();
			if(map.get("stnm")!=null){
			midmap.put("stcd",map.get("stcd"));
			midmap.put("stnm", map.get("stnm"));
			}else{
				midmap.put("stcd", map.get("stcd"));
				midmap.put("stnm", "null");
			}
			stnms.add(midmap);
		}
		List<Map<String, Object>> stnmlst = new ArrayList<Map<String,Object>>();
		stnmlst.addAll(stnms);
		Collections.sort(stnmlst, new ComparatorMap());
		//4.循环STCD列表，依次将两个列表里的对应数据拼接在一起
		for(Map<String, Object> cmap : stnmlst){
			String stcd = cmap.get("stcd").toString();
			List<Map<String,Object>> lt1 = getListFromList(list1,"stcd",stcd);
			List<Map<String,Object>> lt2 = getListFromList(list2,"stcd",stcd);
			List<Map<String,Object>> ltm = new ArrayList<Map<String,Object>>();
			int size = lt1.size()>=lt2.size()?lt1.size():lt2.size();
			for(int i=0;i<size;i++){
			Map<String,Object> map = new HashMap<String,Object>();
			Map<String,Object> m1 = (i<lt1.size())?lt1.get(i):null;
			Map<String,Object> m2 = (i<lt2.size())?lt2.get(i):null;
			if(m1!=null){
				map.put("stcd1", stcd);
				map.put("stnm1", m1.get("stnm"));
				map.put("stct1", m1.get("stct"));
				map.put("obitmcd1", m1.get("obitmcd"));				
			}else{
				map.put("stcd1", null);
				map.put("stnm1", null);
				map.put("stct1", null);
				map.put("obitmcd1", null);
			}
			if(m2!=null){
				map.put("stcd2", stcd);
				map.put("stnm2", lt2.get(i).get("stnm"));
				map.put("stct2", lt2.get(i).get("stct"));
				map.put("obitmcd2", lt2.get(i).get("obitmcd"));
			}else{
				map.put("stcd2", null);
				map.put("stnm2", null);
				map.put("stct2", null);
				map.put("obitmcd2", null);
			}
			ltm.add(map);
			}
			result.addAll(ltm);
		}
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(result));
		return responser;
	}
	
	public Responser addAllST(){
		String result = "0";
		String gid = params.getParam("gid");
		String year = params.getParam("year");
		BaseDao dao1 = DaoFactory.getDao("dbmreorganize");
		BaseDao dao2 = DaoFactory.getDao("stbase");
		
		//1.先清空
		String sql1 = "delete from sys_reorg_stinfo where yyyy=? and gid=?";
		try {
			dao1.executeSQL(sql1,new Object[]{year,gid});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//2.获取测站管理系统里的列表
		String sql2 = "{call getSTList(?)}";
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		try {
			list2 = dao2.executeQuery(sql2,new Object[]{gid});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//2.5获取沿革，更新测站管理系统里的列表
		String sql3 = "{call getSTYG(?)}";
		List<Map<String, Object>> list3 = new ArrayList<Map<String,Object>>();
		try {
			list3 = dao2.executeQuery(sql3,new Object[]{year});
			for(Map<String,Object> map_yg : list3){
				Map<String,Object> map = getMapFromList(list2,"stcd",map_yg.get("stcd").toString());
				if(map!=null){
					map.put("stct", map_yg.get("sttp"));
					map.put("obitmcd", map_yg.get("obitmcd"));
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//循环list2，将数据插入到stinfo里
		String insert_sql_tpl = 
				"insert into sys_reorg_stinfo(yyyy,stcd,stnm,stct,obitmcd,gid,deleted) values(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''0'');";
		String insert_sql = "";
		for(Map<String,Object> map : list2){
			String _sql = MessageFormat.format(insert_sql_tpl, 
					year,
					map.get("stcd"),
					map.get("stnm"),
					map.get("stct"),
					map.get("obitmcd"),
					gid);
			insert_sql = insert_sql + _sql;
		}
		try {
			insert_sql = insert_sql.replaceAll("'null'", "null");
			dao1.executeSQL(insert_sql);
			result = "1";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(insert_sql);
			result="0";
		}
		//刷新清单概况列表
		new DBReorgImpl().refreshResultSum(gid,year);
		responser.setRtType(TEXT);	
		responser.setRtString(result);
		return responser;
	}
	
	private Map<String,Object> getMapFromList(List<Map<String,Object>> list,String key,Object value){
		for(Map<String,Object> map : list){
			if(map.get(key).equals(value))
				return map;
		}
		return null;
	}
	private List<Map<String,Object>> getListFromList(List<Map<String,Object>> list,String key,Object value){
		List<Map<String, Object>> rlist = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : list){
			Object ob = map.get(key)!=null?map.get(key):"";
			if(ob.equals(value)){
				rlist.add(map);
			}
		}
		Collections.sort(rlist, new ComparatorPinYin());
		return rlist;
	}
}
