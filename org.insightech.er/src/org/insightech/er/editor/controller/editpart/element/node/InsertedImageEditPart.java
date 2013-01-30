package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.image.ChangeInsertedImagePropertyCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.view.dialog.element.InsertedImageDialog;
import org.insightech.er.editor.view.figure.InsertedImageFigure;

public class InsertedImageEditPart extends NodeElementXYEditPart {

	private Image image;

	private ImageData imageData;

	@Override
	protected IFigure createFigure() {
		InsertedImage model = (InsertedImage) this.getModel();

		byte[] data = Base64.decodeBase64((model.getBase64EncodedData()
				.getBytes()));
		ByteArrayInputStream in = new ByteArrayInputStream(data);

		this.imageData = new ImageData(in);
		this.changeImage();

		InsertedImageFigure figure = new InsertedImageFigure(this.image, model.getAlpha());
		figure.setMinimumSize(new Dimension(1, 1));

		return figure;
	}

	@Override
	protected void disposeFont() {
		disposeImage();

		super.disposeFont();
	}

	private void disposeImage() {
		if (this.image != null && !this.image.isDisposed()) {
			this.image.dispose();
			this.image = null;
		}
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());

		super.createEditPolicies();
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(InsertedImage.PROPERTY_CHANGE_IMAGE)) {
			changeImage();

			final InsertedImageFigure figure = (InsertedImageFigure) this.getFigure();
			final InsertedImage model = (InsertedImage) this.getModel();

			// 縦横比率固定の場合、要素のサイズを調整する
			if (model.isFixAspectRatio()) {
				model.setLocation(new Location(model.getX(), model.getY(), model.getWidth(), model.getHeight()));
				figure.setSize(model.getWidth(), model.getHeight());
			}
			figure.setImg(this.image, model.getAlpha());

			refreshVisuals();

			if (ERDiagramEditPart.isUpdateable()) {
				this.getFigure().repaint();
			}
		}

		super.doPropertyChange(event);
	}

	private void changeImage() {
		final InsertedImage model = (InsertedImage) this.getModel();

		final ImageData newImageData = new ImageData(this.imageData.width,
				this.imageData.height, this.imageData.depth,
				this.imageData.palette);

		model.setImage(newImageData);

		final int w = this.imageData.width;
		final int h = this.imageData.height;

		final float saturation = (float) (model.getSaturation() / 100f);
		final float brightness = (float) (model.getBrightness() / 100f);
		final float hue = model.getHue() & 360;
		final boolean isHue = model.getHue() != 0;
		
		final int[] pixels = new int[w];
		final PaletteData palette = this.imageData.palette;

		for (int y = 0; y < h; y++) {
			this.imageData.getPixels(0, y, w, pixels, 0);
			for (int x = 0; x < w; x++) {
				final RGB rgb = palette.getRGB(pixels[x]);
				final float[] hsb = rgb.getHSB();

				if (isHue) {
					hsb[0] = hue;
				}

				hsb[1] += saturation;
				if (hsb[1] > 1.0f) {
					hsb[1] = 1.0f;
				} else if (hsb[1] < 0) {
					hsb[1] = 0f;
				}

				hsb[2] += brightness;
				if (hsb[2] > 1.0f) {
					hsb[2] = 1.0f;
				} else if (hsb[2] < 0) {
					hsb[2] = 0f;
				}

				final RGB newRGB = new RGB(hsb[0], hsb[1], hsb[2]);

				pixels[x] = imageData.palette.getPixel(newRGB);
			}
			newImageData.setPixels(0, y, w, pixels, 0);
		}

		disposeImage();

		this.image = new Image(Display.getDefault(), newImageData);
	}

	@Override
	public void performRequestOpen() {
		InsertedImage insertedImage = (InsertedImage) this.getModel();

		InsertedImage oldInsertedImage = (InsertedImage) insertedImage.clone();

		ERDiagram diagram = this.getDiagram();

		InsertedImageDialog dialog = new InsertedImageDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				insertedImage);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(
					diagram, insertedImage, dialog.getNewInsertedImage(),
					oldInsertedImage);

			this.execute(command);

		} else {
			ChangeInsertedImagePropertyCommand command = new ChangeInsertedImagePropertyCommand(
					diagram, insertedImage, oldInsertedImage, oldInsertedImage);
			command.execute();

		}
	}
}
