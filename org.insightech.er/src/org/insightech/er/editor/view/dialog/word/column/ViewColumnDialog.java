package org.insightech.er.editor.view.dialog.word.column;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeColumnCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;

public class ViewColumnDialog extends AbstractColumnDialog {

	public ViewColumnDialog(Shell parentShell, View view) {
		super(parentShell, view.getDiagram());
	}

	public static Command openDialog(final Shell parentShell, final View view, final NormalColumn column) {
		final ViewColumnDialog dialog = new ViewColumnDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), view);
		final CopyColumn targetColumn = CopyColumn.getInstance(column);
		
		final NormalColumn retval = ERTableComposite.addOrEditColumn(dialog, null, targetColumn);
		if (retval != null) {
			return new ChangeColumnCommand(view, column, retval);
		}

		return null;
	}

	protected int getStyle(int style) {
		if (this.foreignKey) {
			style |= SWT.READ_ONLY;
		}

		return style;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.column";
	}

	@Override
	protected void initializeComposite(Composite parent) {
		super.initializeComposite(parent);

		if (this.foreignKey) {
			this.wordCombo.setEnabled(false);
			this.typeCombo.setEnabled(false);
			this.lengthText.setEnabled(false);
			this.decimalText.setEnabled(false);
		}
	}

	@Override
	protected void perfomeOK() throws InputException {
		super.perfomeOK();

		this.returnColumn = new NormalColumn(this.returnWord, false, false,
				false, false, null, null, null, null, null);
	}

}
