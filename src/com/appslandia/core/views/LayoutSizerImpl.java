package com.appslandia.core.views;

import android.util.DisplayMetrics;

public class LayoutSizerImpl extends LayoutSizer {

	private final int gapDp;

	public LayoutSizerImpl(int gapDp) {
		this.gapDp = gapDp;
	}

	@Override
	protected int doMeasureWidth(DisplayMetrics displayMetrics, boolean orientationPortrait) {
		return displayMetrics.widthPixels - (int) (this.gapDp * displayMetrics.density);
	}
}
