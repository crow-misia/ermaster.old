package org.insightech.er.editor.controller.editpart.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;
import org.insightech.er.editor.controller.editpolicy.element.connection.CommentConnectionEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.ERDiagramBendpointEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class CommentConnectionEditPart extends ERDiagramConnectionEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		boolean bezier = this.getDiagram().getDiagramContents().getSettings()
				.isUseBezierCurve();
		PolylineConnection connection = new ERDiagramConnection(bezier);
		connection.setConnectionRouter(new BendpointConnectionRouter());

		connection.setLineStyle(SWT.LINE_DASH);

		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new CommentConnectionEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new ERDiagramBendpointEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshBendpoints() {
		// ベンド・ポイントの位置情報の取得
		final ConnectionElement connection = (ConnectionElement) this.getModel();
		final List<Bendpoint> bendPoints = connection.getBendpoints();

		// 実際のベンド・ポイントのリスト
		List<org.eclipse.draw2d.Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>(bendPoints.size());

		for (final Bendpoint bendPoint : bendPoints) {
			constraint.add(new AbsoluteBendpoint(bendPoint.getX(), bendPoint.getY()));
		}

		this.getConnectionFigure().setRoutingConstraint(constraint);
	}
}
