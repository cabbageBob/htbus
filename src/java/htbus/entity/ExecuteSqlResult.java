package htbus.entity;

import java.util.List;
import java.util.Map;

public class ExecuteSqlResult {
	/**2017年12月8日上午11:25:37
	 *
	 * @author Jokki
	 *  
	 */
	public ExecuteSqlResult(){
		super();
	}
	
	public ExecuteSqlResult(Boolean success, String errorMessage, String time, Integer affectedRow, 
			List<Map<String, Object>> data, Integer size){
		this.success = success;
		this.errorMessage = errorMessage;
		this.time = time;
		this.affectedRow = affectedRow;
		this.data = data;
		this.size = size;
	}
	
	public ExecuteSqlResult(Boolean success, String errorMessage){
		this.success = success;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * 是否成功
	 */
	private Boolean success;
	
	/**
	 * 报错信息
	 */
	private String errorMessage;
	
	/**
	 * 查询时间
	 */
	private String time;
	
	/**
	 * 受影响的行
	 */
	private Integer affectedRow;
	
	/**
	 * 查询返回的数据
	 */
	private List<Map<String, Object>> data;
	
	/**
	 * 数据数量
	 */
	private Integer size;
	
	public Boolean getSuccess() {
        return success;
    }

    public Integer getAffectedRow() {
        return affectedRow;
    }

    public Integer getSize() {
        return size;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTime() {
        return time;
    }

    public void setAffectedRow(Integer affectedRow) {
        this.affectedRow = affectedRow;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setTime(String time) {
        this.time = time;
    }
	
}
