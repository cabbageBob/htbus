package dbm.impl;

import java.util.HashMap;
import java.util.Map;


import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.endec.MD5;
import cn.miao.framework.factory.DaoFactory;
import dbm.service.SSOService;

public class SSOServiceImpl implements SSOService {
	BaseDao dao = DaoFactory.getDao("dbmdb");

	public Map<String, Object> login(String userName, String password) {
		String passcode2 = MD5.getMD5ofStr(password);
		String query = "select * from sys_user t where t.uname=? and upasscode=? and enabled=1";
		password = MD5.getMD5ofStr(password, 3);
		// logger.info("userName:"+userName+" // password:" + password);
		Map<String, Object> rtMap = null;
		try {
			rtMap = dao.executeQueryObject(query, new Object[] {
						userName, password });
			if (null == rtMap) {
				String query2 = "select * from sys_user t where t.uname=? and upasscode2=? and enabled=1";
				rtMap = dao.executeQueryObject(query2, new Object[] { userName,
						passcode2 });
				if (null == rtMap) {
					return rtMap;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.remove("upasscode");
		rtMap.remove("upasscode2");
		rtMap.remove("createtime");
		rtMap.remove("departmentid");
		rtMap.remove("enabled");
		return rtMap;
	}
	public Boolean userIsExist(String username) {
		Boolean exist = false ;
		String sql = "";
		Map<String, Object> rMap = new HashMap<String, Object>();
		try{
			sql = "select * from sys_user where uname=? and enabled=1 ";
			rMap = dao.executeQueryObject(sql,new Object[] { username});
			if( rMap == null){
				return exist ;
			}else{
				exist = true ;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return exist;
	}
	
	
	

}
