package org.insightech.er.db.impl.mysql.tablespace;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.impl.mysql.MySQLAdvancedComposite;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.util.Format;

public class MySQLTablespaceDialog extends TablespaceDialog {

	private Text dataFile;

	private Text logFileGroup;

	private Text extentSize;

	private Text initialSize;

	private Combo engine;

	@Override
	protected void initialize(Composite composite) {
		super.initialize(composite);

		this.dataFile = CompositeFactory.createText(this, composite,
				"label.tablespace.data.file", false);
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.data.file.example");

		this.logFileGroup = CompositeFactory.createText(this, composite,
				"label.tablespace.log.file.group", false);
		this.extentSize = CompositeFactory.createText(this, composite,
				"label.tablespace.extent.size", 1, NUM_TEXT_WIDTH, false);
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.size.example");
		this.initialSize = CompositeFactory.createText(this, composite,
				"label.tablespace.initial.size", 1, NUM_TEXT_WIDTH, false);
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.size.example");
		this.engine = MySQLAdvancedComposite.createEngineCombo(composite, this);
	}

	@Override
	protected TablespaceProperties setTablespaceProperties() {
		MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

		properties.setDataFile(this.dataFile.getText().trim());
		properties.setLogFileGroup(this.logFileGroup.getText().trim());
		properties.setExtentSize(this.extentSize.getText().trim());
		properties.setInitialSize(this.initialSize.getText().trim());
		properties.setEngine(this.engine.getText().trim());

		return properties;
	}

	@Override
	protected void setData(TablespaceProperties tablespaceProperties) {
		if (tablespaceProperties instanceof MySQLTablespaceProperties) {
			MySQLTablespaceProperties properties = (MySQLTablespaceProperties) tablespaceProperties;

			this.dataFile.setText(Format.toString(properties.getDataFile()));
			this.logFileGroup.setText(Format.toString(properties
					.getLogFileGroup()));
			this.extentSize
					.setText(Format.toString(properties.getExtentSize()));
			this.initialSize.setText(Format.toString(properties
					.getInitialSize()));
			this.engine.setText(Format.toString(properties.getEngine()));
		}
	}

	@Override
	protected String getErrorMessage() {
		String errorMessage = super.getErrorMessage();
		if (errorMessage != null) {
			return errorMessage;
		}

		if (StringUtils.isBlank(this.dataFile.getText())) {
			return "error.tablespace.data.file.empty";
		}

		if (StringUtils.isBlank(this.logFileGroup.getText())) {
			return "error.tablespace.log.file.group.empty";
		}

		if (StringUtils.isBlank(this.initialSize.getText().trim())) {
			return "error.tablespace.initial.size.empty";
		}

		if (StringUtils.isBlank(this.engine.getText().trim())) {
			return "error.tablespace.storage.engine.empty";
		}

		return null;
	}

}
