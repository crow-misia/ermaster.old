package org.insightech.er.db.impl.standard_sql;

import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_SETTING;
import static org.insightech.er.db.SupportFunctions.SCHEMA;
import static org.insightech.er.db.SupportFunctions.SEQUENCE;

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

public class StandardSQLDBManager extends DBManagerBase {

	public static final String ID = "StandardSQL";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "";
	}

	@Override
	protected String getURL() {
		return "";
	}

	public int getDefaultPort() {
		return 0;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new StandardSQLSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof StandardSQLTableProperties) {
			return tableProperties;
		}

		return new StandardSQLTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new StandardSQLDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				SEQUENCE,
				SCHEMA,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new StandardSQLTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new StandardSQLPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new StandardSQLPreTableExportManager();
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
		return null;
	}

    public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
    }
}
