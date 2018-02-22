package org.pulseus.auth.service.servlet;
        

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.servlet.PortalDelegateServlet;
import com.liferay.portal.kernel.servlet.PortalDelegatorServlet;
import com.liferay.portal.kernel.util.InstanceFactory;

public class FixedPortalDelegateServlet extends PortalDelegateServlet {
   
	public static final String PLUGIN_CLASS_LOADER = "PLUGIN_CLASS_LOADER";
	
	public final static String FILTER_CLASS_PARAM = "filter-class";

	public final static String FILTER_NAME_PARAM = "filter-name";

	public final static String SERVLET_CLASS_PARAM = "servlet-class";

	public final static String SUB_CONTEXT_PARAM = "sub-context";

	@Override
	protected void doPortalDestroy() {
		PortalDelegatorServlet.removeDelegate(_subContext);

		servlet.destroy();
	}

	@Override
	protected void doPortalInit() throws Exception {
		ServletContext servletContext = servletConfig.getServletContext();

		ClassLoader classLoader = (ClassLoader)servletContext
				.getAttribute(PLUGIN_CLASS_LOADER);

		String servletClassName = servletConfig
				.getInitParameter(SERVLET_CLASS_PARAM);

		_subContext = servletConfig.getInitParameter(SUB_CONTEXT_PARAM);

		if (_subContext == null) {
			_subContext = getServletName();
		}

		servlet = (Servlet)InstanceFactory.newInstance(classLoader,
				servletClassName);

		if (!(servlet instanceof HttpServlet)) {
			throw new IllegalArgumentException(
					"servlet-class is not an instance of " +
							HttpServlet.class.getName());
		}

		servlet.init(servletConfig);

		servlet = wrapServletInFilter((HttpServlet)servlet, classLoader);

		PortalDelegatorServlet.addDelegate(_subContext, (HttpServlet)servlet);
	}

	private static boolean isBlank(String str) {
		int strLen;

		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}

		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}

		return true;
	}

	private HttpServlet wrapServletInFilter(HttpServlet servlet, final ClassLoader classLoader)
			throws Exception {

		String filterClassName = servletConfig
				.getInitParameter(FILTER_CLASS_PARAM);
		final String filterName = servletConfig
				.getInitParameter(FILTER_NAME_PARAM);

		if (isBlank(filterClassName) || isBlank(filterName))
			return servlet;

		/**if (_log.isDebugEnabled())
			_log.debug("Delegate servlet contains the filter: name [" +
					filterName + "], class [" + filterClassName + "]");**/

		Filter filter = (Filter)InstanceFactory.newInstance(classLoader, filterClassName);

		FilterConfig filterConfig = new FilterConfig() {
			public String getFilterName() {
				return filterName;
			}

			public String getInitParameter(String name) {
				return servletConfig.getInitParameter(name);
			}

			public Enumeration<String> getInitParameterNames() {
				return servletConfig.getInitParameterNames();
			}

			public ServletContext getServletContext() {
				return servletConfig.getServletContext();
			}
		};
		filter.init(filterConfig);

		return new FilteredServletWrapper(filter, servlet);
	}

	private String _subContext;

	@SuppressWarnings("serial")
	private final class FilteredServletWrapper extends HttpServlet {

		@Override
		public void destroy() {
			_servlet.destroy();
			_filter.destroy();
		}

		@Override
		public void init(ServletConfig servletConfig) {
			throw new IllegalStateException();
		}

		@Override
		public void service(HttpServletRequest request,
				HttpServletResponse response) throws IOException,
				ServletException {

			FilterChain filterChain = new FilterChain() {
				public void doFilter(ServletRequest servletRequest,
						ServletResponse servletResponse)
						throws java.io.IOException,
						javax.servlet.ServletException {
					_servlet.service(servletRequest, servletResponse);
				}
			};

			_filter.doFilter(request, response, filterChain);
		}

		private FilteredServletWrapper(Filter filter, HttpServlet servlet) {
			_filter = filter;
			_servlet = servlet;
		}

		private final Filter _filter;
		private final HttpServlet _servlet;

	}
	
	//private static Log _log = LogFactoryUtil.getLog(PortalDelegateServlet.class);
	
	/**
	 * This is a fix for a defect in Liferay 7.0
	 */
	@Override
    public String getServletInfo() {
        if (servlet == null) {
            return "";
        }
        return super.getServletInfo();
    }
}
