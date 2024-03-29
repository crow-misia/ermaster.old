package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public final class CopyGroup extends ColumnGroup {

	private static final long serialVersionUID = 8453816730649838482L;

	private ColumnGroup original;

	private CopyGroup(final ColumnGroup original) {
		super();

		this.original = original;

		this.setId(this.original.getId());
		this.setGroupName(this.original.getGroupName());

		for (NormalColumn fromColumn : this.original.getColumns()) {
			CopyColumn copyColumn = CopyColumn.getInstance(fromColumn);
			this.addColumn(copyColumn);
		}
	}

	public static CopyGroup getInstance(final ColumnGroup original) {
		ColumnGroup o = original;
		while (o instanceof CopyGroup) {
			o = ((CopyGroup) o).original;
		}
		return new CopyGroup(o);
	}

	public ColumnGroup restructure(ERDiagram diagram, final boolean dictionaryFire) {
		if (this.original == null) {
			this.original = new ColumnGroup();
		}

		this.restructure(diagram, this.original, dictionaryFire);

		return this.original;
	}

	private void restructure(ERDiagram diagram, ColumnGroup to, final boolean dictionaryFire) {
		Dictionary dictionary = null;

		if (diagram != null) {
			dictionary = diagram.getDiagramContents().getDictionary();
			for (NormalColumn toColumn : to.getColumns()) {
				dictionary.remove(toColumn, false);
			}
		}

		to.setId(this.getId());
		to.setGroupName(this.getGroupName());

		List<NormalColumn> columns = new ArrayList<NormalColumn>();

		for (NormalColumn fromColumn : this.getColumns()) {
			// グループの更新ボタンを押した場合、
			CopyColumn copyColumn = (CopyColumn) fromColumn;
			CopyWord copyWord = copyColumn.getWord();

			if (copyWord != null) {
				Word originalWord = copyColumn.getOriginalWord();

				if (dictionary != null) {
					dictionary.copyTo(copyWord, originalWord, false);

				} else {
					while (originalWord instanceof CopyWord) {
						originalWord = ((CopyWord) originalWord).getOriginal();
					}

					//originalWord = new CopyWord(originalWord);
					//copyWord.copyTo(originalWord);
					copyWord.setOriginal(originalWord);
				}
			}

			NormalColumn restructuredColumn = copyColumn
					.getRestructuredColumn();

			if (to instanceof CopyGroup) {
				if (!(restructuredColumn instanceof CopyColumn)) {
					restructuredColumn = CopyColumn.getInstance(restructuredColumn);
				}
			}

			columns.add(restructuredColumn);

			if (dictionary != null) {
				dictionary.add(restructuredColumn, false);
			}

		}

		if (dictionaryFire && dictionary != null) {
			dictionary.setDirty();
		}

		to.setColumns(columns);
	}

	public ColumnGroup getOriginal() {
		return this.original;
	}
}
