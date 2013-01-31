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

		if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
			TableFigure tableFigure = (TableFigure) parent.getFigure();

			boolean isAdded = false;
			boolean isUpdated = false;
			if (updated != null) {
				isAdded = updated.isAdded(index);
				isUpdated = updated.isUpdated(index);
			}

			if (notationLevel == Settings.NOTATION_LEVLE_KEY) {
				figure.clearLabel();
				return;
			}

			addIndexFigure(diagram, tableFigure, figure, index,
					isAdded, isUpdated, false);

			if (selected) {
				figure.setBackgroundColor(ColorConstants.titleBackground);
				figure.setForegroundColor(ColorConstants.titleForeground);
			}

		} else {
			figure.clearLabel();
			return;
		}
	}

	public static void addIndexFigure(ERDiagram diagram,
			TableFigure tableFigure, IndexFigure figure, Index index,
			boolean isAdded, boolean isUpdated, boolean isRemoved) {
		tableFigure.addIndex(figure, diagram.getDiagramContents()
				.getSettings().getViewMode(), diagram.filter(index.getName()),
				isAdded, isUpdated, isRemoved);
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
