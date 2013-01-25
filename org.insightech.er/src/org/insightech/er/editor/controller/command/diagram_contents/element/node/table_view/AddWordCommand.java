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

	private final int index;

	private NormalColumn column;

	public AddWordCommand(TableView tableView, UniqueWord uniqueWord, int index) {
		this.tableView = tableView;
		this.word = uniqueWord.getWord();
		this.index = index;

		this.dictionary = this.tableView.getDiagram().getDiagramContents()
				.getDictionary();
	}

	@Override
	protected void doExecute() {
		this.column = new NormalColumn(this.word, true, false, false, false,
				null, null, null, null, null);

		this.tableView.addColumn(this.index, this.column, true);
		this.dictionary.add(this.column, true);
	}

	@Override
	protected void doUndo() {
		if (this.column != null) {
			this.tableView.removeColumn(this.column, true);
			this.dictionary.remove(this.column, true);
		}
	}

}
