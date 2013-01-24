package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public final class ChangeIndexCommand extends AbstractCommand {

	private final ERTable table;

	private final List<Index> oldIndexList;

	private final List<Index> newIndexList;

	public ChangeIndexCommand(ERDiagram diagram, Index oldIndex, Index newIndex) {
		this.table = oldIndex.getTable();

		this.oldIndexList = oldIndex.getTable().getIndexes();
		this.newIndexList = new ArrayList<Index>(oldIndexList);

		int i = this.newIndexList.indexOf(oldIndex);

		this.newIndexList.remove(i);
		this.newIndexList.add(i, newIndex);
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
