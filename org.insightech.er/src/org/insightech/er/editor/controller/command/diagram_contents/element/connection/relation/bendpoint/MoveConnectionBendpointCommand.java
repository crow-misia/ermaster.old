package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.eclipse.gef.ConnectionEditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public final class MoveConnectionBendpointCommand extends AbstractCommand {

	private final ConnectionElement connection;

	private final Bendpoint bendPoint;

	private final int index;

	private Bendpoint oldBendpoint;

	private boolean relative;

	private int sourceXp;
	private int sourceYp;
	private int targetXp;
	private int targetYp;

	public MoveConnectionBendpointCommand(final ConnectionEditPart editPart,
			final int x, final int y, final int index) {
		this.connection = (ConnectionElement) editPart.getModel();
		this.bendPoint = new Bendpoint(x, y);
		this.index = index;
	}

	@Override
	protected void doExecute() {
		final Bendpoint firstBendpoint = connection.getBendpoints().get(0);
		
		this.relative = firstBendpoint.isRelative();

		if (this.relative) {
			this.oldBendpoint = firstBendpoint;
			this.sourceXp = connection.getSourceXp();
			this.sourceYp = connection.getSourceYp();
			this.targetXp = connection.getTargetXp();
			this.targetYp = connection.getTargetYp();

			this.bendPoint.setRelative(true);

			final double rateX = (100.0 - (bendPoint.getX() / 2)) / 100.0;
			final double rateY = (100.0 - (bendPoint.getY() / 2)) / 100.0;

			connection.setSourceLocationp(100, (int) (100 * rateY));
			connection.setTargetLocationp((int) (100 * rateX), 100);

			connection.setParentMove();

			connection.replaceBendpoint(0, this.bendPoint, true);
		} else {
			this.oldBendpoint = connection.getBendpoints().get(index);

			connection.replaceBendpoint(index, this.bendPoint, true);
		}
	}

	@Override
	protected void doUndo() {
		if (this.oldBendpoint == null) {
			return;
		}
		
		if (this.relative) {
			connection.setSourceLocationp(this.sourceXp, this.sourceYp);
			connection.setTargetLocationp(this.targetXp, this.targetYp);

			connection.setParentMove();
		}
		connection.replaceBendpoint(index, this.oldBendpoint, true);
	}

}
