package org.insightech.er.editor.view.dialog.element.view;

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
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.element.view.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.tab.AttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.tab.DescriptionTabWrapper;
import org.insightech.er.editor.view.dialog.element.view.tab.SqlTabWrapper;

public class ViewDialog extends AbstractDialog {

	private View copyData;

	private TabFolder tabFolder;

	private EditPartViewer viewer;

	private List<ValidatableTabWrapper> tabWrapperList;

	private ViewDialog(Shell parentShell, EditPartViewer viewer, View copyData,
			GroupSet columnGroups) {
		super(parentShell);

		this.viewer = viewer;
		this.copyData = copyData;

		this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
	}

	public static Command openDialog(
			final Shell parentShell,
			final EditPartViewer viewer, final ERDiagram diagram,
			final View view, final GroupSet columnGroups) {
		final View copyView = view.copyData();

		final ViewDialog dialog = new ViewDialog(
				parentShell,
				viewer,
				copyView, columnGroups);

		if (dialog.open() == IDialogConstants.OK_ID) {
			return createChangeViewPropertyCommand(diagram,
					view, copyView);
		}
		return null;
	}

	private static Command createChangeViewPropertyCommand(
			final ERDiagram diagram, final View view, final View copyView) {
		final CompoundCommand command = new CompoundCommand();

		final ChangeTableViewPropertyCommand changeViewPropertyCommand = new ChangeTableViewPropertyCommand(
				view, copyView);
		command.add(changeViewPropertyCommand);

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

		this.tabWrapperList.add(new SqlTabWrapper(this, tabFolder, SWT.NONE,
				this.copyData));
		this.tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));
		this.tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		this.tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
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
		return "dialog.title.view";
	}

	@Override
	protected void perfomeOK() throws InputException {
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
