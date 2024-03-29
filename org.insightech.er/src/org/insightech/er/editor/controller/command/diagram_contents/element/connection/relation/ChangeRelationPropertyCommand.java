package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public final class ChangeRelationPropertyCommand extends AbstractCommand {

	private final Relation oldCopyRelation;

	private final Relation newCopyRelation;

	private final Relation relation;

	private final TableView oldTargetTable;

	public ChangeRelationPropertyCommand(Relation relation,
			Relation newCopyRelation) {
		this.relation = relation;
		this.oldCopyRelation = relation.copy();
		this.newCopyRelation = newCopyRelation;

		this.oldTargetTable = relation.getTargetTableView().copyData();
	}

	@Override
	protected void doExecute() {
		this.newCopyRelation.restructureRelationData(this.relation);

		if (this.newCopyRelation.isReferenceForPK()) {
			this.relation.setForeignKeyColumnForPK();

		} else if (this.newCopyRelation.getReferencedComplexUniqueKey() != null) {
			this.relation.setForeignKeyForComplexUniqueKey(this.newCopyRelation
					.getReferencedComplexUniqueKey());

		} else {
			this.relation.setForeignKeyColumn(this.newCopyRelation
					.getReferencedColumn());
		}
	}

	@Override
	protected void doUndo() {
		this.oldCopyRelation.restructureRelationData(this.relation);

		this.relation
				.setReferenceForPK(this.oldCopyRelation.isReferenceForPK());
		this.relation.setReferencedComplexUniqueKey(this.oldCopyRelation
				.getReferencedComplexUniqueKey());
		this.relation.setReferencedColumn(this.oldCopyRelation
				.getReferencedColumn());

		this.oldTargetTable.restructureData(this.relation.getTargetTableView());
	}

}
