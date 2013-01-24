package org.insightech.er.editor.controller.command.common;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;

public final class WithoutUpdateCommandWrapper extends Command {

	private final Command command;

	private final ERDiagram diagram;

	public WithoutUpdateCommandWrapper(Command command, ERDiagram diagram) {
		this.command = command;
		this.diagram = diagram;
	}

	@Override
	public void execute() {
		ERDiagramEditPart.setUpdateable(false);

		command.execute();

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	@Override
	public void undo() {
		ERDiagramEditPart.setUpdateable(false);

		command.undo();

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	@Override
	public boolean canExecute() {
		return command.canExecute();
	}

	@Override
	public boolean canUndo() {
		return command.canUndo();
	}

}
