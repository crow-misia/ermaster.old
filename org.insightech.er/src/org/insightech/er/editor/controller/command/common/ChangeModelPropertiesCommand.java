package org.insightech.er.editor.controller.command.common;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.util.NameValue;

public final class ChangeModelPropertiesCommand extends AbstractCommand {

	private final List<NameValue> oldProperties;

	private final List<NameValue> newProperties;

	private final ModelProperties modelProperties;

	public ChangeModelPropertiesCommand(ERDiagram diagram,
			ModelProperties properties) {
		this.modelProperties = diagram.getDiagramContents().getSettings()
				.getModelProperties();

		this.oldProperties = this.modelProperties.getProperties();
		this.newProperties = properties.getProperties();
	}

	@Override
	protected void doExecute() {
		this.modelProperties.setProperties(newProperties, true);

	}

	@Override
	protected void doUndo() {
		this.modelProperties.setProperties(oldProperties, true);
	}
}
