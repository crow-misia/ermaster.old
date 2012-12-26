package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.eclipse.gef.ConnectionEditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class MoveBendpointCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final Bendpoint bendPoint;

	private final Bendpoint oldBendpoint;

	private final int index;

	public MoveBendpointCommand(ConnectionEditPart editPart, int x, int y,
			int index) {
		this.bendPoint = new Bendpoint(x, y);
		this.index = index;
		this.connection = (ConnectionElement) editPart.getModel();
		this.oldBendpoint = connection.getBendpoints().get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		connection.replaceBendpoint(index, this.bendPoint, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		connection.replaceBendpoint(index, this.oldBendpoint, true);
	}

}
