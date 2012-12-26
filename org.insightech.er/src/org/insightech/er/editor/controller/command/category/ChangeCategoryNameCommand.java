package org.insightech.er.editor.controller.command.category;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public final class ChangeCategoryNameCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final String oldName;

	private final String newName;

	private final Category category;

	public ChangeCategoryNameCommand(ERDiagram diagram, Category category,
			String newName) {
		this.diagram = diagram;
		this.category = category;
		this.newName = newName;

		this.oldName = category.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.category.setName(this.newName);
		this.diagram.setCurrentCategoryPageName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.category.setName(this.oldName);
		this.diagram.setCurrentCategoryPageName();
	}
}
