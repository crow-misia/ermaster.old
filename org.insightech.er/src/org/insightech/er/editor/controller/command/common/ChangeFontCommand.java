package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public final class ChangeFontCommand extends AbstractCommand {

	private final ViewableModel viewableModel;

	private final String oldFontName;

	private final String newFontName;

	private final int oldFontSize;

	private final int newFontSize;

	public ChangeFontCommand(ViewableModel viewableModel, String fontName,
			int fontSize) {
		this.viewableModel = viewableModel;

		this.oldFontName = viewableModel.getFontName();
		this.oldFontSize = viewableModel.getFontSize();

		this.newFontName = fontName;
		this.newFontSize = fontSize;
	}

	@Override
	protected void doExecute() {
		this.viewableModel.setFontName(this.newFontName);
		this.viewableModel.setFontSize(this.newFontSize);
	}

	@Override
	protected void doUndo() {
		this.viewableModel.setFontName(this.oldFontName);
		this.viewableModel.setFontSize(this.oldFontSize);
	}
}
