package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public final class MoveRelationBendpointCommand extends AbstractCommand {

	private final Relation relation;

	private final Bendpoint bendPoint;

	private final Bendpoint oldBendpoint;

	private final int index;

	private final boolean relative;

	public MoveRelationBendpointCommand(RelationEditPart editPart, int x,
			int y, int index) {
		this.relation = (Relation) editPart.getModel();
		this.bendPoint = new Bendpoint(x, y);
		this.index = index;
		this.relative = relation.getBendpoints().get(0).isRelative();

		if (relative) {
			this.oldBendpoint = relation.getBendpoints().get(0);
		} else {
			this.oldBendpoint = relation.getBendpoints().get(index);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (relative) {
			this.bendPoint.setRelative(true);

			float rateX = (100f - (bendPoint.getX() / 2)) / 100;
			float rateY = (100f - (bendPoint.getY() / 2)) / 100;

			relation.setSourceLocationp(100, (int) (100 * rateY));
			relation.setTargetLocationp((int) (100 * rateX), 100);

			relation.setParentMove();

			relation.replaceBendpoint(0, this.bendPoint, true);

		} else {
			relation.replaceBendpoint(index, this.bendPoint, true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		if (relative) {
			float rateX = (100f - (this.oldBendpoint.getX() / 2)) / 100;
			float rateY = (100f - (this.oldBendpoint.getY() / 2)) / 100;

			relation.setSourceLocationp(100, (int) (100 * rateY));
			relation.setTargetLocationp((int) (100 * rateX), 100);

			relation.setParentMove();

			relation.replaceBendpoint(0, this.oldBendpoint, true);

		} else {
			relation.replaceBendpoint(index, this.oldBendpoint, true);
		}
	}

}
