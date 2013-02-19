package org.insightech.er.common.widgets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;

public final class FileText {

	private final Text text;

	public FileText(final Composite parent, final int style, final String... filterExtensions) {
		this.text = new Text(parent, style);

		final Button openBrowseButton = new Button(parent, SWT.NONE);
		openBrowseButton.setText(JFaceResources.getString("openBrowse"));

		openBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String saveFilePath = Activator.showSaveDialog(getFilePath(),
						filterExtensions);
				setTextInner(saveFilePath);
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		this.text.setLayoutData(layoutData);
	}

	public void setText(String text) {
		this.text.setText(text);
		this.text.setSelection(text.length());
	}

	protected void setTextInner(final String text) {
		this.text.setText(text);
	}

	public boolean isBlank() {
		return StringUtils.isBlank(this.text.getText());
	}

	public String getFilePath() {
		return this.text.getText().trim();
	}

	public void addModifyListener(ModifyListener listener) {
		this.text.addModifyListener(listener);
	}

}
