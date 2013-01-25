package org.insightech.er.editor.controller.command.diagram_contents.not_element.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public final class ChangeIndexCommand extends AbstractCommand {

	private final ERTable table;

	private final Index oldIndex;

	private final Index newIndex;

	private List<Index> oldIndexList;

	public ChangeIndexCommand(ERDiagram diagram, Index oldIndex, Index newIndex) {
		this.table = oldIndex.getTable();
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	@Override
	protected void doExecute() {
		this.oldIndexList = oldIndex.getTable().getIndexes();

		final List<Index> newIndexList = new ArrayList<Index>(oldIndexList);
		final int i = newIndexList.indexOf(oldIndex);

		newIndexList.set(i, newIndex);

		this.table.setIndexes(newIndexList);
	}

	@Override
	protected void doUndo() {
		if (this.oldIndexList != null) {
			this.table.setIndexes(this.oldIndexList);
		}
	}
}
