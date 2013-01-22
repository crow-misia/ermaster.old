package org.insightech.er.editor.view.dialog.word.column;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.RealWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.word.AbstractWordDialog;
import org.insightech.er.util.Format;

public abstract class AbstractColumnDialog extends AbstractWordDialog {

	protected Combo wordCombo;

	private Text wordFilterText;

	protected CopyColumn targetColumn;

	protected NormalColumn returnColumn;

	protected Word returnWord;

	private List<Word> wordList;

	protected boolean foreignKey;

	protected boolean isRefered;

	public AbstractColumnDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, diagram);
	}

	public void setTargetColumn(CopyColumn targetColumn, boolean foreignKey,
			boolean isRefered) {
		this.targetColumn = targetColumn;
		this.foreignKey = foreignKey;
		this.isRefered = isRefered;

		if (this.targetColumn == null) {
			this.setAdd(true);
		} else {
			this.setAdd(false);
		}
	}

	private void createWordFilter(Composite composite) {
		Composite filterComposite = new Composite(composite, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		filterComposite.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		filterComposite.setLayout(layout);

		FontData fontData = Display.getCurrent().getSystemFont().getFontData()[0];
		Font font = new Font(Display.getCurrent(), fontData.getName(), 7,
				SWT.NORMAL);

		Label label = new Label(filterComposite, SWT.NONE);
		label.setText(ResourceString.getResourceString("label.filter"));
		label.setFont(font);

		GridData textGridData = new GridData();
		textGridData.widthHint = 50;

		this.wordFilterText = new Text(filterComposite, SWT.BORDER);
		this.wordFilterText.setLayoutData(textGridData);
		this.wordFilterText.setFont(font);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeComposite(Composite composite) {
		int numColumns = this.getCompositeNumColumns();

		this.wordCombo = CompositeFactory.createReadOnlyCombo(null, composite,
				"label.word", numColumns - 1 - 4, -1);
		this.createWordFilter(composite);
		this.wordCombo.setVisibleItemCount(20);

		this.initializeWordCombo(null);

		this.wordCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				int index = wordCombo.getSelectionIndex();
				if (index != 0) {
					Word word = wordList.get(index - 1);
					setWordData(word);
				}

				validate();
				setEnabledBySqlType();
			}

		});

		super.initializeComposite(composite);
	}

	protected void createWordCombo(Composite composite, GridData gridData) {
	}

	private void setWordData(Word word) {
		this.setData(word.getPhysicalName(), word.getLogicalName(), word
				.getType(), word.getTypeData(), word.getDescription());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setWordData() {
		this.setData(this.targetColumn.getPhysicalName(), this.targetColumn
				.getLogicalName(), this.targetColumn.getType(),
				this.targetColumn.getTypeData(), this.targetColumn
						.getDescription());

		this.setWordValue();
	}

	private void initializeWordCombo(String filterString) {
		this.wordCombo.removeAll();

		this.wordCombo.add("");

		this.wordList = this.diagram.getDiagramContents().getDictionary()
				.getUniqueWordList(filterString);
		// 論理名でソートする
		Collections.sort(this.wordList, Word.LOGICAL_NAME_COMPARATOR);

		for (final Word word : this.wordList) {
			String name = Format.null2blank(word.getLogicalName());

			this.wordCombo.add(name);
		}
	}

	private void setWordValue() {
		Word word = this.targetColumn.getWord();
		while (word instanceof CopyWord) {
			word = ((CopyWord) word).getOriginal();
		}

		if (word != null) {
			int index = wordList.indexOf(word);

			this.wordCombo.select(index + 1);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() throws InputException {
		String text = lengthText.getText();
		Integer length = null;
		if (!text.equals("")) {
			int len = Integer.parseInt(text);
			length = Integer.valueOf(len);
		}

		text = decimalText.getText();

		Integer decimal = null;
		if (!text.equals("")) {
			int len = Integer.parseInt(text);
			decimal = Integer.valueOf(len);
		}

		boolean array = false;
		Integer arrayDimension = null;

		if (this.arrayDimensionText != null) {
			text = arrayDimensionText.getText();

			if (!text.equals("")) {
				int len = Integer.parseInt(text);
				arrayDimension = Integer.valueOf(len);
			}

			array = this.arrayCheck.getSelection();
		}

		boolean unsigned = false;

		if (this.unsignedCheck != null) {
			unsigned = this.unsignedCheck.getSelection();
		}

		String physicalName = physicalNameText.getText();
		String logicalName = logicalNameText.getText();
		String description = descriptionText.getText();
		String args = null;
		String unit = null;

		if (argsText != null) {
			args = argsText.getText();
		}
		
		if (unitCombo != null) {
			unit = unitCombo.getText();
		}

		String database = this.diagram.getDatabase();

		SqlType selectedType = SqlType.valueOf(database, typeCombo.getText());

		TypeData typeData = new TypeData(length, decimal, array,
				arrayDimension, unsigned, args, unit);

		int wordIndex = this.wordCombo.getSelectionIndex();

		Word originalWord = null;
		final Word realWord = new RealWord(physicalName, logicalName,
				selectedType, typeData, description, database); 

		if (wordIndex > 0) {
			originalWord = wordList.get(wordIndex - 1);

			if (!Word.equals(originalWord, realWord)) {

				final Collection<NormalColumn> columns = this.diagram.getDiagramContents().getDictionary().getColumnList(originalWord);
				int count = 0;
				NormalColumn originalColumn = this.targetColumn;
				while (originalColumn != null && originalColumn instanceof CopyColumn) {
					originalColumn = ((CopyColumn) originalColumn).getOriginalColumn();
				}

				for (final NormalColumn column : columns) {
					if (column != originalColumn) {
						count++;
						break;
					}
				}
				// 単語を使用しているカラムが複数存在する場合、警告ダイアログを表示する
				if (count >= 1 && diagram.getDiagramContents().getSettings().isCheckUsedWord()) {
					final MessageBox confirm = new MessageBox(getShell(), SWT.YES|SWT.NO|SWT.CANCEL|SWT.ICON_WARNING);
					confirm.setMessage(ResourceString.getResourceString("dialog.message.use.word.other.column"));
					switch (confirm.open()) {
					case SWT.NO:
						wordIndex = 0;
						break;
					case SWT.CANCEL:
						throw new InputException(null);
					}
				}
			}
		}
		final CopyWord word;
		if (wordIndex > 0) {
			word = new CopyWord(originalWord);
			realWord.copyTo(word);
		} else {
			word = new CopyWord(realWord);
		}

		this.returnWord = word;
	}

	public NormalColumn getColumn() {
		return this.returnColumn;
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.wordFilterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent modifyevent) {
				String filterString = wordFilterText.getText();
				initializeWordCombo(filterString);
			}

		});
	}

}
