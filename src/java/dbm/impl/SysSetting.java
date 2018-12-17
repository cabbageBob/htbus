package dbm.impl;

import java.util.Map;

/**
 * 系统设置，包括task文件夹路径、creator的api地址
 * @author Clay
 *
 */
public class SysSetting {
	public static Map<String,Object> setting = null;
	
	public static void initSetting() {
		String sql = "select * from syssetting";
		try {
			setting = DataSource.sysdao().executeQueryObject(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String,Object> getSetting(){
		if(setting == null){
			initSetting();
		}
		return setting;
	}
}
