package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public final class CreateIndexCommand extends AbstractCommand {

	private final ERTable table;

	private final Index newIndex;

	private List<Index> oldIndexList;

	public CreateIndexCommand(ERDiagram diagram, Index newIndex) {
		this.table = newIndex.getTable();
		this.newIndex = newIndex;
	}

	@Override
	protected void doExecute() {
		this.oldIndexList = newIndex.getTable().getIndexes();

		final List<Index> newIndexList = new ArrayList<Index>(oldIndexList);
		newIndexList.add(newIndex);
		this.table.setIndexes(newIndexList);
	}

	@Override
	protected void doUndo() {
		if (this.oldIndexList != null) {
			this.table.setIndexes(this.oldIndexList);
		}
	}
}
