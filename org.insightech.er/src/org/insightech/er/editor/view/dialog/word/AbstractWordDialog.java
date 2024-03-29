package org.insightech.er.editor.view.dialog.word;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public abstract class AbstractWordDialog extends AbstractDialog {

	protected Combo typeCombo;

	protected Text logicalNameText;

	protected Text physicalNameText;

	private String oldPhysicalName;

	protected Text lengthText;

	protected Text decimalText;

	protected Button arrayCheck;

	protected Text arrayDimensionText;

	protected Button unsignedCheck;

	protected boolean add;

	protected Text descriptionText;

	protected Text argsText;

	protected Combo unitCombo;

	protected ERDiagram diagram;

	public AbstractWordDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell);

		this.diagram = diagram;
		this.oldPhysicalName = "";
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	@Override
	protected void initialize(Composite composite) {
		Composite rootComposite = this.createRootComposite(composite);

		this.initializeComposite(rootComposite);

		this.physicalNameText.setFocus();

		this.validate();

	}

	protected Composite createRootComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = this.getCompositeNumColumns();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(gridLayout);

		return composite;
	}

	protected int getCompositeNumColumns() {
		DBManager manager = DBManagerFactory.getDBManager(this.diagram);

		int colnum = 6;
		if (manager.isSupported(SupportFunctions.ARRAY_TYPE)) {
			colnum += 4;
		}
		if (MySQLDBManager.ID.equals(this.diagram.getDatabase())) {
			colnum += 2;
		}
		if (manager.isSupported(SupportFunctions.COLUMN_UNIT)) {
			colnum++;
		}

		return colnum;
	}

	protected void initializeComposite(Composite composite) {
		DBManager manager = DBManagerFactory.getDBManager(this.diagram);

		int numColumns = this.getCompositeNumColumns();

		this.physicalNameText = CompositeFactory.createText(this, composite,
				"label.physical.name", numColumns - 1, false);

		this.logicalNameText = CompositeFactory.createText(this, composite,
				"label.logical.name", numColumns - 1, true);

		this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite,
				"label.column.type");

		this.lengthText = CompositeFactory.createNumText(this, composite,
				"label.column.length", 30);
		this.lengthText.setEnabled(false);

		if (manager.isSupported(SupportFunctions.COLUMN_UNIT)) {
			this.unitCombo = CompositeFactory.createReadOnlyCombo(this, composite, null);
		}

		this.decimalText = CompositeFactory.createNumText(this, composite,
				"label.column.decimal", 30);
		this.decimalText.setEnabled(false);

		if (manager.isSupported(SupportFunctions.ARRAY_TYPE)) {
			CompositeFactory.filler(composite, 1, 10);

			this.arrayCheck = CompositeFactory.createCheckbox(this, composite,
					"label.column.array");
			this.arrayCheck.setEnabled(true);
			this.arrayDimensionText = CompositeFactory.createNumText(this,
					composite, "label.column.array.dimension", 15);
			this.arrayDimensionText.setEnabled(false);

			this.arrayCheck.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					arrayDimensionText.setEnabled(arrayCheck.getSelection());

					super.widgetSelected(e);
				}
			});

		}

		if (MySQLDBManager.ID.equals(this.diagram.getDatabase())) {
			CompositeFactory.filler(composite, 1, 10);

			this.unsignedCheck = CompositeFactory.createCheckbox(this,
					composite, "label.column.unsigned");
			this.unsignedCheck.setEnabled(false);

			CompositeFactory.filler(composite, 1);
			this.argsText = CompositeFactory.createText(this, composite,
					"label.column.type.enum.set", getCompositeNumColumns() - 2,
					false);
			this.argsText.setEnabled(false);
		}

		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.column.description", -1, 100, numColumns - 1, true);
	}

	@Override
	final protected void setData() {
		this.initializeTypeCombo();
		this.initializeUnitCombo();

		if (!this.add) {
			this.setWordData();
		}
	}

	protected void setData(String physicalName, String logicalName,
			SqlType sqlType, TypeData typeData, String description) {

		this.physicalNameText.setText(Format.toString(physicalName));
		this.logicalNameText.setText(Format.toString(logicalName));
		this.oldPhysicalName = physicalNameText.getText();
		
		if (sqlType != null) {
			String database = this.diagram.getDatabase();

			if (sqlType.getAlias(database) != null) {
				this.typeCombo.setText(sqlType.getAlias(database));
			}

			if (!sqlType.isNeedLength(database)) {
				this.lengthText.setEnabled(false);
			}
			if (!sqlType.isNeedDecimal(database)) {
				this.decimalText.setEnabled(false);
			}

			if (this.unsignedCheck != null && !sqlType.isNumber()) {
				this.unsignedCheck.setEnabled(false);
			}

			if (this.argsText != null) {
				if (sqlType.doesNeedArgs()) {
					argsText.setEnabled(true);
				} else {
					argsText.setEnabled(false);
				}
			}

		} else {
			this.lengthText.setEnabled(false);
			this.decimalText.setEnabled(false);
			if (this.unsignedCheck != null) {
				this.unsignedCheck.setEnabled(false);
			}
			if (this.argsText != null) {
				this.argsText.setEnabled(false);
			}
		}

		this.lengthText.setText(Format.toString(typeData.getLength()));
		this.decimalText.setText(Format.toString(typeData.getDecimal()));

		if (this.arrayDimensionText != null) {
			this.arrayCheck.setSelection(typeData.isArray());
			this.arrayDimensionText.setText(Format.toString(typeData
					.getArrayDimension()));
			this.arrayDimensionText.setEnabled(this.arrayCheck.getSelection());
		}

		if (this.unsignedCheck != null) {
			this.unsignedCheck.setSelection(typeData.isUnsigned());
		}

		if (this.argsText != null) {
			this.argsText.setText(Format.null2blank(typeData.getArgs()));
		}

		if (this.unitCombo != null) {
			this.unitCombo.setText(Format.null2blank(typeData.getUnit()));
		}

		this.descriptionText.setText(Format.toString(description));
	}

	protected void setEnabledBySqlType() {
		String database = diagram.getDatabase();

		SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo
				.getText());

		if (selectedType != null) {
			if (!selectedType.isNeedLength(diagram.getDatabase())) {
				lengthText.setEnabled(false);
				if (unitCombo != null) {
					unitCombo.setEnabled(false);
				}
			} else {
				lengthText.setEnabled(true);
				if (unitCombo != null) {
					unitCombo.setEnabled(selectedType.isFullTextIndexable());
				}
			}

			if (!selectedType.isNeedDecimal(database)) {
				decimalText.setEnabled(false);
			} else {
				decimalText.setEnabled(true);
			}

			if (this.unsignedCheck != null) {
				if (!selectedType.isNumber()) {
					unsignedCheck.setEnabled(false);
				} else {
					unsignedCheck.setEnabled(true);
				}
			}

			if (this.argsText != null) {
				if (selectedType.doesNeedArgs()) {
					argsText.setEnabled(true);
				} else {
					argsText.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.typeCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setEnabledBySqlType();
			}

		});

		this.physicalNameText.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (logicalNameText.getText().equals("")) {
					logicalNameText.setText(physicalNameText.getText());
				}
			}
		});

		this.physicalNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String logicalName = logicalNameText.getText();
				String physicalName = physicalNameText.getText();

				if (oldPhysicalName.equals(logicalName)
						|| logicalName.equals("")) {
					logicalNameText.setText(physicalName);
					oldPhysicalName = physicalName;
				}
			}
		});
	}

	abstract protected void setWordData();

	private void initializeTypeCombo() {
		this.typeCombo.add("");

		String database = this.diagram.getDatabase();

		for (String alias : SqlType.getAliasList(database)) {
			this.typeCombo.add(alias);
		}
	}

	private void initializeUnitCombo() {
		if (this.unitCombo == null) {
			return;
		}
		this.unitCombo.add("");
		this.unitCombo.add("BYTE");
		this.unitCombo.add("CHAR");
	}

	@Override
	protected String getErrorMessage() {
		String text = physicalNameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			if (this.diagram.getDiagramContents().getSettings()
					.isValidatePhysicalName()) {
				return "error.column.physical.name.not.alphabet";
			}
		}

		String logicalName = this.logicalNameText.getText();
		if (StringUtils.isEmpty(text) && StringUtils.isBlank(logicalName)) {
			return "error.column.name.empty";
		}

		text = lengthText.getText();

		if (StringUtils.isNotEmpty(text)) {
			try {
				int len = Integer.parseInt(text);
				if (len < 0) {
					return "error.column.length.zero";
				}

			} catch (NumberFormatException e) {
				return "error.column.length.digit";
			}
		}

		text = decimalText.getText();

		if (!text.equals("")) {
			try {
				int len = Integer.parseInt(text);
				if (len < 0) {
					return "error.column.decimal.zero";
				}

			} catch (NumberFormatException e) {
				return "error.column.decimal.digit";
			}
		}

		if (arrayDimensionText != null) {
			text = arrayDimensionText.getText();

			if (!text.equals("")) {
				try {
					int len = Integer.parseInt(text);
					if (len < 1) {
						return "error.column.array.dimension.one";
					}

				} catch (NumberFormatException e) {
					return "error.column.array.dimension.digit";
				}

			} else {
				if (this.arrayCheck.getSelection()) {
					return "error.column.array.dimension.one";
				}
			}
		}

		SqlType selectedType = SqlType.valueOf(diagram.getDatabase(), typeCombo
				.getText());

		if (selectedType != null && this.argsText != null) {
			text = argsText.getText();

			if (selectedType.doesNeedArgs()) {
				if (text.equals("")) {
					return "error.column.type.enum.set";
				}
			}
		}

		return null;
	}

}
