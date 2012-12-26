package org.insightech.er.editor.controller.command.diagram_contents.element.node.category;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;

public final class DeleteCategoryCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final CategorySetting categorySettings;

	private final Category category;

	private final List<Category> oldAllCategories;

	private final List<Category> oldSelectedCategories;

	public DeleteCategoryCommand(ERDiagram diagram, Category category) {
		this.diagram = diagram;
		this.categorySettings = diagram.getDiagramContents().getSettings()
				.getCategorySetting();
		this.category = category;
		this.oldAllCategories = new ArrayList<Category>(this.categorySettings
				.getAllCategories());
		this.oldSelectedCategories = new ArrayList<Category>(
				this.categorySettings.getSelectedCategories());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.removeCategory(category, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.categorySettings.setAllCategories(oldAllCategories);
		this.categorySettings.setSelectedCategories(oldSelectedCategories);
		this.diagram.restoreCategories(true);
	}
}
