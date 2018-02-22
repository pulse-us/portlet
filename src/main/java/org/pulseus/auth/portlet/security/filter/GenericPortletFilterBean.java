package org.pulseus.auth.portlet.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.web.context.ServletContextAware;

public class GenericPortletFilterBean implements Filter, BeanNameAware, EnvironmentAware,
EnvironmentCapable, ServletContextAware, InitializingBean, DisposableBean {
	
	private static final Logger log = LogManager.getLogger(GenericPortletFilterBean.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		log.debug("Inside afterPropertiesSet()");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		log.debug("Inside setServletContext()");
	}

	@Override
	public Environment getEnvironment() {
		// TODO Auto-generated method stub
		log.debug("Inside getEnvironment()");
		return null;
	}

	@Override
	public void setEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		log.debug("Inside setEnvironment()");
		
	}

	@Override
	public void setBeanName(String name) {
		// TODO Auto-generated method stub
		log.debug("Inside setBeanName()");
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		log.debug("Inside init()");
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		log.debug("Inside doFilter()");
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		log.debug("Inside destroy()");
		
	}

}
