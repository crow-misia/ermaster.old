package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeCapitalCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final boolean oldCapital;

	private final boolean newCapital;

	private final Settings settings;

	public ChangeCapitalCommand(ERDiagram diagram, boolean isCapital) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newCapital = isCapital;
		this.oldCapital = this.settings.isCapital();
	}

	@Override
	protected void doExecute() {
		this.settings.setCapital(this.newCapital);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.settings.setCapital(this.oldCapital);
		this.diagram.changeAll();
	}
}
