package org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public final class CreateTriggerCommand extends AbstractCommand {

	private final TriggerSet triggerSet;

	private final Trigger trigger;

	public CreateTriggerCommand(ERDiagram diagram, Trigger trigger) {
		this.triggerSet = diagram.getDiagramContents().getTriggerSet();
		this.trigger = trigger;
	}

	@Override
	protected void doExecute() {
		this.triggerSet.addTrigger(this.trigger, true);
	}

	@Override
	protected void doUndo() {
		this.triggerSet.remove(this.trigger, true);
	}
}
