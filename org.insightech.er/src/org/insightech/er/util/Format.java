package org.insightech.er.util;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;

public class Format {

	public static String formatType(SqlType sqlType, TypeData typeData,
			DBManager manager) {
		String database = manager.getId();
		String type = null;

		if (sqlType != null) {
			type = sqlType.getAlias(database);
			if (type != null) {
				if (typeData.getLength() != null
						&& typeData.getDecimal() != null) {
					type = type.replaceAll("\\(.,.\\)", "("
							+ typeData.getLength() + ","
							+ typeData.getDecimal() + ")");

					type = type.replaceFirst("\\([a-z]\\)",
							"(" + typeData.getLength() + ")").replaceFirst(
							"\\([a-z]\\)", "(" + typeData.getDecimal() + ")");

				} else if (typeData.getLength() != null) {
					String len = null;

					if ("BLOB".equalsIgnoreCase(type)) {
						len = getFileSizeStr(typeData.getLength().longValue());
					} else {
						len = String.valueOf(typeData.getLength());
					}
					
					// 単位サポート
					if (manager.isSupported(SupportFunctions.COLUMN_UNIT) &&
							StringUtils.isNotBlank(typeData.getUnit())) {
						len += " " + StringUtils.trim(typeData.getUnit());
					}

					type = type.replaceAll("\\(.\\)", "(" + len + ")");

				}

				if (typeData.isArray() && manager.isSupported(SupportFunctions.ARRAY_TYPE)) {
					for (int i=0,n=typeData.getArrayDimension(); i <n; i++) {
						type += "[]";
					}
				}

				if (sqlType.isNumber() && typeData.isUnsigned()
						&& MySQLDBManager.ID.equals(database)) {
					type += " unsigned";
				}

				if (sqlType.doesNeedArgs()) {
					type += "(" + typeData.getArgs() + ")";
				}

			} else {
				type = "";
			}

		} else {
			type = "";
		}

		return type;
	}

	public static String getFileSizeStr(long fileSize) {
		long size = fileSize;
		String unit = "";

		if (size > 1024) {
			size = size / 1024;
			unit = "K";

			if (size > 1024) {
				size = size / 1024;
				unit = "M";

				if (size > 1024) {
					size = size / 1024;
					unit = "G";
				}
			}
		}

		return size + unit;
	}

	public static String null2blank(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	public static String escapeSQL(String str) {
		str = StringUtils.replace(str, "'", "''");
		str = StringUtils.replace(str, "\\", "\\\\");

		return str;
	}

	public static String toString(Object value) {
		if (value == null) {
			return "";
		}

		return value.toString();
	}
}
