package org.insightech.er;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

public final class ResourceString {

	private static final ResourceBundle resource = ResourceBundle
			.getBundle("org.insightech.er.ERDiagram");;

	public static String getResourceString(String key) {
		try {
			return resource.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public static String getResourceString(String key, String[] args) {
		try {
			final String string = resource.getString(key);
			return  MessageFormat.format(string, (Object[]) args);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public static Map<String, String> getResources(String prefix) {
		Map<String, String> props = new TreeMap<String, String>(Collections
				.reverseOrder());
		Enumeration keys = resource.getKeys();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(prefix)) {
				props.put(key, resource.getString(key));
			}
		}

		return props;
	}

	private ResourceString() {
		// nothing.
	}
}
