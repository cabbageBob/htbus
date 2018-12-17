package cn.miao.framework.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.miao.framework.util.Cache;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public DownloadServlet() {
		super();
	}

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
		response.setCharacterEncoding("UTF-8");
		//request.setCharacterEncoding("GBK");
		String aFileName = new String(request.getParameter("file").getBytes(
				"iso8859-1"), "UTF-8");
		String path = request.getParameter("path");
		File fileLoad = new File(Cache.downloadRoot + path+"/", aFileName);
		FileInputStream in = null; // 输入流
		OutputStream out = response.getOutputStream();
		byte b[] = new byte[1024];

		try {
			//response.setContentType("application/x-msdownload;");
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(aFileName.getBytes("UTF-8"), "ISO-8859-1"));
			// download the file
			in = new FileInputStream(fileLoad);
			int n = 0;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
