package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class CreateBendpointCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final int x;

	private final int y;

	private final int index;

	public CreateBendpointCommand(ConnectionElement connection, int x, int y,
			int index) {
		this.connection = connection;
		this.x = x;
		this.y = y;
		this.index = index;
	}

	@Override
	protected void doExecute() {
		Bendpoint bendpoint = new Bendpoint(this.x, this.y);
		connection.addBendpoint(index, bendpoint, true);
	}

	@Override
	protected void doUndo() {
		connection.removeBendpoint(index, true);
	}
}
