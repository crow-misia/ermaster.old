package org.insightech.er.db.impl.oracle;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class OracleTableImportManager extends ImportFromDBManagerBase {

	private static final Logger logger = Logger
			.getLogger(OracleTableImportManager.class.getName());

	private static final Pattern INTERVAL_YEAR_TO_MONTH_PATTERN = Pattern
			.compile("interval year\\((.)\\) to month");

	private static final Pattern INTERVAL_DAY_TO_SECCOND_PATTERN = Pattern
			.compile("interval day\\((.)\\) to second\\((.)\\)");

	private static final Pattern TIMESTAMP_PATTERN = Pattern
			.compile("timestamp\\((.)\\).*");

	@Override
	protected void cacheColumnData(String schemaName, String tableName,
			List<DBObject> dbObjectList, IProgressMonitor monitor) throws SQLException, InterruptedException {
		super.cacheColumnData(schemaName, tableName, dbObjectList, monitor);

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			final StringBuilder sql = new StringBuilder("SELECT OWNER, TABLE_NAME, COLUMN_NAME, COMMENTS FROM SYS.ALL_COL_COMMENTS WHERE COMMENTS IS NOT NULL");
			if (StringUtils.isNotEmpty(schemaName)) {
				sql.append(" AND OWNER = ?");
			}
			if (StringUtils.isNotEmpty(tableName)) {
				sql.append(" AND TABLE_NAME = ?");
			}
			stmt = this.con.prepareStatement(sql.toString());
			int paramNum = 1;
			if (StringUtils.isNotEmpty(schemaName)) {
				stmt.setString(paramNum++, schemaName);
			}
			if (StringUtils.isNotEmpty(tableName)) {
				stmt.setString(paramNum, tableName);
			}
			rs = stmt.executeQuery();

			String oldTable = null;
			String oldSchema = null;
			Map<String, ColumnData> cash = null;

			while (rs.next()) {
				String table = rs.getString("TABLE_NAME");
				String schema = rs.getString("OWNER");
				String columnName = rs.getString("COLUMN_NAME");
				String comments = rs.getString("COMMENTS");

				if (cash == null || !StringUtils.equals(table, oldTable) || !StringUtils.equals(schema, oldSchema)) {
					oldSchema = schema;
					oldTable = table;
					String tableNameWithSchema = this.dbSetting.getTableNameWithSchema(table, schema);

					cash = this.columnDataCash.get(tableNameWithSchema);
				}
				if (cash != null) {
					ColumnData columnData = cash.get(columnName);
					if (columnData != null) {
						columnData.description = comments;
					}
				}
			}

		} finally {
			close(rs);
			close(stmt);
		}
	}

	@Override
	protected void cashTableComment(IProgressMonitor monitor)
			throws SQLException, InterruptedException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = this.con
					.prepareStatement("SELECT OWNER, TABLE_NAME, COMMENTS FROM SYS.ALL_TAB_COMMENTS WHERE COMMENTS IS NOT NULL");
			rs = stmt.executeQuery();

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");

				String schema = rs.getString("OWNER");
				String comments = rs.getString("COMMENTS");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				this.tableCommentMap.put(tableName, comments);
			}

		} finally {
			close(rs);
			close(stmt);
		}
	}

	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT TEXT FROM ALL_VIEWS WHERE OWNER = ? AND VIEW_NAME = ?";

		} else {
			return "SELECT TEXT FROM ALL_VIEWS WHERE VIEW_NAME = ?";

		}
	}

	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND SEQUENCE_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, sequenceName);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_SEQUENCES WHERE SEQUENCE_NAME = ?");
				stmt.setString(1, sequenceName);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT_BY"));
				BigDecimal minValue = rs.getBigDecimal("MIN_VALUE");
				sequence.setMinValue(minValue.longValue());
				BigDecimal maxValue = rs.getBigDecimal("MAX_VALUE");
				sequence.setMaxValue(maxValue);
				BigDecimal lastNumber = rs.getBigDecimal("LAST_NUMBER");
				sequence.setStart(lastNumber.longValue());
				sequence.setCache(rs.getInt("CACHE_SIZE"));

				String cycle = rs.getString("CYCLE_FLAG").toLowerCase();
				if ("y".equals(cycle)) {
					sequence.setCycle(true);
				} else {
					sequence.setCycle(false);
				}

				return sequence;
			}

			return null;

		} finally {
			close(rs);
			close(stmt);
		}
	}

	@Override
	protected Trigger importTrigger(String schema, String name)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if (schema != null) {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE OWNER = ? AND TRIGGER_NAME = ?");
				stmt.setString(1, schema);
				stmt.setString(2, name);

			} else {
				stmt = this.con
						.prepareStatement("SELECT * FROM SYS.ALL_TRIGGERS WHERE TRIGGER_NAME = ?");
				stmt.setString(1, name);

			}

			rs = stmt.executeQuery();

			if (rs.next()) {
				Trigger trigger = new Trigger();

				trigger.setName(name);
				trigger.setSchema(schema);
				trigger.setDescription(rs.getString("DESCRIPTION"));
				trigger.setSql(rs.getString("TRIGGER_BODY"));

				return trigger;
			}

			return null;

		} finally {
			close(rs);
			close(stmt);
		}
	}

	public static boolean isValidObjectName(String s) {
		return s.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {
		final String physicalName = table.getPhysicalName();
		if (!isValidObjectName(physicalName)) {
			logger
					.info("is not valid object name : "
							+ physicalName);
			return new ArrayList<Index>();
		}

		try {
			return super.getIndexes(table, metaData, primaryKeys);

		} catch (SQLException e) {
			if (e.getErrorCode() == 38029) {
				logger.info(physicalName + " : " + e.getMessage());
				return new ArrayList<Index>();
			}

			throw e;
		}
	}

	@Override
	protected int getLength(String type, int size) {
		int startIndex = type.indexOf('(');

		if (startIndex > 0) {
			int endIndex = type.indexOf(')', startIndex + 1);
			if (endIndex != -1) {
				String str = type.substring(startIndex + 1, endIndex);
				return Integer.parseInt(str);
			}
		}

		return size;
	}

	@Override
	protected List<ERTable> importSynonyms() throws SQLException,
			InterruptedException {
		return new ArrayList<ERTable>();

		// if (this.isOnlyUserTable()) {
		// PreparedStatement stmt = null;
		// ResultSet rs = null;
		//
		// try {
		// String sql =
		// "SELECT SYNONYM_NAME, TABLE_OWNER, TABLE_NAME FROM USER_SYNONYMS";
		//
		// stmt = this.con.prepareStatement(sql);
		// rs = stmt.executeQuery();
		//
		// while (rs.next()) {
		// String tableName = rs.getString("TABLE_NAME");
		// String schema = rs.getString("TABLE_OWNER");
		// String tableNameWithSchema = DBSetting
		// .getTableNameWithSchema(tableName, schema);
		//
		// if (!this.dbSetting.getUser().equalsIgnoreCase(schema)) {
		// this.cashColumnData(schema, null, null);
		//
		// ERTable table = this.importTable(tableNameWithSchema
		// , tableName, schema);
		//
		// if (table != null) {
		// list.add(table);
		// }
		// }
		// }
		//
		// } finally {
		// this.close(rs);
		// this.close(stmt);
		// }
		// }
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if ("number".equals(type)) {
			if (columnData.size == 22 && columnData.decimalDigits == 0) {
				columnData.size = 0;
			}

		} else if ("float".equals(type)) {
			if (columnData.size == 126 && columnData.decimalDigits == 0) {
				columnData.size = 0;
			}

		} else if ("urowid".equals(type)) {
			if (columnData.size == 4000) {
				columnData.size = 0;
			}

		} else if ("anydata".equals(type)) {
			columnData.size = 0;

		} else {
			Matcher yearToMonthMatcber = INTERVAL_YEAR_TO_MONTH_PATTERN
					.matcher(columnData.type);
			Matcher dayToSecondMatcber = INTERVAL_DAY_TO_SECCOND_PATTERN
					.matcher(columnData.type);
			Matcher timestampMatcber = TIMESTAMP_PATTERN
					.matcher(columnData.type);

			if (yearToMonthMatcber.matches()) {
				columnData.type = "interval year to month";

				if (columnData.size == 2) {
					columnData.size = 0;
				}

			} else if (dayToSecondMatcber.matches()) {
				columnData.type = "interval day to second";

				if (columnData.size == 2 && columnData.decimalDigits == 6) {
					columnData.size = 0;
					columnData.decimalDigits = 0;
				}

			} else if (timestampMatcber.matches()) {
				columnData.type = columnData.type.replaceAll("\\(.\\)", "");
				columnData.size = 0;

				if (columnData.decimalDigits == 6) {
					columnData.size = 0;

				} else {
					columnData.size = columnData.decimalDigits;
				}

				columnData.decimalDigits = 0;
			}

		}

		return columnData;
	}

}
