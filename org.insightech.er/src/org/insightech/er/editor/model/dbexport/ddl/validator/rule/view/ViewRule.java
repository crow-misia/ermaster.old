package org.insightech.er.editor.model.dbexport.ddl.validator.rule.view;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public abstract class ViewRule extends BaseRule {

	private List<ValidateResult> errorList;

	private String database;

	public ViewRule() {
		this.errorList = new ArrayList<ValidateResult>();
	}

	@Override
	protected void addError(ValidateResult errorMessage) {
		this.errorList.add(errorMessage);
	}

	@Override
	public List<ValidateResult> getErrorList() {
		return this.errorList;
	}

	@Override
	public void clear() {
		this.errorList.clear();
	}

	public boolean validate(ERDiagram diagram) {
		this.database = diagram.getDatabase();

		for (View view : diagram.getDiagramContents().getContents()
				.getViewSet()) {
			if (!this.validate(view)) {
				return false;
			}
		}

		return true;
	}

	protected DBManager getDBManager() {
		return DBManagerFactory.getDBManager(this.database);
	}

	abstract public boolean validate(View view);
}
