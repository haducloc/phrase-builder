package com.appslandia.phrasebuilder.utils;

import com.appslandia.core.utils.TextSecure;

public class TextSecureUtils {

	private static final class TextSecureHolder {
		//@formatter:off
		static final TextSecure Instance = new TextSecure()
												.add(new TextSecure.ForwardRevert())
												.add(new TextSecure.BackwardRevert(0.75f));
		//@formatter:on
	}

	public static String secure(String text, int revertLen) {
		return TextSecureHolder.Instance.secure(text, revertLen);
	}

	public static String unsecure(String text, int revertLen) {
		return TextSecureHolder.Instance.unsecure(text, revertLen);
	}
}
