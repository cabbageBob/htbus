package htbus.entity;

public class Organization {
	/**2017年11月26日下午2:59:19
	 *
	 * @author Jokki
	 *  
	 */
	
	private String id;
	
	private String pid;
	
	private String orgname;
	
	private String guid;
	
	private String ord;
	
	public Organization(){
		super ();
	}
	
	public Organization(String id, String pid, String orgname, String guid){
		this.id = id;
		this.pid = pid;
		this.orgname = orgname;
		this.guid = guid;
	}
	
	public String getId(){
		return id;
	}
	
	public String getPid(){
		return pid;
	}
	
	public String getOrgname(){
		return orgname;
	}
	
	public String getGuid(){
		return guid;
	}
	
	public String getOrd(){
		return ord;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setPid(String pid){
		this.pid = pid;
	}
	
	public void setOrgname(String orgname){
		this.orgname = orgname;
	}
	
	public void setGuid(String guid){
		this.guid = guid;
	}
	
	public void setOrd(String ord){
		this.ord = ord;
	}
}
	
	
