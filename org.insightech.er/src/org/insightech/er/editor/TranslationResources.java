package org.insightech.er.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.util.StringUtils;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.io.IOUtils;

public class TranslationResources {

	private final Map<String, String> translationMap;
	private final Map<String, Pattern> cache;

	public TranslationResources(TranslationSetting translationSettings) {
		this.translationMap = new TreeMap<String, String>(
				new TranslationResourcesComparator());
		this.cache = new HashMap<String, Pattern>();

		String defaultFileName = ResourceString
				.getResourceString("label.translation.default");

		if (translationSettings.isUse()) {
			for (String translation : PreferenceInitializer
					.getAllUserTranslations()) {
				if (translationSettings.isSelected(translation)) {
					File file = new File(PreferenceInitializer
							.getTranslationPath(translation));

					if (file.exists()) {
						FileInputStream in = null;

						try {
							in = new FileInputStream(file);
							load(in);

						} catch (IOException e) {
							Activator.showExceptionDialog(e);

						} finally {
							IOUtils.closeQuietly(in);
						}
					}

				}
			}

			if (translationSettings.isSelected(defaultFileName)) {
				InputStream in = this.getClass().getResourceAsStream(
						"/translation.txt");
				try {
					load(in);

				} catch (IOException e) {
					Activator.showExceptionDialog(e);

				} finally {
					IOUtils.closeQuietly(in);
				}

			}
		}
	}

	private void load(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));

		String line = null;

		while ((line = reader.readLine()) != null) {
			int index = line.indexOf(",");
			if (index == -1 || index == line.length() - 1) {
				continue;
			}

			String key = line.substring(0, index).trim();
			if ("".equals(key)) {
				continue;
			}

			String value = line.substring(index + 1).trim();
			this.translationMap.put(key, value);
			this.cache.put(key, getPattern(key));

			key = key.replaceAll("[aiueo]", "");
			if (key.length() > 1) {
				this.translationMap.put(key, value);
				this.cache.put(key, getPattern(key));
			}
		}
	}
	
	private static Pattern getPattern(final String key) {
		return Pattern.compile("_*" + Pattern.quote(key) + "_*",
				Pattern.CASE_INSENSITIVE);
	}

	/**
	 * ERDiagram.properties の指定されたキーに対応する値を返します
	 * 
	 * @param key
	 *            ERDiagram.properties で定義されたキー
	 * @return ERDiagram.properties の指定されたキーに対応する値
	 */
	public String translate(String str) {
		for (Entry<String, String> entry : translationMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			Pattern p = cache.get(key);
			if (p == null) {
				p = getPattern(key);
			}
			Matcher m = p.matcher(str);
			str = m.replaceAll(value);
		}

		return str;
	}

	public boolean contains(String key) {
		return this.translationMap.containsKey(key);
	}

	/**
	 * 長い順に並べる。同じ長さなら辞書順。ただし [A-Z] より [_] を優先する。
	 */
	private static class TranslationResourcesComparator implements Comparator<String> {

		public int compare(String o1, String o2) {
			int diff = o2.length() - o1.length();
			if (diff != 0) {
				return diff;
			}
			return StringUtils.replace(o1, "_", " ")
					.compareTo(StringUtils.replace(o2, "_", " "));
		}
	}
}
