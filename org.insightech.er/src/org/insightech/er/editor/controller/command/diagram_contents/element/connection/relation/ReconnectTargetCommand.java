package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public final class ReconnectTargetCommand extends AbstractCommand {

	private final Relation relation;

	private final int xp;

	private final int yp;

	private final int oldXp;

	private final int oldYp;

	public ReconnectTargetCommand(Relation relation, int xp, int yp) {
		this.relation = relation;

		this.xp = xp;
		this.yp = yp;
		this.oldXp = relation.getTargetXp();
		this.oldYp = relation.getTargetYp();
	}

	@Override
	protected void doExecute() {
		relation.setTargetLocationp(this.xp, this.yp);
		relation.setParentMove();
	}

	@Override
	protected void doUndo() {
		relation.setTargetLocationp(this.oldXp, this.oldYp);
		relation.setParentMove();
	}
}
