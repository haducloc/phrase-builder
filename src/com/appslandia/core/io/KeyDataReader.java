package com.appslandia.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import com.appslandia.core.utils.CharsetUtils;
import com.appslandia.core.utils.MathUtils;

public class KeyDataReader implements Closeable {

	private final InputStream is;
	private final KeyDataHandler handler;

	public KeyDataReader(InputStream is, KeyDataHandler handler) {
		this.is = is;
		this.handler = handler;
	}

	public void startRead() throws IOException {
		while (true) {
			// keyLen: 1 byte
			int keyLen = this.is.read();
			if (keyLen == -1) {
				break;
			}

			// keyData
			byte[] keyData = new byte[keyLen];
			this.is.read(keyData);

			// dataLenBytes: 4 bytes
			byte[] dataLenBytes = new byte[4];
			this.is.read(dataLenBytes);
			int dataLen = MathUtils.toInt(dataLenBytes);

			// Data
			byte[] data = new byte[dataLen];
			this.is.read(data);

			// Callback handler
			if (this.handler.handle(new String(keyData, CharsetUtils.UTF_8), data) == false) {
				break;
			}
		}
	}

	@Override
	public void close() throws IOException {
		this.is.close();
	}
}