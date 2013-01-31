package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
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
import org.insightech.er.editor.controller.editpart.element.node.column.IndexEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.IndexFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class TableViewEditPart extends NodeElementXYEditPart {

	private Font titleFont;

	@SuppressWarnings("unchecked")
	@Override
	protected List<Object> getModelChildren() {
		final TableView tableView = (TableView) this.getModel();

		final ERDiagram diagram = this.getDiagram();
		if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
			return (List) tableView.getExpandedColumns();
		}

		return (List) tableView.getColumns();
	}

	@Override
	public void doPropertyChange(final PropertyChangeEvent event) {
		final String propertyName = event.getPropertyName();

		if (propertyName.equals(
				TableView.PROPERTY_CHANGE_PHYSICAL_NAME)) {
			refreshVisuals();
		} else if (propertyName.equals(
				TableView.PROPERTY_CHANGE_LOGICAL_NAME)) {
			refreshVisuals();

		} else if (propertyName.equals(
				TableView.PROPERTY_CHANGE_COLUMNS)) {
			this.refreshChildren();
			refreshVisuals();

		} else if (propertyName.equals(
				IndexSet.PROPERTY_CHANGE_INDEXES)) {
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
			TableFigure tableFigure, Collection<? extends AbstractModel> removedColumns,
			boolean isRemoved) {

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		for (final AbstractModel removedColumn : removedColumns) {

			if (removedColumn instanceof ColumnGroup) {
				final ColumnGroup columnGroup = (ColumnGroup) removedColumn;

				if (diagram.getDiagramContents().getSettings()
						.isNotationExpandGroup()) {

					for (NormalColumn normalColumn : columnGroup.getColumns()) {
						if (notationLevel == Settings.NOTATION_LEVLE_KEY
								&& !normalColumn.isPrimaryKey()
								&& !normalColumn.isForeignKey()
								&& !normalColumn.isReferedStrictly()) {
							continue;
						}

						NormalColumnFigure columnFigure = new NormalColumnFigure();
						tableFigure.getContent().add(columnFigure);

						NormalColumnEditPart.addColumnFigure(diagram, tableFigure,
								columnFigure, normalColumn, false, false, false,
								false, isRemoved);
					}
				
				} else {
					if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
						continue;
					}

					GroupColumnFigure columnFigure = new GroupColumnFigure();
					tableFigure.getContent().add(columnFigure);

					GroupColumnEditPart.addGroupColumnFigure(diagram,
							tableFigure, columnFigure, columnGroup, false,
							false, isRemoved);
				}

			} else if (removedColumn instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) removedColumn;
				if (notationLevel == Settings.NOTATION_LEVLE_KEY
						&& !normalColumn.isPrimaryKey()
						&& !normalColumn.isForeignKey()
						&& !normalColumn.isReferedStrictly()) {
					continue;
				}

				NormalColumnFigure columnFigure = new NormalColumnFigure();
				tableFigure.getContent().add(columnFigure);

				NormalColumnEditPart.addColumnFigure(diagram, tableFigure,
						columnFigure, normalColumn, false, false, false,
						false, isRemoved);

			} else if (removedColumn instanceof Index) {
				Index index = (Index) removedColumn;
				if (notationLevel == Settings.NOTATION_LEVLE_KEY) {
					continue;
				}

				IndexFigure figure = new IndexFigure();
				tableFigure.getContent().add(figure);

				IndexEditPart.addIndexFigure(diagram, tableFigure,
						figure, index, false, false, isRemoved);
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
		final TableFigure figure = (TableFigure) super.getContentPane();

		return figure.getContent();
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new TableViewComponentEditPolicy());
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new TableViewGraphicalNodeEditPolicy());
	}
}
