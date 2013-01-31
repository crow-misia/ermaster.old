package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.settings.Settings;

public final class ChangeNotationIndexCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final boolean oldNotationIndex;

	private final boolean newNotationIndex;

	private final Settings settings;

	public ChangeNotationIndexCommand(ERDiagram diagram,
			boolean notationIndex) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newNotationIndex = notationIndex;
		this.oldNotationIndex = this.settings.isNotationIndex();
	}

	@Override
	protected void doExecute() {
		this.settings.setNotationIndex(this.newNotationIndex);

		for (TableView tableView : this.diagram.getDiagramContents()
				.getContents().getTableViewList()) {
			tableView.setDirty();
		}
	}

	@Override
	protected void doUndo() {
		this.settings.setNotationExpandGroup(this.oldNotationIndex);
		for (TableView tableView : this.diagram.getDiagramContents()
				.getContents().getTableViewList()) {
			tableView.setDirty();
		}
	}
}
