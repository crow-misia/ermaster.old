package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeOutlineViewModeCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final int oldViewMode;

	private final int newViewMode;

	private final Settings settings;

	public ChangeOutlineViewModeCommand(ERDiagram diagram, int viewMode) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newViewMode = viewMode;
		this.oldViewMode = this.settings.getViewMode();
	}

	@Override
	protected void doExecute() {
		this.settings.setOutlineViewMode(this.newViewMode);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.settings.setOutlineViewMode(this.oldViewMode);
		this.diagram.changeAll();
	}
}
