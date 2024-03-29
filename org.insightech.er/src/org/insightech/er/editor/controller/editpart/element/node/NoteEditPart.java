package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.note.NoteDirectEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.view.editmanager.NoteCellEditor;
import org.insightech.er.editor.view.editmanager.NoteEditManager;
import org.insightech.er.editor.view.editmanager.NoteEditorLocator;
import org.insightech.er.editor.view.figure.NoteFigure;

public class NoteEditPart extends NodeElementXYEditPart {

	private NoteEditManager editManager = null;

	@Override
	protected IFigure createFigure() {
		NoteFigure noteFigure = new NoteFigure();

		this.changeFont(noteFigure);

		return noteFigure;
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Note.PROPERTY_CHANGE_NOTE)) {
			refreshVisuals();
		}

		super.doPropertyChange(event);
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
		this.installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NoteDirectEditPolicy());

		super.createEditPolicies();
	}

	@Override
	public void refreshVisuals() {
		Note note = (Note) this.getModel();

		NoteFigure figure = (NoteFigure) this.getFigure();

		figure.setText(note.getText(), note.getColor());

		super.refreshVisuals();
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				|| request.getType().equals(RequestConstants.REQ_OPEN)) {
			performDirectEdit();
		}
	}

	private void performDirectEdit() {
		final IFigure figure = getFigure();

		if (this.editManager == null) {
			this.editManager = new NoteEditManager(this, NoteCellEditor.class,
					new NoteEditorLocator(figure));
		}

		this.editManager.setFont(figure.getFont());
		this.editManager.show();
	}

	@Override
	protected void performRequestOpen() {
	}
}
