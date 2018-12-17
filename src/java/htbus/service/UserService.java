package htbus.service;

import htbus.util.DaoUtil;
import htbus.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.miao.framework.helper.HeaderTokenHelper;
import dbm.helper.DBTransferUtil;

public class UserService {
	static Logger logger = Logger.getLogger(UserService.class);
	
	public Map<String,Object> login(String username,String userpassword){
		Map<String,Object> map  = new HashMap<String,Object>();
		try{
			String sql = "select * from sys_user where username=? and userpassword=?";
			List<Map<String,Object>> result = DaoUtil.sysdao().executeQuery(sql, new Object[]{username,userpassword});
			if(result.size()>0){
				map.put("result", true);
				map.put("message","登陆成功");
				map.put("token", HeaderTokenHelper.createToken(username + "-" + "sysuser"));
				LogUtil.log(username, "login", "登陆成功");
			}else{
				map.put("result", false);
				map.put("message","账号密码错误");
				LogUtil.log(username, "login", "账号密码错误");
			}
		}catch(Exception e){
			map.put("result", false);
			map.put("message","系统故障");
			LogUtil.log(username, "error", username+"登陆操作出现故障："+e.getMessage());
		}
		return map;
	}
	public Map<String,Object> applogin(String username,String userpassword){
		Map<String,Object> map  = new HashMap<String,Object>();
		try{
			String sql = "select * from r_app where account=? and password=?";
			List<Map<String,Object>> result = DaoUtil.sysdao().executeQuery(sql, new Object[]{username,userpassword});
			if(result.size()>0){
				map.put("result", true);
				map.put("message","登陆成功");
				map.put("token", HeaderTokenHelper.createToken(username + "-appuser-"+result.get(0).get("id").toString()));
				LogUtil.log(username, "login", "登陆成功-app");
			}else{
				map.put("result", false);
				map.put("message","账号密码错误");
				LogUtil.log(username, "login", "账号密码错误-app");
			}
		}catch(Exception e){
			map.put("result", false);
			map.put("message","系统故障");
			LogUtil.log(username, "error", username+"app登陆操作出现故障："+e.getMessage());
			logger.error(e.getMessage());
		}
		return map;
	}
	
	public Map<String,Object> getUserInfo(String username){
		String sql = "select * from sys_user where username=?";
		Map<String, Object> map = null;
		try {
			map = DaoUtil.sysdao().executeQueryObject(sql,new Object[]{username});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return map;
	}
	
	public List<Map<String,Object>> getUserMenu(String username){
		Map<String,Object> user = getUserInfo(username);
		List<Map<String,Object>> list = null;
		if(null != user){
			String rolecode = user.get("rolecode").toString();
			String sql = "select b.id,b.pId,b.name,b.path,b.icon from sys_role_menu a left join sys_menu b on a.menuid=b.id where a.rolecode=? order by id";
			try {
				list = DaoUtil.sysdao().executeQuery(sql, new Object[]{rolecode});
				if(list.size()>0) list = _getMenu(list,"0");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return list;
	}
	
	private List<Map<String,Object>> _getMenu(List<Map<String,Object>> list,String id){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : list){
			if(map.get("pId").toString().equals(id)){
				map.put("children", _getMenu(list,map.get("id").toString()));
				result.add(map);
			}
		}
		return result;
	}
	
	/**
	 * 修改密码
	 * @param username
	 * @param oldpassword
	 * @param newpassword
	 * @return 
	 */
	public boolean updatePassword(String username,String oldpassword,String newpassword){
		boolean result = false;
		String sql = "select username from sys_user where username=? and userpassword=?";
		try {
			List<Map<String,Object>> list = DaoUtil.sysdao().executeQuery(sql, new Object[]{username,oldpassword});
			if(list.size()>0){
				sql = "update sys_user set userpassword=? where username=? and userpassword=?";
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{newpassword,username,oldpassword});
			}else{
				result = false;
			}
		} catch (Exception e) {
			result = false;
			logger.error(e.getMessage());
		}
		return result;
	}
	
	public static void main(String[] args){
		boolean map = new UserService().updatePassword("admin","admin2","admin");
		System.out.print(map);
	}
}
