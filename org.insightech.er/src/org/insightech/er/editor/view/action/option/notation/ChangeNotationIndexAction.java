package org.insightech.er.editor.view.action.option.notation;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeNotationIndexCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ChangeNotationIndexAction extends AbstractBaseAction {

	public static final String ID = ChangeNotationIndexAction.class
			.getName();

	public ChangeNotationIndexAction(ERDiagramEditor editor) {
		super(ID, null, IAction.AS_CHECK_BOX, editor);
		this.setText(ResourceString
				.getResourceString("action.title.notation.index"));
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeNotationIndexCommand command = new ChangeNotationIndexCommand(
				diagram, this.isChecked());

		this.execute(command);
	}
}
