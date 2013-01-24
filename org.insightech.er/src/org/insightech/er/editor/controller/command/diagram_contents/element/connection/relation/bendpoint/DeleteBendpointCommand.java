package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class DeleteBendpointCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final Bendpoint oldBendpoint;

	private final int index;

	public DeleteBendpointCommand(ConnectionElement connection, int index) {
		this.connection = connection;
		this.index = index;
		this.oldBendpoint = this.connection.getBendpoints().get(index);
	}

	@Override
	protected void doExecute() {
		this.connection.removeBendpoint(index, true);
	}

	@Override
	protected void doUndo() {
		this.connection.addBendpoint(index, oldBendpoint, true);
	}
}
