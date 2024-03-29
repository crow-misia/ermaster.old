package org.insightech.er.editor.view.dialog.word.word;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.RealWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.word.AbstractWordDialog;

public class WordDialog extends AbstractWordDialog {

	private Word targetWord;

	private Word returnWord;

	public WordDialog(Shell parentShell, Word targetWord, boolean add,
			ERDiagram diagram) {
		super(parentShell, diagram);

		this.targetWord = targetWord;
	}

	@Override
	protected void setWordData() {
		this.setData(this.targetWord.getPhysicalName(), this.targetWord
				.getLogicalName(), this.targetWord.getType(), this.targetWord
				.getTypeData(), this.targetWord.getDescription());
	}

	@Override
	protected String getErrorMessage() {
		String text = logicalNameText.getText().trim();
		if (text.equals("")) {
			return "error.column.logical.name.empty";
		}

		return super.getErrorMessage();
	}

	@Override
	protected void perfomeOK() {
		String text = lengthText.getText();
		Integer length = null;
		if (!text.equals("")) {
			int len = Integer.parseInt(text);
			length = Integer.valueOf(len);
		}

		text = decimalText.getText();

		Integer decimal = null;
		if (!text.equals("")) {
			int len = Integer.parseInt(text);
			decimal = Integer.valueOf(len);
		}

		boolean array = false;
		Integer arrayDimension = null;

		if (this.arrayDimensionText != null) {
			text = arrayDimensionText.getText();

			if (StringUtils.isNotEmpty(text)) {
				int len = Integer.parseInt(text);
				arrayDimension = Integer.valueOf(len);
			}

			array = this.arrayCheck.getSelection();
		}

		boolean unsigned = false;

		if (this.unsignedCheck != null) {
			unsigned = this.unsignedCheck.getSelection();
		}

		String database = this.diagram.getDatabase();

		SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

		String args = null;
		if (this.argsText != null) {
			args = this.argsText.getText();
		}

		String unit = null;
		if (this.unitCombo != null) {
			unit = this.unitCombo.getText();
		}
		
		TypeData typeData = new TypeData(length, decimal, array,
				arrayDimension, unsigned, args, unit);

		this.returnWord = new RealWord(physicalNameText.getText(), logicalNameText
				.getText(), selectedType, typeData, descriptionText.getText(),
				database);
	}

	public Word getWord() {
		return this.returnWord;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.word";
	}
}
