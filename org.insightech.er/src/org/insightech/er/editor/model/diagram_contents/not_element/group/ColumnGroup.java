package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ColumnGroup extends Column implements ObjectModel,
		Comparable<ColumnGroup>, ColumnHolder {

	private static final long serialVersionUID = -5923128797828160786L;

	private String id;

	private String groupName;

	private List<NormalColumn> columns;

	public ColumnGroup() {
		this.columns = new ArrayList<NormalColumn>();
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = StringUtils.isNumeric(id) ? id : null;
	}

	public static void setId(final Set<String> check, final ColumnGroup group) {
		String id = group.id;
		while (id == null) {
			id = Integer.toString(RandomUtils.nextInt());
			if (check.add(id)) {
				group.id = id;
				break;
			}
			id = null;
		}
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<NormalColumn> getColumns() {
		return this.columns;
	}

	public NormalColumn getColumn(int index) {
		return this.columns.get(index);
	}

	public void addColumn(NormalColumn column) {
		this.columns.add(column);
		column.setColumnHolder(this);
	}

	public void setColumns(List<NormalColumn> columns) {
		this.columns = columns;
		for (Column column : columns) {
			column.setColumnHolder(this);
		}
	}

	public void removeColumn(NormalColumn column) {
		this.columns.remove(column);
	}

	public List<TableView> getUsedTableList(ERDiagram diagram) {
		List<TableView> usedTableList = new ArrayList<TableView>();

		for (TableView table : diagram.getDiagramContents().getContents()
				.getTableViewList()) {
			for (Column tableColumn : table.getColumns()) {
				if (tableColumn == this) {
					usedTableList.add(table);
					break;
				}
			}
		}

		return usedTableList;
	}

	public int compareTo(ColumnGroup other) {
		if (other == null) {
			return -1;
		}

		if (this.groupName == null) {
			return 1;
		}
		if (other.getGroupName() == null) {
			return -1;
		}

		return this.groupName.toUpperCase().compareTo(
				other.getGroupName().toUpperCase());
	}

	@Override
	public String getName() {
		return this.getGroupName();
	}

	@Override
	public ColumnGroup clone() {
		ColumnGroup clone = (ColumnGroup) super.clone();

		List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>(this.columns.size());

		for (NormalColumn column : this.columns) {
			NormalColumn cloneColumn = (NormalColumn) column.clone();
			cloneColumns.add(cloneColumn);
		}

		clone.setColumns(cloneColumns);

		return clone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());

		sb.append(", id:").append(id);
		sb.append(", groupName:").append(groupName);
		sb.append(", columns:").append(columns);

		return sb.toString();
	}

	public String getDescription() {
		return "";
	}

	public String getObjectType() {
		return "group";
	}

}
