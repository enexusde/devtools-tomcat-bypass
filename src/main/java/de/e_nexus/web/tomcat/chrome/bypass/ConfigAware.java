package de.e_nexus.web.tomcat.chrome.bypass;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class ConfigAware implements Filter {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(ConfigAware.class.getCanonicalName());
	private transient FilterConfig filterConfig = null;

	private DynamicPropertyFile dynamicPropFile = new DynamicPropertyFile();
	private DynamicPropertyFile usersPropFile = new DynamicPropertyFile();
	private DynamicPropertyFile systemPropFile = new DynamicPropertyFile();

	public final void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		setup(filterConfig);
	}

	public abstract void setup(FilterConfig filterConfig);

	public abstract void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

	public abstract void destroy();

	/**
	 * Returns the parameter for the bypass filter.
	 * <p>
	 * The most dynamically changeable parameter is returned. The exact order is:
	 * <ol>
	 * <li>URL-Parameter.
	 * <li>Propertie-file parameter (specified by
	 * {@value DynamicPropertyFile#PARAM}.
	 * <li>Users-Propertie-file parameter (located at
	 * ${user.home}/devtools.properties).
	 * <li>System-Propertie-file parameter (located at /devtools.properties}.
	 * <li>Session-Attribute (using {@link Object#toString()} to get a passable
	 * return value).
	 * <li>Filter-Init-Parameter.
	 * <li>Servlet-Context-Init-Parameter.
	 * <li>System-Property.
	 * <li>Environment-Parameter.
	 * </ol>
	 * <p>
	 * If multiple URL-Parameters are set it returns the last parameter-value.
	 * 
	 * @param name    The name of the parameter, never <code>null</code>.
	 * @param err     The error-value if no such parameter exists.
	 * @param request The request to respect.
	 * @return <code>err</code>-value if no such parameter exists, the value
	 *         otherwise.
	 */
	protected String getBypassParameter(String name, String err, HttpServletRequest request) {
		if (request != null) {
			String[] values = request.getParameterValues(name);
			if (values != null && values.length > 0) {
				String val = values[values.length - 1];
				LOG.info("Found property '" + name + "' \t in http-request having a value of: '" + val + "'.");
				return val;
			}
		}
		if (dynamicPropFile.hasParam(name)) {
			String val = dynamicPropFile.getParam(name, null);
			if (val != null) {
				LOG.info("Found property '" + name + "' \t in dynamic property file having a value of: '" + val + "' from file "
						+ dynamicPropFile.getFile().getAbsolutePath() + ".");
				return val;
			} else {
				throw new RuntimeException("Property is available but null is not allowed.");
			}
		}
		if (usersPropFile.hasParam(name)) {
			String val = usersPropFile.getParam(name, null);
			if (val != null) {
				LOG.info("Found property '" + name + "' \t in users property file having a value of: '" + val + "' from file "
						+ usersPropFile.getFile().getAbsolutePath() + ".");
				return val;
			} else {
				throw new RuntimeException("Property is available but null is not allowed.");
			}
		}
		if (systemPropFile.hasParam(name)) {
			String val = systemPropFile.getParam(name, null);
			if (val != null) {
				LOG.info("Found property '" + name + "' \t in system property file having a value of: '" + val + "' from file "
						+ systemPropFile.getFile().getAbsolutePath() + ".");
				return val;
			} else {
				throw new RuntimeException("Property is available but null is not allowed.");
			}
		}
		if (request != null && request.getSession() != null) {
			HttpSession session = request.getSession();
			Object attribute = session.getAttribute(name);
			if (attribute != null) {
				LOG.info("Found property '" + name + "' \t in session having a value of: '" + attribute.toString() + "' having session-id of '"
						+ session.getId() + "'.");
				return attribute.toString();
			}
		}
		String initParameter = filterConfig.getInitParameter(name);
		if (initParameter != null) {
			LOG.info("Found property '" + name + "' \t in filter-init-configuration having a value of: '" + initParameter + "'.");
			return initParameter;
		}
		String servletContextInitParam = filterConfig.getServletContext().getInitParameter(name);
		if (servletContextInitParam != null) {
			LOG.info("Found property '" + name + "' \t in servlet-context having a value of: '" + servletContextInitParam + "'.");
			return servletContextInitParam;
		}
		String sysProp = System.getProperty(name);
		if (sysProp != null) {
			LOG.info("Found property '" + name + "' \t in System.getProperty() having a value of: '" + sysProp + "'.");
			return sysProp;
		}
		String envProp = System.getenv(name);
		if (envProp != null) {
			LOG.info("Found property '" + name + "' \t in System.getenv() having a value of: '" + envProp + "'.");
			return envProp;
		}
		if (request != null) {
			LOG.warning("Missing property '" + name + "' for request '" + request.getRequestURL() + "'.");
		} else {
			LOG.warning("Missing property '" + name + "' detached from request.");
		}
		return err;
	}

	public void setPropertiesFile(File dynamic, File user, File system) {
		dynamicPropFile.setFile(dynamic);
		usersPropFile.setFile(user);
		systemPropFile.setFile(system);
	}
}
