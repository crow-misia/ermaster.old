package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public final class CreateIndexCommand extends AbstractCommand {

	private final ERTable table;

	private final List<Index> oldIndexList;

	private final List<Index> newIndexList;

	public CreateIndexCommand(ERDiagram diagram, Index newIndex) {
		this.table = newIndex.getTable();

		this.oldIndexList = newIndex.getTable().getIndexes();
		this.newIndexList = new ArrayList<Index>(oldIndexList);

		this.newIndexList.add(newIndex);
	}

	@Override
	protected void doExecute() {
		this.table.setIndexes(this.newIndexList);
	}

	@Override
	protected void doUndo() {
		this.table.setIndexes(this.oldIndexList);
	}
}
