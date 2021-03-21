package com.appslandia.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class FileUtils {

	public static void writeAppend(Context context, String fileName, String content) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(context.getFilesDir(), fileName), true));
		out.writeChar('\n');
		out.writeUTF(content);
		out.close();
	}

	public static void writeAppend(Context context, String fileName, Throwable throwable) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(context.getFilesDir(), fileName), true));
		out.writeChar('\n');
		out.writeUTF(Log.getStackTraceString(throwable));
		out.close();
	}
}
