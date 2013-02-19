package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;

public class CategoryFigure extends RectangleFigure {

	public CategoryFigure(String name) {
		this.setOpaque(true);

		ToolbarLayout layout = new ToolbarLayout();
		this.setLayoutManager(layout);

		final Label label = new Label();
		label.setText(name);
		label.setBorder(new MarginBorder(7));
		this.add(label);
	}

	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAlpha(100);
		super.fillShape(graphics);
	}

}
