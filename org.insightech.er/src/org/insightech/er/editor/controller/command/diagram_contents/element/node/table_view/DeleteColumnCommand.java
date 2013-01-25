package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;

public final class DeleteColumnCommand extends AbstractCommand {

	private final TableView tableView;

	private final ColumnHolder holder;

	private final Column newColumn;

	private Column oldColumn;

	private int oldIndex;

	public DeleteColumnCommand(final TableView tableView, final Column column) {
		this.tableView = tableView;
		this.newColumn = column;
		this.holder = column.getColumnHolder();
	}

	@Override
	public boolean canExecute() {
		if (holder instanceof TableView) {
			final NormalColumn normalColumn = (NormalColumn) this.newColumn;
			return !normalColumn.isForeignKey()
					&& !normalColumn.isReferedStrictly();
		} else if (holder instanceof ColumnGroup) {
			return true;
		}
		return false;
	}
		
	@Override
	protected void doExecute() {
		if (holder instanceof TableView) {
			final NormalColumn normalColumn = (NormalColumn) this.newColumn;

			oldIndex = -1;
			for (final NormalColumn targetColumn : tableView.getNormalColumns()) {
				oldIndex++;
				final Column originalColumn;
				if (targetColumn instanceof CopyColumn) {
					originalColumn = ((CopyColumn) targetColumn).getOriginalColumn();
				} else {
					originalColumn = targetColumn;
				}
				if (originalColumn == normalColumn) {
					tableView.removeColumn(targetColumn, true);
					this.oldColumn = targetColumn;
					break;
				}
			}

		} else if (holder instanceof ColumnGroup) {
			final ColumnGroup columnGroup = (ColumnGroup) holder;

			oldIndex = -1;
			for (final Column targetColumn : tableView.getColumns()) {
				oldIndex++;
				final Column originalColumn;
				if (targetColumn instanceof NormalColumn) {
					continue;
				}
				if (targetColumn instanceof CopyGroup) {
					originalColumn = ((CopyGroup) targetColumn).getOriginal();
				} else {
					originalColumn = targetColumn;
				}
				if (originalColumn == columnGroup) {
					tableView.removeColumn(targetColumn, true);
					this.oldColumn = targetColumn;
					break;
				}
			}
		}
	}

	@Override
	protected void doUndo() {
		if (this.oldColumn == null || this.oldIndex < 0) {
			return;
		}

		tableView.addColumn(this.oldIndex, oldColumn, true);
	}

}
