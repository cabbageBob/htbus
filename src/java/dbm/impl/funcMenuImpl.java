package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.endec.MD5;
import cn.miao.framework.factory.DaoFactory;
import dbm.helper.ServerPageHelper;

public class funcMenuImpl {
	public BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public List<Map<String, Object>> getUserInfo(String uname) {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select su.uid,su.uname,su.realname, su.address,su.phone,sg.gname from sys_user su left join sys_group sg on su.group=sg.gid where uname=?";
			rList = sysdao.executeQuery(sql,new Object[]{uname});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	
	public Map<String, Object> editUserInfo (Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			//String psd = params.get("upasscode");
			//psd = MD5.getMD5ofStr(psd, 3);
			sql = "UPDATE sys_user set uname=?,realname=?,address=?,phone=? WHERE uid=?";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("uname"),params.get("realname"),params.get("address"),params.get("phone"),params.get("uid")})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> updateUserPsd (String psd,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			psd = MD5.getMD5ofStr(psd, 3);
			sql = "UPDATE sys_user set upasscode=? WHERE uname=?";
			data = sysdao.executeSQL(sql,
					new Object[]{psd,uname})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> checkUserPsd (String uname,String psd) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		Map<String, Object> rtMap2 = null;
		String data = "no";
		String sql = "";
		try{
			psd = MD5.getMD5ofStr(psd, 3);
			sql = "select * from sys_user where uname=? and upasscode=?";
			rtMap2 = sysdao.executeQueryObject(sql, new Object[] {uname, psd });
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(null!=rtMap2){data = "yes";}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public List<Map<String, Object>> getWarnInfo() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select * from warn_set";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	
	public Map<String, Object> editWarnInfo (Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			//String psd = params.get("upasscode");
			//psd = MD5.getMD5ofStr(psd, 3);
			sql = "UPDATE warn_set set cpu=?,memory=?,con_num=?,con_tm=?";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("cpu"),params.get("memory"),params.get("con_num"),params.get("con_tm")})?"Success":"Defeat";
			Monitor.refreshAlert();
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	/*public List<Map<String, Object>> getLoginLog() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select a.id,a.login_tm,a.ip,b.uname from login_log a left join sys_user b on a.uid=b.uid order by a.login_tm desc;";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}*/
	
	public Map<String, Object> getLoginLog(Map<String, String> map) {
		String sql = "" ;
		String tm1 = map.get("tm1");
		String tm2 = map.get("tm2");
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		try{
			sql = "select a.id,a.login_tm,a.ip,b.uname from login_log a left join sys_user b on a.uid=b.uid where a.login_tm>=? and a.login_tm<=? order by a.login_tm desc";
			rList = sysdao.executeQuery(sql,new Object[]{tm1,tm2});
			rtMap.put("cnt", rList.size());
			rList = ServerPageHelper.getListFromPage( map.get("page_flag"),
					map.get("page_cnt"),map.get("page_index"),
					map.get("orderby_field"),map.get("orderby_order"),
					rList);
			rtMap.put("data", rList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	
	public Map<String, Object> addLoginLog(Map<String, String> params,String ip) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		String sql1 = "";
		try{
			sql1 = "update sys_user set curtime=? where uid=?";
			sysdao.executeSQL(sql1,new Object[]{params.get("time"),params.get("uid")});
			sql = "insert into login_log(uid,login_tm,ip) values(?,?,?);";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("uid"),params.get("time"),ip})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
}
