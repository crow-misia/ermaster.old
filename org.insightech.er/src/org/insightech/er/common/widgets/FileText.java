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

	private final String[] filterExtensions;

	public FileText(Composite parent, int style) {
		this(parent, style, new String[0]);
	}

	public FileText(Composite parent, int style, String filterExtension) {
		this(parent, style, new String[] { filterExtension });
	}

	public FileText(Composite parent, int style, String[] filterExtensions) {
		this.text = new Text(parent, style);

		this.filterExtensions = filterExtensions;

		final Button openBrowseButton = new Button(parent, SWT.NONE);
		openBrowseButton.setText(JFaceResources.getString("openBrowse"));

		openBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String saveFilePath = Activator.showSaveDialog(text.getText(),
						FileText.this.filterExtensions);
				text.setText(saveFilePath);
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
