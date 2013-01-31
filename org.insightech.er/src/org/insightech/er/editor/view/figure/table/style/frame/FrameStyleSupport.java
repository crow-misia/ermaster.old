package org.insightech.er.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class FrameStyleSupport extends AbstractStyleSupport {

	private ImageFrameBorder border;

	private TitleBarBorder titleBarBorder;

	public FrameStyleSupport(TableFigure tableFigure) {
		super(tableFigure);
	}

	@Override
	public void init(TableFigure tableFigure) {
		this.border = new ImageFrameBorder();
		this.border.setFont(tableFigure.getFont());

		tableFigure.setBorder(this.border);
	}

	@Override
	public void initTitleBar(Figure top) {
		this.titleBarBorder = (TitleBarBorder) this.border.getInnerBorder();
		this.titleBarBorder.setTextAlignment(PositionConstants.CENTER);
		this.titleBarBorder.setPadding(new Insets(5, 20, 5, 20));
	}

	public void setDependence(final Boolean dependence) {
    }

    public void setName(String name) {
		this.titleBarBorder.setTextColor(this.getTextColor());
		this.titleBarBorder.setLabel(name);
	}

	public void setFont(Font font, Font titleFont) {
		this.titleBarBorder.setFont(titleFont);
	}

	@Override
	public void adjustBounds(Rectangle rect) {
		int width = this.border.getTitleBarWidth(this.getTableFigure());

		if (width > rect.width) {
			rect.width = width;
		}
	}
}
