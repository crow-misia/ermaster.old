package org.insightech.er.editor.view.figure.table.style.simple;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class SimpleStyleSupport extends AbstractStyleSupport {

	private Label nameLabel;

	public SimpleStyleSupport(TableFigure tableFigure) {
		super(tableFigure);
	}

	@Override
	public void init(TableFigure tableFigure) {
		tableFigure.setBorder(null);
	}

	@Override
	public void initTitleBar(Figure top) {
		ToolbarLayout topLayout = new ToolbarLayout();

		topLayout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		topLayout.setStretchMinorAxis(true);
		top.setLayoutManager(topLayout);

		this.nameLabel = new Label();
		this.nameLabel.setBorder(new MarginBorder(new Insets(5, 20, 5, 20)));
		top.add(nameLabel);

		Figure separater = new Figure();
		separater.setSize(-1, 1);
		separater.setBackgroundColor(this.getTextColor());
		separater.setOpaque(true);

		top.add(separater);
	}

	public void setDependence(final Boolean dependence) {
		if (dependence == null || dependence.booleanValue()) {
			getTableFigure().setCornerDimensions(new Dimension(10, 10));
		} else {
			getTableFigure().setCornerDimensions(new Dimension(0, 0));
		}
	}

	public void setName(String name) {
		this.nameLabel.setForegroundColor(this.getTextColor());
		this.nameLabel.setText(name);
	}

	public void setFont(Font font, Font titleFont) {
		this.nameLabel.setFont(titleFont);
	}
}
