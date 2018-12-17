package htbus.service;

import htbus.util.ConfigUtil;
import htbus.util.DaoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

public class ResourceService {
	static Logger logger = Logger.getLogger(UserService.class);
	
	/**
	 * 获取开发商列表
	 * @return
	 */
	public List<Map<String,Object>> getCompanyList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DaoUtil.sysdao().executeQuery("select * from r_company");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return list;
	}
	
	/**
	 * 添加开发商
	 * @param companyname
	 * @param keeper
	 * @param mobile
	 * @return
	 */
	public boolean addCompany(String companyname,String keeper,String mobile){
		boolean result = false;
		String sql = "insert into r_company(id,companyname,regtm,keeper,mobile) select ifnull(max(id)+1,1),?,now(),?,? from r_company";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{companyname,keeper,mobile});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 修改开发商
	 * @param id
	 * @param companyname
	 * @param keeper
	 * @param mobile
	 * @return
	 */
	public boolean updateCompany(String id,String companyname,String keeper,String mobile){
		boolean result = false;
		String sql = "update r_company set companyname=?,keeper=?,mobile=? where id=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{companyname,keeper,mobile,id});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 删除开发商
	 * @param id
	 * @return
	 */
	public boolean deleteCompany(String id){
		boolean result = false;
		String sql = "delete from r_company where id = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{id});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取应用系统列表
	 * @return
	 */
	public List<Map<String,Object>> getAppList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.*,b.companyname from r_app a left join r_company b on a.companyid = b.id";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 添加app
	 * @param appname
	 * @param companyid
	 * @param url
	 * @param ip
	 * @param domain
	 * @param account
	 * @param password
	 * @return
	 */
	public boolean addApp(String appname,String companyid,String url,String ip,String domain,String account,String password, String outip, String remark){
		boolean result = false;
		String sql = "insert into r_app(id,appname,companyid,url,ip,domain,account,password,token,outip, remark) select ifnull(max(id)+1,1),?,?,?,?,?,?,?,?,?,? from r_app";
		try {
			String token = UUID.randomUUID().toString().replace("-", "");
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{appname,companyid,url,ip,domain,account,password,token,outip, remark});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 修改app
	 * @param id
	 * @param appname
	 * @param companyid
	 * @param url
	 * @param ip
	 * @param domain
	 * @param account
	 * @param password
	 * @return
	 */
	public boolean updateApp(String id,String appname,String companyid,String url,String ip,String domain,String account,String password, String remark){
		boolean result = false;
		String sql = "update r_app set appname=?,companyid=?,url=?,ip=?,domain=?,account=?,password=?,remark=? where id=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{appname,companyid,url,ip,domain,account,password,remark,id});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 删除app
	 * @param id
	 * @return
	 */
	public boolean deleteApp(String id){
		boolean result = false;
		String sql = "delete from r_app where id = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{id});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取某app详细信息
	 * @param appaccount
	 * @return
	 */
	public Map<String,Object> currentAppInfo(String appaccount){
		Map<String,Object> result = new HashMap<String,Object>();
		String sql = "select a.id appid,a.appname,a.companyid,a.url,a.ip,a.domain,a.outip,a.token,b.companyname from r_app a left join r_company b on a.companyid = b.id where account = ?";
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql, new Object[]{appaccount});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
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
	public boolean updateAppPassword(String account,String oldpassword,String newpassword){
		boolean result = false;
		String sql = "select account from r_app where account=? and password=?";
		try {
			List<Map<String,Object>> list = DaoUtil.sysdao().executeQuery(sql, new Object[]{account,oldpassword});
			if(list.size()>0){
				sql = "update r_app set password=? where account=? and password=?";
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{newpassword,account,oldpassword});
			}else{
				result = false;
			}
		} catch (Exception e) {
			result = false;
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取所有文档列表
	 * @return
	 */
	public List<Map<String,Object>> getAllFileList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.*,b.appname from r_file a left join r_app b on a.appid=b.id";
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
			for(Map<String,Object> map : list){
				if(null != map.get("filepath")){
					map.put("filepath", ConfigUtil.config("doc_download_host")+map.get("filepath").toString());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取当前登陆的app的文档列表
	 * @param appid
	 * @return
	 */
	public List<Map<String,Object>> getAppFileList(String appid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.*,b.appname from r_file a left join r_app b on a.appid=b.id where a.appid = ?";
		try {
			list = DaoUtil.sysdao().executeQuery(sql,new Object[]{appid});
			for(Map<String,Object> map : list){
				if(null != map.get("filepath")){
					map.put("filepath", ConfigUtil.config("doc_download_host")+map.get("filepath").toString());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 删除文档
	 * @param fileid
	 * @return
	 */
	public boolean deleteAppFile(String fileid){
		boolean result = false;
		String sql = "delete from r_file where fileid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{fileid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 添加文档
	 * @param appid
	 * @param filepath
	 * @param filename
	 * @param tag
	 * @return
	 */
	public boolean addAppFile(String appid,String filepath,String filename,String tag, String remark){
		boolean result = false;
		String sql = "insert into r_file(appid,filename,filepath,tag,tm, remark) values(?,?,?,?,now(),?)";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{appid,filename,filepath,tag,remark});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	

	
	/**
	 * 已作废，合并到了staServiceResourceInfo!service
	 * 统计应用系统服务接口数量信息
	 * @return
	 
	public List<Map<String,Object>> staAppServiceCount(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select a.id appid,a.appname,(select count(1) from service b where b.appid=a.id and b.status='publish') cnt from r_app a";
		try {
			list  = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}*/
	
	public static void main(String[] args){
		new ResourceService().addAppFile("4","/doc/数据服务管理平台设计说明书.doc","数据服务管理平台设计说明书","设计", null);
	}
}
