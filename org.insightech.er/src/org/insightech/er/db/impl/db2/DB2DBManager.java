package org.insightech.er.db.impl.db2;

import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT;
import static org.insightech.er.db.SupportFunctions.AUTO_INCREMENT_CACHE;
import static org.insightech.er.db.SupportFunctions.SCHEMA;
import static org.insightech.er.db.SupportFunctions.SEQUENCE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CACHE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CYCLE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MAXVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MINVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_ORDER;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2DBManager extends DBManagerBase {

	public static final String ID = "DB2";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	@Override
	protected String getURL() {
		return "jdbc:db2://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 50000;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new DB2SqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof DB2TableProperties) {
			return tableProperties;
		}

		return new DB2TableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new DB2DDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				AUTO_INCREMENT,
				AUTO_INCREMENT_CACHE,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CACHE,
				SEQUENCE_CYCLE,
				SEQUENCE_ORDER,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new DB2TableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new DB2PreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new DB2PreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new DB2TablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof DB2TablespaceProperties)) {
			return new DB2TablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT TIMESTAMP" };
	}

	@Override
	public Set<String> getSystemSchemaList() {
		Set<String> list = new HashSet<String>();

		list.add("nullid");
		list.add("sqlj");
		list.add("syscat");
		list.add("sysfun");
		list.add("sysibm");
		list.add("sysibmadm");
		list.add("sysibminternal");
		list.add("sysibmts");
		list.add("sysproc");
		list.add("syspublic");
		list.add("sysstat");
		list.add("systools");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

    public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
    }
}
