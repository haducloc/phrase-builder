package com.appslandia.core.io;

import java.io.IOException;

import android.app.backup.BackupDataOutput;

public class DataBackupWriter implements KeyDataOutput {

	private final BackupDataOutput output;

	public DataBackupWriter(BackupDataOutput output) {
		this.output = output;
	}

	@Override
	public void writeEntity(String key, byte[] data) throws IOException {
		this.output.writeEntityHeader(key, data.length);
		this.output.writeEntityData(data, data.length);
	}

	@Override
	public void close() throws IOException {
	}
}
