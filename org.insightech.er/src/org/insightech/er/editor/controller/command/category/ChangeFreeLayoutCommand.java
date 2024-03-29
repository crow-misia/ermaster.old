package org.insightech.er.editor.controller.command.category;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.CategorySetting;

public final class ChangeFreeLayoutCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final boolean oldFreeLayout;

	private final boolean newFreeLayout;

	private final CategorySetting categorySettings;

	public ChangeFreeLayoutCommand(ERDiagram diagram, boolean isFreeLayout) {
		this.diagram = diagram;
		this.categorySettings = this.diagram.getDiagramContents().getSettings()
				.getCategorySetting();

		this.newFreeLayout = isFreeLayout;
		this.oldFreeLayout = this.categorySettings.isFreeLayout();
	}

	@Override
	protected void doExecute() {
		this.categorySettings.setFreeLayout(this.newFreeLayout);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.categorySettings.setFreeLayout(this.oldFreeLayout);
		this.diagram.changeAll();
	}
}
