package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class DeleteConnectionCommand extends AbstractCommand {

	private final ConnectionElement connection;

	public DeleteConnectionCommand(ConnectionElement connection) {
		this.connection = connection;
	}

	@Override
	protected void doExecute() {
		this.connection.delete();
	}

	@Override
	protected void doUndo() {
		this.connection.connect();
		this.connection.setDirtyForBendpoint();
	}
}
