package org.insightech.er.editor.view.dialog.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.dialog.word.column.AbstractColumnDialog;
import org.insightech.er.util.Format;

public class ERTableComposite extends Composite {

	private static final int DEFAULT_HEIGHT = 200;

	private static final int KEY_WIDTH = 45;

	public static final int NAME_WIDTH = 150;

	private static final int TYPE_WIDTH = 100;

	private static final int NOT_NULL_WIDTH = 80;

	public static final int UNIQUE_KEY_WIDTH = 70;

	private Table table;

	private Button columnAddButton;

	private Button columnEditButton;

	private Button columnDeleteButton;

	private Button upButton;

	private Button downButton;

	private ERDiagram diagram;

	private ERTable ertable;

	private List<Column> columnList;

	private AbstractColumnDialog columnDialog;

	private AbstractDialog parentDialog;

	private Map<Column, TableEditor[]> columnNotNullCheckMap = new HashMap<Column, TableEditor[]>();

	private boolean buttonDisplay;
	
	private boolean checkboxEnabled;

	private int height;

	private ERTableCompositeHolder holder;

	public ERTableComposite(ERTableCompositeHolder holder, Composite parent,
			ERDiagram diagram, ERTable erTable, List<Column> columnList,
			AbstractColumnDialog columnDialog, AbstractDialog parentDialog,
			int horizontalSpan, boolean buttonDisplay, boolean checkboxEnabled) {
		this(holder, parent, diagram, erTable, columnList, columnDialog,
				parentDialog, horizontalSpan, buttonDisplay, checkboxEnabled, DEFAULT_HEIGHT);
	}

	public ERTableComposite(ERTableCompositeHolder holder, Composite parent,
			ERDiagram diagram, ERTable erTable, List<Column> columnList,
			AbstractColumnDialog columnDialog, AbstractDialog parentDialog,
			int horizontalSpan, boolean buttonDisplay, boolean checkboxEnabled, int height) {
		super(parent, SWT.NONE);

		this.holder = holder;
		this.height = height;
		this.buttonDisplay = buttonDisplay;
		this.checkboxEnabled = checkboxEnabled;

		this.diagram = diagram;
		this.ertable = erTable;
		this.columnList = columnList;

		this.columnDialog = columnDialog;
		this.parentDialog = parentDialog;

		GridData gridData = new GridData();
		gridData.horizontalSpan = horizontalSpan;
		this.setLayoutData(gridData);

		this.createComposite();
		this.initComposite();
	}

	private void createComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		this.setLayout(gridLayout);

		this.createTable();

