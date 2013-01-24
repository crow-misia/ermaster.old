package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;

public final class NothingToDoCommand extends AbstractCommand {
	
	/** インスタンス */
	public static final NothingToDoCommand INSTANCE = new NothingToDoCommand();

	private NothingToDoCommand() {
		// do nothing.
	}

	@Override
	protected void doExecute() {
		// do nothing.
	}

	@Override
	protected void doUndo() {
		// do nothing.
	}
}
