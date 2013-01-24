package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class ReconnectSourceCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final int xp;

	private final int yp;

	private final int oldXp;

	private final int oldYp;

	public ReconnectSourceCommand(final ConnectionElement connection, final int xp, final int yp) {
		this.connection = connection;

		this.xp = xp;
		this.yp = yp;
		this.oldXp = connection.getSourceXp();
		this.oldYp = connection.getSourceYp();
	}

	@Override
	protected void doExecute() {
		connection.setSourceLocationp(this.xp, this.yp);
		connection.setParentMove();
	}

	@Override
	protected void doUndo() {
		connection.setSourceLocationp(this.oldXp, this.oldYp);
		connection.setParentMove();
	}

}
