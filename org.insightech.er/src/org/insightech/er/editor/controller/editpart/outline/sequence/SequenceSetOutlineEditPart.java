package org.insightech.er.editor.controller.editpart.outline.sequence;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.TreeItem;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class SequenceSetOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				SequenceSet.PROPERTY_CHANGE_SEQUENCE_SET)) {
			refresh();
		}
	}

	@Override
	protected List getModelChildren() {
		SequenceSet sequenceSet = (SequenceSet) this.getModel();

		List<Sequence> sequenceList = sequenceSet.getSequenceList();

		Collections.sort(sequenceList);

		return sequenceList;
	}

	@Override
	protected void refreshOutlineVisuals() {
		if (!DBManagerFactory.getDBManager(this.getDiagram()).isSupported(
				SupportFunctions.SEQUENCE)) {
			((TreeItem) getWidget()).setForeground(ColorConstants.lightGray);

		} else {
			((TreeItem) getWidget()).setForeground(ColorConstants.black);

		}

		this.setWidgetText(ResourceString.getResourceString("label.sequence")
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
