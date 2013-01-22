package org.insightech.er.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CompoundCommand;
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

public interface DBManager {

	public static final int SUPPORT_AUTO_INCREMENT = 0;

	public static final int SUPPORT_AUTO_INCREMENT_SETTING = 1;

	public static final int SUPPORT_AUTO_INCREMENT_MINVALUE = 8;

	public static final int SUPPORT_AUTO_INCREMENT_MAXVALUE = 9;

	public static final int SUPPORT_AUTO_INCREMENT_CACHE = 10;

	public static final int SUPPORT_AUTO_INCREMENT_CYCLE = 11;

	public static final int SUPPORT_DESC_INDEX = 2;

	public static final int SUPPORT_FULLTEXT_INDEX = 3;

	public static final int SUPPORT_SCHEMA = 4;

	public static final int SUPPORT_SEQUENCE = 5;

	public static final int SUPPORT_SEQUENCE_MINVALUE = 12;

	public static final int SUPPORT_SEQUENCE_MAXVALUE = 13;

	public static final int SUPPORT_SEQUENCE_CACHE = 14;

	public static final int SUPPORT_SEQUENCE_CYCLE = 15;

	public static final int SUPPORT_SEQUENCE_ORDER = 16;

	public static final int SUPPORT_UNIT = 6;

	public static final int SUPPORT_ARRAY_TYPE = 7;

    String getId();

    String getURL(String serverName, String dbName, int port);

    int getDefaultPort();

    String getDriverClassName();

    Class<Driver> getDriverClass(String driverClassName);

    SqlTypeManager getSqlTypeManager();

    TableProperties createTableProperties(TableProperties tableProperties);

    TablespaceProperties createTablespaceProperties();

    TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties);

    DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon);

    boolean isSupported(SupportFunctions support);

    boolean doesNeedURLDatabaseName();

    boolean doesNeedURLServerName();

    boolean isReservedWord(String str);

    String[] getIndexTypeList(ERTable table);

    PreImportFromDBManager getPreTableImportManager();

    ImportFromDBManager getTableImportManager();

    PreTableExportManager getPreTableExportManager();

    String[] getCurrentTimeValue();

    List<String> getImportSchemaList(Connection con) throws SQLException;

    Set<String> getSystemSchemaList();

    BigDecimal getSequenceMaxValue();

    void setEnabledBySqlType(final SqlType sqlType, final ColumnDialog dialog);

    List<String> getCharacterSetList();

    List<String> getCollationList(String characterset);

    void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName);
}
