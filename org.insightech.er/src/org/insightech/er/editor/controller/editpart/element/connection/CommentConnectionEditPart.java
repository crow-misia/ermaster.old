package org.insightech.er.editor.controller.editpart.element.connection;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;
import org.insightech.er.editor.controller.editpolicy.element.connection.CommentConnectionEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.ERDiagramBendpointEditPolicy;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class CommentConnectionEditPart extends ERDiagramConnectionEditPart {

	@Override
	protected IFigure createFigure() {
		boolean bezier = this.getDiagram().getDiagramContents().getSettings()
				.isUseBezierCurve();
		PolylineConnection connection = new ERDiagramConnection(bezier);
		connection.setConnectionRouter(new BendpointConnectionRouter());

		connection.setLineStyle(SWT.LINE_DASH);

		return connection;
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new CommentConnectionEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new ERDiagramBendpointEditPolicy());
	}
}
