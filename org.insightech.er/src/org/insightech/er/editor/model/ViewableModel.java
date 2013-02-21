package org.insightech.er.editor.model;

import org.apache.commons.lang.ArrayUtils;



public abstract class ViewableModel extends AbstractModel {

	private static final long serialVersionUID = 5866202173090969615L;

	public static final String PROPERTY_CHANGE_COLOR = "color";

	public static final String PROPERTY_CHANGE_FONT = "font";

	public static final int DEFAULT_FONT_SIZE = 9;

	private String fontName;

	private int fontSize;

	private int[] color;

	public ViewableModel() {
		this.fontName = null;
		this.fontSize = DEFAULT_FONT_SIZE;
	}

	public final int getFontSize() {
		return fontSize;
	}

	public final void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		this.firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
	}

	public final String getFontName() {
		return fontName;
	}

	public final void setFontName(String fontName) {
		this.fontName = fontName;		
		this.firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
	}

	public final void setColor(final int red, final int green, final int blue) {
		this.color = new int[] { red, green, blue, };
		

		this.firePropertyChange(PROPERTY_CHANGE_COLOR, null, null);
	}

	public final void setColor(final int[] color) {
		this.color = ArrayUtils.clone(color);

		this.firePropertyChange(PROPERTY_CHANGE_COLOR, null, null);
	}

	public final int[] getColor() {
		return this.color;
	}

	@Override
	public ViewableModel clone() {
		ViewableModel clone = (ViewableModel) super.clone();
		if (this.color != null) {
			clone.color = ArrayUtils.clone(this.color);
		}

		return clone;
	}
}
