package htbus.util;

import htbus.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigUtil {
	private static Properties properties = new Properties();
	static Logger logger = Logger.getLogger(UserService.class);
	public static String config(String key){
		String result = null;
		InputStream in = ConfigUtil.class.getResourceAsStream("/config.properties");
		try {
			properties.load(in);
			in.close();
			
			if(properties.containsKey(key)){
				result = properties.getProperty(key);
			};
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
}
