package dbm.servlet;

import java.util.HashMap;

import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;
import cn.miao.framework.util.Cache;
import cn.miao.framework.util.DateUtil;
import cn.miao.framework.util.FileUtil;
import dbm.helper.DBReorgHelper;
import dbm.impl.DBReorgImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
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

public class uploadMdfServlet extends HttpServlet{
	private String path = "mdf";
	private String uploadPath,uploadPath2;
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

		String callback = request.getParameter("callback");

		setUploadPath();

		if(uploadMdfFiles(request, response)){
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
		//this.uploadPath2 = (this.uploadPath + this.path2 + "/");
		Properties properties = getPropFromPropFile("/updownload.properties");

		this.uploadPath2 = properties.getProperty("dbm_mdf_work");
		this.uploadPath = (this.uploadPath + this.path + "/");
    
		File uploadFile = new File(this.uploadPath);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
		}
		File uploadFile2 = new File(this.uploadPath2);
		if (!uploadFile2.exists()) {
			uploadFile2.mkdirs();
		}
	}
  
	private boolean uploadMdfFiles(HttpServletRequest request, HttpServletResponse response){
		boolean result = false;
		String encoding = request.getCharacterEncoding();
		try
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(encoding);
  
			List<FileItem> items = upload.parseRequest(request);
			if( items.size()<2 ) {
				this.params.put("msg", "所选mdf、ldf文件个数不等于2");
				throw(new Exception("导入mdf失败：所选mdf、ldf文件个数不等于2"));
			}
			String mdffname = "";
			String ldffname = "";
			FileItem mdffileitem = null;
			FileItem ldffileitem = null;
			if( items.get(0).getName().contains(".mdf") || items.get(0).getName().contains(".MDF") ){
				mdffileitem = items.get(0);
				mdffname = mdffileitem.getName().replace(".MDF", ".mdf");
				ldffileitem = items.get(1);
				ldffname = ldffileitem.getName().replace(".LDF", ".ldf");
				
			}else{
				mdffileitem = items.get(1);
				mdffname = mdffileitem.getName().replace(".MDF", ".mdf");
				ldffileitem = items.get(0);
				ldffname = ldffileitem.getName().replace(".LDF", ".ldf");
			}
			if( !mdffname.contains(".mdf") || !ldffname.contains(".ldf") ) {
				this.params.put("msg", "所选文件缺少mdf或者ldf");
				throw(new Exception("导入mdf失败：所选文件缺少mdf或者ldf"));
			}

			String mdfname_new = getFileNewName()+mdffname.substring(mdffname.lastIndexOf("."));
			File mdfsavedFile = new File(this.uploadPath, mdfname_new);
			mdffileitem.write(mdfsavedFile);
			String ldfname_new = getFileNewName()+ldffname.substring(ldffname.lastIndexOf("."));
			File ldfsavedFile = new File(this.uploadPath, ldfname_new);
			ldffileitem.write(ldfsavedFile);
			
			if( isUploadMdf(mdffname,ldffname) ){//已经上传过，先试着分离数据库（可能报错没关系），确保数据库文件不在使用中，然后再copy
				atdetachDB(mdffname,ldffname,2);
			}
			
			File mdfworksavedFile = new File(this.uploadPath2, mdffname);
			copyFileUsingFileChannels(mdfsavedFile,mdfworksavedFile);
			File ldfworksavedFile = new File(this.uploadPath2, ldffname);
			copyFileUsingFileChannels(ldfsavedFile,ldfworksavedFile);
			
	        InputStream inputStream = new FileInputStream(this.uploadPath + mdfname_new);
	        inputStream.close();
			InputStream inputStream1 = new FileInputStream(this.uploadPath2 + mdffname);
	        inputStream1.close();
			InputStream inputStream2 = new FileInputStream(this.uploadPath + ldfname_new);
	        inputStream2.close();
			InputStream inputStream3 = new FileInputStream(this.uploadPath2 + ldffname);
			inputStream3.close();
			
			//附加数据库，得到dbid
			String dbid = addToDBSource(mdffname,ldffname);
			String listid = request.getParameter("listid");
			if( !dbid.equals("-1") && null!=listid && listid.length()>0 ){
				Properties properties = getPropFromPropFile("/updownload.properties");
	
				String mdfworkhost = properties.getProperty("dbm_mdf");
				String mdflink = mdfworkhost + mdfname_new;
				String mdfpath = this.uploadPath2 + mdffname;
	
				String ldflink = mdfworkhost + ldfname_new;
				String ldfpath = this.uploadPath2 + ldffname;
				result = new DBReorgImpl().insertAttachMdf(dbid, listid, mdfname_new, mdffname, mdfpath, mdflink,
						ldfname_new, ldffname, ldfpath, ldflink);
				if( result ){
					this.params.put("mdfname", mdffname);
					this.params.put("mdfname", ldffname);
					this.params.put("mdflink", mdflink);
					this.params.put("ldflink", ldflink);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @description 判断文件是否已经上传过 ，上传过的文件，可能处于附加状态，需要先分离才能copy新的上传文件进去
	 * @date 2016年11月30日下午7:46:15
	 * @author lxj
	 * @param mdffname
	 * @param ldffname
	 * @return
	 */
	private boolean isUploadMdf(String mdffname, String ldffname) {
		File uploadFile1 = new File(this.uploadPath2+mdffname);
		File uploadFile2 = new File(this.uploadPath2+ldffname);
		if ( uploadFile1.exists() && uploadFile2.exists() ) {
			return true;
		}
		return false;
	}

	/**
	 * @description 附加数据库，附加成功返回 sys_reorg_dbsource 中dbid ，失败返回-1
	 * @date 2016年11月30日上午10:32:50
	 * @author lxj
	 * @param mdffname
	 * @return
	 */
	private String addToDBSource(String mdffname,String ldffname) {
		String result = "-1";
		try {
			//此处需要配置好 sys_reorg_dbsource 中 整编总库的连接方式
			if( atdetachDB(mdffname,ldffname,1) ){
				List<Map<String,Object>> dbList = DBReorgHelper.callProByReorg("{call addToDBSource(?)}", new Object[]{mdffname});
				if( null!=dbList && !dbList.isEmpty() ){
					result = dbList.get(0).get("id").toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @description 附加或者分离库
	 * @date 2016年11月30日下午7:33:36
	 * @author lxj
	 * @param mdffname
	 * @param ldfname
	 * @param flag 1 附加 2 分离
	 */
	private boolean atdetachDB(String mdffname,String ldffname,int flag){
		boolean result = false;
		try {
			String mdfname = mdffname.substring(0,mdffname.lastIndexOf("."));
			String path = this.uploadPath2.substring(0, this.uploadPath2.length()-1);//去掉最后的反斜杠
			String sql =  "";
			switch(flag){
			case 1:
				sql = DBReorgHelper.attachDbSql.replace("mdffname", mdffname).replace("ldffname", ldffname)
				.replace("mdfname", mdfname).replace("path", path).replace("/", "\\");
	
				break;
			case 2:
				sql = DBReorgHelper.detachDbSql.replace("mdfname", mdfname).replace("/", "\\");
				break;
			}
			if( sql.length()>0 ){
				DBReorgHelper.executeSqlByZK(sql, null);//附加或者分离数据库
				result = true;//附加必须要求附加成功后，才能进行后一步操作
			}
		} catch (Exception e) {
			if( flag==1 ){
				System.out.println("附加数据库失败："+mdffname+" ,不能对该数据库进行统计操作");
			}else if( flag==2 ){//如果是分离，不管分离结果如何，都返回成功
				result = true;
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * @description copy 文件，最快捷方式
	 * @date 2016年11月30日下午7:38:44
	 * @author lxj
	 * @param source
	 * @param dest
	 */
	private static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    }catch(IOException e){
	    	e.printStackTrace();
	    	System.out.println("拷贝文件"+source.getName()+"到工作目录失败");
	    }
	    finally {
	    	try {
				inputChannel.close();
				outputChannel.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    }
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2038521326230862282L;
	
	public static void main(String[] args) throws Exception{
		BaseDao dao = DaoFactory.getDao("localhost");
		boolean b = dao.executeSQL("use master;exec sp_detach_db 'Test1'", args);
		boolean c = dao.executeSQL("use master;exec sp_attach_db 'Test1','C:\\Program Files\\Microsoft SQL Server\\MSSQL10.MSSQLSERVER\\MSSQL\\DATA\\Test1.mdf',"
				+ "'C:\\Program Files\\Microsoft SQL Server\\MSSQL10.MSSQLSERVER\\MSSQL\\DATA\\Test1_log.ldf'", args);

		if(b && c){
			System.out.println("1");
		}
		System.exit(0);
	}
	
	public static Properties getPropFromPropFile( String filename ){
		Properties result = new Properties();
		try {
			InputStream in = FileUtil.class.getResourceAsStream("/"+filename);
			if (in != null) {
				result.load(in);
			}
			in.close();
            in = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getFileNewName(){
        String newFileName = "";
        if (Cache.newfileRule == null) {
          newFileName = DateUtil.getToday("yyyyMMddHHmmss");
        }
        else if (Cache.newfileRule.contains("date")) {
          String dateRule = Cache.newfileRule.split(":")[1];
          newFileName = DateUtil.getToday(dateRule);
        } else {
          newFileName = RandomUtils.nextLong()+"";
        }
        return newFileName;
	}
}
