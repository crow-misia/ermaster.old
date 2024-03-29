package org.insightech.er.db.impl.hsqldb;

import static org.insightech.er.db.SupportFunctions.SCHEMA;
import static org.insightech.er.db.SupportFunctions.SEQUENCE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CYCLE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MAXVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MINVALUE;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

public class HSQLDBDBManager extends DBManagerBase {

	public static final String ID = "HSQLDB";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "org.hsqldb.jdbcDriver";
	}

	@Override
	protected String getURL() {
		return "jdbc:hsqldb:hsql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 9001;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new HSQLDBSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof HSQLDBTableProperties) {
			return tableProperties;
		}

		return new HSQLDBTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new HSQLDBDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CYCLE,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new HSQLDBTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new HSQLDBPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new HSQLDBPreTableExportManager();
	}

	@Override
	public boolean doesNeedURLDatabaseName() {
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

	@Override
	public Set<String> getSystemSchemaList() {
		Set<String> list = new HashSet<String>();

		list.add("information_schema");
		list.add("system_lobs");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

    public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
    }
}
