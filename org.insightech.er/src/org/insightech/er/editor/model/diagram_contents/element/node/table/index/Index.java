package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Index extends AbstractModel implements ObjectModel,
		Comparable<Index> {

	private static final long serialVersionUID = -6734284409681329690L;

	private String name;

	private boolean nonUnique;

	private boolean bitmap;

	private boolean fullText;

	private String type;

	private String description;

	private List<Boolean> descs;

	private List<NormalColumn> columns;

	private List<String> columnNames;

	private ERTable table;

	public Index(ERTable table, String name, boolean nonUnique, boolean bitmap, String type,
			String description) {
		this.table = table;

		this.nonUnique = nonUnique;
		this.bitmap = bitmap;
		this.type = type;
		this.description = description;

		this.descs = new ArrayList<Boolean>();

		this.columns = new ArrayList<NormalColumn>();
		this.columnNames = new ArrayList<String>();

		this.name = name;
	}

	public void setDescs(List<Boolean> descs) {
		this.descs = descs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNonUnique(boolean nonUnique) {
		this.nonUnique = nonUnique;
	}

	public void setColumns(List<NormalColumn> columns) {
		this.columns = columns;
	}

	public void addColumn(NormalColumn column) {
		this.columns.add(column);
	}

	public void addColumn(NormalColumn column, Boolean desc) {
		this.columns.add(column);
		this.descs.add(desc);
	}

	public List<NormalColumn> getColumns() {
		return this.columns;
	}

	public boolean isNonUnique() {
		return nonUnique;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void addColumnName(String columnName, Boolean desc) {
		this.columnNames.add(columnName);
		this.descs.add(desc);
	}

	public List<Boolean> getDescs() {
		return descs;
	}

	public String getName() {
		return name;
	}

	public boolean isFullText() {
		return fullText;
	}

	public void setFullText(boolean fullText) {
		this.fullText = fullText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBitmap() {
		return bitmap;
	}

	public void setBitmap(boolean bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public Index clone() {
		Index clone = (Index) super.clone();

		clone.descs = new ArrayList<Boolean>(this.descs);

		clone.columnNames = new ArrayList<String>(this.columnNames);

		return clone;
	}

	public int compareTo(Index other) {
		return this.name.toUpperCase().compareTo(other.name.toUpperCase());
	}

	public ERTable getTable() {
		return table;
	}

	protected void setTable(ERTable table) {
		this.table = table;
	}

	/**
	 * description を取得します.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * description を設定します.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getObjectType() {
		return "index";
	}
}
