package com.appslandia.phrasebuilder.utils;

import com.appslandia.core.utils.ActivityUtils;
import com.appslandia.phrasebuilder.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PhraseBuilderUtils {

	public static final int PHRASES_LOAD_LIMIT = 25;

	public static void shareUs(Activity context) {
		Bundle params = new Bundle(2);
		params.putString(Intent.EXTRA_SUBJECT, context.getString(R.string.message_check_this_out));
		params.putString(Intent.EXTRA_TEXT, context.getString(R.string.share_us_text, "https://goo.gl/iYQsD5"));

		ActivityUtils.shareUs(context, params);
	}
}
