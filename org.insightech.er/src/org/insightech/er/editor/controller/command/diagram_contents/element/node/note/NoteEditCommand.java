package org.insightech.er.editor.controller.command.diagram_contents.element.node.note;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public final class NoteEditCommand extends AbstractCommand {

	private final String oldText;

	private final String text;

	private final Note note;

	public NoteEditCommand(Note note, String text) {
		this.note = note;
		this.oldText = this.note.getText();
		this.text = text;
	}

	@Override
	protected void doExecute() {
		this.note.setText(text);
	}

	@Override
	protected void doUndo() {
		this.note.setText(oldText);
	}
}
