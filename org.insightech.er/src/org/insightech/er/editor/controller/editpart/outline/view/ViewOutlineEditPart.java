package org.insightech.er.editor.controller.editpart.outline.view;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
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
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;

public class ViewOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Override
	protected void refreshOutlineVisuals() {
		this.refreshName();

		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final View view = (View) this.getModel();
			final ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

			final Command command = ViewDialog.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					this.getViewer(), diagram,
					view, diagram.getDiagramContents().getGroups());

			if (command != null) {
				this.execute(command);
			}
		}

		super.performRequest(request);
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
	}

	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}

	protected void refreshName() {
		View model = (View) this.getModel();

		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		String name = null;

		int viewMode = diagram.getDiagramContents().getSettings()
				.getOutlineViewMode();

		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
			if (model.getPhysicalName() != null) {
				name = model.getPhysicalName();

			} else {
				name = "";
			}

		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
			if (model.getLogicalName() != null) {
				name = model.getLogicalName();

			} else {
				name = "";
			}

		} else {
			if (model.getLogicalName() != null) {
				name = model.getLogicalName();

			} else {
				name = "";
			}

			name += "/";

			if (model.getPhysicalName() != null) {
				name += model.getPhysicalName();

			}
		}

		this.setWidgetText(diagram.filter(name));
		this.setWidgetImage(Activator.getImage(ImageKey.VIEW));
	}
}
