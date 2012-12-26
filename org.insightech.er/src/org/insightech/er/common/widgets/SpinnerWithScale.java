package org.insightech.er.common.widgets;

import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public final class SpinnerWithScale {

	private final Spinner spinner;

	private final Scale scale;

	private final int diff;

	public SpinnerWithScale(Spinner spinner, Scale scale, int diff) {
		this.spinner = spinner;
		this.scale = scale;
		this.diff = diff;
	}

	public void setSelection(int value) {
		this.spinner.setSelection(value);
		this.scale.setSelection(this.spinner.getSelection() - diff);
	}

	public int getSelection() {
		return this.spinner.getSelection();
	}
	
}
