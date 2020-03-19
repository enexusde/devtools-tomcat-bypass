package de.e_nexus.web.tomcat.chrome.bypass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * The order to bypass a file-request to the server.
 */
public final class BypassWork implements Serializable {

	private static final long serialVersionUID = -679309896733271951L;
	private final File overrideFile;

	public BypassWork(File overrideFile) {
		this.overrideFile = overrideFile;
	}

	public void doBypass(ServletRequest request, ServletResponse response) throws IOException {
		try (FileInputStream fis = new FileInputStream(overrideFile)) {
			byte[] tmp = new byte[1024];
			int read = 0;
			ServletOutputStream outputStream = response.getOutputStream();
			while ((read = fis.read(tmp)) > -0) {
				outputStream.write(tmp, 0, read);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString().concat("[").concat(String.valueOf(overrideFile)).concat("]");
	}
}
