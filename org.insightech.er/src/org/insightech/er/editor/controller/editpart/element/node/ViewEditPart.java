package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PlatformUI;
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
		final View view = (View) this.getModel();
		final ERDiagram diagram = this.getDiagram();

		final Command command = ViewDialog.openDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				this.getViewer(), diagram,
				view, diagram.getDiagramContents().getGroups());

		if (command != null) {
			this.execute(command);
		}
	}
}
