package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public final class CreateTablespaceCommand extends AbstractCommand {

	private final TablespaceSet tablespaceSet;

	private final Tablespace tablespace;

	public CreateTablespaceCommand(ERDiagram diagram, Tablespace tablespace) {
		this.tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
		this.tablespace = tablespace;
	}

	@Override
	protected void doExecute() {
		this.tablespaceSet.addTablespace(this.tablespace, true);
	}

	@Override
	protected void doUndo() {
		this.tablespaceSet.remove(this.tablespace, true);
	}
}
