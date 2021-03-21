package com.appslandia.core.utils;

import java.util.Random;

public class RandomUtils {

	private static final class NumberRandomHolder {
		static final Random Instance = new Random();
	}

	public static int nextInt(int min, int max) {
		if (min == max) {
			return min;
		}
		return NumberRandomHolder.Instance.nextInt(max - min + 1) + min;
	}

	public static int nextInt(int max) {
		return NumberRandomHolder.Instance.nextInt(max + 1);
	}
}
