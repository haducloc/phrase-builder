package com.appslandia.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

	private static final class Md5DigestHolder {
		static final MessageDigest Instance = initMessageDigest();

		private static MessageDigest initMessageDigest() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	public static byte[] md5Digest(byte[] data) {
		return Md5DigestHolder.Instance.digest(data);
	}
}
