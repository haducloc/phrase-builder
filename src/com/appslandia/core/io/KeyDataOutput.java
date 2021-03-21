package com.appslandia.core.io;

import java.io.Closeable;
import java.io.IOException;

public interface KeyDataOutput extends Closeable {

	void writeEntity(String key, byte[] data) throws IOException;
}
