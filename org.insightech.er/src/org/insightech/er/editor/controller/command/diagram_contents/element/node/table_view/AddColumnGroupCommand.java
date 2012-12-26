package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public final class AddColumnGroupCommand extends AbstractCommand {

	private final TableView tableView;

	private final ColumnGroup columnGroup;

	private final int index;

	public AddColumnGroupCommand(TableView tableView, ColumnGroup columnGroup,
			int index) {
		this.tableView = tableView;
		this.columnGroup = columnGroup;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (this.index != -1) {
			this.tableView.addColumn(index, columnGroup, true);
		}

		this.tableView.getDiagram().changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.tableView.removeColumn(columnGroup, true);
		this.tableView.getDiagram().changeAll();
	}

}
