package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public abstract class AbstractCreateRelationCommand extends
		AbstractCreateConnectionCommand<ERTable, ERTable> {

	@Override
	public String validate() {
		ERTable sourceTable = this.getSourceModel();

		if (sourceTable.isReferable()) {
			return null;
		}
		return ResourceString
				.getResourceString("error.no.referenceable.column");
	}

	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		if (this.getSourceModel() instanceof ERTable
				&& this.getTargetModel() instanceof ERTable) {
			return true;
		}

		return false;
	}

}
