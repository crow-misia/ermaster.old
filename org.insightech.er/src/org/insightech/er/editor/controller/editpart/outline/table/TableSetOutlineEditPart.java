package org.insightech.er.editor.controller.editpart.outline.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.settings.Settings;

public class TableSetOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
			refresh();
		}
	}

	@Override
	protected List getModelChildren() {
		TableSet tableSet = (TableSet) this.getModel();

		List<ERTable> list = new ArrayList<ERTable>();

		Category category = this.getCurrentCategory();
		for (ERTable table : tableSet) {
			if (category == null || category.contains(table)) {
				list.add(table);
			}
		}

		if (this.getDiagram().getDiagramContents().getSettings()
				.getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
			Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);

		} else {
			Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);

		}

		return list;
	}

	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.table")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

	@Override
	protected void refreshChildren() {
		super.refreshChildren();

		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}

}
