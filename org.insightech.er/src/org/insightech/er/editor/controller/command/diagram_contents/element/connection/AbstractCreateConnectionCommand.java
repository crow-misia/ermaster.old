package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public abstract class AbstractCreateConnectionCommand<S extends NodeElement, T extends NodeElement>
						extends AbstractCommand {

	protected EditPart source;

	protected EditPart target;

	public AbstractCreateConnectionCommand() {
		super();
	}

	public void setSource(EditPart source) {
		this.source = source;
	}

	public void setTarget(EditPart target) {
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public S getSourceModel() {
		return (S) this.source.getModel();
	}

	@SuppressWarnings("unchecked")
	public T getTargetModel() {
		return (T) this.target.getModel();
	}

	@Override
	public boolean canExecute() {
		return source != null && target != null && source != target;
	}

	abstract public String validate();

}
