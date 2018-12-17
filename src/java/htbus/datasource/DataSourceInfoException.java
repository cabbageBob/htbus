package htbus.datasource;

public class DataSourceInfoException extends RuntimeException{

	private static final long serialVersionUID = -6690989578839375191L;
	
	public DataSourceInfoException(String msg){
		super(msg);
	}
	
	public DataSourceInfoException(String msg, Throwable e){
		super(msg, e);
	}
}
