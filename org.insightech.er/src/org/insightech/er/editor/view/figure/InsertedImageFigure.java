package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class InsertedImageFigure extends Figure {

	private Image image;

	private int alpha;

	public InsertedImageFigure(final Image image, final int alpha) {
		setImg(image, alpha);
	}

	public void setImg(final Image image, final int alpha) {
		this.image = image;
		this.alpha = alpha;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		graphics.setAlpha(alpha);

		final Rectangle destination = getClientArea();

		graphics.drawImage(this.image,
				new Rectangle(this.image.getBounds()), destination);

	}

}
