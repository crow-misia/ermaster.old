package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public final class ChangeBackgroundColorCommand extends AbstractCommand {

	private final ViewableModel model;

	private final int red;

	private final int green;

	private final int blue;

	private int[] oldColor;

	public ChangeBackgroundColorCommand(ViewableModel model, int red,
			int green, int blue) {
		this.model = model;

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	protected void doExecute() {
		int[] color = model.getColor();
		if (color == null) {
			color = new int[] { 255, 255, 255, };
		}
		this.oldColor = color;

		this.model.setColor(red, green, blue);
	}

	@Override
	protected void doUndo() {
		if (this.oldColor != null) {
			this.model.setColor(this.oldColor[0], this.oldColor[1], this.oldColor[2]);
		}
	}
}
