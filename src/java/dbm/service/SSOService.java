package dbm.service;

import java.util.Map;

public interface SSOService {

	/**
	 * 登录认证
	 * 
	 * @param userName
	 * @param password
	 * @return int 
	 * @since v 1.0
	 */
	public Map<String, Object> login(String userName, String password);
	public Boolean userIsExist(String username) ;
}

