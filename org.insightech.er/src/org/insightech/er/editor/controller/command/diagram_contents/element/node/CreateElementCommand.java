package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public final class CreateElementCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final NodeElement element;

	private final List<NodeElement> enclosedElementList;

	public CreateElementCommand(ERDiagram diagram, NodeElement element, int x,
			int y, Dimension size, List<NodeElement> enclosedElementList) {
		this.diagram = diagram;
		this.element = element;

		if (this.element instanceof Category && size != null) {
			this.element
					.setLocation(new Location(x, y, size.width, size.height));
		} else {
			this.element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH,
					ERTable.DEFAULT_HEIGHT));
		}

		if (element instanceof ERTable) {
			ERTable table = (ERTable) element;
			table.setLogicalName(ERTable.NEW_LOGICAL_NAME, true);
			table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME, true);

		} else if (element instanceof View) {
			View view = (View) element;
			view.setLogicalName(View.NEW_LOGICAL_NAME, true);
			view.setPhysicalName(View.NEW_PHYSICAL_NAME, true);
		}
		
		this.enclosedElementList = enclosedElementList;
	}

	@Override
	protected void doExecute() {
		if (this.element instanceof Category) {
			Category category = (Category) this.element;
			category
					.setName(ResourceString.getResourceString("label.category"));
			category.setContents(this.enclosedElementList);
			this.diagram.addCategory(category, true);
		} else {
			this.diagram.addNewContent(this.element, true, true);
		}
	}

	@Override
	protected void doUndo() {
		if (this.element instanceof Category) {
			Category category = (Category) this.element;
			category.getContents().clear();
			this.diagram.removeCategory(category, true);
		} else {
			this.diagram.removeContent(this.element, true, true);
		}
	}

	@Override
	public boolean canExecute() {
		if (this.element instanceof Category) {
			if (this.diagram.getCurrentCategory() != null) {
				return false;
			}
		}

		return super.canExecute();
	}

}
