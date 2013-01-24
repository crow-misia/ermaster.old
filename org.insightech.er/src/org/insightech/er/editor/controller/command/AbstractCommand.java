package org.insightech.er.editor.controller.command;

import org.eclipse.gef.commands.Command;
import org.insightech.er.Activator;

public abstract class AbstractCommand extends Command {

	@Override
	public final void execute() {
		try {
			doExecute();
		} catch (final Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	@Override
	public final void undo() {
		try {
			doUndo();
		} catch (final Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	/**
	 * コマンドを実行する
	 */
	protected abstract void doExecute();

	/**
	 * 実行したコマンドの結果を取り消す
	 */
	protected abstract void doUndo();
}
