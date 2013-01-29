package org.insightech.er.util;

public class Check {

	public static boolean isAlphabet(final String str) {
		final char[] ch = str.toCharArray();
		final int n = ch.length;

		for (int i = 0; i < n; i++) {
			final char c = ch[i];
			if (c < '0' || 'z' < c) {
				return false;
			}

			if ('9' < c && c < 'A') {
				return false;
			}

			if ('z' < c && c < '_') {
				return false;
			}

			if ('_' < c && c < 'a') {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(final Object str1, final Object str2) {
		if (str1 == null) {
			if (str2 == null) {
				return true;
			}

			return false;
		}

		return str1.equals(str2);
	}
}
