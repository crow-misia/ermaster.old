package org.insightech.er.editor.controller.command.diagram_contents.not_element.dictionary;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.RealWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public final class EditWordCommand extends AbstractCommand {

	private final Word oldWord;

	private final Word word;

	private final Word newWord;

	private final ERDiagram diagram;

	private Dictionary dictionary;

	public EditWordCommand(Word word, Word newWord, ERDiagram diagram) {
		this.oldWord = new RealWord(word);
		this.diagram = diagram;
		this.word = word;
		this.newWord = newWord;

		this.dictionary = this.diagram.getDiagramContents().getDictionary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		dictionary.copyTo(newWord, word, true);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		dictionary.copyTo(oldWord, word, true);
		this.diagram.changeAll();
	}

}
