package org.insightech.er.db.impl.sqlite;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class SQLiteTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {
		return new ArrayList<Index>();
	}

	@Override
	protected void setForeignKeys(List<ERTable> list) throws SQLException {
		// SQLite note yet implemented
	}

	@Override
	protected Map<String, ColumnData> getColumnDataMap(
			String tableNameWithSchema, String tableName, String schema)
			throws SQLException, InterruptedException {
		this.cashColumnData(null, tableName, null, null);

		return super.getColumnDataMap(tableNameWithSchema, tableName, schema);
	}

}
