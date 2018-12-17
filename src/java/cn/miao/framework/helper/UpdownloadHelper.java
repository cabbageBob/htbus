/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2013-3-5 Neal Miao 
 * 
 * Copyright(c) 2013, by htwater.net. All Rights Reserved.
 */
package cn.miao.framework.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.miao.framework.util.Cache;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2013-3-5 上午9:17:23
 * 
 * @see
 */
public class UpdownloadHelper {

	/**
	 * @param args
	 * @return void
	 * @since v 1.0
	 */
	public static void init() {
		Properties properties = new Properties();
		InputStream in = UpdownloadHelper.class.getResourceAsStream(
				"/updownload.properties");
		if (null == in) {
			return;
		}
		try {
			properties.load(in);
			Cache.uploadRoot = properties.get("upload_root_dir").toString();
			Cache.downloadRoot = properties.get("download_root_dir").toString();
			if (null != properties.get("newfile_name_rule")) {
				Cache.newfileRule = properties.get("newfile_name_rule").toString();
			}
			in.close();
			in = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
