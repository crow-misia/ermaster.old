package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeNotationDependenceCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final boolean oldNotationDependence;

	private final boolean newNotationDependence;

	private final Settings settings;

	public ChangeNotationDependenceCommand(ERDiagram diagram,
			boolean notationDependence) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newNotationDependence = notationDependence;
		this.oldNotationDependence = this.settings.isNotationDependence();
	}

	@Override
	protected void doExecute() {
		this.settings.setNotationDependence(this.newNotationDependence);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.settings.setNotationDependence(this.oldNotationDependence);
		this.diagram.changeAll();
	}
}
