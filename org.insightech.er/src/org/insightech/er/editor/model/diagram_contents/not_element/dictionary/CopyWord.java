package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

public final class CopyWord extends RealWord {

	private static final long serialVersionUID = 5610038803601000225L;

	private Word original;

	private CopyWord(Word original) {
		super(original);
		this.original = original;
	}

	public static CopyWord getInstance(final Word original) {
		Word o = original;
		while (o instanceof CopyWord) {
			o = ((CopyWord) o).original;
		}
		return new CopyWord(o);
	}

	public Word restructure(Dictionary dictionary) {
		dictionary.copyTo(this, this.original, true);
		return this.original;
	}

	public Word getOriginal() {
		return original;
	}

	@Override
	public void copyTo(Word to) {
		super.copyTo(to);
		if (to instanceof CopyWord) {
			((CopyWord) to).original = this.original;
		}
	}

	public void setOriginal(Word original) {
		this.original = original;
	}

	
}
