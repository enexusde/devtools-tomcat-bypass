package de.e_nexus.web.tomcat.chrome.bypass;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicPropertyFile {
	public final static String PROP = "devtools.file";
	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(DynamicPropertyFile.class.getCanonicalName());

	private Properties properties = null;

	private Long lastPropertyLoad = null;

	private File file = null;

	public boolean hasParam(String name) {
		if (repair()) {
			return properties.get(name) != null;
		}
		return false;
	}

	private boolean repair() {
		if (file == null) {
			return false;
		}
		if (!file.canRead()) {
			return false;
		}
		if (lastPropertyLoad == null || file.lastModified() > lastPropertyLoad) {
			return reload();
		}
		return true;
	}

	private synchronized boolean reload() {
		if (file == null) {
			return false;
		}
		try (FileInputStream fileStream = new FileInputStream(file)) {
			if (properties == null) {
				properties = new Properties();
			}
			properties.clear();
			properties.load(fileStream);
			return true;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Fault-Barier! Could not read properties file: " + file, e);
			return false;
		}
	}

	/**
	 * Returns the parameter from file.
	 * <p>
	 * If contents of the property has changed, they are reloaded.
	 * 
	 * <p>
	 * If no such value exists or no propertie-file is available it returns the
	 * err-value.
	 * 
	 * @param name The name of the parameter to be read from the property file.
	 * @param err  The err-value if no such property exists.
	 * @return The value of the parameter (never-null) or the err-value (nullable).
	 */
	public String getParam(String name, String err) {
		if (repair()) {
			return properties.get(name).toString();
		}
		return err;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
