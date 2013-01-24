package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;

public final class ChangeColumnCommand extends AbstractCommand {

	private final Dictionary dictionary;

	private final TableView tableView;

	private final NormalColumn column;

	private final CopyColumn oldColumn;

	private final NormalColumn newColumn;

	public ChangeColumnCommand(final TableView tableView, final NormalColumn column, final NormalColumn newColumn) {
		this.dictionary = tableView.getDiagram().getDiagramContents().getDictionary();

		this.tableView = tableView;
		this.column = column;
		this.newColumn = newColumn;
		this.oldColumn = new CopyColumn(column);
	}

	@Override
	protected void doExecute() {
		NormalColumn.copyData(this.newColumn, this.column);
		this.column.setColumnHolder(tableView);
		this.dictionary.setDirty();
		this.tableView.setDirty();
		this.tableView.getDiagram().changeAll();
	}

	@Override
	protected void doUndo() {
		NormalColumn.copyData(this.oldColumn, this.column);
		this.column.setColumnHolder(tableView);
		this.dictionary.setDirty();
		this.tableView.setDirty();
		this.tableView.getDiagram().changeAll();
	}

}
