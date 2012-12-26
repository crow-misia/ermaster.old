package org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public final class EditTriggerCommand extends AbstractCommand {

	private final TriggerSet triggerSet;

	private final Trigger oldTrigger;

	private final Trigger newTrigger;

	public EditTriggerCommand(ERDiagram diagram, Trigger oldTrigger,
			Trigger newTrigger) {
		this.triggerSet = diagram.getDiagramContents().getTriggerSet();
		this.oldTrigger = oldTrigger;
		this.newTrigger = newTrigger;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.triggerSet.remove(this.oldTrigger, false);
		this.triggerSet.addTrigger(this.newTrigger, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.triggerSet.remove(this.newTrigger, false);
		this.triggerSet.addTrigger(this.oldTrigger, true);
	}
}
