package com.appslandia.core.utils;

public class ThreadUtils {

	public static void trySleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException ex) {
		}
	}
}
