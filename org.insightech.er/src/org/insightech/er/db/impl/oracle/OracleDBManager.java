package org.insightech.er.db.impl.oracle;

import static org.insightech.er.db.SupportFunctions.BITMAP_INDEX;
import static org.insightech.er.db.SupportFunctions.COLUMN_UNIT;
import static org.insightech.er.db.SupportFunctions.DESC_INDEX;
import static org.insightech.er.db.SupportFunctions.SCHEMA;
import static org.insightech.er.db.SupportFunctions.SEQUENCE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CACHE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_CYCLE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MAXVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_MINVALUE;
import static org.insightech.er.db.SupportFunctions.SEQUENCE_ORDER;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.commands.CompoundCommand;
import org.insightech.er.Activator;
import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.CreateSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.CreateTriggerCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.DeleteTriggerCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public class OracleDBManager extends DBManagerBase {

	private static final ResourceBundle CHARACTER_SET_RESOURCE = ResourceBundle
			.getBundle("oracle_characterset");
	
	public static final String ID = "Oracle";

	public String getId() {
		return ID;
	}

	public String getDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	@Override
	protected String getURL() {
		return "jdbc:oracle:thin:@<SERVER NAME>:<PORT>:<DB NAME>";
	}

	public int getDefaultPort() {
		return 1521;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new OracleSqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof OracleTableProperties) {
			return tableProperties;
		}

		return new OracleTableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new OracleDDLCreator(diagram, semicolon);
	}

	public String[] getIndexTypeList(ERTable table) {
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	protected SupportFunctions[] getSupportItems() {
		return new SupportFunctions[] {
				DESC_INDEX,
				BITMAP_INDEX,
				SCHEMA,
				SEQUENCE,
				SEQUENCE_MINVALUE,
				SEQUENCE_MAXVALUE,
				SEQUENCE_CACHE,
				SEQUENCE_CYCLE,
				SEQUENCE_ORDER,
				COLUMN_UNIT,
		};
	}

	public ImportFromDBManager getTableImportManager() {
		return new OracleTableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new OraclePreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new OraclePreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new OracleTablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof OracleTablespaceProperties)) {
			return new OracleTablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "SYSDATE" };
	}

	@Override
	public Set<String> getSystemSchemaList() {
		Set<String> list = new HashSet<String>();

		list.add("anonymous");
		list.add("ctxsys");
		list.add("dbsnmp");
		list.add("dip");
		list.add("flows_020100");
		list.add("flows_files");
		list.add("hr");
		list.add("mdsys");
		list.add("outln");
		list.add("sys");
		list.add("system");
		list.add("tsmsys");
		list.add("xdb");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return new BigDecimal("9999999999999999999999999999");
	}

	@Override
	public List<String> getCharacterSetList() {
		final List<String> list = new ArrayList<String>();

		final Enumeration<String> keys = CHARACTER_SET_RESOURCE.getKeys();

		while (keys.hasMoreElements()) {
			list.add(keys.nextElement());
		}

		return list;
	}

    /**
     * Oracleデータベースの場合、AutoIncrement用トリガー、シーケンスの登録・削除を行う
     */
    public void createAutoIncrement(ERDiagram diagram, ERTable table, ERTable copyTable, CompoundCommand command, String tableName) {
        NormalColumn autoIncrementColumn = copyTable
                .getAutoIncrementColumn();

        if (autoIncrementColumn != null) {
            String columnName = autoIncrementColumn.getPhysicalName();

            if (StringUtils.isNotEmpty(columnName)) {
                String triggerName = "TRI_" + tableName + "_" + columnName;
                String sequenceName = "SEQ_" + tableName + "_" + columnName;

                TriggerSet triggerSet = diagram.getDiagramContents()
                        .getTriggerSet();
                SequenceSet sequenceSet = diagram.getDiagramContents()
                        .getSequenceSet();

                if (!triggerSet.contains(triggerName)
                        || !sequenceSet.contains(sequenceName)) {
                    if (Activator
                            .showConfirmDialog("dialog.message.confirm.create.autoincrement.trigger")) {
                        if (!triggerSet.contains(triggerName)) {
                            // トリガーの作成
                            Trigger trigger = new Trigger();
                            trigger.setName(triggerName);
                            trigger.setSql("BEFORE INSERT ON " + tableName
                                    + "\r\nFOR EACH ROW" + "\r\nBEGIN"
                                    + "\r\n\tSELECT " + sequenceName
                                    + ".nextval\r\n\tINTO :new."
                                    + columnName + "\r\n\tFROM dual;"
                                    + "\r\nEND");

                            CreateTriggerCommand createTriggerCommand = new CreateTriggerCommand(
                                    diagram, trigger);
                            command.add(createTriggerCommand);
                        }

                        if (!sequenceSet.contains(sequenceName)) {
                            // シーケンスの作成
                            Sequence sequence = new Sequence();
                            sequence.setName(sequenceName);
                            sequence.setStart(1L);
                            sequence.setIncrement(1);

                            CreateSequenceCommand createSequenceCommand = new CreateSequenceCommand(
                                    diagram, sequence);
                            command.add(createSequenceCommand);
                        }
                    }
                }
            }
        }

        NormalColumn oldAutoIncrementColumn = table
                .getAutoIncrementColumn();

        if (oldAutoIncrementColumn != null) {
            if (autoIncrementColumn == null
                    || ((CopyColumn) autoIncrementColumn)
                            .getOriginalColumn() != oldAutoIncrementColumn) {
                String oldTableName = table.getPhysicalName();
                String columnName = oldAutoIncrementColumn
                        .getPhysicalName();

                if (StringUtils.isNotEmpty(columnName)) {
                    String triggerName = "TRI_" + oldTableName + "_"
                            + columnName;
                    String sequenceName = "SEQ_" + oldTableName + "_"
                            + columnName;

                    TriggerSet triggerSet = diagram.getDiagramContents()
                            .getTriggerSet();
                    SequenceSet sequenceSet = diagram.getDiagramContents()
                            .getSequenceSet();

                    if (triggerSet.contains(triggerName)
                            || sequenceSet.contains(sequenceName)) {
                        if (Activator
                                .showConfirmDialog("dialog.message.confirm.remove.autoincrement.trigger")) {

                            // トリガーの削除
                            Trigger trigger = triggerSet.get(triggerName);

                            if (trigger != null) {
                                DeleteTriggerCommand deleteTriggerCommand = new DeleteTriggerCommand(
                                        diagram, trigger);
                                command.add(deleteTriggerCommand);
                            }

                            // シーケンスの作成
                            Sequence sequence = sequenceSet
                                    .get(sequenceName);

                            if (sequence != null) {
                                DeleteSequenceCommand deleteSequenceCommand = new DeleteSequenceCommand(
                                        diagram, sequence);
                                command.add(deleteSequenceCommand);
                            }
                        }
                    }
                }
            }
        }
    }
}
