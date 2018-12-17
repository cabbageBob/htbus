package htbus.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	/**
	 * 字符串按花括号替换为Map中的值
	 * @param pattern
	 * @param arguments
	 * @return
	 */
	public static String format(String pattern, Map<String,Object> arguments){  
        String formatedStr = pattern;  
        for (String key : arguments.keySet()) {  
            formatedStr = formatedStr.replaceAll("\\{"+key+"\\}", arguments.get(key).toString());  
        }  
        return formatedStr;  
    }  
	
	public static String nvl(Object str){
		if(str == null){
			return null;
		}else{
			return str.toString();
		}
	}
	
	public static String nvlToBlank(Object str){
		if(str == null){
			return "";
		}else{
			return str.toString();
		}
	}
	
	public static String nvlToLine(Object str){
		if(str == null){
			return "-";
		}else{
			return str.toString();
		}
	}
	
	public static String replaceBlank(String str) {  
        String dest = "";  
        if (str!=null) {  
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
            Matcher m = p.matcher(str);  
            dest = m.replaceAll("");  
        }  
        return dest;  
    }  
	
	public static boolean isBlank(String str){
		if(StringUtils.isBlank(str) || "-".equals(str)){
			return true;
		}else{
			return false;
		}
	}
}
