package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public final class ChangeTableViewPropertyCommand extends AbstractCommand {

	private final TableView oldCopyTableView;

	private final TableView tableView;

	private final TableView newCopyTableView;

	public ChangeTableViewPropertyCommand(TableView tableView,
			TableView newCopyTableView) {
		this.tableView = tableView;
		this.oldCopyTableView = tableView.copyData();
		this.newCopyTableView = newCopyTableView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.newCopyTableView.restructureData(tableView);
		this.tableView.getDiagram().changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.oldCopyTableView.restructureData(tableView);
		this.tableView.getDiagram().changeAll();
	}

}
