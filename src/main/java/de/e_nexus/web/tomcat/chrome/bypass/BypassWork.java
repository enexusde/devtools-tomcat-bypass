package de.e_nexus.web.tomcat.chrome.bypass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * The order to bypass a local-file-request to the server.
 * <p>
 * Since this work is serializable it might be part of the http-session and
 * might get passivated.
 * 
 * <p>
 * Reactivation on a different system might cause a
 * {@link FileNotFoundException}.
 */
public final class BypassWork implements Serializable {

	/**
	 * The serial version uid.
	 */
	private static final long serialVersionUID = -679309896733271951L;

	/**
	 * The file to override.
	 */
	private final File overrideFile;

	public BypassWork(File overrideFile) {
		this.overrideFile = overrideFile;
	}

	/**
	 * Bypass the local file into the http-request.
	 * <p>
	 * However if the original service might add some header this bypass does not.
	 * 
	 * @param request  The request, never <code>null</code>.
	 * @param response The response, never <code>null</code>.
	 * @throws IOException In the rare case that a request-socket has been closed
	 *                     unexpectedly.
	 */
	public void doBypass(ServletRequest request, ServletResponse response) throws IOException {
		try (FileInputStream fis = new FileInputStream(overrideFile)) {
			byte[] tmp = new byte[1024];
			int read = 0;
			ServletOutputStream outputStream = response.getOutputStream();
			while ((read = fis.read(tmp)) > -1) {
				outputStream.write(tmp, 0, read);
			}
		}
	}

	@Override
	public String toString() {
		return super.toString().concat("[").concat(String.valueOf(overrideFile)).concat("]");
	}
}
