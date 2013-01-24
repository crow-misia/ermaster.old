package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand<NodeElement, NodeElement> {

	private final ConnectionElement connection;

	public CreateConnectionCommand(ConnectionElement connection) {
		super();
		this.connection = connection;
	}

	@Override
	protected void doExecute() {
		connection.setSource(getSourceModel(), false);
		connection.setTarget(getTargetModel(), false);
		connection.setDirtyForConnection();
	}

	@Override
	protected void doUndo() {
		connection.setSource(null, false);
		connection.setTarget(null, false);
		connection.setDirtyForConnection();
	}

	@Override
	public String validate() {
		return null;
	}
}
