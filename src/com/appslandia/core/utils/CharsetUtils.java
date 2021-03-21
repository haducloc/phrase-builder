package com.appslandia.core.utils;

import java.nio.charset.Charset;

public class CharsetUtils {

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static byte[] toBytes(String str) {
		if (str == null) {
			return null;
		}
		return str.getBytes(UTF_8);
	}

	public static String toString(byte[] data) {
		if (data == null) {
			return null;
		}
		return new String(data, UTF_8);
	}
}
