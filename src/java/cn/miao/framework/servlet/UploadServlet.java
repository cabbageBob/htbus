package cn.miao.framework.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.math.RandomUtils;

import cn.miao.framework.util.Cache;
import cn.miao.framework.util.DateUtil;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {

	private String uploadPath; // 上传文件的目录
	private Map<String, Object> result = new HashMap<String, Object>();

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPut(req, resp);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 每次调用前需要初始化
		response.setContentType("application/json");
		result.clear();// = new HashMap<String, String>();
		String path = request.getParameter("path");
		String callback = request.getParameter("callback");
		//HttpSession session = request.getSession();
		//String mid = session.getAttribute("token").toString();
		setUploadPath(path);
		try {
			uploadFiles(request, response);
			result.put("success", true);
			result.put("message", "文件上传成功");
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "文件上传失败");
		}
		if (null == callback || 0 == callback.length()) {
			response.getWriter().print(JSONObject.fromObject(result).toString());
		} else {
			response.getWriter().print(callback + "(" +JSONObject.fromObject(result).toString() + ");");
		}
	}

	/**
	 * 设置上传的路径
	 * 
	 * @param method
	 * @return void
	 * @since v 1.0
	 */
	private void setUploadPath(String path) {
		//initPath();
		if (null == Cache.uploadRoot) {
			uploadPath = getServletContext().getRealPath("/") + "temp/";
		} else {
			uploadPath = Cache.uploadRoot;
		}
		if(path != null){
			uploadPath += path + "/";
		}
		File uploadFile = new File(uploadPath);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
		}
	}

	/**
	 * 处理文件上传和参数读取
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @return void
	 * @since v 1.0
	 */
	private void uploadFiles(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		String encoding = request.getCharacterEncoding();
		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(encoding);
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(request);// 得到所有的文件
			Iterator<FileItem> i = items.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				String fileName = fi.getName();
				if (null != fileName) { //对文件的处理
					File fullFile = new File(fileName);
					File savedFile = new File(uploadPath, fullFile.getName());
					fi.write(savedFile);
					if (fileName.indexOf('\\') > -1) {
						fileName = fileName.substring(fileName
								.lastIndexOf('\\') + 1);
					}
					InputStream inputStream = new FileInputStream(uploadPath
							+ fileName);
					inputStream.close();
					String newFileName = "";
					if (null == Cache.newfileRule) {
						newFileName = DateUtil.getToday("yyyyMMddHHmmss");
					} else {
						if (Cache.newfileRule.contains("date")) {
							String dateRule = Cache.newfileRule.split(":")[1];
							newFileName = DateUtil.getToday(dateRule);
						} else {
							newFileName = "" + RandomUtils.nextLong();
						}
					}
					newFileName += fileName.substring(fileName.lastIndexOf('.'));
					String path = request.getParameter("path") == null ? "/"+ newFileName 
							: request.getParameter("path") +"/"+ newFileName;
					result.put("path", path);
					// savedFile.delete();
					savedFile.renameTo(new File(uploadPath + newFileName));
				} else { // 对其他Field的处理
					//result.put(fi.getFieldName(), fi.getString(encoding));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
