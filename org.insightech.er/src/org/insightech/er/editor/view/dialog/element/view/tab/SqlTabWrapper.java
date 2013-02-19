package org.insightech.er.editor.view.dialog.element.view.tab;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.util.Format;

public final class SqlTabWrapper extends ValidatableTabWrapper<ViewDialog> {

	private View copyData;

	private Text sqlText;

	public SqlTabWrapper(ViewDialog viewDialog, TabFolder parent, int style,
			View copyData) {
		super(viewDialog, parent, style, "label.sql");

		this.copyData = copyData;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);

		this.sqlText = CompositeFactory.createTextArea(this.dialog, this,
				"label.sql", 400, 400, 1, true);

		this.sqlText.setText(Format.null2blank(copyData.getSql()));
	}

	@Override
	public void validatePage() throws InputException {
		final String text = sqlText.getText();

		if (StringUtils.isBlank(text)) {
			throw new InputException("error.view.sql.empty");
		}

		this.copyData.setSql(text.trim());
	}

	@Override
	public void setInitFocus() {
		this.sqlText.setFocus();
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
