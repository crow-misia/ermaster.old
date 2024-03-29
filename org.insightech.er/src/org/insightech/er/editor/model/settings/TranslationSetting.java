package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = -7691417386790834828L;

	private List<String> selectedTranslations;

	private boolean use;

	public TranslationSetting() {
		this.selectedTranslations = new ArrayList<String>();
	}

	/**
	 * allTranslations を取得します.
	 * 
	 * @return allTranslations
	 */
	public static List<String> getAllTranslations() {
		List<String> list = PreferenceInitializer.getAllUserTranslations();

		list.add(ResourceString.getResourceString("label.translation.default"));

		return list;
	}

	/**
	 * selectedTranslations を取得します.
	 * 
	 * @return selectedTranslations
	 */
	public List<String> getSelectedTranslations() {
		return selectedTranslations;
	}

	/**
	 * selectedTranslations を設定します.
	 * 
	 * @param selectedTranslations
	 *            selectedTranslations
	 */
	public void setSelectedTranslations(List<String> selectedTranslations) {
		this.selectedTranslations = selectedTranslations;
	}

	/**
	 * selectedTranslations を設定します.
	 * 
	 * @param selectedTranslations
	 *            selectedTranslations
	 */
	public void selectDefault() {
		this.selectedTranslations.add(ResourceString
				.getResourceString("label.translation.default"));
	}

	/**
	 * use を取得します.
	 * 
	 * @return use
	 */
	public boolean isUse() {
		return use;
	}

	/**
	 * use を設定します.
	 * 
	 * @param use
	 *            use
	 */
	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean isSelected(String translationName) {
		for (String translation : this.selectedTranslations) {
			if (translation.equals(translationName)) {
				return true;
			}
		}

		return false;
	}

	public void addTranslationAsSelected(String translation) {
		this.selectedTranslations.add(translation);
	}

	@Override
	public Object clone() {
		try {
			TranslationSetting settings = (TranslationSetting) super.clone();
			settings.selectedTranslations = new ArrayList<String>(this.selectedTranslations);

			return settings;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void load() {
	}

	protected void parseString(String stringList) {
	}
}
