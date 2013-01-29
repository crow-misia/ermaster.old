package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class TableViewEditPart extends NodeElementXYEditPart {

	private Font titleFont;

	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();

		TableView tableView = (TableView) this.getModel();

		ERDiagram diagram = this.getDiagram();
		if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
			modelChildren.addAll(tableView.getExpandedColumns());
			
		} else {
			modelChildren.addAll(tableView.getColumns());
		}

		return modelChildren;
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_PHYSICAL_NAME)) {
			refreshVisuals();
		} else if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_LOGICAL_NAME)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_COLUMNS)) {
			this.refreshChildren();
			refreshVisuals();
		}

		super.doPropertyChange(event);
	}

	@Override
	public void refreshVisuals() {
		try {
			final ERDiagram diagram = this.getDiagram();
			final TableFigure tableFigure = (TableFigure) this.getFigure();
			final TableView tableView = (TableView) this.getModel();
			
			// 依存・非依存でテーブルの形を変える
			final Settings settings = diagram.getDiagramContents().getSettings();
			if (settings.isNotationDependence()) {
				tableFigure.setDependence(tableView.isDependence());
			} else {
				tableFigure.setDependence(null);
			}

			tableFigure.create(tableView.getColor());

			tableFigure.setName(getTableViewName(tableView, diagram));

			UpdatedNodeElement updated = null;
			if (diagram.getChangeTrackingList().isCalculated()) {
				updated = diagram.getChangeTrackingList()
						.getUpdatedNodeElement(tableView);
			}

			for (Object child : this.getChildren()) {
				ColumnEditPart part = (ColumnEditPart) child;
				part.refreshTableColumns(updated);
			}

			if (updated != null) {
				showRemovedColumns(diagram, tableFigure, updated
						.getRemovedColumns(), true);
			}

			super.refreshVisuals();

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

	}

	public static void showRemovedColumns(ERDiagram diagram,
			TableFigure tableFigure, Collection<Column> removedColumns,
			boolean isRemoved) {

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		for (Column removedColumn : removedColumns) {

			if (removedColumn instanceof ColumnGroup) {
				if (diagram.getDiagramContents().getSettings()
						.isNotationExpandGroup()) {
					ColumnGroup columnGroup = (ColumnGroup) removedColumn;

					for (NormalColumn normalColumn : columnGroup.getColumns()) {
						if (notationLevel == Settings.NOTATION_LEVLE_KEY
								&& !normalColumn.isPrimaryKey()
								&& !normalColumn.isForeignKey()
								&& !normalColumn.isReferedStrictly()) {
							continue;
						}

						NormalColumnFigure columnFigure = new NormalColumnFigure();
						tableFigure.getColumns().add(columnFigure);

						NormalColumnEditPart.addColumnFigure(diagram, tableFigure,
								columnFigure, normalColumn, false, false, false,
								false, isRemoved);
					}
				
				} else {
					if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
						continue;
					}

					GroupColumnFigure columnFigure = new GroupColumnFigure();
					tableFigure.getColumns().add(columnFigure);

					GroupColumnEditPart.addGroupColumnFigure(diagram,
							tableFigure, columnFigure, removedColumn, false,
							false, isRemoved);
				}

			} else {
				NormalColumn normalColumn = (NormalColumn) removedColumn;
				if (notationLevel == Settings.NOTATION_LEVLE_KEY
						&& !normalColumn.isPrimaryKey()
						&& !normalColumn.isForeignKey()
						&& !normalColumn.isReferedStrictly()) {
					continue;
				}

				NormalColumnFigure columnFigure = new NormalColumnFigure();
				tableFigure.getColumns().add(columnFigure);

				NormalColumnEditPart.addColumnFigure(diagram, tableFigure,
						columnFigure, normalColumn, false, false, false,
						false, isRemoved);
			}
		}
	}

	@Override
	public void changeSettings(Settings settings) {
		TableFigure figure = (TableFigure) this.getFigure();
		figure.setTableStyle(settings.getTableStyle());

		super.changeSettings(settings);
	}

	@Override
	protected void disposeFont() {
		if (this.titleFont != null && !this.titleFont.isDisposed()) {
			this.titleFont.dispose();
			this.titleFont = null;
		}
		super.disposeFont();
	}

	protected Font changeFont(TableFigure tableFigure) {
		Font font = super.changeFont(tableFigure);

		FontData fonData = font.getFontData()[0];

		this.titleFont = new Font(Display.getCurrent(), fonData.getName(),
				fonData.getHeight(), SWT.BOLD);

		tableFigure.setFont(font, this.titleFont);

		return font;
	}

	public static String getTableViewName(TableView tableView, ERDiagram diagram) {
		final int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

		switch (viewMode) {
		case Settings.VIEW_MODE_PHYSICAL:
			return diagram.filter(tableView.getPhysicalName());
		case Settings.VIEW_MODE_LOGICAL:
			return diagram.filter(tableView.getLogicalName());
		default:
			return diagram.filter(tableView.getLogicalName()) + "/"
					+ diagram.filter(tableView.getPhysicalName());
		}
	}

	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) super.getContentPane();

		return figure.getColumns();
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new TableViewComponentEditPolicy());
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new TableViewGraphicalNodeEditPolicy());
	}
}
