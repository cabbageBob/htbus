package dbm.servlet;

import java.util.HashMap;

import cn.miao.framework.util.Cache;
import cn.miao.framework.util.DateUtil;
import dbm.impl.DBReorgImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.math.RandomUtils;

public class uploadGuaServlet extends HttpServlet{
	private String uploadPath;
	private String path = "gua";
	private Map<String, String> params = new HashMap<>();

	public void destroy(){
	    super.destroy();
	}
  
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		super.doDelete(req, resp);
	}
  
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		super.doPut(req, resp);
	}
  
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
  
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		response.setContentType("text/javascript");
		this.params.clear();
		//String path = request.getParameter("path");//mdf
		
		String callback = request.getParameter("callback");

		setUploadPath();
		if(uploadGuaFiles(request, response)){
			this.params.put("result", "success");
		}else{
			this.params.put("result", "fail");
		}
		if ((callback == null) || (callback.length() == 0)) {
			response.getWriter().print(JSONObject.fromObject(this.params).toString());
		} else {
			response.getWriter().print(callback + "(" + JSONObject.fromObject(this.params).toString() + ");");
		}
	}
  
	private void setUploadPath(){
		if (Cache.uploadRoot == null) {
			this.uploadPath = (getServletContext().getRealPath("/") + "temp/");
		} else {
			this.uploadPath = Cache.uploadRoot;
		}
		this.uploadPath = (this.uploadPath + this.path + "/");
    
		File uploadFile = new File(this.uploadPath);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
		}
	}
  
	private boolean uploadGuaFiles(HttpServletRequest request, HttpServletResponse response){
		boolean result = false;
		String encoding = request.getCharacterEncoding();
		try
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(encoding);
  
			List<FileItem> items = upload.parseRequest(request);
			if( items.size()<1 ) {
				this.params.put("msg", "所选pdf/word文件个数小于1");
				throw(new Exception("导入mdf失败：所选pdf/word文件个数小于1"));
			}
			String guaname = "";
			FileItem guaitem = null;
			if( items.get(0).getName().toLowerCase().contains(".pdf") 
				|| items.get(0).getName().toLowerCase().contains(".doc")
				|| items.get(0).getName().toLowerCase().contains(".docx")
					){
				guaitem = items.get(0);
				guaname= guaitem.getName().replace(".PDF", ".pdf").replace(".DOC", ".word").replace(".DOCX", ".word");
			}
			if( !guaname.contains(".pdf") && !guaname.contains(".doc") && !guaname.contains(".docx") ) {
				this.params.put("msg", "所选文件不是指定的 pdf/doc/docx 文件");
				throw(new Exception("导入mdf失败：所选文件不是指定的 pdf/doc/docx 文件"));
			}

			String gua_new = uploadMdfServlet.getFileNewName()+guaname.substring(guaname.lastIndexOf("."));
			File savedFile = new File(this.uploadPath, gua_new);
			guaitem.write(savedFile);
			InputStream inputStream = new FileInputStream(this.uploadPath + gua_new);
			inputStream.close();

			String listid = request.getParameter("listid");
			if( null!=listid && listid.length()>0 ){
				Properties properties = new Properties();
				InputStream in = uploadGuaServlet.class.getResourceAsStream("/updownload.properties");
				try {
					properties.load(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String dbmhost = properties.getProperty("dbm_gua");
				String gualink = dbmhost + gua_new;
				String guapath = this.uploadPath + guaname;;
				result = new DBReorgImpl().insertAttachGua(listid, gua_new,gualink,guapath,guaname);
				if( result ){
					this.params.put("orgname", guaname);
					this.params.put("filename", gualink);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2038521326230862282L;

}
