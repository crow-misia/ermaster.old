package org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.ColumnRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class NoColumnNameRule extends ColumnRule {

	@Override
	public boolean validate(ERTable table, NormalColumn column) {
		if (StringUtils.isBlank(column.getPhysicalName())) {
			ValidateResult validateResult = new ValidateResult();
			validateResult.setMessage(ResourceString
					.getResourceString("error.validate.no.column.name")
					+ table.getPhysicalName());
			validateResult.setLocation(table.getLogicalName());
			validateResult.setSeverity(IMarker.SEVERITY_WARNING);
			validateResult.setObject(table);

			this.addError(validateResult);
		}

		return true;
	}
}
