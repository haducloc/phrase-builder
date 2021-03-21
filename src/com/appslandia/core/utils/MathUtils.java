package com.appslandia.core.utils;

public class MathUtils {

	public static int pow(int x, int y) {
		if (y < 0) {
			throw new IllegalArgumentException("The given parameter 'y' can't be negative.");
		}
		int p = 1;
		while (y > 0) {
			p *= x;
			y--;
		}
		return p;
	}

	public static byte[] toByteArray(int value) {
		// @formatter:off
	    return new byte[] {
	        (byte) (value >> 24),
	        (byte) (value >> 16),
	        (byte) (value >> 8),
	        (byte) value};
		  // @formatter:on
	}

	public static byte[] toByteArray(long value) {
		byte[] result = new byte[8];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte) (value & 0xffL);
			value >>= 8;
		}
		return result;
	}

	public static int toInt(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	public static long toLong(byte[] bytes) {
		// @formatter:off
	    return (bytes[0] & 0xffL) << 56
	        | (bytes[1] & 0xffL) << 48
	        | (bytes[2] & 0xffL) << 40
	        | (bytes[3] & 0xffL) << 32
	        | (bytes[4] & 0xffL) << 24
	        | (bytes[5] & 0xffL) << 16
	        | (bytes[6] & 0xffL) << 8
	        | (bytes[7] & 0xffL);
	    // @formatter:on
	}
}
