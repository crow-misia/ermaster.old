package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public final class ConstraintTabWrapper extends ValidatableTabWrapper<TableDialog> {

	private ERTable copyData;

	private Text constraintText;

	private Text primaryKeyNameText;

	private Text optionText;

	public ConstraintTabWrapper(TableDialog tableDialog, TabFolder parent,
			int style, ERTable copyData) {
		super(tableDialog, parent, style, "label.constraint.and.option");

		this.copyData = copyData;

		this.init();
	}

	@Override
	public void validatePage() throws InputException {
		String text = constraintText.getText().trim();
		this.copyData.setConstraint(text);

		text = primaryKeyNameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			throw new InputException("error.primary.key.name.not.alphabet");
		}
		this.copyData.setPrimaryKeyName(text);

		text = optionText.getText().trim();
		this.copyData.setOption(text);
	}

	@Override
	public void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);

		CompositeFactory.createLabel(this, "label.table.constraint", 1);

		this.constraintText = CompositeFactory.createTextArea(this.dialog,
				this, null, -1, 100, 1, false);

		this.constraintText
				.setText(Format.null2blank(copyData.getConstraint()));

		CompositeFactory.filler(this, 1);

		this.primaryKeyNameText = CompositeFactory.createText(this.dialog,
				this, "label.primary.key.name", 1, false);
		this.primaryKeyNameText.setText(Format.null2blank(copyData
				.getPrimaryKeyName()));

		CompositeFactory.filler(this, 1);

		CompositeFactory.createLabel(this, "label.option", 1);

		this.optionText = CompositeFactory.createTextArea(this.dialog, this,
				null, -1, 100, 1, false);

		this.optionText.setText(Format.null2blank(copyData.getOption()));
	}

	@Override
	public void setInitFocus() {
		this.constraintText.setFocus();
	}

	@Override
	public void perfomeOK() {
	}

	@Override
	public void reset() {
	}

	@Override
	protected void addListener() {
	}

	@Override
	protected void setData() {
	}
}
