package org.insightech.er.editor.controller.editpart.element.node.column;

import org.eclipse.draw2d.IFigure;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.IndexHeaderFigure;

public class IndexHeaderEditPart extends ColumnEditPart {

	@Override
	protected IFigure createFigure() {
		return new IndexHeaderFigure();
	}

	@Override
	protected void createEditPolicies() {
		// do nothing.
	}

	@Override
	public void refreshTableColumns(UpdatedNodeElement updated) {
		final ERDiagram diagram = this.getDiagram();

		final IndexHeaderFigure figure = (IndexHeaderFigure) this.getFigure();

		final int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		if (notationLevel == Settings.NOTATION_LEVLE_TITLE) {
			figure.clearLabel();
		} else {
			TableViewEditPart parent = (TableViewEditPart) this.getParent();
			parent.getContentPane().add(figure);

			TableFigure tableFigure = (TableFigure) parent.getFigure();

			addIndexHeaderFigure(diagram, tableFigure, figure);
		}
	}

	public static void addIndexHeaderFigure(ERDiagram diagram,
			TableFigure tableFigure, IndexHeaderFigure figure) {
		tableFigure.addIndexHeader(figure);
	}
}
