package com.appslandia.core.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class StringUtils {

	public static final char TAB_CHAR = 9;
	public static final char RECORD_SEP_CHAR = 30;

	public static final char SPACE_CHAR = 32;
	public static final char HYPHEN_CHAR = 45;

	public static final String EMPTY_STRING = new String();
	public static final String[] EMPTY_STRINGS = new String[0];

	public static String truncate(String str, int maxLen) {
		if (str == null) {
			return null;
		}
		if (str.length() <= maxLen) {
			return str;
		}
		return str.substring(0, maxLen - 3) + ("...");
	}

	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static String trimToNull(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		return str.isEmpty() ? null : str;
	}

	public static String trimWhitespace(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(str.length());
		sb.append(str);

		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.length() != 0 ? sb.toString() : null;
	}

	@SuppressLint("DefaultLocale")
	public static String toText(String text, boolean firstUpperCase) {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(text.length());
		sb.append(text);

		int i = 0;
		while (i < sb.length()) {
			if (sb.charAt(i) == SPACE_CHAR) {
				while ((i < sb.length() - 1) && toText_nextInvalid(sb.charAt(i + 1))) {
					sb.deleteCharAt(i + 1);
				}
				if (i == sb.length() - 1) {
					sb.deleteCharAt(i);
				}
				i++;
			} else if (toText_toSpace(sb.charAt(i))) {
				sb.setCharAt(i, SPACE_CHAR);
			} else {
				i++;
			}
		}

		if (sb.length() > 0) {
			if (sb.charAt(0) == SPACE_CHAR) {
				sb.deleteCharAt(0);
			}
		}

		if (firstUpperCase) {
			if (sb.length() > 0) {
				sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
			}
		}
		return sb.length() == 0 ? null : sb.toString();
	}

	private static boolean toText_nextInvalid(char chr) {
		return chr == SPACE_CHAR || chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	private static boolean toText_toSpace(char chr) {
		return chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	private static final Pattern ACCENT_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

	@SuppressLint("DefaultLocale")
	public static String toSearchable(String text) {
		if (TextUtils.isEmpty(text)) {
			return EMPTY_STRING;
		}
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = ACCENT_PATTERN.matcher(text).replaceAll(EMPTY_STRING);

		StringBuilder sb = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char chr = text.charAt(i);
			int chrType = Character.getType(chr);
			if ((chrType != Character.NON_SPACING_MARK) && (chrType != Character.ENCLOSING_MARK)) {
				sb.append(chr);
			}
		}

		int i = 0;
		while (i < sb.length()) {
			if (sb.charAt(i) == SPACE_CHAR) {
				while ((i < sb.length() - 1) && toSearchable_nextInvalid(sb.charAt(i + 1))) {
					sb.deleteCharAt(i + 1);
				}
				if (i < sb.length() - 1) {
					sb.setCharAt(i, HYPHEN_CHAR);
				} else {
					sb.deleteCharAt(i);
				}
				i++;
			} else if (toSearchable_toSpace(sb.charAt(i))) {
				sb.setCharAt(i, SPACE_CHAR);
			} else {
				i++;
			}
		}

		if (sb.length() > 0) {
			if (sb.charAt(0) == HYPHEN_CHAR) {
				sb.deleteCharAt(0);
			}
		}
		return sb.length() == 0 ? EMPTY_STRING : sb.toString().toLowerCase();
	}

	private static boolean toSearchable_nextInvalid(char chr) {
		return chr == SPACE_CHAR || chr == HYPHEN_CHAR || chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	private static boolean toSearchable_toSpace(char chr) {
		return chr == HYPHEN_CHAR || chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	@SuppressLint("DefaultLocale")
	public static String toLabel(String text) {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(text.length());
		sb.append(text);

		int i = 0;
		while (i < sb.length()) {
			if (sb.charAt(i) == SPACE_CHAR) {
				while ((i < sb.length() - 1) && toLabel_nextInvalid(sb.charAt(i + 1))) {
					sb.deleteCharAt(i + 1);
				}
				if (i < sb.length() - 1) {
					sb.setCharAt(i, HYPHEN_CHAR);
				} else {
					sb.deleteCharAt(i);
				}
				i++;
			} else if (toLabel_toSpace(sb.charAt(i))) {
				sb.setCharAt(i, SPACE_CHAR);
			} else {
				i++;
			}
		}

		if (sb.length() > 0) {
			if (sb.charAt(0) == HYPHEN_CHAR) {
				sb.deleteCharAt(0);
			}
		}
		return sb.length() == 0 ? null : sb.toString().toLowerCase();
	}

	private static boolean toLabel_nextInvalid(char chr) {
		return chr == SPACE_CHAR || chr == HYPHEN_CHAR || chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	private static boolean toLabel_toSpace(char chr) {
		return chr == HYPHEN_CHAR || chr == TAB_CHAR || chr == RECORD_SEP_CHAR;
	}

	public static String toDisplayLabel(String label) {
		StringBuilder sb = new StringBuilder(label.length());
		for (int i = 0; i < label.length(); i++) {
			char chr = label.charAt(i);
			if (chr == HYPHEN_CHAR)
				sb.append(SPACE_CHAR);
			else
				sb.append(chr);
		}
		return sb.toString();
	}
}