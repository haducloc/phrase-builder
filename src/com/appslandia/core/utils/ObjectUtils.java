package com.appslandia.core.utils;

import java.util.ArrayList;
import java.util.List;

public class ObjectUtils {

	@SuppressWarnings("unchecked")
	public static <T2, T1> List<T2> castToList(List<T1> list) {
		return (List<T2>) list;
	}

	@SuppressWarnings("unchecked")
	public static <T2, T1> ArrayList<T2> castToArrayList(List<T1> list) {
		return (ArrayList<T2>) list;
	}
}
