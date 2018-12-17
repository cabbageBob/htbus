package dbm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Platform {
	public List<Map<String, Object>> getMenus(String uname)  {
		String sql = "select sys_menu.name,sys_menu.link,sys_menu.component,sys_menu.icon,"
				+ "sys_menu.type,sys_menu.id,sys_menu.pid from sys_user left join sys_permission "
				+ "on sys_permission.rid=sys_user.uname left join sys_menu on "
				+ "sys_permission.description=sys_menu.name	where sys_user.uname=? "
				+ "and sys_permission.enable=1 and sys_menu.enable=1 order by id asc";
		/*String sql="select sys_menu.name,sys_menu.link,sys_menu.component,sys_menu.icon,sys_menu.type,sys_menu.id,sys_menu.pid from sys_user "
		+ "left join sys_role on sys_user.utype=sys_role.rid "
		+ "left join sys_permission on sys_permission.rid=sys_role.rid "
		+ "left join sys_menu on sys_permission.description=sys_menu.name "
		+ "where sys_user.uname=? and sys_permission.enable=1 order by id asc";*/
		List<Map<String, Object>> rtlist = new ArrayList<Map<String,Object>>();
		try {
			rtlist = DataSource.sysdao().executeQuery(sql,new Object[]{uname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtlist;
	}
}
