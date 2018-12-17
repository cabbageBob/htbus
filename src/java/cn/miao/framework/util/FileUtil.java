/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2013-3-5 Neal Miao 
 * 
 * Copyright(c) 2013, by htwater.net. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件处理相关的工具类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2013-3-5 上午10:07:17
 * 
 * @see
 */
public class FileUtil {
	
	private int FILESIZE = 4 * 1024;
	
	/**
	 * 获取一个目录下的所有文件
	 * 
	 * @param dir
	 * @return List<Map<String,String>>
	 * @since v 1.0
	 */
	public static List<Map<String, String>> getFileList(String dir) {
		File fileDir = new File(dir);
		File[] files = fileDir.listFiles();
		Map<String, String> map = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			}
			map = new HashMap<String, String>();
			map.put("filename", files[i].getName());
			// map.put("filepath", files[i].getAbsolutePath());
			NumberFormat bFormat = NumberFormat.getInstance();
			bFormat.setMaximumFractionDigits(2);
			map.put("filesize", bFormat.format(files[i].length() / 1024.0)
					+ "KB");
			long create = files[i].lastModified(); // 修改时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			map.put("filecreate",
					simpleDateFormat.format(new Timestamp(create)));
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 根据路径获取文件的名称
	 * 
	 * @param path
	 * @return String
	 * @since v 1.0
	 */
	public static String getFileName(String path) {
		String fileName = "";
		if (path.indexOf('/') > -1) {
			fileName = path.substring(path.lastIndexOf('/')+1);
		} else {
			fileName = path.substring(path.lastIndexOf('\\')+1);
		}
		return fileName;
	}
	
	/**
	 * 根据路径获取文件的所在目录
	 * 
	 * @param path
	 * @return String
	 * @since v 1.0
	 */
	public static String getFileDir(String path) {
		String dirName = "";
		if (path.indexOf('/') > -1) {
			dirName = path.substring(0, path.lastIndexOf('/')+1);
		} else {
			dirName = path.substring(0, path.lastIndexOf('\\')+1);
		}
		return dirName;
	}
	
	/**
	 * 根据路径获取文件的类型
	 * 
	 * @param path
	 * @return String
	 * @since v 1.0
	 */
	public static String getFileType(String path) {
		String type = path.substring(path.lastIndexOf('.'));
		return type;
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public void createDir(String path) {
		File dir = new File(path);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean fileExist(String path, String fileName) {
		File file = new File(path + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2Path(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createDir(path);
			file = new File(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
