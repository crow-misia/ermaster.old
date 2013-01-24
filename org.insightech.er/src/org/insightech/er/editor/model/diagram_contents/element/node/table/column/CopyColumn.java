package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class CopyColumn extends NormalColumn {

	private static final long serialVersionUID = 5638703275130616851L;

	private NormalColumn originalColumn;

	public CopyColumn(NormalColumn originalColumn) {
		super(originalColumn);

		if (originalColumn == null) {
			throw new IllegalArgumentException("originalColumn is null.");
		}

		this.originalColumn = originalColumn;

		final Word originalWord = originalColumn.getWord();
		if (originalWord != null && !(originalWord instanceof CopyWord)) {
			setWord(new CopyWord(originalWord));
		}
	}

	public NormalColumn getRestructuredColumn() {
		copyData(this, this.originalColumn);

		return this.originalColumn;
	}

	@Override
	public boolean isForeignKey() {
		return this.originalColumn.isForeignKey();
	}

	@Override
	public boolean isRefered() {
		return this.originalColumn.isRefered();
	}

	public NormalColumn getOriginalColumn() {
		return originalColumn;
	}

	public Word getOriginalWord() {
		final CopyWord word = this.getWord();
		if (word != null) {
			return word.getOriginal();
		}

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		NormalColumn originalColumn = this.getOriginalColumn();

		if (obj instanceof CopyColumn) {
			CopyColumn copy = (CopyColumn) obj;
			obj = copy.getOriginalColumn();
		}

		return originalColumn.equals(obj);
	}

	@Override
	public int hashCode() {
		return originalColumn.hashCode();
	}

	@Override
	public CopyWord getWord() {
		return (CopyWord) super.getWord();
	}

}
