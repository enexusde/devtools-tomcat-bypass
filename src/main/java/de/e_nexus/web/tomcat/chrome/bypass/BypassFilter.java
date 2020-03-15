package de.e_nexus.web.tomcat.chrome.bypass;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import de.e_nexus.web.tomcat.chrome.bypass.util.Utils;

/**
 * Filter for locally served resources that might change dynamically on
 * localhost.
 * 
 * @author Guest
 *
 */
@WebFilter(displayName = "devtools-bypass", description = "Bypass for devtools", urlPatterns = { "*" })
public class BypassFilter extends ConfigAware implements Filter {
	private static final String DEVTOOLS_PROPERTIES = "devtools.properties";

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(BypassFilter.class.getCanonicalName());

	public boolean enabled = false;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		LOG.finer("Check if " + getClass() + " is enabled: " + isEnabled());
		if (isEnabled()) {
			BypassWork order = findBypassOrder(request);
			if (order != null) {
				LOG.warning("Warning, bypassing static resource: " + order + " for request: '"
						+ (request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURL() : request.toString()) + "'!");
				order.doBypass(request, response);
			} else {
				LOG.fine("No bypass order calculated, skip bypassing!");
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private BypassWork findBypassOrder(ServletRequest request) {
		Set<String> availableKeys = new LinkedHashSet<>();
		HttpServletRequest hsr = null;
		if (request instanceof HttpServletRequest) {
			hsr = (HttpServletRequest) request;
			String url = hsr.getRequestURI();
			String urlMD5 = Utils.toMD5(url);
			availableKeys.add("servlet.http." + urlMD5);
			availableKeys.add("servlet.http." + url);
		}
		availableKeys.add("serlvet." + Arrays.toString(request.getParameterMap().keySet().toArray(new String[0])));
		for (String possible : availableKeys) {
			String value = getBypassParameter(possible, null, hsr);
			if (value == null) {
				continue;
			}
			File f = new File(value);
			if (!f.canRead()) {
				continue;
			}
			if (!f.exists()) {
				continue;
			}
			return new BypassWork(f);
		}
		return null;
	}

	public void destroy() {
		setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setup(FilterConfig filterConfig) {
		String value = getBypassParameter(DynamicPropertyFile.PROP, new File(DEVTOOLS_PROPERTIES).getAbsolutePath(), null);
		setPropertiesFile(new File(value), new File(System.getProperty("user.home"), DEVTOOLS_PROPERTIES), new File("/", DEVTOOLS_PROPERTIES));
		if ("true".equals(getBypassParameter("devtools.bypass.active", "false", null))) {
			setEnabled(true);
		}
		if (isEnabled()) {
			LOG.warning("-- DevTools:ON --");
		} else {
			LOG.info("-- DevTools: OFF --");
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}
