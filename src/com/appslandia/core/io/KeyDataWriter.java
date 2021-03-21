package com.appslandia.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import com.appslandia.core.utils.CharsetUtils;
import com.appslandia.core.utils.MathUtils;

public class KeyDataWriter implements Closeable, KeyDataOutput {

	private final OutputStream os;

	public KeyDataWriter(OutputStream os) {
		this.os = os;
	}

	@Override
	public void writeEntity(String key, byte[] data) throws IOException {
		byte[] keyData = key.getBytes(CharsetUtils.UTF_8);

		// keyLen: 1 byte
		this.os.write(keyData.length);

		// keyData
		this.os.write(keyData);

		// dataLenBytes: 4 bytes
		this.os.write(MathUtils.toByteArray(data.length));

		// Data
		this.os.write(data);
	}

	@Override
	public void close() throws IOException {
		this.os.close();
	}
}
