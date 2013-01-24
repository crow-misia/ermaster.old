package org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public final class DeleteSequenceCommand extends AbstractCommand {

	private final SequenceSet sequenceSet;

	private final Sequence sequence;

	public DeleteSequenceCommand(ERDiagram diagram, Sequence sequence) {
		this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
		this.sequence = sequence;
	}

	@Override
	protected void doExecute() {
		this.sequenceSet.remove(this.sequence, true);
	}

	@Override
	protected void doUndo() {
		this.sequenceSet.addSequence(this.sequence, true);
	}
}
