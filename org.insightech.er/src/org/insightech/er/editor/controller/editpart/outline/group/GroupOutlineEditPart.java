package org.insightech.er.editor.controller.editpart.outline.group;

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
import org.insightech.er.editor.controller.editpolicy.not_element.group.GroupComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.dialog.group.GroupDialog;

public class GroupOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		ColumnGroup columnGroup = (ColumnGroup) this.getModel();

		this.setWidgetText(this.getDiagram().filter(columnGroup.getName()));
		this.setWidgetImage(Activator.getImage(ImageKey.GROUP));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final ColumnGroup columnGroup = (ColumnGroup) this.getModel();
			final ERDiagram diagram = this.getDiagram();

			final Command command = GroupDialog.openDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), columnGroup, diagram);

			if (command != null) {
				this.executeCommand(command);
			}
		}

		super.performRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new GroupComponentEditPolicy());
	}
}
