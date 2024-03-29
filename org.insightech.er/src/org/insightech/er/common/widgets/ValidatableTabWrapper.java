package org.insightech.er.common.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;

public abstract class ValidatableTabWrapper<E extends AbstractDialog> extends Composite {

	protected final TabItem tabItem;

	protected final E dialog;

	public ValidatableTabWrapper(E dialog, TabFolder parent,
			int style, String title) {
		super(parent, style);

		this.dialog = dialog;

		this.tabItem = new TabItem(parent, style);
		this.tabItem.setText(ResourceString.getResourceString(title));

		this.tabItem.setControl(this);
	}

	abstract public void validatePage() throws InputException;

	protected final void init() {
		this.initComposite();
		this.addListener();
		this.setData();
	}

	public abstract void reset();

	protected abstract void initComposite();

	protected abstract void addListener();

	protected abstract void setData();

	abstract public void perfomeOK();

	abstract public void setInitFocus();
}
