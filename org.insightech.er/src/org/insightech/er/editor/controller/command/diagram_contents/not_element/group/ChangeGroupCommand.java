package org.insightech.er.editor.controller.command.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public final class ChangeGroupCommand extends AbstractCommand {

	private final GroupSet groupSet;

	private final List<CopyGroup> newGroups;

	private final Map<TableView, List<Column>> oldColumnListMap;

	private final ERDiagram diagram;

	private List<CopyGroup> oldCopyGroups;

	public ChangeGroupCommand(ERDiagram diagram, GroupSet groupSet,
			List<CopyGroup> newGroups) {
		this.diagram = diagram;

		this.groupSet = groupSet;

		this.newGroups = newGroups;

		this.oldColumnListMap = new HashMap<TableView, List<Column>>();
	}

	@Override
	protected void doExecute() {
		ERDiagram diagram = this.diagram;

		this.groupSet.clear();
		this.oldColumnListMap.clear();

		this.oldCopyGroups = new ArrayList<CopyGroup>();
		for (ColumnGroup columnGroup : groupSet.getGroupList()) {
			CopyGroup oldCopyGroup = CopyGroup.getInstance(columnGroup);
			this.oldCopyGroups.add(oldCopyGroup);
		}

		final Dictionary dictionary = diagram.getDiagramContents().getDictionary();
		for (CopyGroup oldCopyColumnGroup : oldCopyGroups) {
			for (NormalColumn column : oldCopyColumnGroup.getColumns()) {
				dictionary.remove(((CopyColumn) column).getOriginalColumn(), false);
			}
		}

		for (CopyGroup newCopyColumnGroup : newGroups) {
			this.groupSet.add(newCopyColumnGroup.restructure(diagram, false), false);
		}
		this.groupSet.setDirty();

		dictionary.setDirty();

		for (TableView tableView : this.diagram.getDiagramContents()
				.getContents().getTableViewList()) {
			List<Column> columns = tableView.getColumns();
			List<Column> oldColumns = new ArrayList<Column>(columns);

			this.oldColumnListMap.put(tableView, oldColumns);

			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				Column column = iter.next();

				if (column instanceof ColumnGroup) {
					if (!this.groupSet.contains((ColumnGroup) column)) {
						iter.remove();
					}
				}
			}

			tableView.setColumns(columns, true);
		}
	}

	@Override
	protected void doUndo() {
		if (this.oldCopyGroups == null) {
			return;
		}
		
		ERDiagram diagram = this.diagram;

		this.groupSet.clear();

		final Dictionary dictionary = diagram.getDiagramContents().getDictionary();
		for (CopyGroup newCopyColumnGroup : newGroups) {
			for (NormalColumn column : newCopyColumnGroup.getColumns()) {
				dictionary.remove(((CopyColumn) column).getOriginalColumn(), false);
			}
		}

		for (CopyGroup copyGroup : oldCopyGroups) {
			ColumnGroup group = copyGroup.restructure(diagram, false);
			this.groupSet.add(group, false);
		}
		this.groupSet.setDirty();

		dictionary.setDirty();

		for (TableView tableView : this.oldColumnListMap.keySet()) {
			List<Column> oldColumns = this.oldColumnListMap.get(tableView);
			tableView.setColumns(oldColumns, true);
		}
	}
}
