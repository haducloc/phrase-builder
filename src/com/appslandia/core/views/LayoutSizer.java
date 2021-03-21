package com.appslandia.core.views;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public abstract class LayoutSizer {

	public final int measureWidth(Resources res) {
		boolean orientationPortrait = res.getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
		return doMeasureWidth(res.getDisplayMetrics(), orientationPortrait);
	}

	protected abstract int doMeasureWidth(DisplayMetrics displayMetrics, boolean orientationPortrait);
}
