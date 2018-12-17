package htbus.entity;

public class FieldEntity {
	/**2017年11月16日下午8:38:34
	 *
	 * @author Jokki
	 *  
	 */
	public FieldEntity() {
		super();
	}
	public FieldEntity(String field, String fieldDescription, String instanceId,
			String dbname, String dbDescription, String tbname, String tbDescription){
		super();
		this.field = field;
		this.fieldDescription = fieldDescription;
		this.instanceId = instanceId;
		//this.instanceName = instanceName;
		this.dbname = dbname;
		this.dbDescription = dbDescription;
		this.tbname = tbname;
		this.tbDescription = tbDescription;
	}
	private String field;
	
	private String fieldDescription;
	
	private String instanceId;
	
	//private String instanceName;
	
	private String dbname;
	
	private String dbDescription;
	
	private String tbname;
	
	private String tbDescription;
	
	public String getField() {
        return field;
    }

    public String getDbDescription() {
        return dbDescription;
    }

    public String getDbname() {
        return dbname;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public String getInstanceId() {
        return instanceId;
    }

    /*public String getInstanceName() {
        return instanceName;
    }*/

    public String getTbname() {
        return tbname;
    }

    public String getTbDescription() {
        return tbDescription;
    }
    public void setField(String field) {
        this.field = field;
    }

    public void setDbDescription(String dbDescription) {
        this.dbDescription = dbDescription;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /*public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }*/

    public void setTbDescription(String tbDescription) {
        this.tbDescription = tbDescription;
    }

    public void setTbname(String tbname) {
        this.tbname = tbname;
    }
}
