package ar.com.tecsat.loans.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.tecsat.loans.bean.OperadorBean;

@WebFilter("/admin/*")
public class UrlPermissionsFilter implements Filter{

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		OperadorBean operadorBean = (OperadorBean) req.getSession().getAttribute("operadorBean");
		
		if(operadorBean != null) {
			System.out.println("usuario corecto!!!!!!!!!!!!!!!!!!!!!!");
			filterChain.doFilter(request, response);
		} else {
			System.out.println("usuario incorecto!!!!!!!!!!!!!!!!!!!!!!");
			HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(req.getContextPath() + "/index.html");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
