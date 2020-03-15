package de.e_nexus.web.tomcat.chrome.bypass.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public final class Utils {

	public static String toMD5(String key) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			throw new Error("MD5 must be known, its essential!");
		}
		byte[] digest = md5.digest(key.getBytes());
		return byteToHex(digest);
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
