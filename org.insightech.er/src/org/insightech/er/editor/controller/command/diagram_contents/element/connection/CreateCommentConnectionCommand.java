package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public final class CreateCommentConnectionCommand extends CreateConnectionCommand {

	public CreateCommentConnectionCommand(ConnectionElement connection) {
		super(connection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		final NodeElement source = this.getSourceModel();
		final NodeElement target = this.getTargetModel();

		return
				source instanceof Note ||
				source instanceof InsertedImage ||
				target instanceof Note ||
				target instanceof InsertedImage;
	}

}
