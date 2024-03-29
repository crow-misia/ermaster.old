package org.insightech.er.editor.controller.editpolicy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.common.NothingToDoCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.MoveBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.CreateElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.category.MoveCategoryCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.CategoryEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementSelectionEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ERDiagramLayoutEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected void showSizeOnDropFeedback(CreateRequest request) {
		Point p = new Point(request.getLocation().getCopy());

		ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
				.getHost().getRoot()).getZoomManager();
		double zoom = zoomManager.getZoom();

		IFigure feedback = getSizeOnDropFeedback(request);

		Dimension size = request.getSize().getCopy();
		feedback.translateToRelative(size);
		feedback.setBounds(new Rectangle((int) (p.x * zoom),
				(int) (p.y * zoom), size.width, size.height)
				.expand(getCreationFeedbackOffset(request)));
	}

	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if (!(child instanceof NodeElementEditPart)) {
			return null;
		}

		try {
			Rectangle rectangle = (Rectangle) constraint;

			List selectedEditParts = this.getHost().getViewer()
					.getSelectedEditParts();

			NodeElementEditPart editPart = (NodeElementEditPart) child;
			NodeElement nodeElement = (NodeElement) editPart.getModel();
			Rectangle currentRectangle = editPart.getFigure().getBounds();

			boolean move = false;

			if (rectangle.width == currentRectangle.width
					&& rectangle.height == currentRectangle.height) {
				move = true;
			}

			boolean nothingToDo = false;

			if (move && !(editPart instanceof CategoryEditPart)) {
				for (Object selectedEditPart : selectedEditParts) {
					if (selectedEditPart instanceof CategoryEditPart) {
						CategoryEditPart categoryEditPart = (CategoryEditPart) selectedEditPart;
						Category category = (Category) categoryEditPart
								.getModel();

						if (category.contains(nodeElement)) {
							nothingToDo = true;
						}
					}
				}
			}

			List<Command> bendpointMoveCommandList = new ArrayList<Command>();

			int oldX = nodeElement.getX();
			int oldY = nodeElement.getY();

			int diffX = rectangle.x - oldX;
			int diffY = rectangle.y - oldY;

			for (Object obj : editPart.getSourceConnections()) {
				AbstractConnectionEditPart connection = (AbstractConnectionEditPart) obj;

				if (selectedEditParts.contains(connection.getTarget())) {
					ConnectionElement connectionElement = (ConnectionElement) connection
							.getModel();

					List<Bendpoint> bendpointList = connectionElement
							.getBendpoints();

					for (int index = 0, n = bendpointList.size(); index < n; index++) {
						Bendpoint bendPoint = bendpointList.get(index);

						if (bendPoint.isRelative()) {
							break;
						}

						MoveBendpointCommand moveCommand = new MoveBendpointCommand(
								connection, bendPoint.getX() + diffX, bendPoint
										.getY()
										+ diffY, index);
						bendpointMoveCommandList.add(moveCommand);
					}

				}
			}

			CompoundCommand compoundCommand = new CompoundCommand();

			if (!nothingToDo) {
				Command changeConstraintCommand = this
						.createChangeConstraintCommand(editPart, rectangle);

				if (bendpointMoveCommandList.isEmpty()) {
					return changeConstraintCommand;

				}

				compoundCommand.add(changeConstraintCommand);

			} else {
				compoundCommand.add(NothingToDoCommand.INSTANCE);
			}

			for (Command command : bendpointMoveCommandList) {
				compoundCommand.add(command);
			}

			return compoundCommand;

		} catch (Exception e) {
			Activator.log(e);
			return null;
		}
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		Rectangle rectangle = (Rectangle) constraint;

		NodeElementEditPart editPart = (NodeElementEditPart) child;
		NodeElement nodeElement = (NodeElement) editPart.getModel();
		Rectangle currentRectangle = editPart.getFigure().getBounds();

		boolean move = false;

		if (rectangle.width == currentRectangle.width
				&& rectangle.height == currentRectangle.height) {
			move = true;
		}

		if (nodeElement instanceof Category) {
			Category category = (Category) nodeElement;

			List<Category> otherCategories = null;

			if (move) {
				if (this.getOtherCategory((Category) nodeElement) != null) {
					return null;
				}

				otherCategories = this.getOtherSelectedCategories(category);
			}

			return new MoveCategoryCommand((ERDiagram) this.getHost()
					.getModel(), rectangle.x, rectangle.y, rectangle.width,
					rectangle.height, category, otherCategories, move);

		} else {
			return new MoveElementCommand(
					(ERDiagram) this.getHost().getModel(), currentRectangle,
					rectangle.x, rectangle.y, rectangle.width,
					rectangle.height, nodeElement);
		}
	}

	private Category getOtherCategory(Category category) {
		ERDiagram diagram = (ERDiagram) this.getHost().getModel();

		List<Category> selectedCategories = diagram.getDiagramContents()
				.getSettings().getCategorySetting().getSelectedCategories();

		for (NodeElement nodeElement : category.getContents()) {
			for (Category otherCategory : selectedCategories) {
				if (otherCategory != category && !isSelected(otherCategory)) {
					if (otherCategory.contains(nodeElement)) {
						return otherCategory;
					}
				}
			}
		}

		return null;
	}

	private List<Category> getOtherSelectedCategories(Category category) {
		List<Category> otherCategories = new ArrayList<Category>();

		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof CategoryEditPart) {
				CategoryEditPart categoryEditPart = (CategoryEditPart) object;
				Category otherCategory = (Category) categoryEditPart.getModel();

				if (otherCategory == category) {
					break;
				}

				otherCategories.add(otherCategory);
			}
		}

		return otherCategories;
	}

	private boolean isSelected(Category category) {
		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof NodeElementEditPart) {
				NodeElementEditPart editPart = (NodeElementEditPart) object;
				if (editPart.getModel() == category) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		ERDiagramEditPart editPart = (ERDiagramEditPart) this.getHost();

		Point point = request.getLocation();
		editPart.getFigure().translateToRelative(point);

		NodeElement element = (NodeElement) request.getNewObject();
		ERDiagram diagram = (ERDiagram) editPart.getModel();

		Dimension size = request.getSize();
		List<NodeElement> enclosedElementList = new ArrayList<NodeElement>();

		if (size != null) {
			ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
					.getHost().getRoot()).getZoomManager();
			double zoom = zoomManager.getZoom();
			size = new Dimension((int) (size.width / zoom),
					(int) (size.height / zoom));

			for (Object child : editPart.getChildren()) {
				if (child instanceof NodeElementEditPart) {
					NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) child;
					Rectangle bounds = nodeElementEditPart.getFigure()
							.getBounds();

					if (bounds.x > point.x
							&& bounds.x + bounds.width < point.x + size.width
							&& bounds.y > point.y
							&& bounds.y + bounds.height < point.y + size.height) {
						enclosedElementList
								.add((NodeElement) nodeElementEditPart
										.getModel());
					}
				}
			}
		}
		return new CreateElementCommand(diagram, element, point.x, point.y,
				size, enclosedElementList);
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new NodeElementSelectionEditPolicy();
	}

}
