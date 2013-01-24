package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

/**
 * 変更履歴計算コマンド
 */
public final class ResetChangeTrackingCommand extends AbstractCommand {

	private final ERDiagram diagram;
	private final ChangeTrackingList changeTrackingList;

	private final boolean oldCalculated;

	public ResetChangeTrackingCommand(ERDiagram diagram) {
		this.diagram = diagram;
		this.changeTrackingList = this.diagram.getChangeTrackingList();
		this.oldCalculated = this.changeTrackingList.isCalculated();
	}

	@Override
	protected void doExecute() {
		this.changeTrackingList.setCalculated(false);
		this.diagram.changeAll();
	}

	@Override
	protected void doUndo() {
		this.changeTrackingList.setCalculated(oldCalculated);
		this.diagram.changeAll();
	}
}
