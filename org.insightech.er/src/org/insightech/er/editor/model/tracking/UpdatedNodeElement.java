package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class UpdatedNodeElement implements Serializable {

	private static final long serialVersionUID = -1547406607441505291L;

	private final NodeElement nodeElement;

	private final Set<AbstractModel> addedColumns;

	private final Set<AbstractModel> updatedColumns;

	private final Set<AbstractModel> removedColumns;

	public UpdatedNodeElement(NodeElement nodeElement) {
		this.nodeElement = nodeElement;

		this.addedColumns = new HashSet<AbstractModel>();
		this.updatedColumns = new HashSet<AbstractModel>();
		this.removedColumns = new HashSet<AbstractModel>();
	}

	public NodeElement getNodeElement() {
		return nodeElement;
	}

	public void setAddedColumns(Collection<? extends AbstractModel> columns) {
		this.addedColumns.clear();
		this.addedColumns.addAll(columns);
	}

	public void setUpdatedColumns(Collection<? extends AbstractModel> columns) {
		this.updatedColumns.clear();
		this.updatedColumns.addAll(columns);
	}

	public void setRemovedColumns(Collection<? extends AbstractModel> columns) {
		this.removedColumns.clear();
		this.removedColumns.addAll(columns);
	}

	public boolean isAdded(AbstractModel column) {
		if (this.addedColumns.contains(column)) {
			return true;
		}

		return false;
	}

	public boolean isUpdated(AbstractModel column) {
		if (this.updatedColumns.contains(column)) {
			return true;
		}

		return false;
	}

	public Set<? extends AbstractModel> getRemovedColumns() {
		return removedColumns;
	}

}
