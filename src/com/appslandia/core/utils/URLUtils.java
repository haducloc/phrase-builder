package com.appslandia.core.utils;

import java.io.CharArrayWriter;
import java.util.BitSet;

public class URLUtils {

	static BitSet dontNeedEncoding;
	static final int caseDiff = ('a' - 'A');

	static {
		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set(' ');
		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
	}

	public static String encodeQueryParam(String s) {
		boolean needToChange = false;
		StringBuffer out = new StringBuffer(s.length());
		CharArrayWriter charArrayWriter = new CharArrayWriter();

		for (int i = 0; i < s.length();) {
			int c = (int) s.charAt(i);
			if (dontNeedEncoding.get(c)) {
				if (c == ' ') {
					c = '+';
					needToChange = true;
				}
				out.append((char) c);
				i++;
			} else {
				// convert to external encoding before hex conversion
				do {
					charArrayWriter.write(c);

					// UNICODE surrogate pair
					if (c >= 0xD800 && c <= 0xDBFF) {
						if ((i + 1) < s.length()) {
							int d = (int) s.charAt(i + 1);
							if (d >= 0xDC00 && d <= 0xDFFF) {
								charArrayWriter.write(d);
								i++;
							}
						}
					}
					i++;
				} while (i < s.length() && !dontNeedEncoding.get((c = (int) s.charAt(i))));

				charArrayWriter.flush();
				String str = new String(charArrayWriter.toCharArray());
				byte[] ba = str.getBytes(CharsetUtils.UTF_8);
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);

					// Use UPPERCASE
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
					ch = Character.forDigit(ba[j] & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= caseDiff;
					}
					out.append(ch);
				}
				charArrayWriter.reset();
				needToChange = true;
			}
		}

		return (needToChange ? out.toString() : s);
	}

	public static String decodeQueryParam(String s) {
		boolean needToChange = false;
		int numChars = s.length();
		StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
		int i = 0;

		char c;
		byte[] bytes = null;
		while (i < numChars) {
			c = s.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				i++;
				needToChange = true;
				break;
			case '%':
				try {
					// (numChars-i)/3 is an upper bound for the number
					// of remaining bytes
					if (bytes == null)
						bytes = new byte[(numChars - i) / 3];
					int pos = 0;

					while (((i + 2) < numChars) && (c == '%')) {
						int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
						if (v < 0)
							throw new IllegalArgumentException("Illegal hex characters in escape (%) pattern - negative value");
						bytes[pos++] = (byte) v;
						i += 3;
						if (i < numChars)
							c = s.charAt(i);
					}

					// A trailing, incomplete byte encoding such as
					// "%x" will cause an exception to be thrown

					if ((i < numChars) && (c == '%'))
						throw new IllegalArgumentException("Incomplete trailing escape (%) pattern");

					sb.append(new String(bytes, 0, pos, CharsetUtils.UTF_8));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Illegal hex characters in escape (%) pattern - " + e.getMessage());
				}
				needToChange = true;
				break;
			default:
				sb.append(c);
				i++;
				break;
			}
		}

		return (needToChange ? sb.toString() : s);
	}

}
