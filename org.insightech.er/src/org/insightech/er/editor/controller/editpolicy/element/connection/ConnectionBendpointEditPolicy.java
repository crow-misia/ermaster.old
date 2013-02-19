package org.insightech.er.editor.controller.editpolicy.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.handles.BendpointMoveHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.swt.SWT;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.MoveConnectionBendpointCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class ConnectionBendpointEditPolicy extends ERDiagramBendpointEditPolicy {

	@Override
	protected void showMoveBendpointFeedback(BendpointRequest bendpointrequest) {
		ConnectionElement connection = (ConnectionElement) getHost().getModel();
		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();

		if (connection.getSource() == connection.getTarget()) {
			if (bendpointrequest.getIndex() != 1) {
				return;
			}
			Point point = bendpointrequest.getLocation();
			this.getConnection().translateToRelative(point);
			Bendpoint rate = this.getRate(point);
			rate.setRelative(true);

			final double rateX = (100.0 - (rate.getX() / 2)) / 100.0;
			final double rateY = (100.0 - (rate.getY() / 2)) / 100.0;

			ERTableEditPart tableEditPart = (ERTableEditPart) editPart
					.getSource();
			Rectangle bounds = tableEditPart.getFigure().getBounds();

			Rectangle rect = new Rectangle();
			rect.x = (int) (bounds.x + (bounds.width * rateX));
			rect.y = (int) (bounds.y + (bounds.height * rateY));
			rect.width = (int) (bounds.width * rate.getX() / 100);
			rect.height = (int) (bounds.height * rate.getY() / 100);

			connection.setSourceLocationp(100, (int) (100 * rateY));

			connection.setTargetLocationp((int) (100 * rateX), 100);

			LayerManager manager = (LayerManager) tableEditPart.getRoot();
			IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
			this.getFeedbackLayer().setBounds(layer.getBounds());

			List children = this.getFeedbackLayer().getChildren();
			children.clear();
			this.getFeedbackLayer().repaint();

			ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
					.getHost().getRoot()).getZoomManager();
			double zoom = zoomManager.getZoom();

			Polyline feedbackFigure = new Polyline();
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) (rect.y * zoom)));
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) ((rect.y + rect.height) * zoom)));
			feedbackFigure.addPoint(new Point(
					(int) ((rect.x + rect.width) * zoom),
					(int) ((rect.y + rect.height) * zoom)));
			feedbackFigure
					.addPoint(new Point((int) ((rect.x + rect.width) * zoom),
							(int) (rect.y * zoom)));
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) (rect.y * zoom)));

			feedbackFigure.setLineStyle(SWT.LINE_DASH);

			feedbackFigure.translateToRelative(feedbackFigure.getLocation());

			this.addFeedback(feedbackFigure);

		} else {
			super.showMoveBendpointFeedback(bendpointrequest);
		}
	}

	@Override
	protected void showCreateBendpointFeedback(BendpointRequest bendpointrequest) {
		ConnectionElement connection = (ConnectionElement) getHost().getModel();

		if (connection.getSource() == connection.getTarget()) {
			return;
		}
		super.showCreateBendpointFeedback(bendpointrequest);
	}

	@Override
	protected void eraseConnectionFeedback(BendpointRequest request) {
		this.getFeedbackLayer().getChildren().clear();
		super.eraseConnectionFeedback(request);
	}

	@Override
	protected Command getMoveBendpointCommand(BendpointRequest bendpointrequest) {
		ConnectionElement connection = (ConnectionElement) getHost().getModel();
		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();

		if (connection.getSource() == connection.getTarget()) {
			if (bendpointrequest.getIndex() != 1) {
				return null;

			} else {
				Point point = bendpointrequest.getLocation();
				Bendpoint rate = this.getRate(point);

				return new MoveConnectionBendpointCommand(
						editPart, rate.getX(), rate.getY(), bendpointrequest
								.getIndex());
			}
		}

		Point point = bendpointrequest.getLocation();
		this.getConnection().translateToRelative(point);

		return new MoveConnectionBendpointCommand(
				editPart, point.x, point.y, bendpointrequest.getIndex());
	}

	private Bendpoint getRate(Point point) {
		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();

		ERTableEditPart tableEditPart = (ERTableEditPart) editPart.getSource();
		Rectangle rectangle = tableEditPart.getFigure().getBounds();

		int xRate = (point.x - rectangle.x - rectangle.width) * 200
				/ rectangle.width;
		int yRate = (point.y - rectangle.y - rectangle.height) * 200
				/ rectangle.height;

		return new Bendpoint(xRate, yRate);
	}

	@Override
	protected void showSelection() {
		super.showSelection();

		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();
		editPart.refresh();
	}

	@Override
	protected void hideSelection() {
		super.hideSelection();

		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();
		editPart.refresh();
	}

	@Override
	protected List createSelectionHandles() {
		ConnectionElement connection = (ConnectionElement) getHost().getModel();

		if (connection.getSource() == connection.getTarget()) {
			List<BendpointMoveHandle> list = new ArrayList<BendpointMoveHandle>();

			ConnectionEditPart connEP = (ConnectionEditPart) getHost();

			list.add(new BendpointMoveHandle(connEP, 1, 2));

			this.showSelectedLine();

			ERDiagramEditPart diagramEditPart = (ERDiagramEditPart) this
					.getHost().getRoot().getContents();
			diagramEditPart.refreshVisuals();

			return list;
		}

		return super.createSelectionHandles();
	}
}
