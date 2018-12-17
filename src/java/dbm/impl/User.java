package dbm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.endec.MD5;
import cn.miao.framework.factory.DaoFactory;
import sun.management.counter.Variability;

public class User {
	public BaseDao sysdao = DaoFactory.getDao("dbmdb");
	public List<Map<String, Object>> getGroup() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select distinct * from sys_group group by gcode order by value asc";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getGroup2() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select * from sys_group where value<>'0'";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getGroup3() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select * from sys_group where value<>'3'";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getRole() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select * from sys_role";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getData() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select * from dbsource";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getUser() {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select su.*,sg.gname,sg.value,sr.rname from sys_user su left join sys_group sg "
					+ "on su.group=sg.gid left join sys_role sr on sr.rid=su.utype order by enabled desc;";
			rList = sysdao.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<Map<String, Object>> getLoginUser(String uname) {
		String sql = "" ;
		List<Map<String, Object>> rList = new ArrayList<Map<String, Object>>();
		try{
			sql = "select uid,realname from sys_user where uname=?";
			rList = sysdao.executeQuery(sql,new Object[]{uname});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public Map<String, Object> addUser(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			String uuid = UUID.randomUUID().toString();
			String psd = params.get("upasscode");
			psd = MD5.getMD5ofStr(psd, 3);
			sql = "insert into sys_user(uid,uname,upasscode,realname,`group`,utype,createtime,enabled,address,phone,`status`) values(?,?,?,?,?,?,?,1,?,?,?);";
			data = sysdao.executeSQL(sql,
					new Object[]{uuid,params.get("uname"),psd,params.get("realname"),params.get("group"),params.get("utype")
						,params.get("createtime"),params.get("address"),params.get("phone"),params.get("status")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> addPer(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>() ;
		String sql = "";
		String data ="Defeat" ;
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			sql = "select name from sys_menu";
			list = sysdao.executeQuery(sql);
			for(Map<String,Object> map :list){
				String enable = "0";
				String uuid = UUID.randomUUID().toString();
				if(map.get("name").equals("首页")){
					enable = "1";
				}
			    sql = "insert into sys_permission(pid,rid,description,enable) values(?,?,?,?)";
			    data = sysdao.executeSQL(sql,
					new Object[]{uuid,params.get("uname"),map.get("name"),enable})?"Success":"Defeat";
				
			}
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> delUser(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try {
			sql = "delete from sys_user where uid =? ";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("uid")})?"Success":"Defeat";
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	public Map<String, Object> freezeUser (Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			sql = "UPDATE sys_user set status=? WHERE uid=?";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("status"),params.get("uid")})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	public Map<String, Object> editUser (Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			String psd = params.get("upasscode");
			sql = "UPDATE sys_user set upasscode=?,uname=?,realname=?,`group`=?,utype=?,address=?,phone=?,status=? WHERE uid=?";
			data = sysdao.executeSQL(sql,
					new Object[]{psd,params.get("uname"),params.get("realname"),params.get("group"),params.get("utype"),params.get("address"),params.get("phone"),params.get("status"),params.get("uid")})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	public Map<String, Object> editUser2 (Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "Defeat";
		String sql = "";
		try{
			String psd = params.get("upasscode");
			psd = MD5.getMD5ofStr(psd, 3);
			sql = "UPDATE sys_user set upasscode=?,uname=?,realname=?,`group`=?,utype=?,address=?,phone=?,status=? WHERE uid=?";
			data = sysdao.executeSQL(sql,
					new Object[]{psd,params.get("uname"),params.get("realname"),params.get("group"),params.get("utype"),params.get("address"),params.get("phone"),params.get("status"),params.get("uid")})?"Success":"Defeat";
		}catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	public Map<String, Object> updateTime(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data ="NO";
		try {
			sql = "update sys_user set curtime=? "
					+ " where uname =? ";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("curtime"),params.get("uname")})?"YES":"NO";
			rtMap.put("DATA", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> giveUser(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data ="Defeat" ;
		String[] aaa = params.get("description").split(",");
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			sql="update sys_user set permenu=? where uname=?;";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("description"),params.get("uname")})?"Success":"Defeat";
			sql = "update sys_permission set enable='0' where rid=?;";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("uname")})?"Success":"Defeat";
			for (int i=0,len=aaa.length;i<len;i++){
				sql = "update sys_permission set enable=1 where rid=? and description=?;";
				data = sysdao.executeSQL(sql,
					new Object[]{params.get("uname"),aaa[i]})?"Success":"Defeat";
			};
			sql="update sys_permission set enable=1 where rid=? and description='首页'";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("uname")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public Map<String, Object> giveUser2(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data ="Defeat" ;
		String[] aaa = params.get("description").split(",");
		try{
			sql="update sys_user set permission=? where uname=?";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("description"),params.get("uname")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public List<String>giveIp(String uname) {
		//String[] aaa = params.get("description").split(",");
		String[] aaa = new String[]{};
		List<String> rList = new ArrayList<String>();
		Map<String, Object> rtMap = new HashMap<String, Object>();
		try{
			String sql = "select bip from sys_user where uname=?";
			rtMap = sysdao.executeQueryObject(sql,new Object[]{uname});
			aaa=rtMap.get("bip").toString().split(",");
			for(String a: aaa){
				rList.add(a);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public static void main(String[] args) {
		new User().giveIp("陈凯琦");
		System.exit(0);
	}
	public Map<String, Object> givelimit(Map<String, String> params) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String sql = "";
		String data ="Defeat" ;
		try{
			sql="update sys_user set bip=? where uname=?";
			data = sysdao.executeSQL(sql,
					new Object[]{params.get("bip"),params.get("uname")})?"Success":"Defeat";
			rtMap.put("result", data);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}
	public List<String>getPermission(String uname) {
		//String[] aaa = params.get("description").split(",");
		String[] aaa = new String[]{};
		List<String> rList = new ArrayList<String>();
		Map<String, Object> rtMap = new HashMap<String, Object>();
		try{
			String sql = "select permenu from sys_user where uname=?";
			rtMap = sysdao.executeQueryObject(sql,new Object[]{uname});
			aaa=rtMap.get("permenu").toString().split(",");
			for(String a: aaa){
				rList.add(a);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
	public List<String>getPermission2(String uname) {
		//String[] aaa = params.get("description").split(",");
		String[] aaa = new String[]{};
		List<String> rList = new ArrayList<String>();
		Map<String, Object> rtMap = new HashMap<String, Object>();
		try{
			String sql = "select permission from sys_user where uname=?";
			rtMap = sysdao.executeQueryObject(sql,new Object[]{uname});
			aaa=rtMap.get("permission").toString().split(",");
			for(String a: aaa){
				rList.add(a);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rList;
	}
}
	
