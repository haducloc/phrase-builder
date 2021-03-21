package com.appslandia.core.utils;

public class StringBuilderUtils {

	public static void revert(StringBuilder sb, int fromIdx, int toIdx) {
		int mid = (fromIdx + toIdx) / 2;
		for (int i = fromIdx; i <= mid; i++) {
			int j = fromIdx + toIdx - i;

			char chr = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(j));
			sb.setCharAt(j, chr);
		}
	}
}
