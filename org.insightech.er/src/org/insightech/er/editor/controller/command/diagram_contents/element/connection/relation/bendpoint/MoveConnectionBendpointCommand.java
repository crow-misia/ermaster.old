package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.eclipse.gef.ConnectionEditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class MoveConnectionBendpointCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final Bendpoint bendPoint;

	private final Bendpoint oldBendpoint;

	private final int index;

	private final boolean relative;

	public MoveConnectionBendpointCommand(ConnectionEditPart editPart, int x,
			int y, int index) {
		this.connection = (ConnectionElement) editPart.getModel();
		this.bendPoint = new Bendpoint(x, y);
		this.index = index;
		this.relative = connection.getBendpoints().get(0).isRelative();

		if (relative) {
			this.oldBendpoint = connection.getBendpoints().get(0);
		} else {
			this.oldBendpoint = connection.getBendpoints().get(index);
		}
	}

	@Override
	protected void doExecute() {
		if (relative) {
			this.bendPoint.setRelative(true);

			float rateX = (100f - (bendPoint.getX() / 2)) / 100;
			float rateY = (100f - (bendPoint.getY() / 2)) / 100;

			connection.setSourceLocationp(100, (int) (100 * rateY));
			connection.setTargetLocationp((int) (100 * rateX), 100);

			connection.setParentMove();

			connection.replaceBendpoint(0, this.bendPoint, true);

		} else {
			connection.replaceBendpoint(index, this.bendPoint, true);
		}
	}

	@Override
	protected void doUndo() {
		if (relative) {
			float rateX = (100f - (this.oldBendpoint.getX() / 2)) / 100;
			float rateY = (100f - (this.oldBendpoint.getY() / 2)) / 100;

			connection.setSourceLocationp(100, (int) (100 * rateY));
			connection.setTargetLocationp((int) (100 * rateX), 100);

			connection.setParentMove();

			connection.replaceBendpoint(0, this.oldBendpoint, true);

		} else {
			connection.replaceBendpoint(index, this.oldBendpoint, true);
		}
	}

}
