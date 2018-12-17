package cn.miao.framework.helper;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import cn.miao.framework.util.Base64Util;

public class HeaderTokenHelper {
	
	//这里以后要改，应该是只从header中取token，现在为了调试方便，同时也从params中取
	public static String getHeaderToken(HttpServletRequest request){
		String result = request.getHeader("token");
		if(result==null) result = request.getParameter("token");
		return result;
	}
	
	/**
	 * 创建编码后的token，带有时间戳
	 * @param info
	 * @return
	 */
	public static String createToken(String info){
		String token = Base64Util.getBase64("T"+info+"|"+new Date().getTime());
		token = token.substring(3) + token.substring(0, 3);
		return token;
	}
	
	/**
	 * 解码token
	 * @param token
	 * @return
	 */
	public static String decodeToken(String token){
		String result = null;
		token = token.substring(token.length()-3) + token.substring(0,token.length()-3);
		String s = Base64Util.getFromBase64(token);
		s=s.substring(1);
		if(s.length()>0){
			result=s.split("\\|")[0];
		}
		return result;
	}
	
	/**
	 * 验证时间戳过期时间
	 * @param token
	 * @return
	 */
	public static boolean checkToken(String token){
		boolean result = false;
		token = token.substring(token.length()-3) + token.substring(0,token.length()-3);
		String s = Base64Util.getFromBase64(token);
		if(s!=null && s.length()>1 && s.substring(0, 1).equals("T")){
			//这里要写验证时间的代码，暂时写死返回true
			result = true;
		}
		
		return result;
	}
	
	public static void main(String args[]){
		//String result = createToken("admin-sysuser");
		//String result = decodeToken("kbWluLXN5c3VzZXJ8MTUwMzYxMzQwNTgwMg==VGF");
		boolean result = checkToken("kbWluLXN5c3VzZXJ8MTUwMzYxMzQwNTgwMg==VGF");
		System.out.print(result);
	}
}
