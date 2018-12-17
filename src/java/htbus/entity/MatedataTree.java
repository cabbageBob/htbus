package htbus.entity;

import java.util.List;

public class MatedataTree {
	/**2018年7月20日上午8:36:55
	 *
	 * @author Jokki
	 *  
	 */
	
	private String code;

    private String name;

    private String parent_code;

    private String type;

    private Double ord;

    private List<MatedataTree> child;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Double getOrd() {
        return ord;
    }
    public String getParent_code() {
        return parent_code;
    }

    public String getType() {
        return type;
    }

    public List<MatedataTree> getChild() {
        return child;
    }

    public void setChild(List<MatedataTree> child) {
        this.child = child;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent_code(String parent_code) {
        this.parent_code = parent_code;
    }

    public void setOrd(Double ord) {
        this.ord = ord;
    }

    public void setType(String type) {
        this.type = type;
    }
	
}
