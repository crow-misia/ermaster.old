package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.figure.view.ViewFigure;

public class ViewEditPart extends TableViewEditPart {

	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		ViewFigure figure = new ViewFigure(settings.getTableStyle());

		this.changeFont(figure);

		return figure;
	}

	@Override
	public void performRequestOpen() {
		View view = (View) this.getModel();
		ERDiagram diagram = this.getDiagram();

		View copyView = view.copyData();

		ViewDialog dialog = new ViewDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), this.getViewer(),
				copyView, diagram.getDiagramContents().getGroups());

		if (dialog.open() == IDialogConstants.OK_ID) {
			Command command = createChangeViewPropertyCommand(diagram,
					view, copyView);

			this.executeCommand(command);
		}
	}

	public static Command createChangeViewPropertyCommand(
			ERDiagram diagram, View view, View copyView) {
		CompoundCommand command = new CompoundCommand();

		ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(
				view, copyView);
		command.add(changeViewPropertyCommand);

		return command.unwrap();
	}

}
