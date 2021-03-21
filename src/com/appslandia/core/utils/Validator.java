package com.appslandia.core.utils;

import android.widget.EditText;

public final class Validator {

	public static void validateRequired(EditText editText, int requiredMessageId, Bool isValid) {
		if (editText.getError() != null) {
			isValid.setValue(false);
		} else {
			String value = editText.getText().toString().trim();
			if (value.isEmpty()) {
				editText.setError(editText.getResources().getString(requiredMessageId));
				isValid.setValue(false);
			}
		}
	}
}
