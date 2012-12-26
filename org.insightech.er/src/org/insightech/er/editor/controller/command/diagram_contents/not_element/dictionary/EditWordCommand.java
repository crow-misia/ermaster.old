package org.insightech.er.editor.controller.command.diagram_contents.not_element.dictionary;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public final class EditWordCommand extends AbstractCommand {

	private final Word oldWord;

	private final Word word;

	private final Word newWord;

	private final ERDiagram diagram;

	public EditWordCommand(Word word, Word newWord, ERDiagram diagram) {
		this.oldWord = new Word(word.getPhysicalName(), word.getLogicalName(),
				word.getType(), word.getTypeData().clone(), word
						.getDescription(), diagram.getDatabase());
		this.diagram = diagram;
		this.word = word;
		this.newWord = newWord;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		Dictionary.copyTo(newWord, word);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		Dictionary.copyTo(oldWord, word);
		this.diagram.changeAll();
	}

}
