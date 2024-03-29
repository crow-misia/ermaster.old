package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.graphics.ImageData;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.io.IOUtils;

public class InsertedImage extends NodeElement {

	private static final long serialVersionUID = -2035035973213266486L;

	public static final String PROPERTY_CHANGE_IMAGE = "image";

	private String base64EncodedData;

	/** 0　～　360 */
	private int hue;

	/** -100　～　+100 */
	private int saturation;

	/** -100　～　+100 */
	private int brightness;

	private int alpha;
	
	private boolean fixAspectRatio;

	private ImageData imageData;

	public InsertedImage() {
		this.alpha = 255;
	}

	public String getBase64EncodedData() {
		return base64EncodedData;
	}

	public void setBase64EncodedData(String base64EncodedData) {
		this.base64EncodedData = base64EncodedData;
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "image";
	}

	public void setImageFilePath(String imageFilePath) {
		InputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(imageFilePath));

			byte[] data = IOUtils.toByteArray(in);

			String encodedData = new String(Base64.encodeBase64(data));
			this.setBase64EncodedData(encodedData);

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public int getHue() {
		return hue;
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	public int getSaturation() {
		return saturation;
	}

	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public boolean isFixAspectRatio() {
		return fixAspectRatio;
	}

	public void setFixAspectRatio(boolean fixAspectRatio) {
		this.fixAspectRatio = fixAspectRatio;
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_IMAGE, null, null);
	}

	public void setImage(final ImageData imageData) {
		this.imageData = imageData;
	}

	@Override
	public void setLocation(final Location location) {
		if (imageData != null && isFixAspectRatio()) {
			int w = location.width;
			int h = location.height;
			
			final double aspect = (double) imageData.width / imageData.height;
			final double realAspect = (double) w / h;

			if (aspect < realAspect) {
				h = (int) (w / aspect);
			} else {
				w = (int) (h * aspect);
			}

			location.width = w;
			location.height = h;
		}

		super.setLocation(location);
	}
}
