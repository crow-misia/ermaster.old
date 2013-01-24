package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeNotationLevelCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final int oldNotationLevel;

	private final int newNotationLevel;
	
	private final Settings settings;

	public ChangeNotationLevelCommand(ERDiagram diagram, int notationLevel) {
		this.diagram = diagram;
		this.settings = diagram.getDiagramContents().getSettings();
		this.newNotationLevel = notationLevel;
		this.oldNotationLevel = this.settings.getNotationLevel();
	}

	@Override
	protected void doExecute() {
		this.settings.setNotationLevel(this.newNotationLevel);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.settings.setNotationLevel(this.oldNotationLevel);
		this.diagram.changeAll();
	}
}
