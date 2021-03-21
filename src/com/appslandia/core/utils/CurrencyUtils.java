package com.appslandia.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtils {

	public static float roundHalfUp(float value) {
		BigDecimal bd = new BigDecimal(value);
		bd.setScale(2, RoundingMode.HALF_UP);

		return bd.floatValue();
	}
}
