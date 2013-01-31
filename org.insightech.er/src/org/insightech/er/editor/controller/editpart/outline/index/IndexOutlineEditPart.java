package org.insightech.er.editor.controller.editpart.outline.index;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.index.IndexComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;

public class IndexOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Override
	protected void refreshOutlineVisuals() {
		Index index = (Index) this.getModel();

		this.setWidgetText(this.getDiagram().filter(index.getName()));
		this.setWidgetImage(Activator.getImage(ImageKey.INDEX));
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new IndexComponentEditPolicy());
	}

	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}

	@Override
	public void performRequest(Request request) {
		final Index index = (Index) this.getModel();
		final ERDiagram diagram = this.getDiagram();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final Command command = IndexDialog.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					diagram, index, index.getTable());

			if (command != null) {
				this.execute(command);
			}
		}

		super.performRequest(request);
	}
}
