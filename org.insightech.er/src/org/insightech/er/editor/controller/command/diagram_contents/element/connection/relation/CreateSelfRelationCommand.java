package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public final class CreateSelfRelationCommand extends AbstractCreateRelationCommand {

	private final Relation relation;

	public CreateSelfRelationCommand(Relation relation) {
		super();
		this.relation = relation;
	}

	@Override
	public void setSource(EditPart source) {
		this.source = source;
		this.target = source;

	}

	@Override
	protected void doExecute() {
		ERDiagramEditPart.setUpdateable(false);

		boolean anotherSelfRelation = false;

		ERTable sourceTable = getSourceModel();

		for (Relation otherRelation : sourceTable.getOutgoingRelations()) {
			if (otherRelation.getSource() == otherRelation.getTarget()) {
				anotherSelfRelation = true;
				break;
			}
		}

		final int rate = anotherSelfRelation ? 50 : 100;

		Bendpoint bendpoint0 = new Bendpoint(rate, rate);
		bendpoint0.setRelative(true);

		int xp = 100 - (rate / 2);
		int yp = 100 - (rate / 2);

		relation.setSourceLocationp(100, yp);
		relation.setTargetLocationp(xp, 100);

		relation.addBendpoint(0, bendpoint0, true);

		relation.setSource(sourceTable, false);

		ERDiagramEditPart.setUpdateable(true);

		relation.setTargetTableView(getTargetModel(), true, true);

		sourceTable.setDirty();
	}

	@Override
	protected void doUndo() {
		ERDiagramEditPart.setUpdateable(false);

		relation.setSource(null, false);

		ERDiagramEditPart.setUpdateable(true);

		relation.setTargetTableView(null, true, true);

		this.relation.removeBendpoint(0, true);
		
		getTargetModel().setDirty();
	}

	@Override
	public boolean canExecute() {
		return source != null && target != null;
	}
}
