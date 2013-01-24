package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.util.Check;

public final class ERTableEditPart extends TableViewEditPart implements IResizable {

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
		ERTable table = (ERTable) this.getModel();
		ERDiagram diagram = this.getDiagram();

		ERTable copyTable = table.copyData();

		TableDialog dialog = new TableDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), this.getViewer(),
				copyTable, diagram.getDiagramContents().getGroups());

		if (dialog.open() == IDialogConstants.OK_ID) {
			CompoundCommand command = createChangeTablePropertyCommand(diagram,
					table, copyTable);

			this.executeCommand(command.unwrap());
		}
	}

	public static CompoundCommand createChangeTablePropertyCommand(
			ERDiagram diagram, ERTable table, ERTable copyTable) {
		CompoundCommand command = new CompoundCommand();

		ChangeTableViewPropertyCommand changeTablePropertyCommand = new ChangeTableViewPropertyCommand(
				table, copyTable);
		command.add(changeTablePropertyCommand);

		String tableName = copyTable.getPhysicalName();

		if (!Check.isEmpty(tableName)) {
		    diagram.getDBManager().createAutoIncrement(diagram, table, copyTable, command, tableName);
		}

		return command;
	}
}
