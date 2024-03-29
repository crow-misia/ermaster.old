package org.insightech.er;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public final class Resources {
	private static final Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

	public static final int BUTTON_WIDTH = 60;
	
	public static final int DESCRIPTION_WIDTH = 400;

	public static final Color PINK = getColor(255, 0, 255);

	public static final Color ADDED_COLOR = getColor(128, 128, 255);

	public static final Color UPDATED_COLOR = getColor(128, 255, 128);

	public static final Color REMOVED_COLOR = getColor(255, 128, 128);

	public static final Color GRID_COLOR = getColor(220, 220, 255);

	public static final Color DEFAULT_TABLE_COLOR = getColor(128, 128, 192);

	public static final Color SELECTED_REFERENCED_COLUMN = getColor(255, 230, 230);

	public static final Color SELECTED_FOREIGNKEY_COLUMN = getColor(230, 255, 230);

	public static final Color SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN = getColor(230, 230, 255);

	public static final Color VERY_LIGHT_GRAY = getColor(230, 230, 230);

	public static final Color LINE_COLOR = getColor(180, 180, 255);

	public static final Color TEST_COLOR = getColor(230, 230, 230);

	public static final Color PRIMARY_COLOR = getColor(252, 250, 167);

	public static final Color FOREIGN_COLOR = getColor(211, 231, 245);

	public static final Color NOT_NULL_COLOR = getColor(254, 228, 207);

	public static Color getColor(int[] rgb) {
		return getColor(rgb[0], rgb[1], rgb[2]);
	}
	public static Color getColor(final int r, final int g, final int b) {
		Integer key = Integer.valueOf(r * 1000000 + g * 1000 + b);

		Color color = colorMap.get(key);

		if (color != null) {
			return color;
		}

		color = new Color(Display.getCurrent(), r, g, b);
		colorMap.put(key, color);

		return color;
	}

	public static void disposeColorMap() {
		for (Color color : colorMap.values()) {
			if (!color.isDisposed()) {
				color.dispose();
			}
		}
	}

	private Resources() {
		// nothing.
	}
}
