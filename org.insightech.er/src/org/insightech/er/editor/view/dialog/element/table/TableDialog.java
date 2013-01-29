package org.insightech.er.editor.view.dialog.element.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.AttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ComplexUniqueKeyTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ConstraintTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.DescriptionTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.IndexTabWrapper;
import org.insightech.er.util.Check;

public class TableDialog extends AbstractDialog {

	private final ERTable copyData;

	private TabFolder tabFolder;

	private final EditPartViewer viewer;

	private final List<ValidatableTabWrapper> tabWrapperList;

	private TableDialog(Shell parentShell, EditPartViewer viewer,
			ERTable copyData, GroupSet columnGroups) {
		super(parentShell);

		this.viewer = viewer;
		this.copyData = copyData;

		this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
	}

	public static Command openDialog(final Shell parentShell,
			final EditPartViewer viewer, final ERDiagram diagram, 
			final ERTable table, final GroupSet columnGroups) {
		final ERTable copyTable = table.copyData();

		final TableDialog dialog = new TableDialog(parentShell, viewer,
				copyTable, columnGroups);

		if (dialog.open() == IDialogConstants.OK_ID) {
			return createChangeTablePropertyCommand(diagram, table, copyTable);
		}
		return null;
	}

	private static Command createChangeTablePropertyCommand(
			final ERDiagram diagram, final ERTable table, final ERTable copyTable) {
		final CompoundCommand command = new CompoundCommand();

		final ChangeTableViewPropertyCommand changeTablePropertyCommand = new ChangeTableViewPropertyCommand(
				table, copyTable);
		command.add(changeTablePropertyCommand);

		String tableName = copyTable.getPhysicalName();

		if (!Check.isEmpty(tableName)) {
		    diagram.getDBManager().createAutoIncrement(diagram, table, copyTable, command, tableName);
		}

		return command.unwrap();
	}

	@Override
	protected void initialize(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		this.tabFolder = new TabFolder(composite, SWT.NONE);
		this.tabFolder.setLayoutData(gridData);

		final AttributeTabWrapper attributeTabWrapper = new AttributeTabWrapper(
				this, tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(attributeTabWrapper);

		this.tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		final ComplexUniqueKeyTabWrapper complexUniqueKeyTabWrapper = new ComplexUniqueKeyTabWrapper(
				this, tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(complexUniqueKeyTabWrapper);

		this.tabWrapperList.add(new ConstraintTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		final IndexTabWrapper indexTabWrapper = new IndexTabWrapper(this,
				tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(indexTabWrapper);

		this.tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		this.tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				complexUniqueKeyTabWrapper.restruct();
				indexTabWrapper.restruct();

				int index = tabFolder.getSelectionIndex();

				ValidatableTabWrapper selectedTabWrapper = tabWrapperList
						.get(index);
				selectedTabWrapper.setInitFocus();
			}

		});

		attributeTabWrapper.setInitFocus();
	}

	@Override
	protected String getErrorMessage() {
		try {
			for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
				tabWrapper.validatePage();
			}

		} catch (InputException e) {
			return e.getMessage();
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.table";
	}

	@Override
	protected void perfomeOK() {
	}

	@Override
	protected void setData() {
	}

	public EditPartViewer getViewer() {
		return viewer;
	}

	public ERDiagram getDiagram() {
		return (ERDiagram) this.viewer.getContents().getModel();
	}

}
