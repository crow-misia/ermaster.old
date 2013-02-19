package org.insightech.er.editor.view.dialog.word.column.real;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeColumnCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.element.table.sub.AutoIncrementSettingDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ColumnDialog extends AbstractRealColumnDialog {

	private final ERTable erTable;

	private final DBManager dbManager;

	private Sequence autoIncrementSetting;

	protected Button primaryKeyCheck;

	protected Text uniqueKeyNameText;

	protected Combo characterSetCombo;

	protected Combo collationCombo;

	protected Button autoIncrementCheck;

	protected Button autoIncrementSettingButton;

	public ColumnDialog(Shell parentShell, ERTable erTable) {
		super(parentShell, erTable.getDiagram());

		this.erTable = erTable;
		this.dbManager = DBManagerFactory.getDBManager(erTable.getDiagram());
	}

	public static Command openDialog(final Shell parentShell, final ERTable erTable,
			final NormalColumn column) {
		final ColumnDialog dialog = new ColumnDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), erTable);
		final CopyColumn targetColumn = CopyColumn.getInstance(column);
		
		final NormalColumn retval = ERTableComposite.openDialog(dialog, erTable, targetColumn);
		if (retval != null) {
			return new ChangeColumnCommand(erTable, column, retval);
		}

		return null;
	}

	@Override
	protected void initializeDetailTab(Composite composite) {
		this.uniqueKeyNameText = CompositeFactory.createText(this, composite,
				"label.unique.key.name", false);

		super.initializeDetailTab(composite);

		if (dbManager.isSupported(SupportFunctions.COLUMN_CHARSET)) {
			this.characterSetCombo = CompositeFactory.createCombo(this,
					composite, "label.character.set", 1);
			this.collationCombo = CompositeFactory.createCombo(this, composite,
					"label.collation", 1);
		}

		if (dbManager.isSupported(SupportFunctions.AUTO_INCREMENT_SETTING)) {
			CompositeFactory.filler(composite, 2);

			this.autoIncrementSettingButton = new Button(composite, SWT.NONE);
			this.autoIncrementSettingButton.setText(ResourceString
					.getResourceString("label.auto.increment.setting"));
			this.autoIncrementSettingButton.setEnabled(false);

			GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			this.autoIncrementSettingButton.setLayoutData(gridData);
		}
	}

	@Override
	protected int getCheckBoxCompositeNumColumns() {
		if (dbManager.isSupported(SupportFunctions.AUTO_INCREMENT)) {
			return 4;
		}

		return 3;
	}

	@Override
	protected void initializeCheckBoxComposite(Composite composite) {
		this.primaryKeyCheck = CompositeFactory.createCheckbox(this, composite,
				"label.primary.key");

		super.initializeCheckBoxComposite(composite);

		if (dbManager.isSupported(SupportFunctions.AUTO_INCREMENT)) {
			this.autoIncrementCheck = CompositeFactory.createCheckbox(this,
					composite, "label.auto.increment");
		}

		if (this.isRefered) {
			this.uniqueKeyCheck.setEnabled(false);
		}

		this.enableAutoIncrement(false);
	}

	protected int getStyle(int style) {
		if (this.foreignKey) {
			style |= SWT.READ_ONLY;
		}

		return style;
	}

	@Override
	protected void initializeComposite(Composite composite) {
		super.initializeComposite(composite);

		if (this.foreignKey) {
			this.wordCombo.setEnabled(false);
			this.typeCombo.setEnabled(false);
			this.defaultText.setEnabled(false);
			this.lengthText.setEnabled(false);
			this.decimalText.setEnabled(false);
		}
	}

	@Override
	protected void setWordData() {
		super.setWordData();

		this.primaryKeyCheck.setSelection(this.targetColumn.isPrimaryKey());

		if (this.autoIncrementCheck != null) {
			this.autoIncrementCheck.setSelection(this.targetColumn
					.isAutoIncrement());
		}

		if (this.primaryKeyCheck.getSelection()) {
			this.notNullCheck.setSelection(true);
			this.notNullCheck.setEnabled(false);
		} else {
			this.notNullCheck.setEnabled(true);
		}

		final NormalColumn autoIncrementColumn = this.erTable
				.getAutoIncrementColumn();

		if (this.primaryKeyCheck.getSelection()) {
			if (autoIncrementColumn == null
					|| autoIncrementColumn == targetColumn) {
				this.enableAutoIncrement(true);

			} else {
				this.enableAutoIncrement(false);
			}

		} else {
			this.enableAutoIncrement(false);
		}

		this.defaultText.setText(Format.null2blank(this.targetColumn
				.getDefaultValue()));

		this.setEnabledBySqlType();

		this.uniqueKeyNameText.setText(Format.null2blank(this.targetColumn
				.getUniqueKeyName()));

		if (this.characterSetCombo != null) {
			this.characterSetCombo.add("");

			for (String characterSet : dbManager.getCharacterSetList()) {
				this.characterSetCombo.add(characterSet);
			}

			this.characterSetCombo.setText(Format.null2blank(this.targetColumn
					.getCharacterSet()));

			this.collationCombo.add("");

			for (String collation : dbManager.getCollationList(this.targetColumn.getCharacterSet())) {
				this.collationCombo.add(collation);
			}

			this.collationCombo.setText(Format.null2blank(this.targetColumn
					.getCollation()));
		}
	}

	@Override
	protected String getTitle() {
		return "dialog.title.column";
	}

	private void enableAutoIncrement(boolean enabled) {
		if (this.autoIncrementCheck != null) {
			if (!enabled) {
				this.autoIncrementCheck.setSelection(false);
			}

			this.autoIncrementCheck.setEnabled(enabled);

			if (autoIncrementSettingButton != null) {
				this.autoIncrementSettingButton.setEnabled(enabled
						&& this.autoIncrementCheck.getSelection());
			}
		}
	}

	@Override
	protected void setEnabledBySqlType() {
		super.setEnabledBySqlType();

		SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo
				.getText());

		if (selectedType != null) {
			dbManager.setEnabledBySqlType(selectedType, this);
		}
	}

	@Override
	protected void perfomeOK() throws InputException {
		super.perfomeOK();

		this.returnColumn.setPrimaryKey(primaryKeyCheck.getSelection());

		if (this.autoIncrementCheck != null) {
			this.returnColumn.setAutoIncrement(this.autoIncrementCheck
					.getSelection());
		}

		this.returnColumn.setAutoIncrementSetting(this.autoIncrementSetting);

		this.returnColumn.setUniqueKeyName(this.uniqueKeyNameText.getText());

		if (this.characterSetCombo != null) {
			this.returnColumn.setCharacterSet(this.characterSetCombo.getText());
			this.returnColumn.setCollation(this.collationCombo.getText());
		}
	}

	@Override
	protected String getErrorMessage() {
		if (this.autoIncrementCheck != null
				&& this.autoIncrementCheck.getSelection()) {
			SqlType selectedType = SqlType.valueOf(this.diagram.getDatabase(),
					this.typeCombo.getText());
			if (selectedType == null || !selectedType.isNumber()) {
				return "error.no.auto.increment.column";
			}
		}

		String text = uniqueKeyNameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			return "error.unique.key.name.not.alphabet";
		}

		return super.getErrorMessage();
	}

	@Override
	protected void addListener() {
		super.addListener();

		if (this.autoIncrementSettingButton != null) {
			this.autoIncrementSettingButton
					.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							AutoIncrementSettingDialog dialog = new AutoIncrementSettingDialog(
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getShell(), autoIncrementSetting,
									diagram.getDatabase());

							if (dialog.open() == IDialogConstants.OK_ID) {
								// 新たな Sequence が作成される
								autoIncrementSetting = dialog.getResult();
							}
						}
					});
		}

		// Primary Key は、AUTO_INCREMENT のチェックボックスの制御リスナーの呼出し後に
		// validate のリスナーを呼びたいのでここでリスナーの追加を
		// ListenerAppender.addCheckBoxListener(this.primaryKeyCheck, this);

		final NormalColumn autoIncrementColumn = this.erTable
				.getAutoIncrementColumn();

		this.primaryKeyCheck.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (primaryKeyCheck.getSelection()) {
					notNullCheck.setSelection(true);
					notNullCheck.setEnabled(false);

					if (autoIncrementColumn == null
							|| autoIncrementColumn == targetColumn) {
						enableAutoIncrement(true);

					} else {
						enableAutoIncrement(false);
					}

				} else {
					notNullCheck.setEnabled(true);
					enableAutoIncrement(false);
				}
			}
		});

		this.uniqueKeyCheck.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				uniqueKeyNameText.setEnabled(uniqueKeyCheck.getSelection());
			}
		});

		if (autoIncrementSettingButton != null
				&& this.autoIncrementCheck != null) {
			this.autoIncrementCheck
					.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {

							autoIncrementSettingButton
									.setEnabled(autoIncrementCheck
											.getSelection());
						}

					});
		}

		if (this.characterSetCombo != null) {

			this.characterSetCombo.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					String selectedCollation = collationCombo.getText();

					collationCombo.removeAll();
					collationCombo.add("");

					for (String collation : dbManager.getCollationList(characterSetCombo.getText())) {
						collationCombo.add(collation);
					}

					int index = collationCombo.indexOf(selectedCollation);

					collationCombo.select(index);
				}
			});
		}
	}

	@Override
	public void setTargetColumn(CopyColumn targetColumn, boolean foreignKey,
			boolean isRefered) {
		super.setTargetColumn(targetColumn, foreignKey, isRefered);

		if (targetColumn != null) {
			this.autoIncrementSetting = targetColumn.getAutoIncrementSetting();

		} else {
			this.autoIncrementSetting = new Sequence();
		}
	}

	public void setAutoIncrementSettingButtonEnabled(final boolean enabled) {
		if (this.autoIncrementSettingButton != null) {
			this.autoIncrementSettingButton.setEnabled(enabled);
		}
	}
}
