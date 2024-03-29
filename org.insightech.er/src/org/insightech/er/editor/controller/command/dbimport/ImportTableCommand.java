package org.insightech.er.editor.controller.command.dbimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;

public final class ImportTableCommand extends AbstractCommand {

	private final ERDiagram diagram;

	private final SequenceSet sequenceSet;

	private final TriggerSet triggerSet;

	private final TablespaceSet tablespaceSet;

	private final GroupSet columnGroupSet;

	private final List<NodeElement> nodeElementList;

	private final List<Sequence> sequences;

	private final List<Trigger> triggers;

	private final List<Tablespace> tablespaces;

	private final List<ColumnGroup> columnGroups;

	private final DirectedGraph graph;

	private static final int AUTO_GRAPH_LIMIT = 100;

	private static final int ORIGINAL_X = 20;
	private static final int ORIGINAL_Y = 20;

	private static final int DISTANCE_X = 300;
	private static final int DISTANCE_Y = 300;

	private static final int SIZE_X = 6;

	public ImportTableCommand(ERDiagram diagram,
			List<NodeElement> nodeElementList, List<Sequence> sequences,
			List<Trigger> triggers, List<Tablespace> tablespaces,
			List<ColumnGroup> columnGroups) {
		this.diagram = diagram;
		this.nodeElementList = nodeElementList;
		this.sequences = sequences;
		this.triggers = triggers;
		this.tablespaces = tablespaces;
		this.columnGroups = columnGroups;

		DiagramContents diagramContents = this.diagram.getDiagramContents();

		this.sequenceSet = diagramContents.getSequenceSet();
		this.triggerSet = diagramContents.getTriggerSet();
		this.tablespaceSet = diagramContents.getTablespaceSet();
		this.columnGroupSet = diagramContents.getGroups();

		this.graph = new DirectedGraph();

		this.decideLocation();
	}

	@SuppressWarnings("unchecked")
	private void decideLocation() {
		if (this.nodeElementList.size() < AUTO_GRAPH_LIMIT) {
			Map<NodeElement, Node> nodeElementNodeMap = new HashMap<NodeElement, Node>();

			int fontSize = this.diagram.getFontSize();

			Insets insets = new Insets(5 * fontSize, 10 * fontSize,
					35 * fontSize, 20 * fontSize);

			for (NodeElement nodeElement : this.nodeElementList) {
				Node node = new Node();

				node.setPadding(insets);
				this.graph.nodes.add(node);
				nodeElementNodeMap.put(nodeElement, node);
			}

			for (NodeElement nodeElement : this.nodeElementList) {
				for (ConnectionElement outgoing : nodeElement.getOutgoings()) {
					Node sourceNode = nodeElementNodeMap.get(outgoing
							.getSource());
					Node targetNode = nodeElementNodeMap.get(outgoing
							.getTarget());
					if (sourceNode != targetNode) {
						Edge edge = new Edge(sourceNode, targetNode);
						this.graph.edges.add(edge);
					}
				}
			}

			DirectedGraphLayout layout = new DirectedGraphLayout();

			layout.visit(this.graph);

			for (final Map.Entry<NodeElement, Node> entry : nodeElementNodeMap.entrySet()) {
				final NodeElement nodeElement = entry.getKey();
				final Node node = entry.getValue();

				if (nodeElement.getWidth() == 0) {
					nodeElement
							.setLocation(new Location(node.x, node.y, -1, -1));
				}
			}

		} else {
			int x = ORIGINAL_X;
			int y = ORIGINAL_Y;

			for (NodeElement nodeElement : this.nodeElementList) {
				if (nodeElement.getWidth() == 0) {
					nodeElement.setLocation(new Location(x, y, -1, -1));

					x += DISTANCE_X;
					if (x > DISTANCE_X * SIZE_X) {
						x = ORIGINAL_X;
						y += DISTANCE_Y;
					}
				}
			}
		}
	}

	@Override
	protected void doExecute() {
		if (this.columnGroups != null) {
			for (ColumnGroup columnGroup : columnGroups) {
				this.columnGroupSet.add(columnGroup, false);
			}
			this.columnGroupSet.setDirty();
		}

		ERDiagramEditPart.setUpdateable(false);

		for (NodeElement nodeElement : this.nodeElementList) {
			this.diagram.addNewContent(nodeElement, false, false);

			if (nodeElement instanceof TableView) {
				for (final NormalColumn normalColumn : ((TableView) nodeElement)
						.getNormalColumns()) {
					if (normalColumn.isForeignKey()) {
						for (Relation relation : normalColumn.getRelationList()) {
							if (relation.getSourceTableView() == nodeElement) {
								setSelfRelation(relation);
							}
						}
					}
				}
			}
		}
		this.diagram.setDirtyForContent();
		this.diagram.getDiagramContents().getDictionary().setDirty();

		for (Sequence sequence : sequences) {
			this.sequenceSet.addSequence(sequence, false);
		}
		this.sequenceSet.setDirty();

		for (Trigger trigger : triggers) {
			this.triggerSet.addTrigger(trigger, false);
		}
		this.triggerSet.setDirty();

		for (Tablespace tablespace : tablespaces) {
			this.tablespaceSet.addTablespace(tablespace, false);
		}
		this.tablespaceSet.setDirty();

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll(this.nodeElementList);
	}

	private static void setSelfRelation(Relation relation) {
		boolean anotherSelfRelation = false;

		TableView sourceTable = relation.getSourceTableView();
		for (Relation otherRelation : sourceTable.getOutgoingRelations()) {
			if (otherRelation == relation) {
				continue;
			}
			if (otherRelation.getSource() == otherRelation.getTarget()) {
				anotherSelfRelation = true;
				break;
			}
		}

		int rate = 0;

		if (anotherSelfRelation) {
			rate = 50;

		} else {
			rate = 100;
		}

		Bendpoint bendpoint0 = new Bendpoint(rate, rate);
		bendpoint0.setRelative(true);

		int xp = 100 - (rate / 2);
		int yp = 100 - (rate / 2);

		relation.setSourceLocationp(100, yp);
		relation.setTargetLocationp(xp, 100);

		relation.addBendpoint(0, bendpoint0, true);
	}

	@Override
	protected void doUndo() {
		ERDiagramEditPart.setUpdateable(false);

		final Dictionary dictionary = this.diagram.getDiagramContents().getDictionary();
		for (NodeElement nodeElement : this.nodeElementList) {
			this.diagram.removeContent(nodeElement, false, false);

			if (nodeElement instanceof TableView) {
				dictionary.remove((TableView) nodeElement, false);
			}
		}
		this.diagram.setDirtyForContent();
		dictionary.setDirty();

		for (Sequence sequence : sequences) {
			this.sequenceSet.remove(sequence, false);
		}
		this.sequenceSet.setDirty();

		for (Trigger trigger : triggers) {
			this.triggerSet.remove(trigger, false);
		}
		this.triggerSet.setDirty();

		for (Tablespace tablespace : tablespaces) {
			this.tablespaceSet.remove(tablespace, false);
		}
		this.tablespaceSet.setDirty();

		if (this.columnGroups != null) {
			for (ColumnGroup columnGroup : columnGroups) {
				this.columnGroupSet.remove(columnGroup, false);
			}
			this.columnGroupSet.setDirty();
		}

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}
}
