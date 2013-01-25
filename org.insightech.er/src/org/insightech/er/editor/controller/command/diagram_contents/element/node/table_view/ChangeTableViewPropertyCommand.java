package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public final class ChangeTableViewPropertyCommand extends AbstractCommand {

	private final TableView tableView;

	private final TableView newCopyTableView;

	private TableView oldCopyTableView;

	public ChangeTableViewPropertyCommand(TableView tableView,
			TableView newCopyTableView) {
		this.tableView = tableView;
		this.newCopyTableView = newCopyTableView;
	}

	@Override
	protected void doExecute() {
		this.oldCopyTableView = tableView.copyData();

		this.newCopyTableView.restructureData(tableView);
		this.tableView.getDiagram().changeAll();
	}

	@Override
	protected void doUndo() {
		if (this.oldCopyTableView != null) {
			this.oldCopyTableView.restructureData(tableView);
			this.tableView.getDiagram().changeAll();
		}
	}

}
