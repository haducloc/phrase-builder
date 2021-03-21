package com.appslandia.phrasebuilder;

import com.appslandia.core.views.HelpActivity;

import android.view.ViewGroup;
import android.webkit.WebView;

public class HelpActivityImpl extends HelpActivity {

	ViewGroup webViewLayout;
	WebView webView;

	@Override
	protected void initActivityProps() {
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.ActivityEditBackgroundColor));
	}

	@Override
	protected int getActivityLayoutId() {
		return R.layout.help_activity;
	}

	@Override
	protected void initWebView(String sectionId) {
		webViewLayout = (ViewGroup) findViewById(R.id.container);
		webView = (WebView) findViewById(R.id.help_activity_webview);

		if (sectionId == null) {
			webView.loadUrl("file:///android_asset/help.html");
		} else {
			webView.loadUrl("file:///android_asset/help.html#" + sectionId);
		}
	}

	@Override
	protected void onDestroy() {
		webViewLayout.removeView(webView);
		webView.destroy();
		webView = null;

		super.onDestroy();
	}
}
