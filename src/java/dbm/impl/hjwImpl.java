package dbm.impl;

import java.util.HashMap;
import java.util.Map;

public class hjwImpl {
	public Map<String, Object> addTreeGroup(String name,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "insert into dbgroup(group_name,uname,ordno,dbsource_ids"
						+ " ) values (?,?,?,?)";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{name,uname,128,""});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> updateTreeGroup(String ids,String id,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "update dbgroup set dbsource_ids=? where id=? and uname=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{ids,id,uname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> updateGroupName(String name,String id,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "update dbgroup set group_name=? where id=? and uname=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{name,id,uname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> deleteTreeGroup(String id,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "delete from dbgroup where id=? and uname=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{id,uname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> orderTree(String ordno,String id,String uname) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "update dbgroup set ordno=? where id=? and uname=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{ordno,id,uname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
	
	public Map<String, Object> addMAccess(String access_tm,String uname,String name) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		String data = "success";
		String sql = "insert into module_access(uname,access_tm,module_name"
						+ " ) values (?,?,?)";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{uname,access_tm,name});
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("result", data);
		return rtMap;
	}
}