		if (this.buttonDisplay) {
			this.createButton();
			this.setButtonEnabled(false);
		}
	}

	private void createTable() {
		this.table = CompositeFactory.createTable(this, this.height, 3);

		CompositeFactory.createTableColumn(this.table, "PK", KEY_WIDTH,
				SWT.CENTER);
		CompositeFactory.createTableColumn(this.table, "FK", KEY_WIDTH,
				SWT.CENTER);
		CompositeFactory.createTableColumn(this.table, "label.physical.name",
				NAME_WIDTH, SWT.NONE);
		CompositeFactory.createTableColumn(this.table, "label.logical.name",
				NAME_WIDTH, SWT.NONE);
		CompositeFactory.createTableColumn(this.table, "label.column.type",
				TYPE_WIDTH, SWT.NONE);
		CompositeFactory.createTableColumn(this.table, "label.not.null",
				NOT_NULL_WIDTH, SWT.NONE);
		CompositeFactory.createTableColumn(this.table, "label.unique.key",
				UNIQUE_KEY_WIDTH, SWT.NONE);

		this.table.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				selectTable(index);

				Column selectedColumn = columnList.get(index);
				if (selectedColumn instanceof ColumnGroup) {
					holder.selectGroup((ColumnGroup) selectedColumn);
				}
			}
		});

		if (this.buttonDisplay) {
			this.table.addMouseListener(new MouseAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					Column targetColumn = getTargetColumn();

					if (targetColumn == null
							|| !(targetColumn instanceof CopyColumn)) {
						return;
					}

					addOrEditColumn((CopyColumn) targetColumn, false);
				}
			});
		}
	}

	/**
	 * This method initializes composite2
	 * 
	 */
	private void createButton() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayoutData(gridData);
		buttonComposite.setLayout(gridLayout);

		this.columnAddButton = CompositeFactory.createButton(buttonComposite,
				"label.button.add");

		this.columnAddButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				addOrEditColumn(null, true);
			}
		});

		this.columnEditButton = CompositeFactory.createButton(buttonComposite,
				"label.button.edit");

		this.columnEditButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Column targetColumn = getTargetColumn();

				if (targetColumn == null
						|| !(targetColumn instanceof CopyColumn)) {
					return;
				}

				addOrEditColumn((CopyColumn) targetColumn, false);
			}

		});

		this.columnDeleteButton = CompositeFactory.createButton(
				buttonComposite, "label.button.delete");

		this.columnDeleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();

				removeColumn();

				if (index >= table.getItemCount()) {
					index = table.getItemCount() - 1;
				}

				selectTable(index);
			}

		});

		Label filler = new Label(buttonComposite, SWT.NONE);
		GridData fillerGridData = new GridData();
		fillerGridData.widthHint = 30;
		filler.setLayoutData(fillerGridData);

		this.upButton = CompositeFactory.createButton(buttonComposite,
				"label.up.arrow");

		this.upButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				upColumn();
			}

		});

		this.downButton = CompositeFactory.createButton(buttonComposite,
				"label.down.arrow");

		this.downButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				downColumn();
			}

		});

	}

	private void initComposite() {
		if (this.columnList != null) {
			for (Column column : this.columnList) {
				TableItem tableItem = new TableItem(this.table, SWT.NONE);
				this.column2TableItem(column, tableItem);
			}
		}
	}

	private void disposeCheckBox(Column column) {
		TableEditor[] oldEditors = this.columnNotNullCheckMap.get(column);

		if (oldEditors != null) {
			for (TableEditor oldEditor : oldEditors) {
				if (oldEditor.getEditor() != null) {
					oldEditor.getEditor().dispose();
					oldEditor.dispose();
				}
			}

			this.columnNotNullCheckMap.remove(column);
		}
	}

	private void column2TableItem(Column column, TableItem tableItem) {
		this.disposeCheckBox(column);

		if (column instanceof NormalColumn) {
			tableItem.setBackground(ColorConstants.white);

			NormalColumn normalColumn = (NormalColumn) column;

			if (normalColumn.isPrimaryKey()) {
				tableItem.setImage(0, Activator.getImage(ImageKey.PRIMARY_KEY));
			} else {
				tableItem.setImage(0, null);
			}

			if (normalColumn.isForeignKey()) {
				tableItem.setImage(1, Activator.getImage(ImageKey.FOREIGN_KEY));
			} else {
				tableItem.setImage(1, null);
			}

			tableItem.setText(2, Format.null2blank(normalColumn
					.getPhysicalName()));
			tableItem.setText(3, Format.null2blank(normalColumn
					.getLogicalName()));

			SqlType sqlType = normalColumn.getType();

			tableItem.setText(4, Format.formatType(sqlType, normalColumn
					.getTypeData(), this.diagram.getDatabase()));

			this.setTableEditor(normalColumn, tableItem);

		} else {
			tableItem.setBackground(ColorConstants.white);
			tableItem.setImage(0, Activator.getImage(ImageKey.GROUP));
			tableItem.setImage(1, null);
			tableItem.setText(2, column.getName());
			tableItem.setText(3, "");
			tableItem.setText(4, "");
		}
	}

	private void setTableEditor(final NormalColumn normalColumn,
			TableItem tableItem) {

		final Button notNullCheckButton = new Button(this.table, SWT.CHECK);
		notNullCheckButton.pack();

		final Button uniqueCheckButton = new Button(this.table, SWT.CHECK);
		uniqueCheckButton.pack();

		TableEditor[] editors = new TableEditor[2];

		editors[0] = new TableEditor(this.table);

		editors[0].minimumWidth = notNullCheckButton.getSize().x;
		editors[0].horizontalAlignment = SWT.CENTER;
		editors[0].setEditor(notNullCheckButton, tableItem, 5);

		editors[1] = new TableEditor(this.table);

		editors[1].minimumWidth = uniqueCheckButton.getSize().x;
		editors[1].horizontalAlignment = SWT.CENTER;
		editors[1].setEditor(uniqueCheckButton, tableItem, 6);

		if (normalColumn.isNotNull()) {
			notNullCheckButton.setSelection(true);
		} else {
			notNullCheckButton.setSelection(false);
		}
		if (normalColumn.isUniqueKey()) {
			uniqueCheckButton.setSelection(true);
		} else {
			uniqueCheckButton.setSelection(false);
		}

		if (normalColumn.isPrimaryKey()) {
			notNullCheckButton.setEnabled(false);
		}

		if (this.ertable != null) {
			if (normalColumn.isRefered()) {
				uniqueCheckButton.setEnabled(false);
			}
		}

		this.columnNotNullCheckMap.put(normalColumn, editors);

		if (this.checkboxEnabled) {
			notNullCheckButton.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					normalColumn.setNotNull(notNullCheckButton.getSelection());
					super.widgetSelected(e);
				}
			});

			uniqueCheckButton.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					normalColumn.setUniqueKey(uniqueCheckButton.getSelection());
					super.widgetSelected(e);
				}
			});

		} else {
			notNullCheckButton.setEnabled(false);
			uniqueCheckButton.setEnabled(false);
		}
	}

	/**
	 * <pre>
	 * カラムの追加をした場合
	 * CopyColumn が追加されます
	 * この際、word には CopyWord が設定されています
	 * （CopyWord の original は ワードを選択した場合は、そのインスタンス
	 *  選択しなかった場合は、新しいインスタンス）
	 * </pre>
	 * 
	 * @param column
	 * @param add
	 */
	private void addTableData(NormalColumn column, boolean add) {
		int index = this.table.getSelectionIndex();

		TableItem tableItem = null;
		CopyColumn copyColumn = null;

		if (add) {
			tableItem = new TableItem(table, SWT.NONE);

			copyColumn = new CopyColumn(column);
			this.columnList.add(copyColumn);

		} else {
			tableItem = this.table.getItem(index);

			copyColumn = (CopyColumn) this.columnList.get(index);
			CopyColumn.copyData(column, copyColumn);
		}

		this.column2TableItem(copyColumn, tableItem);

		this.parentDialog.validate();
	}

	public void addTableData(ColumnGroup column) {
		TableItem tableItem = null;
		tableItem = new TableItem(table, SWT.NONE);

		this.columnList.add(column);
		this.column2TableItem(column, tableItem);

		this.parentDialog.validate();
	}

	private void removeColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1) {
			Column column = this.columnList.get(index);

			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (normalColumn.isForeignKey()) {
					this
							.setMessage(ResourceString
									.getResourceString("error.foreign.key.not.deleteable"));

				} else {
					if (this.ertable != null && normalColumn.isRefered()) {
						this
								.setMessage(ResourceString
										.getResourceString("error.reference.key.not.deleteable"));

					} else {
						removeColumn(index);
					}
				}

			} else {
				this.removeColumn(index);
			}
		}

		this.parentDialog.validate();
	}

	public void removeColumn(int index) {
		Column column = this.columnList.get(index);

		this.table.remove(index);

		this.columnList.remove(index);

		this.disposeCheckBox(column);

		for (int i = index; i < this.table.getItemCount(); i++) {
			TableItem tableItem = this.table.getItem(i);
			column = this.columnList.get(i);

			this.disposeCheckBox(column);

			if (column instanceof NormalColumn) {
				this.setTableEditor((NormalColumn) column, tableItem);
			}
		}
	}

	private CopyColumn getTargetColumn() {
		CopyColumn column = null;

		int index = this.table.getSelectionIndex();

		if (index != -1) {
			column = (CopyColumn) this.columnList.get(index);
		}

		return column;
	}

	private void setMessage(String message) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(ResourceString
				.getResourceString("dialog.title.error"));
		messageBox.setMessage(message);
		messageBox.open();
	}

	private void upColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1 && index != 0) {
			this.changeColumn(index - 1, index);
			this.table.setSelection(index - 1);
		}
	}

	private void downColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1 && index != table.getItemCount() - 1) {
			this.changeColumn(index, index + 1);
			table.setSelection(index + 1);
		}
	}

	private void changeColumn(int index1, int index2) {
		Column column1 = this.columnList.remove(index1);
		Column column2 = null;

		if (index1 < index2) {
			column2 = this.columnList.remove(index2 - 1);
			this.columnList.add(index1, column2);
			this.columnList.add(index2, column1);

		} else if (index1 > index2) {
			column2 = this.columnList.remove(index2);
			this.columnList.add(index1 - 1, column2);
			this.columnList.add(index2, column1);
		}

		TableItem[] tableItems = this.table.getItems();

		this.column2TableItem(column1, tableItems[index2]);
		this.column2TableItem(column2, tableItems[index1]);
	}

	private void addOrEditColumn(CopyColumn targetColumn, boolean add) {
		boolean foreignKey = false;
		boolean isRefered = false;

		if (targetColumn != null) {
			foreignKey = targetColumn.isForeignKey();
			if (this.ertable != null) {
				isRefered = targetColumn.isRefered();
			}
		}
		this.columnDialog.setTargetColumn(targetColumn, foreignKey, isRefered);

		if (this.columnDialog.open() == IDialogConstants.OK_ID) {
			NormalColumn column = this.columnDialog.getColumn();
			addTableData(column, add);
		}
	}

	public void setColumnList(List<Column> columnList) {
		this.table.removeAll();

		if (this.columnList != null) {
			for (Column column : this.columnList) {
				this.disposeCheckBox(column);
			}
		}

		this.columnList = columnList;

		initComposite();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (this.buttonDisplay) {
			this.columnAddButton.setEnabled(enabled);
			this.columnEditButton.setEnabled(false);
			this.columnDeleteButton.setEnabled(false);
			this.upButton.setEnabled(false);
			this.downButton.setEnabled(false);
		}
	}

	private void setButtonEnabled(boolean enabled) {
		if (this.buttonDisplay) {
			this.columnEditButton.setEnabled(enabled);
			this.columnDeleteButton.setEnabled(enabled);
			this.upButton.setEnabled(enabled);
			this.downButton.setEnabled(enabled);
		}
	}

	private void selectTable(int index) {
		this.table.select(index);

		if (index >= 0) {
			this.setButtonEnabled(true);
		} else {
			this.setButtonEnabled(false);
		}
	}

}
