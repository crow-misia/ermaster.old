package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeSettingsCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final Settings oldSettings;

	private final Settings settings;

	public ChangeSettingsCommand(ERDiagram diagram, Settings settings) {
		this.diagram = diagram;
		this.oldSettings = this.diagram.getDiagramContents().getSettings();
		this.settings = settings;
	}

	@Override
	protected void doExecute() {
		this.diagram.setSettings(settings, true);
	}

	@Override
	protected void doUndo() {
		this.diagram.setSettings(oldSettings, true);
	}
}
