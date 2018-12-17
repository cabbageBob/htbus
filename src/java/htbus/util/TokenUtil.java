package htbus.util;

import javax.servlet.http.HttpServletRequest;

import cn.miao.framework.helper.HeaderTokenHelper;

public class TokenUtil {
	/**
	 * 获取请求头中的token
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request){
		String token = HeaderTokenHelper.getHeaderToken(request);
		token = HeaderTokenHelper.decodeToken(token);
		return token;
	}
	
	/**
	 * 获取请求头中的用户名
	 * @param request
	 * @return
	 */
	public static String getUsername(HttpServletRequest request){
		String token = HeaderTokenHelper.getHeaderToken(request);
		token = HeaderTokenHelper.decodeToken(token);
		String username = token.split("-")[0];
		return username;
	}
	
	/**
	 * 获取请求头中的用户角色
	 * @param request
	 * @return
	 */
	public static String getUserRole(HttpServletRequest request){
		String token = HeaderTokenHelper.getHeaderToken(request);
		token = HeaderTokenHelper.decodeToken(token);
		String role = token.split("-")[1];
		return role;
	}
	
	/**
	 * 获取请求头中的当前登陆控制台的应用id
	 * @param request
	 * @return
	 */
	public static String getAppid(HttpServletRequest request){
		String token = HeaderTokenHelper.getHeaderToken(request);
		token = HeaderTokenHelper.decodeToken(token);
		String[] array = token.split("-");
		if(array.length > 2){
			return array[2];
		}else{
			return "0";
		}
	}
	public static void main(String[] args) {
		System.out.println(HeaderTokenHelper.decodeToken("6aC1hcHB1c2VyLTl8MTUyNDEyMDY0MTkyMQ==VGJ"));
		System.out.println(HeaderTokenHelper.decodeToken("6aC1hcHB1c2VyLTl8MTUyNDEyMjM0MzM1Nw==VGJ"));
	}
}
