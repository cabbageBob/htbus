package dbm.util;

import java.util.Map;

import dbm.impl.DataSource;
import dbm.impl.Monitor;

public class IpUtil {
	//用户名ip的限制，本地的ip统一为127.0.0.1
	public static Boolean ipAllowed(String ip,String uname){
		if(ip.equals("localhost")||ip.equals("0:0:0:0:0:0:0:1")){
			ip = "127.0.0.1";
		}
		Boolean result = false;
		String sql = "";
		try{
			int a = ip.indexOf(".");
			int b = ip.indexOf(".",a+1);
			String ip1 = ip.substring(0,b);
			sql = "select bip from sys_user where uname=?";
			Map<String,Object> map = DataSource.sysdao().executeQueryObject(sql, new Object[]{uname});
			String ipstr = map.get("bip")==null?"":map.get("bip").toString();
			if(ipstr.contains(ip)||ipstr==null||ipstr.equals("")){
				result = true;
			}else{
				if(ipstr.contains(ip1)){
					int i = 0;
					while(ipstr.indexOf("-", i)>=0){
						int c = ipstr.indexOf("-",i);
						int d = ipstr.lastIndexOf(",",c);
						int e = ipstr.indexOf(",", c);
						String ip2 = ipstr.substring(d+1,c);
						String ip3 = e>=0?ipstr.substring(c+1, e):ipstr.substring(c+1);
						if((Monitor.ipToLong(ip)>=Monitor.ipToLong(ip2))&&(Monitor.ipToLong(ip)<=Monitor.ipToLong(ip3))){
							result = true;
						}
						i = c+1;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void main(String[] args) {
		IpUtil.ipAllowed("192.168.10.5","admin");
		System.exit(0);
	}
}
