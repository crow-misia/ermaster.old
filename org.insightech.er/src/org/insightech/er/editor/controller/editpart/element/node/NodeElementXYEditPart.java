package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.ERDiagramConnectionEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;

public abstract class NodeElementXYEditPart extends NodeElementEditPart implements
		IResizable {

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		super.doPropertyChange(event);
		this.refreshConnections();
	}

	@Override
	public void refresh() {
		super.refresh();
		this.refreshConnections();
	}

	@Override
	public void refreshVisuals() {
		super.refreshVisuals();

		if (ERDiagramEditPart.isUpdateable()) {
			getFigure().getUpdateManager().performValidation();
		}
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart editPart) {
		if (!(editPart instanceof ERDiagramConnectionEditPart)) {
			return super.getSourceConnectionAnchor(editPart);
		}

		ConnectionElement connection = (ConnectionElement) editPart.getModel();

		Rectangle bounds = this.getFigure().getBounds();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		if (connection.getSourceXp() != -1 && connection.getSourceYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * connection.getSourceXp() / 100), bounds.y
					+ (bounds.height * connection.getSourceYp() / 100)));
		}

		return anchor;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			if (!(connectionEditPart instanceof ERDiagramConnectionEditPart)) {
				return super.getSourceConnectionAnchor(request);
			}

			ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();
			if (connection.getSource() == connection.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(connection.getSource())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure sourceFigure = ((NodeElementXYEditPart) connectionEditPart
					.getSource()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = sourceFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, sourceFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return super.getTargetConnectionAnchor(request);
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart editPart) {
		if (!(editPart instanceof ERDiagramConnectionEditPart)) {
			return super.getTargetConnectionAnchor(editPart);
		}

		ConnectionElement connection = (ConnectionElement) editPart.getModel();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		Rectangle bounds = this.getFigure().getBounds();

		if (connection.getTargetXp() != -1 && connection.getTargetYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * connection.getTargetXp() / 100), bounds.y
					+ (bounds.height * connection.getTargetYp() / 100)));
		}

		return anchor;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			if (!(connectionEditPart instanceof ERDiagramConnectionEditPart)) {
				return super.getTargetConnectionAnchor(request);
			}

			ConnectionElement connection = (ConnectionElement) connectionEditPart.getModel();
			if (connection.getSource() == connection.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(connection.getTarget())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure targetFigure = ((NodeElementXYEditPart) connectionEditPart
					.getTarget()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = targetFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, targetFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return super.getTargetConnectionAnchor(request);
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	public static Point getIntersectionPoint(Point s, IFigure figure) {

		Rectangle r = figure.getBounds();

		int x1 = s.x - r.x;
		int x2 = r.x + r.width - s.x;
		int y1 = s.y - r.y;
		int y2 = r.y + r.height - s.y;

		int x = 0;
		int dx = 0;
		if (x1 < x2) {
			x = r.x;
			dx = x1;

		} else {
			x = r.x + r.width;
			dx = x2;
		}

		int y = 0;
		int dy = 0;

		if (y1 < y2) {
			y = r.y;
			dy = y1;

		} else {
			y = r.y + r.height;
			dy = y2;
		}

		if (dx < dy) {
			y = s.y;
		} else {
			x = s.x;
		}

		return new Point(x, y);
	}

}
