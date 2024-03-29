package org.insightech.er.editor.controller.editpolicy.element.node.note;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.note.NoteEditCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class NoteDirectEditPolicy extends DirectEditPolicy {

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		final String text = (String) request.getCellEditor().getValue();
		return new NoteEditCommand((Note) getHost().getModel(), text);
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
	}
}
