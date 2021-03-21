package com.appslandia.core.utils;

import java.util.Arrays;

public class ArrayUtils {

	public static char[] fill(char[] chars, char chr) {
		Arrays.fill(chars, chr);
		return chars;
	}

	public static byte[] appendArrays(byte[] src1, byte[] src2) {
		byte[] result = new byte[src1.length + src2.length];

		System.arraycopy(src1, 0, result, 0, src1.length);
		System.arraycopy(src2, 0, result, src1.length, src2.length);
		return result;
	}

	public static int[] remove(int[] src, int index) {
		int[] result = new int[src.length - 1];

		System.arraycopy(src, 0, result, 0, index);
		System.arraycopy(src, index + 1, result, index, src.length - 1 - index);

		return result;
	}
}
