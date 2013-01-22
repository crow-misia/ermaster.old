package org.insightech.er.editor.view.figure.connection;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Geometry;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;

public class ERDiagramConnection extends PolylineConnection {

	private static final double DELTA = 0.01;

	private static final int TOLERANCE = 2;

	private static final int[] NON_DEPEND_DASH = new int[] { 7, 3, };

	private boolean selected;

	private boolean bezier;

	private Boolean dependence;

	public ERDiagramConnection(final boolean bezier) {
		this.bezier = bezier;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setBezier(boolean bezier) {
		this.bezier = bezier;
	}

	public void setDependence(Boolean dependence) {
		this.dependence = dependence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(Graphics g) {
		g.setAntialias(SWT.ON);

		int width;
		Color color;
		final int originalLineStyle = g.getLineStyle();;

		if (this.selected) {
			if (this.bezier) {
				g.setForegroundColor(ColorConstants.gray);

				PointList points = getPoints();
				g.drawPolyline(points);
			}

			g.setLineStyle(SWT.LINE_SOLID);
			color = Resources.LINE_COLOR;
			width = 7;
		} else {
			color = ColorConstants.black;
			width = 1;
		}

		final PointList points = getBezierPoints();

		final int lineRed = color.getRed();
		final int lineGreen = color.getGreen();
		final int lineBlue = color.getBlue();

		final int deltaRed = (255 - lineRed) * 2 / width;
		final int deltaGreen = (255 - lineGreen) * 2 / width;
		final int deltaBlue = (255 - lineBlue) * 2 / width;

		int red = 255;
		int green = 255;
		int blue = 255;

		Color tmpColor;
		while (width > 1) {
			red -= deltaRed;
			green -= deltaGreen;
			blue -= deltaBlue;

			if (red < lineRed) {
				red = lineRed;
			}
			if (green < lineGreen) {
				green = lineGreen;
			}
			if (blue < lineBlue) {
				blue = lineBlue;
			}

			tmpColor = Resources.getColor(red, green, blue);

			g.setLineWidth(width);
			g.setForegroundColor(tmpColor);
			g.drawPolyline(points);

			width -= 2;
		}
		
		if (dependence != null) {
			if (dependence.booleanValue()) {
				g.setLineStyle(SWT.LINE_SOLID);
			} else {
				g.setLineStyle(SWT.LINE_CUSTOM);
				g.setLineDash(NON_DEPEND_DASH);
			}
		} else {
			g.setLineStyle(originalLineStyle);
		}
		
		g.setLineWidth(1);
		g.setForegroundColor(color);
		g.drawPolyline(points);
	}

	public PointList getBezierPoints() {
		PointList controlPoints = this.getPoints();

		if (this.bezier && controlPoints.size() >= 3) {
			int index = 0;

			PointList pointList = new PointList();

			Point p0 = controlPoints.getPoint(index++);
			Point p1 = controlPoints.getPoint(index++);
			Point p2 = null;
			Point nextPoint = controlPoints.getPoint(index++);

			while (true) {
				if (index != controlPoints.size()) {
					p2 = new Point((p1.x + nextPoint.x) / 2,
							(p1.y + nextPoint.y) / 2);

				} else {
					p2 = nextPoint;
				}

				for (double t = 0.0; t <= 1.0; t = t + DELTA) {
					Point point = new Point();

					point.x = (int) (p0.x * (1 - t) * (1 - t) + 2 * p1.x * t
							* (1 - t) + p2.x * t * t);

					point.y = (int) (p0.y * (1 - t) * (1 - t) + 2 * p1.y * t
							* (1 - t) + p2.y * t * t);

					pointList.addPoint(point);
				}

				pointList.addPoint(p2);

				if (index == controlPoints.size()) {
					break;
				}

				p0 = p2;
				p1 = nextPoint;
				nextPoint = controlPoints.getPoint(index++);
			}

			return pointList;
		}

		return controlPoints;
	}

	@Override
	protected boolean shapeContainsPoint(int x, int y) {
		return Geometry.polylineContainsPoint(this.getBezierPoints(), x, y,
				TOLERANCE);
	}
}
