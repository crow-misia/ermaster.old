package org.insightech.er.editor.controller.editpart.element.connection;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.editpolicy.element.connection.ConnectionBendpointEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;
import org.insightech.er.editor.view.figure.connection.decoration.DecorationFactory;
import org.insightech.er.editor.view.figure.connection.decoration.DecorationFactory.Decoration;
import org.insightech.er.util.Format;

public class RelationEditPart extends ERDiagramConnectionEditPart {

	private Label targetLabel;

	@Override
	protected IFigure createFigure() {
		boolean bezier = this.getDiagram().getDiagramContents().getSettings()
				.isUseBezierCurve();
		PolylineConnection connection = new ERDiagramConnection(bezier);
		connection.setConnectionRouter(new BendpointConnectionRouter());

		ConnectionEndpointLocator targetLocator = new ConnectionEndpointLocator(
				connection, true);
		this.targetLabel = new Label("");
		connection.add(targetLabel, targetLocator);

		return connection;
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new RelationEditPolicy());
		this.installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new ConnectionBendpointEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		ERDiagram diagram = this.getDiagram();

		if (diagram != null) {
			final Settings settings = diagram.getDiagramContents().getSettings();

			Relation relation = (Relation) this.getModel();

			// 関連線の始点・終点に ER図の表記法に基づいた記号を付与する
			ERDiagramConnection connection = (ERDiagramConnection) this
					.getConnectionFigure();

			String notation = settings.getNotation();

			Decoration decoration = DecorationFactory.getDecoration(notation,
					relation.getParentCardinality(), relation
							.getChildCardinality());

			connection.setSourceDecoration(decoration.getSourceDecoration());
			connection.setTargetDecoration(decoration.getTargetDecoration());
			targetLabel.setText(Format.null2blank(decoration.getTargetLabel()));

			// 関連の子のFKが　PK の場合、依存(実線)、それ以外は非依存(破線)とする
			if (settings.isNotationDependence()) {
				connection.setDependence(Boolean.valueOf(relation.isDependence()));
			} else {
				connection.setDependence(null);
			}
		}

		this.refreshBendpoints();
	}

	@Override
	public void performRequest(Request request) {

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			final Relation relation = (Relation) this.getModel();

			final Command command = RelationDialog.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					relation);

			if (command != null) {
				this.execute(command);
			}
		}

		super.performRequest(request);
	}
}
