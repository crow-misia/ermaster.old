package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.awt.Dimension;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class PictureSheetGenerator {

	private static final String KEYWORD_ER = "$ER";

	private byte[] imageBuffer;

	private int pictureIndex;

	public PictureSheetGenerator(HSSFWorkbook workbook, byte[] imageBuffer,
			int excelPictureType) {
		this.imageBuffer = imageBuffer;

		if (this.imageBuffer != null) {
			this.pictureIndex = workbook.addPicture(this.imageBuffer, excelPictureType);
		}
	}

	public void setImage(HSSFWorkbook workbook, HSSFSheet sheet) {
		CellLocation cellLocation = POIUtils.findMatchCell(sheet, "\\"
				+ KEYWORD_ER + ".*");

		if (cellLocation != null) {
			int width = -1;
			int height = -1;

			String value = POIUtils.getCellValue(sheet, cellLocation);

			int startIndex = value.indexOf('(');
			if (startIndex != -1) {
				int middleIndex = value.indexOf(',', startIndex + 1);
				if (middleIndex != -1) {
					width = Integer.parseInt(value.substring(startIndex + 1,
							middleIndex).trim());
					height = Integer.parseInt(value.substring(middleIndex + 1,
							value.length() - 1).trim());
				}
			}

			this.setImage(workbook, sheet, cellLocation, width, height);
		}
	}

	private void setImage(HSSFWorkbook workbook, HSSFSheet sheet,
			CellLocation cellLocation, int width, int height) {
		POIUtils.setCellValue(sheet, cellLocation, "");

		if (this.imageBuffer != null) {
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

			HSSFPicture picture = patriarch.createPicture(
					new HSSFClientAnchor(), this.pictureIndex);

			Dimension dimension = picture.getImageDimension();

			if (width == -1 || height == -1) {
				width = dimension.width;
				height = dimension.height;
				
			} else {
				double rate = Math.min(width / dimension.getWidth(), height / dimension.getHeight());
				width = (int) (dimension.getWidth() * rate);
				height = (int) (dimension.getHeight() * rate);
			}

			HSSFClientAnchor preferredSize = getPreferredSize(sheet,
					new HSSFClientAnchor(0, 0, 0, 0, (short) cellLocation.c,
							cellLocation.r, (short) 0, 0), width, height);
			picture.setAnchor(preferredSize);
		}
	}

	public static HSSFClientAnchor getPreferredSize(HSSFSheet sheet,
			HSSFClientAnchor anchor, float width, float height) {
		float w = 0.0F;
		w += getColumnWidthInPixels(sheet, anchor.getCol1())
				* (float) (1 - anchor.getDx1() / 1024);

		short col2 = (short) (anchor.getCol1() + 1);
		int dx2 = 0;
		while(w < width){
			w += getColumnWidthInPixels(sheet, col2++);
		}
		if (w > width) {
			col2--;
			float cw = getColumnWidthInPixels(sheet, col2);
			float delta = w - width;
			dx2 = (int) (((cw - delta) / cw) * 1024F);
		}
		anchor.setCol2(col2);
		anchor.setDx2(dx2);
		float h = 0.0F;
		h += (float) (1 - anchor.getDy1() / 256)
				* getRowHeightInPixels(sheet, anchor.getRow1());
		int row2 = anchor.getRow1() + 1;
		int dy2 = 0;
		while(h < height) {
			h += getRowHeightInPixels(sheet, row2++);
		}
		if (h > height) {
			row2--;
			float ch = getRowHeightInPixels(sheet, row2);
			float delta = h - height;
			dy2 = (int) (((ch - delta) / ch) * 256F);
		}
		anchor.setRow2(row2);
		anchor.setDy2(dy2);
		return anchor;
	}

	private static float getColumnWidthInPixels(Sheet sheet, int column) {
		int cw = sheet.getColumnWidth(column);
		float px = getPixelWidth(sheet, column);
		return (float) cw / px;
	}

	private static float getRowHeightInPixels(Sheet sheet, int i) {
		Row row = sheet.getRow(i);
		float height;
		if (row != null) {
			height = row.getHeight();
		} else {
			height = sheet.getDefaultRowHeight();
		}

		return height / 15F;
	}

	private static float getPixelWidth(Sheet sheet, int column) {
		int def = sheet.getDefaultColumnWidth() * 256;
		int cw = sheet.getColumnWidth(column);
		return cw != def ? 28.44444444f : 32.0f;
	}

}
