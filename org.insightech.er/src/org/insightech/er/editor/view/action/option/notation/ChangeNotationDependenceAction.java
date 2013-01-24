package org.insightech.er.editor.view.action.option.notation;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeNotationDependenceCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ChangeNotationDependenceAction extends AbstractBaseAction {

	public static final String ID = ChangeNotationDependenceAction.class
			.getName();

	public ChangeNotationDependenceAction(ERDiagramEditor editor) {
		super(ID, null, IAction.AS_CHECK_BOX, editor);
		this.setText(ResourceString
				.getResourceString("action.title.change.notation.dependence"));
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeNotationDependenceCommand command = new ChangeNotationDependenceCommand(
				diagram, this.isChecked());

		this.execute(command);
	}
}
