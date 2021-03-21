package com.appslandia.core.utils;

import android.media.MediaPlayer;

public class MediaPlayerUtils {

	public static void release(MediaPlayer player) {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
		}
	}
}
