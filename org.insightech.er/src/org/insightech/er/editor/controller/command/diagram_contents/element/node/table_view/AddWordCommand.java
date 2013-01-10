package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public final class AddWordCommand extends AbstractCommand {

	private final TableView tableView;

	private final Dictionary dictionary;

	private final Word word;

	private final NormalColumn column;

	private final int index;

	public AddWordCommand(TableView tableView, UniqueWord uniqueWord, int index) {
		this.tableView = tableView;
		this.word = uniqueWord.getWord();
		this.index = index;

		this.dictionary = this.tableView.getDiagram().getDiagramContents()
				.getDictionary();

		this.column = new NormalColumn(this.word, true, false, false, false,
				null, null, null, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.tableView.addColumn(this.index, this.column, true);
		this.dictionary.add(this.column, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.tableView.removeColumn(this.column, true);
		this.dictionary.remove(this.column, true);
	}

}
