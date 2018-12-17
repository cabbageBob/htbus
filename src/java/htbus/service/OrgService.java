package htbus.service;

import htbus.entity.Organization;
import htbus.entity.User;
import htbus.util.DaoUtil;
import htbus.util.ListUtil;
import htbus.util.WebServiceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class OrgService {
	static Logger logger = Logger.getLogger(UserService.class);
	/*
	 * 获取行政组织机构
	 */
	public List<Map<String,Object>> getXingzhengOrg(){
		String sql = "select * from org_tree where left(id,2)='10' order by ord";
		List<Map<String, Object>> list = null;
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		list = _getTree(list,"00");
		return list;
	}
	
	/*
	 * 获取党组织机构 
	 */
	public List<Map<String,Object>> getDangOrg(){
		String sql = "select * from org_tree where left(id,2)='11' order by ord";
		List<Map<String, Object>> list=  null;
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		list = _getTree(list,"00");
		return list;
	}
	
	/*
	 * 获取某组织下所有人员
	 */
	public List<Map<String,Object>> getPersonListByOrgid(String orgid){
		String sql = "select distinct b.*,c.rolename,d.postname,(select GROUP_CONCAT(org_id) from org_mapping c where c.uid=a.uid) org from org_mapping a left join org_person b on a.uid=b.uid left join org_role c on b.roleid=c.roleid left join org_post d on b.postid=d.postid where left(a.org_id,LENGTH(?))=?";
		List<Map<String,Object>> list = null;
		try {
			list = DaoUtil.sysdao().executeQuery(sql, new Object[]{orgid,orgid});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return list;
	}
	
	private List<Map<String,Object>> _getTree(List<Map<String,Object>> list,String id){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : list){
			if(map.get("pId").toString().equals(id)){
				List<Map<String,Object>> children = _getTree(list,map.get("id").toString());
				map.put("children", children.size()>0 ? children : null);
				result.add(map);
			}
		}
		return result;
	}
	
	/*
	 * 获取全部组织机构树 
	 */
	public List<Map<String,Object>> getOrgTree(){
		String sql = "select * from org_tree order by ord";
		List<Map<String, Object>> list=  null;
		try {
			list = DaoUtil.sysdao().executeQuery(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		list = _getTree(list,"root");
		return list;
	}
	
	/**
	 * 获取新建组织机构的ID
	 * @param pid 父节点id
	 * @return
	 */
	public String getNewOrgID(String pid){
		String sql = "select id from org_tree where pId=? order by ord desc limit 1";
		String result = null;
		try {
			Map<String,Object> map = DaoUtil.sysdao().executeQueryObject(sql, new Object[]{pid});
			if(map==null){
				result = pid+"-1";
			}else{
				String[] arr = map.get("id").toString().split("-");
				int id = Integer.parseInt(arr[arr.length-1]) + 1;
				arr[arr.length-1] = String.valueOf(id);
				result=ListUtil.join("-", arr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	public String getNewOrgOrd(){
		String sql = "select max(ord)+1 ord from org_tree";
		String result = null;
		try {
			result = DaoUtil.sysdao().executeQueryObject(sql).get("ord").toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 在某父节点下新建组织机构
	 * @param id
	 * @param pid
	 * @param orgname
	 * @return
	 */
	public boolean addOrg(String pid,String orgname){
		boolean result = false;
		String newid = getNewOrgID(pid);
		String maxord = getNewOrgOrd();
		if(newid != null){
			String sql = "insert into org_tree(id,pId,orgname,ord) values(?,?,?,?)";
			try {
				result = DaoUtil.sysdao().executeSQL(sql, new Object[]{newid,pid,orgname,maxord});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * 修改组织机构名称
	 * @param id
	 * @param pid
	 * @param orgname
	 * @return
	 */
	public boolean updateOrg(String id,String orgname){
		boolean result = false;
		try {
			String sql = "update org_tree set orgname=? where id = ?";
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{orgname,id});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 删除组织机构，请在客户端确认好该节点下没有人员
	 * @param id
	 * @return
	 */
	public boolean delOrg(String id){
		boolean result = false;
		try {
			String sql = "delete from org_tree where id = ?";
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{id});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 修改组织机构排序（切换两个机构的排序号）
	 * @param id1 第一个机构的id
	 * @param id2 第二个机构的id
	 * @return
	 */
	public boolean updateOrgOrd(String id1,String id2){
		boolean result = false;
		try {
			String ord1 = DaoUtil.sysdao().executeQueryObject("select ord from org_tree where id=?",new Object[]{id1}).get("ord").toString();
			String ord2 = DaoUtil.sysdao().executeQueryObject("select ord from org_tree where id=?",new Object[]{id2}).get("ord").toString();
			DaoUtil.sysdao().executeSQL("update org_tree set ord=? where id=?", new Object[]{ord2,id1});
			DaoUtil.sysdao().executeSQL("update org_tree set ord=? where id=?", new Object[]{ord1,id2});
			result = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 检查人员ID是否重复
	 * @param uid
	 * @return
	 */
	public boolean checkPersonUid(String uid){
		boolean result = false;
		String sql = "select uid from org_person where uid=?";
		try {
			List<Map<String,Object>> list = DaoUtil.sysdao().executeQuery(sql, new Object[]{uid});
			if(list.size()==0){
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 添加组织机构人员
	 * @param uid 用户ID
	 * @param name 用户中文姓名
	 * @param mobile 手机号码
	 * @param tel 办公电话
	 * @param smobile 手机短号
	 * @param mail 邮箱
	 * @param sex 性别
	 * @param photo 头像url
	 * @param orgids 所属组织机构编码连接字符串（用逗号间隔）
	 * @param roleid
	 * @param postid
	 * @return
	 */
	public boolean addPerson(String uid,String name,String mobile,String tel,String smobile,String mail,String sex,String photo,String orgids,String roleid,String postid){
		boolean result = false;
		String sql = "insert into org_person(uid,name,mobile,tel,smobile,mail,sex,photo,roleid,postid) values(?,?,?,?,?,?,?,?,?,?)";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{uid,name,mobile,tel,smobile,mail,sex,photo,roleid,postid});
			String[] arr = orgids.split(",");
			removeMapping(uid);
			for(String org_id : arr){
				orgPersonMapping(uid,org_id);
			}	
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 取消用户的所有行政组织机构映射
	 * @param uid
	 * @return
	 */
	public boolean removeMapping(String uid){
		boolean result = false;
		String sql1="delete from org_mapping where uid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql1, new Object[]{uid});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	/**
	 * 修改或添加用户组织机构映射关系
	 * @param uid
	 * @param org_id
	 * @return
	 */
	public boolean orgPersonMapping(String uid,String org_id){
		boolean result = false;
		String sql2 ="insert into org_mapping(uid,org_id) values(?,?)";
		try {
			result = DaoUtil.sysdao().executeSQL(sql2, new Object[]{uid,org_id});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 修改组织机构人员信息
	 * @param uid
	 * @param name
	 * @param mobile
	 * @param tel
	 * @param smobile
	 * @param mail
	 * @param sex
	 * @param photo
	 * @param orgids 所属组织机构编码连接字符串（用逗号间隔）
	 * @param roleid
	 * @param postid
	 * @return
	 */
	public boolean updatePerson(String uid,String name,String mobile,String tel,String smobile,String mail,String sex,String photo,String orgids,String roleid,String postid){
		boolean result = false;
		String sql = "update org_person set name=?,mobile=?,tel=?,smobile=?,mail=?,sex=?,photo=?,roleid=?,postid=? where uid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{name,mobile,tel,smobile,mail,sex,photo,roleid,postid,uid});
			String[] arr = orgids.split(",");
			removeMapping(uid);
			for(String org_id : arr){
				orgPersonMapping(uid,org_id);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 删除人员删除后不可恢复
	 * @param uid
	 * @return
	 */
	public boolean delPerson(String uid){
		boolean result = false;
		String sql = "delete from org_person where uid = ?";
		String sql2 = "delete from org_mapping where uid=?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{uid});
			result = DaoUtil.sysdao().executeSQL(sql2, new Object[]{uid});
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取某机构下的所有岗位
	 * @param orgid
	 * @return
	 */
	public List<Map<String,Object>> getPostList(String orgid){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select postid,postname,orgid from org_post where orgid = ?";
		try {
			list =  DaoUtil.sysdao().executeQuery(sql, new Object[]{orgid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 添加岗位
	 * @param orgid
	 * @param postname
	 * @return
	 */
	public Map<String,Object> addPost(String orgid,String postname){
		Map<String,Object> result = null;
		try {
			result = new HashMap<String,Object>();
			String sql = "insert into org_post(postid,orgid,postname) select ifnull(max(postid)+1,1),?,? from org_post";
			boolean b = DaoUtil.sysdao().executeSQL(sql, new Object[]{orgid,postname});
			result.put("result", b);
			
			String sql2 = "select postid,orgid,postname from org_post where orgid = ? and postname = ? limit 1";
			Map<String,Object> data = DaoUtil.sysdao().executeQueryObject(sql2, new Object[]{orgid,postname});
			result.put("data", data);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 修改岗位
	 * @param postid
	 * @param postname
	 * @return
	 */
	public boolean updatePost(String postid,String postname){
		boolean result = false;
		String sql = "update org_post set postname=? where postid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{postname,postid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 删除岗位
	 * @param postid
	 * @return
	 */
	public boolean delPost(String postid){
		boolean result = false;
		String sql = "delete from org_post where postid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{postid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取角色列表
	 * @return
	 */
	public List<Map<String,Object>> getRoleList(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = DaoUtil.sysdao().queryAll("org_role");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 添加角色
	 * @param rolename
	 * @return
	 */
	public Map<String,Object> addRole(String rolename){
		Map<String,Object> result = null;
		try {
			result = new HashMap<String,Object>();
			String sql = "insert into org_role(roleid,rolename) select ifnull(max(roleid)+1,1),? from org_role";
			boolean b = DaoUtil.sysdao().executeSQL(sql, new Object[]{rolename});
			result.put("result", b);
			
			String sql2 = "select roleid,rolename from org_role where rolename = ? limit 1";
			Map<String,Object> m = DaoUtil.sysdao().executeQueryObject(sql2, new Object[]{rolename});
			result.put("data", m);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 修改角色
	 * @param roleid
	 * @param rolename
	 * @return
	 */
	public boolean updateRole(String roleid,String rolename){
		boolean result =  false;
		String sql = "update org_role set rolename=? where roleid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{rolename,roleid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 删除角色
	 * @param roleid
	 * @return
	 */
	public boolean delRole(String roleid){
		boolean result = false;
		String sql = "delete from org_role where roleid = ?";
		try {
			result = DaoUtil.sysdao().executeSQL(sql, new Object[]{roleid});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	
	private static List<Organization> orgs = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		/*Map<String, String> params = new HashMap<>();
		params.put("oucode", "01");
		String result = WebServiceHelper.getInstance("http://bpm.qgj.cn/api/bpmapi.asmx").getResultByMethod("GetData",params);*/
		//Document document = DocumentHelper.parseText(result);
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File("E:\\xml2.xml"));
		Element root = document.getRootElement();
		Iterator<Element> iterator = root.elementIterator(); 
		new OrgService().listNodes(root, "0-2");
		//System.out.println(JSON.toJSONString(orgs));
		String sql = "replace into org_person (uid,name,mobile,tel,smobile,mail,sex,photo) values (?,?,?,?,?,?,?,?)";
		/*List<Object[]> params = new ArrayList<>();
		List<User> users = new OrgService().GetUsers("01", "true");
		for(User user : users){
			params.add(new Object[]{user.getUid(), user.getName(), user.getMobile(), user.getTel(), user.getSmobile(),
					user.getMail(), user.getSex(), user.getPhoto()});
		}
		DaoUtil.sysdao().executeSQLBatch(sql, params);*/
		sql = "replace into org_mapping values(?,?)";
		List<Object[]> params2 = new ArrayList<>();
		for(Organization org : orgs){
			List<User> users2 = new OrgService().GetUsers(org.getGuid(), "true");
			for(User user : users2){
				params2.add(new Object[]{user.getUid(), org.getId()});
			}
		}
		DaoUtil.sysdao().executeSQLBatch(sql, params2);
		
	}
	
	public List<User> GetUsers(String oucode, String deep) throws DocumentException{
		List<User> users = new ArrayList<>();
		Map<String, String> params = new HashMap<>();
		params.put("oucode", oucode);
		params.put("deep", deep);
		String result = WebServiceHelper.getInstance("http://bpm.qgj.cn/api/bpmapi.asmx").getResultByMethod("GetUsers",params);
		Document document = DocumentHelper.parseText(result);
		Element root = document.getRootElement();
		for(Element e : root.element("GetUsersResult").element("DocumentElement").elements("User")){
			User user = new User();
			user.setUid(e.elementText("samaccountname"));
			user.setName(e.elementText("displayname"));
			user.setMobile(e.elementText("mobile"));
			user.setTel(e.elementText("telephonenumber"));
			user.setSmobile(e.elementText("pager"));
			user.setMail(e.elementText("mail"));
			user.setIpphone(e.elementText("ipphone"));
			user.setSex(e.elementText("description"));
			user.setPhoto(e.elementText("photo"));
			users.add(user);
		}
		return users;
	}
	
	public void listNodes(Element node, String pid){
		//List<Organization> orgs = new ArrayList<>();
		 //首先获取当前节点的所有属性节点  
        /*List<Attribute> list = node.attributes(); 
        //遍历属性节点 
        for(Attribute attribute : list){  
            System.out.println("属性"+attribute.getName() +":" + attribute.getValue());  
        }*/
        if("ORG".equals(node.getName())){
        	
        	//同时迭代当前节点下面的所有子节点  
            //使用递归  
            Iterator<Element> iterator = node.elementIterator();
            Organization org = new Organization();
            org.setPid(pid);
            String id = "";
            while(iterator.hasNext()){  
                Element e = iterator.next();
                if("OUName".equals(e.getName())){
                	org.setOrgname(e.getTextTrim());
                }else if("Code".equals(e.getName())){
                	org.setGuid(e.getTextTrim());
                }else if("OUID".equals(e.getName())){
                	String ouid = e.getTextTrim();
                	org.setOrd(ouid);;
                	id = pid + "-" + ouid;
                	org.setId(id);
                }else if("ORG".equals(e.getName())){
                	listNodes(e, id);
                }
            }
            orgs.add(org);
        }else{
        	Iterator<Element> iterator = node.elementIterator();
        	while(iterator.hasNext()){
        		listNodes(iterator.next(), pid);
        	}
        }
        
        //如果当前节点内容不为空，则输出  
/*        if(!(node.getTextTrim().equals(""))){  
             System.out.println( node.getName() + "：" + node.getText());    
        } */ 
        
	}
	
}
