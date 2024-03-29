package org.insightech.er.editor.view.action.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.ERDiagram;

public abstract class AbstractOutlineBaseAction extends Action {

	private TreeViewer treeViewer;

	public AbstractOutlineBaseAction(String id, String text,
			TreeViewer treeViewer) {
		this(id, text, SWT.NONE, treeViewer);
	}

	public AbstractOutlineBaseAction(String id, String text, int style,
			TreeViewer treeViewer) {
		super(text, style);
		this.setId(id);

		this.treeViewer = treeViewer;
	}

	@Override
	public final void runWithEvent(Event event) {
		try {
			execute(event);
		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	protected final void execute(final Command command) {
		this.treeViewer.getEditDomain().getCommandStack().execute(command);
	}

	protected ERDiagram getDiagram() {
		final EditPart editPart = treeViewer.getContents();
		return (ERDiagram) editPart.getModel();
	}

	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	abstract public void execute(Event event) throws Exception;

}
