package org.insightech.er.editor.controller.editpart.outline.table;

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
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.util.Format;

public class RelationOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_PHYSICAL_NAME)) {
			refreshVisuals();

		} else if (evt.getPropertyName().equals(
				ConnectionElement.PROPERTY_CHANGE_CONNECTION_ATTRIBUTE)) {
			refreshVisuals();

		}

	}

	@Override
	protected void refreshOutlineVisuals() {
		Relation model = (Relation) this.getModel();

		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		int viewMode = diagram.getDiagramContents().getSettings()
				.getOutlineViewMode();

		boolean first = true;
		StringBuilder sb = new StringBuilder();

		for (NormalColumn foreignKeyColumn : model.getForeignKeyColumns()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}

			if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
				sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));

			} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
				sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));

			} else {
				sb.append(Format.null2blank(foreignKeyColumn.getLogicalName()));
				sb.append('/');
				sb.append(Format.null2blank(foreignKeyColumn.getPhysicalName()));
			}
		}

		this.setWidgetText(sb.toString());
		this.setWidgetImage(Activator.getImage(ImageKey.FOREIGN_KEY));
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new RelationEditPolicy());
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final Relation relation = (Relation) this.getModel();

			final Command command = RelationDialog.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					relation);

			if (command != null) {
				this.execute(command);
			}
		}

		super.performRequest(request);
	}

	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}
}
