package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.List;

import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public final class CreateRelationCommand extends AbstractCreateRelationCommand {

	private final Relation relation;

	private final List<NormalColumn> foreignKeyColumnList;

	public CreateRelationCommand(Relation relation) {
		this(relation, null);
	}

	public CreateRelationCommand(Relation relation,
			List<NormalColumn> foreignKeyColumnList) {
		super();
		this.relation = relation;
		this.foreignKeyColumnList = foreignKeyColumnList;
	}

	@Override
	protected void doExecute() {
		ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource(getSourceModel(), false);

		ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView(getTargetModel(),
				this.foreignKeyColumnList, true, true);
	}

	@Override
	protected void doUndo() {
		ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource(null, false);

		ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView(null, true, true);

		getTargetModel().setDirty();
	}
}
