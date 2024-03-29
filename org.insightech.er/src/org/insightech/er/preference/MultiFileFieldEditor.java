package org.insightech.er.preference;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class MultiFileFieldEditor extends FileFieldEditor {

	private String[] extensions = null;

	private boolean multiple = false;

	public MultiFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, false, parent);
	}

	@Override
	protected String changePressed() {
		StringTokenizer tokenizer = new StringTokenizer(getTextControl()
				.getText(), ";");

		File f;
		if (tokenizer.countTokens() == 0) {
			f = null;

		} else {
			f = new File(tokenizer.nextToken());
			if (!f.exists()) {
				f = null;
			}
		}

		File[] d = getFile(f);
		if (d.length == 0) {
			return null;
		}

		StringBuilder ret = new StringBuilder();
		for (final File i : d) {
			ret.append(i.getAbsolutePath());
			ret.append(";");
		}

		return ret.toString();
	}

	private File[] getFile(File startingDirectory) {

		int style = SWT.OPEN;
		if (multiple) {
			style |= SWT.MULTI;
		}

		FileDialog dialog = new FileDialog(getShell(), style);
		if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
		if (extensions != null) {
			dialog.setFilterExtensions(extensions);
		}
		dialog.open();
		String[] fileNames = dialog.getFileNames();

		final int num = fileNames.length;
		File[] files = new File[num];

		for (int i = 0; i < num; i++) {
			files[i] = new File(dialog.getFilterPath(), fileNames[i]);
		}

		return files;
	}

	@Override
	public void setFileExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	/**
	 * multiple を設定します.
	 * 
	 * @param multiple
	 *            multiple
	 */
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

}
