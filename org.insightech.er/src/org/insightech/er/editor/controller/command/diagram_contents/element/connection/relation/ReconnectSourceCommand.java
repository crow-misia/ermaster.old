package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public final class ReconnectSourceCommand extends AbstractCommand {

	private Relation relation;

	private final int xp;

	private final int yp;

	private int oldXp;

	private int oldYp;

	public ReconnectSourceCommand(Relation relation, int xp, int yp) {
		this.relation = relation;

		this.xp = xp;
		this.yp = yp;
		this.oldXp = relation.getSourceXp();
		this.oldYp = relation.getSourceYp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		relation.setSourceLocationp(this.xp, this.yp);
		relation.setParentMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		relation.setSourceLocationp(this.oldXp, this.oldYp);
		relation.setParentMove();
	}

}
