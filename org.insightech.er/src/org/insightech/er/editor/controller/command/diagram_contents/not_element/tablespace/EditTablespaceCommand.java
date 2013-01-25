package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public final class EditTablespaceCommand extends AbstractCommand {

	private final TablespaceSet tablespaceSet;

	private final Tablespace tablespace;

	private final Tablespace newTablespace;

	private Tablespace oldTablespace;

	public EditTablespaceCommand(ERDiagram diagram, Tablespace tablespace,
			Tablespace newTablespace) {
		this.tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
		this.tablespace = tablespace;
		this.newTablespace = newTablespace;
	}

	@Override
	protected void doExecute() {
		this.oldTablespace = (Tablespace) this.tablespace.clone();

		this.newTablespace.copyTo(this.tablespace);
		this.tablespaceSet.addTablespace(this.tablespace, true);
	}

	@Override
	protected void doUndo() {
		if (this.oldTablespace != null) {
			this.oldTablespace.copyTo(this.tablespace);
			this.tablespaceSet.addTablespace(this.tablespace, true);
		}
	}
}
