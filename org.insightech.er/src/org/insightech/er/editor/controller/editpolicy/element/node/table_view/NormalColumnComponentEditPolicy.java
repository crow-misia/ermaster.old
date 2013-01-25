package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.DeleteColumnCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class NormalColumnComponentEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		if (request.getEditParts().size() == 1 &&
				this.getHost().getModel() instanceof Column) {
			final Column column = (Column) this.getHost()
					.getModel();

			final TableView table;
			if (column.getColumnHolder() instanceof TableView) {
				table = (TableView) column.getColumnHolder();
			} else if (column.getColumnHolder() instanceof ColumnGroup) {
				// ColumnGroup の ColumnHolder からはテーブルは取得できないので注意
				table = (TableView) this.getHost().getParent().getModel();
			} else {
				return null;
			}
			return new DeleteColumnCommand(table, column);
		}

		return null;
	}

}
