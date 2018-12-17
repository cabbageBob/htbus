/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 27, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * 编码过滤
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 9:49:10 AM
 * 
 * @see
 */
public class EncodingFilter implements Filter {

	Logger logger = Logger.getLogger(this.getClass());
	
	protected String encoding = null;
	protected FilterConfig filterConfig = null;
	protected boolean ignore = true;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null)
			this.ignore = true;
		else if (value.equalsIgnoreCase("true"))
			this.ignore = true;
		else
			this.ignore = false;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.debug("Encoding Filter start");
		if (ignore || (request.getCharacterEncoding() == null)) {
			String encoding = selectEncoding(request);
			if (encoding != null) {
				request.setCharacterEncoding(encoding);
				response.setCharacterEncoding(encoding);
			}
		}
		chain.doFilter(request, response);
		logger.debug("Encoding Filter end");
	}

	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}

}
