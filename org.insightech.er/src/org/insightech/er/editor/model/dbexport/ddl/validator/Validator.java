package org.insightech.er.editor.model.dbexport.ddl.validator;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.Rule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.all.DuplicatedPhysicalNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.all.ReservedNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.NoColumnTypeRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl.ReservedWordColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.DuplicatedColumnNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.FullTextIndexRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.NoColumnRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.NoTableNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl.ReservedWordTableNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.impl.UninputTablespaceRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewNameRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.NoViewSqlRule;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.view.impl.ReservedWordViewNameRule;

public class Validator {

	private static final List<Rule> RULE_LIST = new ArrayList<Rule>();

	static {
		// 全体に対するルール
		addRule(new DuplicatedPhysicalNameRule());
		addRule(new ReservedNameRule());

		// テーブルに対するルール
		addRule(new NoTableNameRule());
		addRule(new NoColumnRule());
		addRule(new DuplicatedColumnNameRule());
		addRule(new ReservedWordTableNameRule());
		addRule(new FullTextIndexRule());

		// ビューに対するルール
		addRule(new NoViewNameRule());
		addRule(new ReservedWordViewNameRule());
		addRule(new NoViewSqlRule());

		// 列に対するルール
		addRule(new NoColumnNameRule());
		addRule(new NoColumnTypeRule());
		addRule(new ReservedWordColumnNameRule());
		addRule(new UninputTablespaceRule());
	}

	private static void addRule(Rule rule) {
		RULE_LIST.add(rule);
	}

	public static List<ValidateResult> validate(ERDiagram diagram) {
		List<ValidateResult> errorList = new ArrayList<ValidateResult>();

		for (Rule rule : RULE_LIST) {
			boolean ret = rule.validate(diagram);

			errorList.addAll(rule.getErrorList());
			rule.clear();

			if (!ret) {
				break;
			}
		}

		return errorList;
	}

	private Validator() {
		// do nothing.
	}
}
