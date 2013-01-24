package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeOutlineViewOrderByCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final int oldViewOrderBy;

	private final int newViewOrderBy;

	private final Settings settings;

	public ChangeOutlineViewOrderByCommand(ERDiagram diagram, int viewOrderBy) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newViewOrderBy = viewOrderBy;
		this.oldViewOrderBy = this.settings.getViewOrderBy();
	}

	@Override
	protected void doExecute() {
		this.settings.setViewOrderBy(this.newViewOrderBy);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.settings.setViewOrderBy(this.oldViewOrderBy);
		this.diagram.changeAll();
	}
}
