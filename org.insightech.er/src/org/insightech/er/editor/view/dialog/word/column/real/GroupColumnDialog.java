package org.insightech.er.editor.view.dialog.word.column.real;

import org.eclipse.swt.widgets.Shell;
import org.insightech.er.editor.model.ERDiagram;

public class GroupColumnDialog extends AbstractRealColumnDialog {

	public GroupColumnDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, diagram);
	}

	@SuppressWarnings("static-method")
	protected int getStyle(int style) {
		return style;
	}

	@Override
	protected void setWordData() {
		super.setWordData();
		
		super.setEnabledBySqlType();
	}
	
	@Override
	protected String getTitle() {
		return "dialog.title.group.column";
	}

}
