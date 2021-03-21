package com.appslandia.core.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ActivityUtils {

	private static final class ActivityIdSeqHolder {
		static final AtomicInteger Instance = new AtomicInteger(0);
	}

	public static int nextId() {
		return ActivityIdSeqHolder.Instance.incrementAndGet();
	}

	public static void initActionBarUp(ActionBar actionBar) {
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(new ColorDrawable(android.R.color.transparent));
	}

	public static void initActivityBackground(Activity activity, int colorResId) {
		activity.getWindow().getDecorView().setBackgroundColor(activity.getResources().getColor(colorResId));
	}

	public static PackageInfo getPackageInfo(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException ex) {
			return new PackageInfo();
		}
	}

	public static void setTextClipboard(Context context, CharSequence text) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
	}

	public static CharSequence getTextClipboard(Context context) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = clipboard.getPrimaryClip();
		if (clip != null && clip.getItemCount() > 0) {
			return clip.getItemAt(0).coerceToText(context);
		}
		return null;
	}

	public static void navigateToApp(Activity activity) {
		navigateToApp(activity, activity.getPackageName());
	}

	public static void navigateToApp(Activity activity, String packageName) {
		Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			activity.startActivity(goToMarket);

		} catch (ActivityNotFoundException ex) {
			try {
				activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getGooglePlayUri(packageName))));
			} catch (Exception ex1) {
			}
		}
	}

	public static String getGooglePlayUri(String packageName) {
		return "https://play.google.com/store/apps/details?id=" + packageName;
	}

	public static void shareUs(Activity activity, Bundle params) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtras(params);

			String share = params.getString("title_share_us", "Share Us");
			activity.startActivity(Intent.createChooser(intent, share));

		} catch (Exception ex) {
		}
	}

	public static void emailUs(Activity activity, Bundle params) {

		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:"));
		intent.putExtras(params);

		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivity(intent);
		} else {
			String msg = params.getString("message_no_email_client");
			if (msg != null) {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
