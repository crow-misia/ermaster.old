package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public final class DeleteIndexCommand extends AbstractCommand {

	private final ERTable table;

	private final Index index;

	private List<Index> oldIndexList;

	public DeleteIndexCommand(ERDiagram diagram, Index index) {
		this.table = index.getTable();
		this.index = index;
	}

	@Override
	protected void doExecute() {
		this.oldIndexList = index.getTable().getIndexes();

		final List<Index> newIndexList = new ArrayList<Index>(oldIndexList);
		newIndexList.remove(index);

		this.table.setIndexes(newIndexList);
	}

	@Override
	protected void doUndo() {
		if (this.oldIndexList != null) {
			this.table.setIndexes(this.oldIndexList);
		}
	}
}
