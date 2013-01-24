package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;

public final class ChangeColumnOrderCommand extends AbstractCommand {

	private final TableView tableView;

	private final Column column;

	private final int newIndex;
	
	private final int oldIndex;

	public ChangeColumnOrderCommand(TableView tableView, Column column,
			int index) {
		this.tableView = tableView;
		this.column = column;
		this.oldIndex = this.tableView.getColumns().indexOf(column);
		
		if (this.oldIndex < index) {
			this.newIndex = index - 1;
		} else {
			this.newIndex = index;
		}
	}

	@Override
	protected void doExecute() {
		this.tableView.removeColumn(column, false);
		this.tableView.addColumn(newIndex, column, false);
		this.tableView.setDirty();
		this.tableView.getDiagram().changeAll();
	}

	@Override
	protected void doUndo() {
		this.tableView.removeColumn(column, false);
		this.tableView.addColumn(oldIndex, column, false);
		this.tableView.setDirty();
		this.tableView.getDiagram().changeAll();
	}

}
