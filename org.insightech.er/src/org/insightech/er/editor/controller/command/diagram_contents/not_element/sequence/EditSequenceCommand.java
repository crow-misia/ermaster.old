package org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public final class EditSequenceCommand extends AbstractCommand {

	private final SequenceSet sequenceSet;

	private final Sequence oldSequence;

	private final Sequence newSequence;

	public EditSequenceCommand(ERDiagram diagram, Sequence oldSequence,
			Sequence newSequence) {
		this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
		this.oldSequence = oldSequence;
		this.newSequence = newSequence;
	}

	@Override
	protected void doExecute() {
		this.sequenceSet.remove(this.oldSequence, false);
		this.sequenceSet.addSequence(this.newSequence, true);
	}

	@Override
	protected void doUndo() {
		this.sequenceSet.remove(this.newSequence, false);
		this.sequenceSet.addSequence(this.oldSequence, true);
	}
}
