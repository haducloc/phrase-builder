package com.appslandia.core.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;

public class ToStringBuilder {

	private final int maxRecursivingLevel;
	private final int identTabs;

	public ToStringBuilder() {
		this(4, 0);
	}

	public ToStringBuilder(int maxRecursivingLevel) {
		this(maxRecursivingLevel, 0);
	}

	public ToStringBuilder(int maxRecursivingLevel, int identTabs) {
		this.maxRecursivingLevel = maxRecursivingLevel;
		this.identTabs = identTabs;
	}

	public String toString(Object obj) {
		return this.toString(obj, 1);
	}

	private String toString(Object obj, int recursivingLevel) {
		if (obj == null) {
			return "(null)";
		}
		if (obj instanceof Iterable) {
			return toStringIterable((Iterable<?>) obj, recursivingLevel);
		}
		if (obj instanceof Iterator) {
			return toStringIterator((Iterator<?>) obj, recursivingLevel);
		}
		if (obj instanceof Enumeration) {
			return toStringEnumeration((Enumeration<?>) obj, recursivingLevel);
		}
		if (obj.getClass().isArray()) {
			return toStringArray(obj, recursivingLevel);
		}
		if (obj instanceof Map) {
			return toStringMap((Map<?, ?>) obj, recursivingLevel);
		}
		if (obj instanceof Bundle) {
			return toStringBundle((Bundle) obj, recursivingLevel);
		}
		final TextBuilder buffer = new TextBuilder();

		if (obj instanceof CharSequence) {
			CharSequence s = (CharSequence) obj;
			return buffer.append("\"").append(s).append("\"").append(" (").append(s.length()).append(")").toString();
		}
		if (obj instanceof Type || obj instanceof Boolean || obj instanceof Character) {
			return buffer.append(obj).toString();
		}
		if (obj instanceof Number || obj instanceof Date) {
			return buffer.append(obj).append(" (").append(obj.getClass().getSimpleName()).append(")").toString();
		}
		if (obj instanceof Locale) {
			return buffer.append(obj).append(" (Locale)").toString();
		}
		if (obj.getClass().isEnum()) {
			return buffer.append(obj).append(" (Enum)").toString();
		}
		if (obj instanceof URL) {
			return buffer.append(obj).append(" (URL)").toString();
		}
		if (obj instanceof Void) {
			return buffer.append(obj).append(" (Void)").toString();
		}
		// To Fields
		return toStringFields(obj, recursivingLevel);
	}

