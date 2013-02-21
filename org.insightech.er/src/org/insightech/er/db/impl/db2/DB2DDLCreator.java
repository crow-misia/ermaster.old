package org.insightech.er.db.impl.db2;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Format;

public class DB2DDLCreator extends DDLCreator {

	public DB2DDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" GENERATED ALWAYS AS IDENTITY ");

			final Sequence sequence = normalColumn.getAutoIncrementSetting();
			final Long start = sequence.getStart();
			final Integer increment = sequence.getIncrement();

			if (start != null || increment != null) {
				ddl.append("(START WITH ");
				
				if (start == null) {
					ddl.append("1");
				} else {
					ddl.append(start);
				}

				if (increment != null) {
					ddl.append(", INCREMENT BY ");
					ddl.append(increment);
				}

				ddl.append(")");
			}
		}

		return ddl.toString();
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		final DB2TablespaceProperties properties = (DB2TablespaceProperties) tablespace
				.getProperties(this.environment, this.getDiagram());
		final String type = properties.getType();
		final String pageSize = properties.getPageSize();
		final String extentSize = properties.getExtentSize();

		final StringBuilder ddl = new StringBuilder();

		ddl.append("CREATE ");
		if (StringUtils.isNotEmpty(type)) {
			ddl.append(type);
			ddl.append(" ");
		}

		ddl.append("TABLESPACE ");
		ddl.append(filter(tablespace.getName()));
		ddl.append("\r\n");

		if (StringUtils.isNotEmpty(pageSize)) {
			ddl.append(" PAGESIZE ");
			ddl.append(pageSize);
			ddl.append("\r\n");
		}

		ddl.append(" MANAGED BY ");
		ddl.append(properties.getManagedBy());
		ddl.append(" USING(");
		ddl.append(properties.getContainer());
		ddl.append(")\r\n");

		if (StringUtils.isNotEmpty(extentSize)) {
			ddl.append(" EXTENTSIZE ");
			ddl.append(extentSize);
			ddl.append("\r\n");
		}

		if (StringUtils.isNotEmpty(properties.getPrefetchSize())) {
			ddl.append(" PREFETCHSIZE ");
			ddl.append(properties.getPrefetchSize());
			ddl.append("\r\n");
		}

		if (StringUtils.isNotEmpty(properties.getBufferPoolName())) {
			ddl.append(" BUFFERPOOL ");
			ddl.append(properties.getBufferPoolName());
			ddl.append("\r\n");
		}

		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();
	}

	@Override
	public String getDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		String description = sequence.getDescription();
		if (this.semicolon && StringUtils.isNotBlank(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(StringUtils.replace(description, "\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		ddl.append("SEQUENCE ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence
				.getName())));
		if (StringUtils.isNotBlank(sequence.getDataType())) {
			ddl.append(" AS ");
			String dataType = sequence.getDataType();
			dataType = StringUtils.replace(dataType, "(p)", "("
					+ Format.toString(sequence.getDecimalSize() + ")"));
			ddl.append(dataType);
		}
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT BY ");
			ddl.append(sequence.getIncrement());
		}
		if (sequence.getMinValue() != null) {
			ddl.append(" MINVALUE ");
			ddl.append(sequence.getMinValue());
		}
		if (sequence.getMaxValue() != null) {
			ddl.append(" MAXVALUE ");
			ddl.append(sequence.getMaxValue());
		}
		if (sequence.getStart() != null) {
			ddl.append(" START WITH ");
			ddl.append(sequence.getStart());
		}
		if (sequence.getCache() != null) {
			ddl.append(" CACHE ");
			ddl.append(sequence.getCache());
		}
		if (sequence.isCycle()) {
			ddl.append(" CYCLE");
		}
		if (sequence.isOrder()) {
			ddl.append(" ORDER");
		}
		if (this.semicolon) {
			ddl.append(';');
		}

		return ddl.toString();

	}

}
