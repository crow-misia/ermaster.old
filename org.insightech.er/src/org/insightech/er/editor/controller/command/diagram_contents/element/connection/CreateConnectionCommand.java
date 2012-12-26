package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

	private final ConnectionElement connection;

	public CreateConnectionCommand(ConnectionElement connection) {
		super();
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		connection.setSource((NodeElement) source.getModel(), false);
		connection.setTarget((NodeElement) target.getModel(), false);
		connection.setDirtyForConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		connection.setSource(null, false);
		connection.setTarget(null, false);
		connection.setDirtyForConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String validate() {
		return null;
	}

}
