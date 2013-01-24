package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public final class DefaultLineCommand extends AbstractCommand {

	private int sourceXp;

	private int sourceYp;

	private int targetXp;

	private int targetYp;

	private final ConnectionElement connection;

	private final List<Bendpoint> oldBendpointList;

	public DefaultLineCommand(ERDiagram diagram, ConnectionElement connection) {
		if (connection instanceof Relation) {
			Relation relation = (Relation) connection;

			this.sourceXp = relation.getSourceXp();
			this.sourceYp = relation.getSourceYp();
			this.targetXp = relation.getTargetXp();
			this.targetYp = relation.getTargetYp();
		}

		this.connection = connection;
		this.oldBendpointList = this.connection.getBendpoints();
	}

	@Override
	protected void doExecute() {
		this.connection.setBendpoints(new ArrayList<Bendpoint>(), true);
		if (connection instanceof Relation) {
			Relation relation = (Relation) connection;

			relation.setSourceLocationp(-1, -1);
			relation.setTargetLocationp(-1, -1);
			relation.setParentMove();
		}
	}

	@Override
	protected void doUndo() {
		this.connection.setBendpoints(this.oldBendpointList, true);
		if (connection instanceof Relation) {
			Relation relation = (Relation) connection;

			relation.setSourceLocationp(this.sourceXp, this.sourceYp);
			relation.setTargetLocationp(this.targetXp, this.targetYp);
			relation.setParentMove();
		}
	}
}
