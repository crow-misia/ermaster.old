package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeNotationCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final String oldNotation;

	private final String newNotation;

	private final Settings settings;

	public ChangeNotationCommand(ERDiagram diagram, String notation) {
		this.diagram = diagram;
		this.settings = diagram.getDiagramContents().getSettings();
		this.newNotation = notation;
		this.oldNotation = this.settings.getNotation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setNotation(this.newNotation);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setNotation(this.oldNotation);
		this.diagram.changeAll();
	}
}
