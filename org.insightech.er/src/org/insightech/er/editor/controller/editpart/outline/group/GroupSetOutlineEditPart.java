package org.insightech.er.editor.controller.editpart.outline.group;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class GroupSetOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(GroupSet.PROPERTY_CHANGE_GROUP_SET)) {
			refresh();
		}
	}

	@Override
	protected List getModelChildren() {
		GroupSet columnGroupSet = (GroupSet) this.getModel();

		return columnGroupSet.getGroupList();
	}

	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString
				.getResourceString("label.column.group")
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
