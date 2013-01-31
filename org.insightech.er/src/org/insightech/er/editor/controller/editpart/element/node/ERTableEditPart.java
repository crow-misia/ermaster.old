package org.insightech.er.editor.controller.editpart.element.node;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.figure.table.TableFigure;

public final class ERTableEditPart extends TableViewEditPart implements IResizable {

	@Override
	protected List<Object> getModelChildren() {
		final List<Object> modelChildren = super.getModelChildren();

		final ERTable table = (ERTable) this.getModel();

		// add Index
		modelChildren.addAll(table.getIndexes());

		return modelChildren;
	}

	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		TableFigure figure = new TableFigure(settings.getTableStyle());

		this.changeFont(figure);

		return figure;
	}

	@Override
	public void performRequestOpen() {
		final ERTable table = (ERTable) this.getModel();
		final ERDiagram diagram = this.getDiagram();

		final Command command = TableDialog.openDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				this.getViewer(), diagram,
				table, diagram.getDiagramContents().getGroups());

		if (command != null) {
			this.execute(command);
		}
	}
}
