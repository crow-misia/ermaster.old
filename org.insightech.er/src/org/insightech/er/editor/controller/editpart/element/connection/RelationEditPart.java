package org.insightech.er.editor.controller.editpart.element.connection;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ChangeRelationPropertyCommand;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationBendpointEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.connection.RelationEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
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
				new RelationBendpointEditPolicy());
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

		this.calculateAnchorLocation();

		this.refreshBendpoints();
	}

	@Override
	public void performRequest(Request request) {
		Relation relation = (Relation) this.getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			Relation copy = relation.copy();

			RelationDialog dialog = new RelationDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), copy);

			if (dialog.open() == IDialogConstants.OK_ID) {
				ChangeRelationPropertyCommand command = new ChangeRelationPropertyCommand(
						relation, copy);
				this.getViewer().getEditDomain().getCommandStack().execute(
						command);
			}
		}

		super.performRequest(request);
	}

	private void calculateAnchorLocation() {
		Relation relation = (Relation) this.getModel();

		TableViewEditPart sourceEditPart = (TableViewEditPart) this.getSource();

		Point sourcePoint = null;
		Point targetPoint = null;

		if (sourceEditPart != null && relation.getSourceXp() != -1
				&& relation.getSourceYp() != -1) {
			Rectangle bounds = sourceEditPart.getFigure().getBounds();
			sourcePoint = new Point(bounds.x
					+ (bounds.width * relation.getSourceXp() / 100), bounds.y
					+ (bounds.height * relation.getSourceYp() / 100));
		}

		TableViewEditPart targetEditPart = (TableViewEditPart) this.getTarget();

		if (targetEditPart != null && relation.getTargetXp() != -1
				&& relation.getTargetYp() != -1) {
			Rectangle bounds = targetEditPart.getFigure().getBounds();
			targetPoint = new Point(bounds.x
					+ (bounds.width * relation.getTargetXp() / 100), bounds.y
					+ (bounds.height * relation.getTargetYp() / 100));
		}

		ConnectionAnchor sourceAnchor = this.getConnectionFigure()
				.getSourceAnchor();

		if (sourceAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) sourceAnchor).setLocation(sourcePoint);
		}

		ConnectionAnchor targetAnchor = this.getConnectionFigure()
				.getTargetAnchor();

		if (targetAnchor instanceof XYChopboxAnchor) {
			((XYChopboxAnchor) targetAnchor).setLocation(targetPoint);
		}
	}

}
