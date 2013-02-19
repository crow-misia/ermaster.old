package org.insightech.er.editor.controller.editpart.element.node.column;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.index.IndexComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.IndexFigure;

public class IndexEditPart extends ColumnEditPart {

	private boolean selected;

	@Override
	protected IFigure createFigure() {
		return new IndexFigure();
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new IndexComponentEditPolicy());
	}

	@Override
	public void refreshTableColumns(UpdatedNodeElement updated) {
		ERDiagram diagram = this.getDiagram();

		IndexFigure figure = (IndexFigure) this.getFigure();

		Index index = (Index) this.getModel();

		TableViewEditPart parent = (TableViewEditPart) this.getParent();
		parent.getContentPane().add(figure);

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		if (notationLevel == Settings.NOTATION_LEVLE_TITLE) {
			figure.clearLabel();
		} else {
			TableFigure tableFigure = (TableFigure) parent.getFigure();

			boolean isAdded = false;
			boolean isUpdated = false;
			if (updated != null) {
				isAdded = updated.isAdded(index);
				isUpdated = updated.isUpdated(index);
			}

			addIndexFigure(diagram, tableFigure, figure, index,
					isAdded, isUpdated, false);

			if (selected) {
				figure.setBackgroundColor(ColorConstants.titleBackground);
				figure.setForegroundColor(ColorConstants.titleForeground);
			}
		}
	}

	public static void addIndexFigure(ERDiagram diagram,
			TableFigure tableFigure, IndexFigure figure, Index index,
			boolean isAdded, boolean isUpdated, boolean isRemoved) {
		final Settings settings = diagram.getDiagramContents().getSettings();
		final int notationLevel = settings.getNotationLevel();

		final boolean displayIcon = notationLevel == Settings.NOTATION_LEVLE_DETAIL;

		tableFigure.addIndex(figure, settings.getViewMode(), diagram.filter(index.getName()),
				displayIcon, isAdded, isUpdated, isRemoved);
	}

	@Override
	public void setSelected(int value) {
		final EditPart parent = this.getParent();
		
		final IndexFigure figure = (IndexFigure) this.getFigure();

		Color backgroundColor = null;
		Color foregroundColor = null;
		boolean isSelected = false;

		if (value != 0 && parent != null
				&& parent.getParent() != null) {
			List selectedEditParts = this.getViewer().getSelectedEditParts();

			if (selectedEditParts == null || selectedEditParts.size() != 1) {
				return;
			}
			
			backgroundColor = ColorConstants.titleBackground;
			foregroundColor = ColorConstants.titleForeground;
			isSelected = true;
		}
		
		figure.setBackgroundColor(backgroundColor);
		figure.setForegroundColor(foregroundColor);
		selected = isSelected;

		super.setSelected(value);

	}
}
