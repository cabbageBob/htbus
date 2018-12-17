package htbus.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
 
public class CORSFilter implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
 
    }
 
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Headers", "apptoken,token");
        httpResponse.addHeader("Access-Control-Max-Age", "180");
        //清理浏览器web缓存
        httpResponse.setDateHeader("Expires", 0);
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setHeader("Pragma", "no-cache");
        filterChain.doFilter(servletRequest, servletResponse);
    }
 
    @Override
    public void destroy() {
 
    }
}