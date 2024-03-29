package org.insightech.er.db.impl.postgres;

import static org.insightech.er.db.SupportFunctions.ARRAY_TYPE;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_CACHE;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_CYCLE;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_MAXVALUE;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_MINVALUE;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_SETTING;
import static org.insightech.er.db.SupportFunctions.SCHEMA;
import static org.insightech.er.db.SupportFunctions.SEQUENCE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CYCLE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MAXVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MINVALUE;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.word.column.real.ColumnDialog;

public class PostgresDBManager extends DBManagerBase {

	public static final String ID = "PostgreSQL";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "org.postgresql.Driver";
	}

	@Override
	protected String getURL() {
		return "jdbc:postgresql://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 5432;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new PostgresSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof PostgresTableProperties) {
			return tableProperties;
		}

		return new PostgresTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new PostgresDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return new String[] { "BTREE", "RTREE", "HASH", };
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				AUTO_INCREMENT_SETTING,
				AUTO_INCREMENT_MINVALUE,
				AUTO_INCREMENT_MAXVALUE,
				AUTO_INCREMENT_CACHE,
				AUTO_INCREMENT_CYCLE,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CYCLE,
				ARRAY_TYPE,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new PostgresTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new PostgresPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new PostgresPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new PostgresTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof PostgresTablespaceProperties)) {
			return new PostgresTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT_TIMESTAMP", "now()" };
	}

	@Override
	public Set<String> getSystemSchemaList() {
		Set<String> list = new HashSet<String>();

		list.add("information_schema");
		list.add("pg_catalog");
		list.add("pg_toast_temp_1");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return BigDecimal.valueOf(Long.MAX_VALUE);
	}

	@Override
	public void setEnabledBySqlType(final SqlType sqlType, final ColumnDialog dialog) {
		final String id = sqlType.getId();

		if (SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(id)
				|| SqlType.SQL_TYPE_ID_SERIAL.equals(id)) {
			dialog.setAutoIncrementSettingButtonEnabled(true);
		} else {
			dialog.setAutoIncrementSettingButtonEnabled(false);
		}
	}

    public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
    }
}
