package org.insightech.er.editor.controller.editpart.element.node.column;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.ColumnSelectionHandlesEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.NormalColumnComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.dialog.group.GroupDialog;
import org.insightech.er.editor.view.dialog.word.column.ViewColumnDialog;
import org.insightech.er.editor.view.dialog.word.column.real.ColumnDialog;

public abstract class ColumnEditPart extends AbstractModelEditPart {

	public abstract void refreshTableColumns(UpdatedNodeElement updated);

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new ColumnSelectionHandlesEditPolicy());
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NormalColumnComponentEditPolicy());
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		EditPart editPart = super.getTargetEditPart(request);

		if (!this.getDiagram().isDisableSelectColumn()) {
			return editPart;
		}

		if (editPart != null) {
			return editPart.getParent();
		}

		return null;
	}

	@Override
	public final void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				performRequestOpen();

			} catch (Exception e) {
				Activator.showExceptionDialog(e);
			}
		}

		super.performRequest(request);
	}

	protected void performRequestOpen() {
		final Column column = (Column) this.getModel();
		final ColumnHolder columnHolder = column.getColumnHolder();
		final ERDiagram diagram = this.getDiagram();
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		Command command = null;
		if (columnHolder instanceof ERTable) {
			command = ColumnDialog.openDialog(shell, (ERTable) columnHolder, (NormalColumn) column);
		} else if (columnHolder instanceof View) {
			command = ViewColumnDialog.openDialog(shell, (View) columnHolder, (NormalColumn) column);
		} else if (columnHolder instanceof ColumnGroup) {
			command = GroupDialog.openDialog(shell, (ColumnGroup) columnHolder, diagram);

		}

		if (command != null) {
			this.executeCommand(command);
			setSelected(getSelected());
		}
	}
}
