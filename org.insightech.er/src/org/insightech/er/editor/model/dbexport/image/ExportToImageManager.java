package org.insightech.er.editor.model.dbexport.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.insightech.er.editor.model.dbexport.html.page_generator.HtmlReportPageGenerator;

public class ExportToImageManager {

	protected Image img;

	private int format;

	private String saveFilePath;

	public ExportToImageManager(Image img, int format, String saveFilePath) {
		this.img = img;
		this.format = format;
		this.saveFilePath = saveFilePath;
	}

	public void doProcess() throws IOException, InterruptedException {
		if (format == SWT.IMAGE_JPEG || format == SWT.IMAGE_BMP) {
			writeJPGorBMP(img, saveFilePath, format);

		} else if (format == SWT.IMAGE_PNG) {
			writePNGorGIF(img, saveFilePath, "PNG");
		} else if (format == SWT.IMAGE_GIF) {
			writePNGorGIF(img, saveFilePath, "GIF");
		}
	}

	private static void writeJPGorBMP(Image image, String saveFilePath, int format)
			throws IOException {
		final ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = new ImageData[] { image.getImageData(), };
		imgLoader.save(saveFilePath, format);
	}

	private void writePNGorGIF(Image image, String saveFilePath,
			String formatName) throws IOException, InterruptedException {

		try {
			final ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData(), };
			loader.save(saveFilePath, format);

		} catch (SWTException e) {
			// Eclipse 3.2 では、 PNG が Unsupported or unrecognized format となるため、
			// 以下の代替方法を使用する
			// ただし、この方法では上手く出力できない環境あり

			e.printStackTrace();
			BufferedImage bufferedImage = new BufferedImage(
					image.getBounds().width, image.getBounds().height,
					BufferedImage.TYPE_INT_RGB);

			drawAtBufferedImage(bufferedImage, image, 0, 0);

			ImageIO.write(bufferedImage, formatName, new File(saveFilePath));
		}
	}

	private void drawAtBufferedImage(BufferedImage bimg, Image image, int offsetX,
			int offsetY) throws InterruptedException {

		ImageData data = image.getImageData();

		final int width = image.getBounds().width;
		final int height = image.getBounds().height;
		
		final int n = Math.min(width * height * 4, data.data.length);
		
		int x = 0;
		int y = 0;
		for (int i = 0; i < n; i += 4) {
			int r = 0xff & data.data[i + 2];
			int g = 0xff & data.data[i + 1];
			int b = 0xff & data.data[i];

			x++;
			if (x >= width) {
				x = 0;
				y++;
			}
			bimg.setRGB(x + offsetX, y + offsetY, 0xFF << 24 | r << 16 | g << 8 | b << 0);

			this.doPostTask();
		}
	}

	protected void doPreTask(HtmlReportPageGenerator pageGenerator,
			Object object) {
	}

	protected void doPostTask() throws InterruptedException {
	}
}
