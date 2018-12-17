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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Dispatcher;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.helper.DispatchHelper;
import cn.miao.framework.helper.HeaderTokenHelper;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 9:49:10 AM
 * 
 * @see
 */
public class DispatchFilter implements Filter {

	Logger logger = Logger.getLogger(this.getClass());
	final String NEEDAUTHENTICATE = "yes";
	final String NOAUTHENTICATE = "no";
	final String OAUTHAUTHENTICATE = "oauth";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		// 符合一定规范的URL调用方法，否则忽略
		String uri = request.getRequestURI();
		if (DispatchHelper.filterOK(response, uri)) {
			return;
		}
		if (uri.indexOf('!') > -1) {
			String[] method = DispatchHelper.getMethod(uri);
			String alias = method[1];
			Dispatcher dispatcher = DispatchHelper.searchInConfig(alias);
			if (null == dispatcher) {
				// logger.info("配置文件中没有["+ alias +"]相关信息");
				return;
			}
			HttpSession session = request.getSession();
			if (NEEDAUTHENTICATE.equals(dispatcher.getNeedAuthenticate())) {
				// 需要session身份认证的
				//if (null == session.getAttribute("token")) {
				//if (!checkHeadToken(request.getHeader("token"))) {
				if (!checkHeadToken(HeaderTokenHelper.getHeaderToken(request))) {
					// response.sendRedirect(request.getContextPath() +
					// "/login.html");
					response.getWriter().print("invalid token");
					return;
				} else {
					callMethod(request, response, method, dispatcher, session);
				}
			} else if (NOAUTHENTICATE.equals(dispatcher.getNeedAuthenticate())) {
				// 不需要session身份认证的, oauth认证
				callMethod(request, response, method, dispatcher, session);
			} else if (OAUTHAUTHENTICATE.equals(dispatcher
					.getNeedAuthenticate())) {
				if (checkOauthUri(uri)) {
					callMethod(request, response, method, dispatcher, session);
				} else {
					response.sendRedirect("htcloud" + request.getServletPath());
				}
			} else {
				logger.info("出错，查看dispathcer.xml中needAuthenticate的配置");
			}
		} else {
			chain.doFilter(request, response);
		}
	}
	
	private boolean checkHeadToken(String token){
		boolean result = false;
		if(token==null)
			result = false;
		else{
			result = HeaderTokenHelper.checkToken(token);
		}
		return result;
	}
	
	private boolean checkOauthUri(String uri) {
		return uri.subSequence(0, 15).equals("/htbus/htcloud/") && uri.indexOf('!') > -1;
	}

	private void callMethod(HttpServletRequest request,
			HttpServletResponse response, String[] method,
			Dispatcher dispatcher, HttpSession session) throws IOException {
		Responser object = (Responser) DispatchHelper.invoke(dispatcher,
				method[0], DispatchHelper.parseParams(request), session,
				request, response);
		if (null == object) {
			logger.info("没有[" + method[0] + "]这个方法，或者这个方法报错，请打断点调试该方法");
			response.getWriter().print("error");
			return;
		}
		String rtType = object.getRtType();

		if (DoAction.JSON.equals(rtType)) {
			// 只有在json中可以处理跨域
			response.setContentType("application/json");
			response.getWriter().print(crossDomain(request, object));
		} else if (DoAction.XML.equals(rtType)) {
			response.setContentType("text/xml");
			response.getWriter().print(object.getRtString());
		} else if (DoAction.TEXT.equals(rtType)) {
			response.setContentType("text/html");
			response.getWriter().print(object.getRtString());
		} else if (DoAction.NONE.equals(rtType)) {

		} else if (DoAction.REDIRECT.equals(rtType)) {
			// request.getRequestDispatcher("/login.html").forward(req, resp);
			response.sendRedirect(object.getRtString().toString());
			return;
		} else {
			logger.info("配置文件中没有指定返回类型");
		}
	}

	/**
	 * 跨域调用的处理
	 * 
	 * @param request
	 * @param object
	 * @return String
	 * @since v 1.0
	 */
	private String crossDomain(HttpServletRequest request, Responser object) {
		String callback = request.getParameter("callback");
		if (null == callback || 0 == callback.length()) {
			return object.getRtString();
		} else {
			return callback + "(" + object.getRtString() + ");";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
