/*
 * Copyright (C) 2014 www.htwater.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.miao.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author nealmiao
 * @version
 * @since v 1.0
 * @Date 2014年12月9日 下午4:51:01
 */
public class ZipUtil {

	static final int BUFFER = 2048;
	static boolean flag = false;

	public static void unZipFile(String zipPath) {
		try {
			int BUFFER = 1024;
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream("D:/Temp.zip");
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[1024];
				FileOutputStream fos = new FileOutputStream("D:/"
						+ entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File ZipSubdirectory(File myDir) throws IOException {
		// 创建缓冲输入流BufferedInputStream
		BufferedInputStream origin = null;
		// 创建ZipOutputStream对象，将向它传递希望写入文件的输出流
		File zipFile = new File("D:/" + myDir.getName() + ".zip");
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos,
				BUFFER));
		// dirContents[]获取当前目录(myDir)所有文件对象（包括子目录名)
		File dirContents[] = myDir.listFiles();
		// 创建临时文件tempFile,使用后删除
		File tempFile = null;
		try {
			// 处理当前目录所有文件对象，包括子目录
			for (int i = 0; i < dirContents.length; i++) {
				// 使用递归方法将当前目录的子目录转成一个ZIP文件，并作为一个ENTRY加进"ORIGIN"
				if (dirContents[i].isDirectory()) {
					tempFile = ZipSubdirectory(dirContents[i]);
					flag = true;
				}
				// 如果当前文件不是子目录
				else {
					tempFile = dirContents[i];
					// flag标记tempFile是否由子目录压缩成的ZIP文件
					flag = false;
				}
				System.out.println("Compress file: " + tempFile.getName());
				FileInputStream fis = new FileInputStream(tempFile);
				origin = new BufferedInputStream(fis, BUFFER);
				// 为被读取的文件创建压缩条目
				ZipEntry entry = new ZipEntry(tempFile.getName());
				byte data[] = new byte[BUFFER];
				int count;
				// 在向ZIP输出流写入数据之前，必须首先使用out.putNextEntry(entry); 方法安置压缩条目对象
				out.putNextEntry(entry);
				// 向ZIP 文件写入数据
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				// tempFile是临时生成的ZIP文件,删除它
				if (flag == true) {
					flag = tempFile.delete();
					System.out.println("Delete file:" + tempFile.getName()
							+ flag);
				}
				// 关闭输入流
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		// 递归返回
		return zipFile;
	}

	/**
	 * 直接读取zip内容
	 *
	 * @param file
	 * @throws Exception
	 */
	public static void readZipFile(String file) throws Exception {
		ZipFile zf = new ZipFile(file);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
			} else {
				System.err.println("file - " + ze.getName() + " : "
						+ ze.getSize() + " bytes");
				long size = ze.getSize();
				if (size > 0) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(zf.getInputStream(ze)));
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					br.close();
				}
				System.out.println();
			}
		}
		zin.closeEntry();
	}
}
