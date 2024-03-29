package org.insightech.er.editor.controller.command.tracking;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.model.tracking.RemovedNodeElement;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;

/**
 * 変更履歴計算コマンド
 */
public final class CalculateChangeTrackingCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final NodeSet comparison;

	private final ChangeTrackingList changeTrackingList;

	private final List<NodeElement> oldAddedNodeElements;

	private final List<UpdatedNodeElement> oldUpdatedNodeElements;

	private final List<RemovedNodeElement> oldRemovedNodeElements;

	/**
	 * 変更履歴計算コマンドを作成します。
	 * 
	 * @param diagram
	 * @param comparison
	 */
	public CalculateChangeTrackingCommand(ERDiagram diagram, NodeSet comparison) {
		this.diagram = diagram;
		this.comparison = comparison;

		this.changeTrackingList = this.diagram.getChangeTrackingList();

		this.oldAddedNodeElements = this.changeTrackingList
				.getAddedNodeElementSet();
		this.oldUpdatedNodeElements = this.changeTrackingList
				.getUpdatedNodeElementSet();
		this.oldRemovedNodeElements = this.changeTrackingList
				.getRemovedNodeElementSet();
	}

	/**
	 * 変更履歴計算処理を実行する
	 */
	@Override
	protected void doExecute() {
		this.changeTrackingList.calculateUpdatedNodeElementSet(this.comparison,
				this.diagram.getDiagramContents().getContents());
		this.diagram.changeAll();
	}

	/**
	 * 変更履歴計算処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		this.changeTrackingList.restore(this.oldAddedNodeElements,
				this.oldUpdatedNodeElements, this.oldRemovedNodeElements);
		this.diagram.changeAll();
	}
}
