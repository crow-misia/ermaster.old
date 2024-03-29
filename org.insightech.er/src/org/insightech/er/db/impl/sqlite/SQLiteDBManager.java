package org.insightech.er.db.impl.sqlite;

import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT;
import static org.insightech.er.db.SupportFunctions.SCHEMA;

import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SQLiteDBManager extends DBManagerBase {

	public static final String ID = "SQLite";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "org.sqlite.JDBC";
	}

	@Override
	protected String getURL() {
		return "jdbc:sqlite:<DB NAME>";
	}

	public int getDefaultPort() {
		return 0;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new SQLiteSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof SQLiteTableProperties) {
			return tableProperties;
		}

		return new SQLiteTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new SQLiteDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				SCHEMA,
				AUTO_INCREMENT,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new SQLiteTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new SQLitePreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new SQLitePreTableExportManager();
	}

	@Override
	public boolean doesNeedURLServerName() {
		return false;
	}

	public TablespaceProperties createTablespaceProperties() {
		return null;
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {
		return null;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT_TIMESTAMP" };
	}
	
	public BigDecimal getSequenceMaxValue() {
		return BigDecimal.ZERO;
	}

	public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
	}
}
