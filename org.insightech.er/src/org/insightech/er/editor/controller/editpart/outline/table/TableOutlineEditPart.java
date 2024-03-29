package org.insightech.er.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

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
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;

public class TableOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	@Override
	protected List getModelChildren() {
		List<AbstractModel> children = new ArrayList<AbstractModel>();

		ERTable table = (ERTable) this.getModel();

		Category category = this.getCurrentCategory();

		for (Relation relation : table.getIncomingRelations()) {
			if (category == null
					|| category.contains(relation.getSource())) {
				children.add(relation);
			}
		}

		children.addAll(table.getIndexes());

		return children;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ERTable.PROPERTY_CHANGE_PHYSICAL_NAME)) {
			refreshName();

		} else if (evt.getPropertyName().equals(
				ERTable.PROPERTY_CHANGE_LOGICAL_NAME)) {
			refreshName();

		} else if (evt.getPropertyName()
				.equals(ERTable.PROPERTY_CHANGE_COLUMNS)) {
			refresh();

		} else if (evt.getPropertyName().equals(
				IndexSet.PROPERTY_CHANGE_INDEXES)) {
			refresh();

		}
	}

	protected void refreshName() {
		ERTable model = (ERTable) this.getModel();

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
			final StringBuilder t = new StringBuilder();
			if (model.getLogicalName() != null) {
				t.append(model.getLogicalName());
			}

			t.append('/');

			if (model.getPhysicalName() != null) {
				t.append(model.getPhysicalName());
			}

			name = t.toString();
		}

		this.setWidgetText(diagram.filter(name));
		this.setWidgetImage(Activator.getImage(ImageKey.TABLE));
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
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
		// this.installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final ERTable table = (ERTable) this.getModel();
			final ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

			final Command command = TableDialog.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					this.getViewer(), diagram,
					table, diagram.getDiagramContents().getGroups());

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

	public boolean isDeleteable() {
		return true;
	}
}
