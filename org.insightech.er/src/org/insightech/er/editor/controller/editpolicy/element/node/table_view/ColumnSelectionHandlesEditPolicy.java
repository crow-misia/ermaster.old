package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.DeleteRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeColumnOrderCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ColumnSelectionHandlesEditPolicy extends NonResizableEditPolicy {

	@Override
	protected List createSelectionHandles() {
		List list = new ArrayList();

		getHost().getRoot().getContents().refresh();

		// NonResizableHandleKit.addHandles((GraphicalEditPart) getHost(), list,
		// new SelectEditPartTracker(getHost()), SharedCursors.ARROW);
		return list;
	}

	private Rectangle getColumnRectangle() {
		ColumnEditPart columnEditPart = (ColumnEditPart) this.getHost();

		IFigure figure = columnEditPart.getFigure();
		Rectangle rect = figure.getBounds();

		int startY = 0;
		int endY = 0;

		Column column = null;
		if (columnEditPart.getModel() instanceof Column) {
			column = (Column) columnEditPart.getModel();
		}
		if (column != null && column.getColumnHolder() instanceof ColumnGroup) {
			ColumnGroup columnGroup = (ColumnGroup) column.getColumnHolder();

			final List<NormalColumn> columns = columnGroup.getColumns();
			NormalColumn firstColumn = columns.get(0);
			NormalColumn finalColumn = columns.get(columns.size() - 1);

			for (Object editPart : columnEditPart.getParent().getChildren()) {
				ColumnEditPart childrenColumnEditPart = (ColumnEditPart) editPart;
				if (childrenColumnEditPart.getModel() == firstColumn) {
					Rectangle bounds = childrenColumnEditPart.getFigure()
							.getBounds();
					startY = bounds.y;

				} else if (childrenColumnEditPart.getModel() == finalColumn) {
					Rectangle bounds = childrenColumnEditPart.getFigure()
							.getBounds();
					endY = bounds.y + bounds.height;
				}
			}

		} else {
			startY = rect.y;
			endY = rect.y + rect.height;
		}

		return new Rectangle(rect.x, startY, rect.width, endY - startY);
	}

	@Override
	public void showTargetFeedback(Request request) {
		if (request instanceof DirectEditRequest) {
			final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) this.getHost().getRoot();
			final ZoomManager zoomManager = rootEditPart.getZoomManager();
			final Rectangle clientArea = zoomManager.getViewport().getClientArea();
			
			final double zoom = zoomManager.getZoom();

			Rectangle columnRectangle = this.getColumnRectangle();
			int center = (int) ((columnRectangle.y + (columnRectangle.height / 2.0)) * zoom) - clientArea.y;

			DirectEditRequest directEditRequest = (DirectEditRequest) request;

			int y = 0;

			if (directEditRequest.getLocation().y < center) {
				y = columnRectangle.y - 1;

			} else {
				y = columnRectangle.y + columnRectangle.height - 1;
			}

			RectangleFigure feedbackFigure = new RectangleFigure();
			feedbackFigure.setForegroundColor(ColorConstants.lightGray);
			feedbackFigure.setBackgroundColor(ColorConstants.lightGray);
			feedbackFigure.setBounds(new Rectangle(
					(int) (zoom * columnRectangle.x), (int) (zoom * y),
					(int) (zoom * columnRectangle.width), (int) (zoom * 2)));

			LayerManager manager = (LayerManager) this.getHost().getRoot();
			IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);

			IFigure feedbackLayer = this.getFeedbackLayer();

			List children = this.getFeedbackLayer().getChildren();
			children.clear();

			feedbackLayer.setBounds(layer.getBounds());

			feedbackLayer.add(feedbackFigure);
			feedbackLayer.repaint();
		}

		super.showTargetFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		if (request instanceof DirectEditRequest) {
			this.getFeedbackLayer().getChildren().clear();
		}

		super.eraseTargetFeedback(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {

		if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP
				.equals(request.getType())
				|| ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP
						.equals(request.getType())) {
			DirectEditRequest editRequest = (DirectEditRequest) request;

			TableView tableView = (TableView) this.getHost().getParent()
					.getModel();
			ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest
					.getDirectEditFeature())
					.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

			Object parent = ((Map) editRequest.getDirectEditFeature())
					.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);

			if (parent == tableView
					|| !tableView.getColumns().contains(columnGroup)) {
				return getHost();
			}

		} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD
				.equals(request.getType())) {
			return getHost();

		} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN
				.equals(request.getType())) {
			return getHost();

		}

		return super.getTargetEditPart(request);
	}

	@Override
	public Command getCommand(Request request) {
		try {
			if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView tableView = (TableView) this.getHost().getParent()
						.getModel();
				ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest
						.getDirectEditFeature())
						.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

				if (!tableView.getColumns().contains(columnGroup)) {
					return new AddColumnGroupCommand(tableView, columnGroup,
							this.getColumnIndex(editRequest));
				}

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView table = (TableView) this.getHost().getParent()
						.getModel();
				UniqueWord word = (UniqueWord) editRequest.getDirectEditFeature();

				return new AddWordCommand(table, word, this
						.getColumnIndex(editRequest));

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView newTableView = (TableView) this.getHost().getParent()
						.getModel();

				return createMoveColumnCommand(editRequest, this.getHost()
						.getViewer(), newTableView, this
						.getColumnIndex(editRequest));

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView newTableView = (TableView) this.getHost().getParent()
						.getModel();

				return createMoveColumnGroupCommand(editRequest, newTableView,
						this.getColumnIndex(editRequest));
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		return super.getCommand(request);
	}

	public static Command createMoveColumnCommand(
			DirectEditRequest editRequest, EditPartViewer viewer,
			TableView newTableView, int index) {
		NormalColumn oldColumn = (NormalColumn) editRequest
				.getDirectEditFeature();

		TableView oldTableView = (TableView) oldColumn.getColumnHolder();
		if (newTableView == oldTableView) {
			return new ChangeColumnOrderCommand(newTableView, oldColumn, index);
		}

		CompoundCommand command = new CompoundCommand();

		// 参照している外部キーの変更
		List<Relation> relationList = oldColumn.getOutgoingRelationList();

		if (!relationList.isEmpty()) {
			Activator.showErrorDialog("error.reference.key.not.moveable");
			return null;

		} else if (oldColumn.isForeignKey()) {
			Relation oldRelation = oldColumn.getRelationList().get(0);
			TableView referencedTableView = oldRelation.getSourceTableView();

			if (ERTable.isRecursive(referencedTableView, newTableView)) {
				Activator.showErrorDialog("error.recursive.relation");
				return null;
			}

			DeleteRelationCommand deleteOldRelationCommand = new DeleteRelationCommand(
					oldRelation, Boolean.TRUE);
			command.add(deleteOldRelationCommand);

			Relation newRelation = new Relation(oldRelation.isReferenceForPK(),
					oldRelation.getReferencedComplexUniqueKey(), oldRelation
							.getReferencedColumn());

			List<NormalColumn> oldForeignKeyColumnList = new ArrayList<NormalColumn>();

			if (referencedTableView == newTableView) {
				Activator
						.showErrorDialog("error.foreign.key.not.moveable.to.reference.table");
				return null;
			}

			final List<NormalColumn> oldTableColumns = oldTableView.getNormalColumns();

			if (oldRelation.isReferenceForPK()) {
				for (NormalColumn referencedPrimaryKey : ((ERTable) referencedTableView)
						.getPrimaryKeys()) {
					for (final NormalColumn oldTableColumn : oldTableColumns) {
						if (oldTableColumn.isForeignKey()) {
							if (oldTableColumn.getReferencedColumn(oldRelation) == referencedPrimaryKey) {
								oldForeignKeyColumnList.add(oldTableColumn);
								break;
							}
						}
					}
				}
			} else if (oldRelation.getReferencedComplexUniqueKey() != null) {
				for (NormalColumn referencedColumn : oldRelation
						.getReferencedComplexUniqueKey().getColumnList()) {
					for (final NormalColumn oldTableColumn : oldTableColumns) {
						if (oldTableColumn.isForeignKey()) {
							if (oldTableColumn.getReferencedColumn(oldRelation) == referencedColumn) {
								oldForeignKeyColumnList.add(oldTableColumn);
								break;
							}
						}
					}
				}

			} else {
				oldForeignKeyColumnList.add(oldColumn);
			}

			for (NormalColumn oldForeignKey : oldForeignKeyColumnList) {
				List<Relation> oldRelationList = oldForeignKey.getOutgoingRelationList();

				if (!oldRelationList.isEmpty()) {
					Activator.showErrorDialog("error.reference.key.not.moveable");
					return null;
				}
			}
			
			CreateRelationCommand createNewRelationCommand = new CreateRelationCommand(
					newRelation, oldForeignKeyColumnList);

			EditPart sourceEditPart = (EditPart) viewer.getEditPartRegistry()
					.get(referencedTableView);
			EditPart targetEditPart = (EditPart) viewer.getEditPartRegistry()
					.get(newTableView);

			createNewRelationCommand.setSource(sourceEditPart);
			createNewRelationCommand.setTarget(targetEditPart);

			command.add(createNewRelationCommand);

		} else {
			TableView copyOldTableView = oldTableView.copyData();
			for (NormalColumn column : copyOldTableView.getNormalColumns()) {
				CopyColumn copyColumn = (CopyColumn) column;
				if (copyColumn.getOriginalColumn() == oldColumn) {
					copyOldTableView.removeColumn(copyColumn, true);
					break;
				}
			}

			ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(
					oldTableView, copyOldTableView);
			command.add(sourceTableCommand);

			TableView copyNewTableView = newTableView.copyData();
			CopyColumn copyColumn = CopyColumn.getInstance(oldColumn);
			copyColumn.setWord(CopyWord.getInstance(oldColumn.getWord()));
			copyNewTableView.addColumn(index, copyColumn, true);
			ChangeTableViewPropertyCommand targetTableCommand = new ChangeTableViewPropertyCommand(
					newTableView, copyNewTableView);
			command.add(targetTableCommand);
		}

		return command.unwrap();
	}

	public static Command createMoveColumnGroupCommand(
			DirectEditRequest editRequest, TableView newTableView, int index) {
		ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest
				.getDirectEditFeature())
				.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_GROUP);

		TableView oldTableView = (TableView) ((Map) editRequest
				.getDirectEditFeature())
				.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT);

		if (newTableView == oldTableView) {
			return new ChangeColumnOrderCommand(newTableView, columnGroup,
					index);
		}

		CompoundCommand command = new CompoundCommand();

		TableView copyOldTableView = oldTableView.copyData();
		for (Column column : copyOldTableView.getColumns()) {
			if (column == columnGroup) {
				copyOldTableView.removeColumn(column, true);
				break;
			}
		}

		ChangeTableViewPropertyCommand sourceTableCommand = new ChangeTableViewPropertyCommand(
				oldTableView, copyOldTableView);
		command.add(sourceTableCommand);

		if (!newTableView.getColumns().contains(columnGroup)) {
			command.add(new AddColumnGroupCommand(newTableView, columnGroup,
					index));
		}

		return command.unwrap();
	}

	private int getColumnIndex(DirectEditRequest editRequest) {
		final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) this.getHost().getRoot();
		final ZoomManager zoomManager = rootEditPart.getZoomManager();
		final Rectangle clientArea = zoomManager.getViewport().getClientArea();

		final double zoom = zoomManager.getZoom();

		ColumnEditPart columnEditPart = (ColumnEditPart) this.getHost();

		Column column = (Column) columnEditPart.getModel();
		TableView newTableView = (TableView) columnEditPart.getParent()
				.getModel();

		List<Column> columns = newTableView.getColumns();

		if (column.getColumnHolder() instanceof ColumnGroup) {
			column = (ColumnGroup) column.getColumnHolder();
		}
		int index = columns.indexOf(column);

		Rectangle columnRectangle = this.getColumnRectangle();
		final int center = (int) ((columnRectangle.y + (columnRectangle.height / 2)) * zoom) - clientArea.y;

		if (editRequest.getLocation().y >= center) {
			index++;
		}

		return index;
	}
}
