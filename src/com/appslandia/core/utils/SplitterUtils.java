package com.appslandia.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SplitterUtils {

	private static final class CommaLbHolder {
		static final Pattern Instance = Pattern.compile(",|\r?\n");
	}

	public static String[] splitCommaLb(String str) {
		return CommaLbHolder.Instance.split(str);
	}

	public static String[] splitAndRemoveEmptyEntries(String str, char separator) {
		if (str == null) {
			return StringUtils.EMPTY_STRINGS;
		}
		int off = 0;
		int next = 0;
		List<String> list = new ArrayList<>();
		String item;
		while ((next = str.indexOf(separator, off)) != -1) {
			item = str.substring(off, next).trim();
			if (item.isEmpty() == false) {
				list.add(item);
			}
			off = next + 1;
		}
		if (off == 0) {
			item = str.trim();
			if (item.isEmpty() == false) {
				return new String[] { str };
			} else {
				return StringUtils.EMPTY_STRINGS;
			}
		}
		item = str.substring(off, str.length());
		if (item.isEmpty() == false) {
			list.add(item);
		}
		return list.toArray(new String[list.size()]);
	}

	public static String[] split(String str, char separator) {
		if (str == null) {
			return StringUtils.EMPTY_STRINGS;
		}
		int off = 0;
		int next = 0;
		List<String> list = new ArrayList<>();
		while ((next = str.indexOf(separator, off)) != -1) {
			list.add(str.substring(off, next));
			off = next + 1;
		}
		if (off == 0) {
			return new String[] { str };
		}
		list.add(str.substring(off, str.length()));
		return list.toArray(new String[list.size()]);
	}
}
