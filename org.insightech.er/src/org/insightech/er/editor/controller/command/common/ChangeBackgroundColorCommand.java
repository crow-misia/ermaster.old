package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public final class ChangeBackgroundColorCommand extends AbstractCommand {

	private final ViewableModel model;

	private final int red;

	private final int green;

	private final int blue;

	private final int[] oldColor;

	public ChangeBackgroundColorCommand(ViewableModel model, int red,
			int green, int blue) {
		this.model = model;

		int[] color = model.getColor();
		if (color == null) {
			color = new int[] { 255, 255, 255, };
		}
		this.oldColor = color;

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.model.setColor(red, green, blue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.model.setColor(this.oldColor[0], this.oldColor[1],
				this.oldColor[2]);
	}
}