	private String toStringIterable(Iterable<?> iter, int recursivingLevel) {
		if (iter == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(iter.getClass().getName()).append("@").append(Integer.toHexString(iter.hashCode())).append("-Iterable");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");
		boolean isFirst = true;

		Iterator<?> it = iter.iterator();
		while (it.hasNext()) {
			Object element = it.next();

			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);

			// ToString
			String elementToString = this.toString(element, recursivingLevel + 1);
			buffer.append(elementToString);
		}
		if (isFirst) {
			buffer.append(" no elements ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}
		return buffer.toString();
	}

	private String toStringIterator(Iterator<?> it, int recursivingLevel) {
		if (it == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(it.getClass().getName()).append("@").append(Integer.toHexString(it.hashCode())).append("-Iterator");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");
		boolean isFirst = true;

		while (it.hasNext()) {
			Object element = it.next();

			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);

			// ToString
			String elementToString = this.toString(element, recursivingLevel + 1);
			buffer.append(elementToString);
		}
		if (isFirst) {
			buffer.append(" no elements ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}
		return buffer.toString();
	}

	private String toStringEnumeration(Enumeration<?> enumer, int recursivingLevel) {
		if (enumer == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(enumer.getClass().getName()).append("@").append(Integer.toHexString(enumer.hashCode())).append("-Enumeration");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");
		boolean isFirst = true;

		while (enumer.hasMoreElements()) {
			Object element = enumer.nextElement();
			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);

			// ToString
			String elementToString = this.toString(element, recursivingLevel + 1);
			buffer.append(elementToString);
		}
		if (isFirst) {
			buffer.append(" no elements ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}
		return buffer.toString();
	}

	private String toStringArray(Object arr, int recursivingLevel) {
		if (arr == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(arr.getClass().getName()).append("@").append(Integer.toHexString(arr.hashCode())).append("-Array");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");
		boolean isFirst = true;

		int len = Array.getLength(arr);
		for (int i = 0; i < len; i++) {
			Object element = Array.get(arr, i);
			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);

			// ToString
			String elementToString = this.toString(element, recursivingLevel + 1);
			buffer.append(elementToString);
		}
		if (isFirst) {
			buffer.append(" no items ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}
		return buffer.toString();
	}

	private String toStringMap(Map<?, ?> map, int recursivingLevel) {
		if (map == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(map.getClass().getName()).append("@").append(Integer.toHexString(map.hashCode())).append("-Map");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");

		// Entries
		boolean isFirst = true;
		for (Object key : map.keySet()) {
			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);
			Object value = map.get(key);

			// ToString
			String valueToString = this.toString(value, recursivingLevel + 1);
			buffer.append(key).append(" = ").append(valueToString);
		}

		// Map getters
		Method[] methods = map.getClass().getMethods();
		for (Method mth : methods) {

			String mthName = mth.getName();
			if (((mthName.startsWith("get") == false) && (mthName.startsWith("is") == false)) || (mth.getParameterTypes().length > 0) || mthName.equals("getClass")) {
				continue;
			}
			if (mth.getAnnotation(Excluded.class) != null) {
				continue;
			}
			try {
				if (isFirst == false) {
					buffer.append(",");
				} else {
					isFirst = false;
				}
				buffer.appendln().appendtab(recursivingLevel + this.identTabs);

				Object getObj = mth.invoke(map);
				if (getObj == null) {
					buffer.append(mthName).append("() = (null)");

				} else if (mth.getAnnotation(Undetailed.class) == null) {
					String getObjToString = this.toString(getObj, recursivingLevel + 1);
					buffer.append(mthName).append("() = ").append(getObjToString);
				} else {
					buffer.append(mthName).append("() = ").append(getObj.getClass().getName()).append("@").append(Integer.toHexString(getObj.hashCode()));
				}

			} catch (Exception ex) {
				buffer.append(mthName).append("() = ERROR: ").append(ex.getMessage());
			}
		}

		if (isFirst) {
			buffer.append(" no entries/getters ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}

		return buffer.toString();
	}

	private String toStringBundle(Bundle bundle, int recursivingLevel) {
		if (bundle == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(bundle.getClass().getName()).append("@").append(Integer.toHexString(bundle.hashCode())).append("-Bundle");
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");

		// Entries
		boolean isFirst = true;
		for (String key : bundle.keySet()) {
			if (isFirst == false) {
				buffer.append(",");
			} else {
				isFirst = false;
			}
			buffer.appendln().appendtab(recursivingLevel + this.identTabs);
			Object value = bundle.get(key);

			// ToString
			String valueToString = this.toString(value, recursivingLevel + 1);
			buffer.append(key).append(" = ").append(valueToString);
		}

		buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		return buffer.toString();
	}

	private String toStringFields(Object obj, int recursivingLevel) {
		if (obj == null) {
			return "(null)";
		}
		TextBuilder buffer = new TextBuilder();

		buffer.append(obj.getClass().getName()).append("@").append(Integer.toHexString(obj.hashCode()));
		if (recursivingLevel >= this.maxRecursivingLevel) {
			return buffer.toString();
		}
		buffer.append("[");

		// Fields
		boolean isFirst = true;
		Field[] fields = obj.getClass().getFields();
		for (Field field : fields) {

			// Skip: Static || Excluded
			if (Modifier.isStatic(field.getModifiers()) || field.getAnnotation(Excluded.class) != null) {
				continue;
			}
			try {
				if (isFirst == false) {
					buffer.append(",");
				} else {
					isFirst = false;
				}
				buffer.appendln().appendtab(recursivingLevel + this.identTabs);

				Object fieldVal = field.get(obj);
				if (fieldVal == null) {
					buffer.append(field.getName()).append(" = (null)");

				} else if (field.getAnnotation(Undetailed.class) == null) {
					String fieldToString = this.toString(fieldVal, recursivingLevel + 1);
					buffer.append(field.getName()).append(" = ").append(fieldToString);
				} else {
					buffer.append(field.getName()).append(" = ").append(fieldVal.getClass().getName()).append("@").append(Integer.toHexString(fieldVal.hashCode()));
				}
			} catch (Exception ex) {
				buffer.append(field.getName()).append(" = ERROR: ").append(ex.getMessage());
			}
		}

		// Getters
		Method[] methods = obj.getClass().getMethods();

		for (Method mth : methods) {
			String mthName = mth.getName();
			if (((mthName.startsWith("get") == false) && (mthName.startsWith("is") == false)) || (mth.getParameterTypes().length > 0) || mthName.equals("getClass")) {
				continue;
			}
			if (mth.getAnnotation(Excluded.class) != null) {
				continue;
			}
			try {
				if (isFirst == false) {
					buffer.append(",");
				} else {
					isFirst = false;
				}
				buffer.appendln().appendtab(recursivingLevel + this.identTabs);
				Object getObj = mth.invoke(obj);

				if (getObj == null) {
					buffer.append(mthName).append("() = (null)");

				} else if (mth.getAnnotation(Undetailed.class) == null) {
					String getObjToString = this.toString(getObj, recursivingLevel + 1);
					buffer.append(mthName).append("() = ").append(getObjToString);
				} else {
					buffer.append(mthName).append("() = ").append(getObj.getClass().getName()).append("@").append(Integer.toHexString(getObj.hashCode()));
				}

			} catch (Exception ex) {
				buffer.append(mthName).append("() = ERROR: ").append(ex.getMessage());
			}
		}

		if (isFirst) {
			buffer.append(" no fields/getters ]");
		} else {
			buffer.appendln().appendtab(recursivingLevel - 1 + this.identTabs).append("]");
		}
		return buffer.toString();
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Undetailed {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Excluded {
	}
}
