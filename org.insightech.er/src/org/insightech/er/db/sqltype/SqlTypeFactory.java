package org.insightech.er.db.sqltype;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.insightech.er.db.sqltype.SqlType.TypeKey;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.io.IOUtils;

public final class SqlTypeFactory {
	public static void load() throws IOException, ClassNotFoundException {
		InputStream in = null;

		try {
			in = SqlTypeFactory.class.getResourceAsStream("/SqlType.xls");

			HSSFWorkbook workBook = POIUtils.readExcelBook(in);

			HSSFSheet sheet = workBook.getSheetAt(0);

			Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<String, Map<SqlType, String>>();
			Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();

			HSSFRow headerRow = sheet.getRow(0);

			for (int colNum = 4, endColNum = headerRow.getLastCellNum(); colNum < endColNum; colNum++) {
				String dbId = POIUtils.getCellValue(sheet, 0, colNum);

				Map<SqlType, String> aliasMap = new LinkedHashMap<SqlType, String>();
				dbAliasMap.put(dbId, aliasMap);

				Map<TypeKey, SqlType> sqlTypeMap = new LinkedHashMap<TypeKey, SqlType>();
				dbSqlTypeMap.put(dbId, sqlTypeMap);
			}

			SqlType.setDBAliasMap(dbAliasMap, dbSqlTypeMap);

			for (int rowNum = 1, endRowNum = sheet.getLastRowNum(); rowNum <= endRowNum; rowNum++) {
				HSSFRow row = sheet.getRow(rowNum);

				String sqlTypeId = POIUtils.getCellValue(sheet, rowNum, 0);
				if (StringUtils.isEmpty(sqlTypeId)) {
					break;
				}
				Class javaClass = Class.forName(POIUtils.getCellValue(sheet,
						rowNum, 1));
				boolean needArgs = POIUtils.getBooleanCellValue(sheet, rowNum,
						2);
				boolean fullTextIndexable = POIUtils.getBooleanCellValue(sheet,
						rowNum, 3);

				SqlType sqlType = new SqlType(sqlTypeId, javaClass, needArgs,
						fullTextIndexable);

				for (int colNum = 4, endColNum = row.getLastCellNum(); colNum < endColNum; colNum++) {

					String dbId = POIUtils.getCellValue(sheet, 0, colNum);

					if (StringUtils.isEmpty(dbId)) {
						dbId = POIUtils.getCellValue(sheet, 0, colNum - 1);
						String key = POIUtils.getCellValue(sheet, rowNum,
								colNum);
						if (StringUtils.isNotEmpty(key)) {
							sqlType.addToSqlTypeMap(key, dbId);
						}

					} else {
						Map<SqlType, String> aliasMap = dbAliasMap.get(dbId);

						if (POIUtils.getCellColor(sheet, rowNum, colNum) != HSSFColor.RED.index) {
							String alias = POIUtils.getCellValue(sheet, rowNum,
									colNum);

							if (StringUtils.isEmpty(alias)) {
								alias = sqlTypeId;
							}

							aliasMap.put(sqlType, alias);

							if (POIUtils.getCellColor(sheet, rowNum, colNum) == HSSFColor.SKY_BLUE.index) {
								sqlType.addToSqlTypeMap(alias, dbId);
							}
						}
					}
				}
			}

		} finally {
			IOUtils.closeQuietly(in);
		}

	}
	
	public static void main(String[] args) {
		SqlType.main(new String[0]);
	}

	private SqlTypeFactory() {
		// do nothing.
	}
}
