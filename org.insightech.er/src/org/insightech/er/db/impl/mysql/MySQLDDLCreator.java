package org.insightech.er.db.impl.mysql;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.ResourceString;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Format;

public class MySQLDDLCreator extends DDLCreator {

	public MySQLDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	@Override
	public String getPostDDL(ERTable table) {
		MySQLTableProperties commonTableProperties = (MySQLTableProperties) this
				.getDiagram().getDiagramContents().getSettings()
				.getTableViewProperties();

		MySQLTableProperties tableProperties = (MySQLTableProperties) table
				.getTableViewProperties();

		String engine = tableProperties.getStorageEngine();
		if (StringUtils.isEmpty(engine)) {
			engine = commonTableProperties.getStorageEngine();
		}
		String characterSet = tableProperties.getCharacterSet();
		if (StringUtils.isEmpty(characterSet)) {
			characterSet = commonTableProperties.getCharacterSet();
		}

		String collation = tableProperties.getCollation();
		if (StringUtils.isEmpty(collation)) {
			characterSet = commonTableProperties.getCharacterSet();
		}

		StringBuilder postDDL = new StringBuilder();
		if (StringUtils.isNotEmpty(engine)) {
			postDDL.append(" ENGINE = ");
			postDDL.append(engine);
		}

		if (this.ddlTarget.createComment) {
			String comment = this.filterComment(table.getLogicalName(),
					table.getDescription(), false);

			if (StringUtils.isNotBlank(comment)) {
				postDDL.append(" COMMENT = '");
				postDDL.append(StringUtils.replace(comment, "'", "''"));
				postDDL.append("'");
			}
		}

		if (StringUtils.isNotEmpty(characterSet)) {
			postDDL.append(" DEFAULT CHARACTER SET ");
			postDDL.append(characterSet);

			if (StringUtils.isNotEmpty(collation)) {
				postDDL.append(" COLLATE ");
				postDDL.append(collation);
			}
		}

		postDDL.append(super.getPostDDL(table));

		return postDDL.toString();
	}

	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		String description = normalColumn.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineColumnComment) {
			ddl.append("\t-- ");
			ddl.append(StringUtils.replace(description, "\n", "\n\t-- "));
			ddl.append("\r\n");
		}

		ddl.append("\t");
		ddl.append(filter(normalColumn.getPhysicalName()));
		ddl.append(" ");

		ddl.append(filter(Format.formatType(normalColumn.getType(),
				normalColumn.getTypeData(), dbManager)));

		if (StringUtils.isNotEmpty(normalColumn.getCharacterSet())) {
			ddl.append(" CHARACTER SET ");
			ddl.append(normalColumn.getCharacterSet());

			if (StringUtils.isNotEmpty(normalColumn.getCollation())) {
				ddl.append(" COLLATE ");
				ddl.append(normalColumn.getCollation());
			}
		}

		if (StringUtils.isNotEmpty(normalColumn.getDefaultValue())) {
			String defaultValue = normalColumn.getDefaultValue();
			if (ResourceString.getResourceString("label.current.date.time")
					.equals(defaultValue)) {
				defaultValue = this.getDBManager().getCurrentTimeValue()[0];
			}

			ddl.append(" DEFAULT ");
			if (this.doesNeedQuoteDefaultValue(normalColumn)) {
				ddl.append("'");
				ddl.append(Format.escapeSQL(defaultValue));
				ddl.append("'");

			} else {
				ddl.append(defaultValue);
			}
		}

		if (normalColumn.isNotNull()) {
			ddl.append(" NOT NULL");
		}

		if (normalColumn.isUniqueKey()) {
			if (StringUtils.isNotBlank(normalColumn.getUniqueKeyName())) {
				ddl.append(" CONSTRAINT ");
				ddl.append(normalColumn.getUniqueKeyName());
			}
			ddl.append(" UNIQUE");
		}

		String constraint = Format.null2blank(normalColumn.getConstraint());
		if (!"".equals(constraint)) {
			ddl.append(" ");
			ddl.append(constraint);
		}

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" AUTO_INCREMENT");
		}

		if (this.ddlTarget.createComment) {
			String comment = this.filterComment(normalColumn.getLogicalName(),
					normalColumn.getDescription(), true);

			if (StringUtils.isNotBlank(comment)) {
				ddl.append(" COMMENT '");
				ddl.append(StringUtils.replace(comment, "'", "''"));
				ddl.append("'");
			}
		}

		return ddl.toString();
	}

	@Override
	protected boolean doesNeedQuoteDefaultValue(NormalColumn normalColumn) {
		if (!super.doesNeedQuoteDefaultValue(normalColumn)) {
			return false;
		}

		if ("CURRENT_TIMESTAMP".equalsIgnoreCase(normalColumn.getDefaultValue()
				.trim())) {
			return false;
		}

		return true;
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		MySQLTablespaceProperties tablespaceProperties = (MySQLTablespaceProperties) tablespace
				.getProperties(this.environment, this.getDiagram());

		StringBuilder ddl = new StringBuilder();

		ddl.append("CREATE TABLESPACE ");
		ddl.append(filter(tablespace.getName()));
		ddl.append("\r\n");
		ddl.append(" ADD DATAFILE '");
		ddl.append(tablespaceProperties.getDataFile());
		ddl.append("'\r\n");
		ddl.append(" USE LOGFILE GROUP ");
		ddl.append(tablespaceProperties.getLogFileGroup());
		ddl.append("\r\n");

		if (StringUtils.isNotEmpty(tablespaceProperties.getExtentSize())) {
			ddl.append(" EXTENT_SIZE ");
			ddl.append(tablespaceProperties.getExtentSize());
			ddl.append("\r\n");
		}

		ddl.append(" INITIAL_SIZE ");
		ddl.append(tablespaceProperties.getInitialSize());
		ddl.append("\r\n");
		ddl.append(" ENGINE ");
		ddl.append(tablespaceProperties.getEngine());
		ddl.append("\r\n");

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	@Override
	protected String filterComment(String logicalName, String description,
			boolean column) {
		String comment = null;

		if (this.ddlTarget.commentValueLogicalNameDescription) {
			comment = Format.null2blank(logicalName);

			if (StringUtils.isNotBlank(description)) {
				comment = comment + " : " + Format.null2blank(description);
			}

		} else if (this.ddlTarget.commentValueLogicalName) {
			comment = Format.null2blank(logicalName);

		} else {
			comment = Format.null2blank(description);

		}

		if (ddlTarget.commentReplaceLineFeed) {
			final String nl = Format.null2blank(ddlTarget.commentReplaceString);
			comment = StringUtils.replace(comment, "\r\n", nl);
			comment = StringUtils.replace(comment, "\r", nl);
			comment = StringUtils.replace(comment, "\n", nl);
		}

		int maxLength = 60;

		if (column) {
			maxLength = 255;
		}

		if (comment.length() > maxLength) {
			comment = comment.substring(0, maxLength);
		}

		return comment;
	}

	@Override
	public String getDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		String description = index.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(StringUtils.replace(description, "\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		if (!index.isNonUnique()) {
			ddl.append("UNIQUE ");
		}
		ddl.append("INDEX ");
		ddl.append(filter(index.getName()));

		if (StringUtils.isNotBlank(index.getType())) {
			ddl.append(" USING ");
			ddl.append(index.getType().trim());
		}

		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(this.getDiagram()
				.getDatabase())));

		ddl.append(" (");
		boolean first = true;

		int i = 0;
		List<Boolean> descs = index.getDescs();

		final boolean isSupportDescIndex = this.getDBManager().isSupported(SupportFunctions.DESC_INDEX) &&
				descs.size() > 1;
		for (NormalColumn column : index.getColumns()) {
			if (!first) {
				ddl.append(", ");
			}

			ddl.append(filter(column.getPhysicalName()));

			if (isSupportDescIndex) {
				Boolean desc = descs.get(i);
				if (Boolean.TRUE.equals(desc)) {
					ddl.append(" DESC");
				} else {
					ddl.append(" ASC");
				}
			}

			first = false;
			i++;
		}

		ddl.append(')');

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	@Override
	public String getDropDDL(ERDiagram diagram) {
		StringBuilder ddl = new StringBuilder();
		ddl.append("SET SESSION FOREIGN_KEY_CHECKS=0");
		if (this.semicolon) {
			ddl.append(';');
		}
		ddl.append("\r\n");

		ddl.append(super.getDropDDL(diagram));

		return ddl.toString();
	}

	@Override
	public String getDropDDL(Index index, ERTable table) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP INDEX ");
		ddl.append(this.getIfExistsOption());
		ddl.append(filter(index.getName()));
		ddl.append(" ON ");
		ddl.append(filter(table.getNameWithSchema(this.getDiagram()
				.getDatabase())));

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	@Override
	protected String getPrimaryKeyLength(ERTable table, NormalColumn primaryKey) {
		SqlType type = primaryKey.getType();

		if (type != null && type.isFullTextIndexable()
				&& !type.isNeedLength(this.getDiagram().getDatabase())) {
			Integer length = null;

			MySQLTableProperties tableProperties = (MySQLTableProperties) table
					.getTableViewProperties();

			length = tableProperties.getPrimaryKeyLengthOfText();

			if (length == null) {
				tableProperties = (MySQLTableProperties) this.getDiagram()
						.getDiagramContents().getSettings()
						.getTableViewProperties();

				length = tableProperties.getPrimaryKeyLengthOfText();
			}

			return "(" + length + ")";
		}

		return "";
	}

}
