package org.insightech.er.editor.view.action.outline.notation.type;

import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.common.ChangeOutlineViewModeCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.outline.AbstractOutlineBaseAction;

public class ChangeOutlineViewToPhysicalAction extends
		AbstractOutlineBaseAction {

	public static final String ID = ChangeOutlineViewToPhysicalAction.class
			.getName();

	public ChangeOutlineViewToPhysicalAction(TreeViewer treeViewer) {
		super(ID, null, IAction.AS_RADIO_BUTTON, treeViewer);
		this.setText(ResourceString
				.getResourceString("action.title.change.mode.to.physical"));
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeOutlineViewModeCommand command = new ChangeOutlineViewModeCommand(
				diagram, Settings.VIEW_MODE_PHYSICAL);

		this.execute(command);
	}

}
