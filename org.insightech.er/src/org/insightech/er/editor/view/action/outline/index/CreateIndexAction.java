package org.insightech.er.editor.view.action.outline.index;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.action.outline.AbstractOutlineBaseAction;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;

public class CreateIndexAction extends AbstractOutlineBaseAction {

	public static final String ID = CreateIndexAction.class.getName();

	public CreateIndexAction(TreeViewer treeViewer) {
		super(ID,
				ResourceString.getResourceString("action.title.create.index"),
				treeViewer);
	}

	@Override
	public void execute(Event event) {

		final ERDiagram diagram = this.getDiagram();

		final List selectedEditParts = this.getTreeViewer().getSelectedEditParts();
		final EditPart editPart = (EditPart) selectedEditParts.get(0);
		final ERTable table = (ERTable) editPart.getModel();

		final Command command = IndexDialog.openDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				diagram, null, table);

		if (command != null) {
			this.execute(command);
		}
	}

}
