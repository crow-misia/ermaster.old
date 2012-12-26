package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public final class DeleteElementCommand extends AbstractCommand {

	private final ERDiagram container;

	private final NodeElement element;

	public DeleteElementCommand(ERDiagram container, NodeElement element) {
		this.container = container;
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.container.removeContent(this.element, true, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.container.addContent(this.element, true, true);
	}
}
