package org.insightech.er.db.impl.sqlserver;

import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_SETTING;
import static org.insightech.er.db.SupportFunctions.SCHEMA;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.sqlserver.tablespace.SqlServerTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class SqlServerDBManager extends DBManagerBase {

	public static final String ID = "SQLServer";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}

	@Override
	protected String getURL() {
		return "jdbc:sqlserver://<SERVER NAME>:<PORT>;database=<DB NAME>";
	}

	public int getDefaultPort() {
		return 1433;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new SqlServerSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof SqlServerTableProperties) {
			return tableProperties;
		}

		return new SqlServerTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new SqlServerDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_SETTING,
				SCHEMA,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new SqlServerTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new SqlServerPreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new SqlServerPreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new SqlServerTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof SqlServerTablespaceProperties)) {
			return new SqlServerTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "GETDATE()", "CURRENT_TIMESTAMP" };
	}
	
	@Override
	public Set<String> getSystemSchemaList() {
		Set<String> list = new HashSet<String>();
		
		list.add("db_accessadmin");
		list.add("db_backupoperator");
		list.add("db_datareader");
		list.add("db_datawriter");
		list.add("db_ddladmin");
		list.add("db_denydatareader");
		list.add("db_denydatawriter");
		list.add("db_owner");
		list.add("db_securityadmin");
		
		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

	public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
    }
}
