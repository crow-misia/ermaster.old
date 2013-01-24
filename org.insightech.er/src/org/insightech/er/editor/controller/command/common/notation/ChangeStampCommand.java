package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

public final class ChangeStampCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final boolean oldStamp;

	private final boolean newStamp;

	private final ModelProperties modelProperties;

	public ChangeStampCommand(ERDiagram diagram, boolean isDisplay) {
		this.diagram = diagram;
		this.modelProperties = this.diagram.getDiagramContents().getSettings()
				.getModelProperties();
		this.newStamp = isDisplay;
		this.oldStamp = this.modelProperties.isDisplay();
	}

	@Override
	protected void doExecute() {
		this.modelProperties.setDisplay(this.newStamp, true);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.modelProperties.setDisplay(this.oldStamp, true);
		this.diagram.changeAll();
	}
}
