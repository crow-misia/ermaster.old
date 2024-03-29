package org.insightech.er.editor.model.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.settings.Settings;

public class CopyManager {

	private static NodeSet copyList = new NodeSet();

	private static int numberOfCopy;

	private Map<NodeElement, NodeElement> nodeElementMap;

	public static void copy(NodeSet nodeElementList) {
		CopyManager copyManager = new CopyManager();
		copyList = copyManager.copyNodeElementList(nodeElementList);
	}

	public static NodeSet paste() {
		numberOfCopy++;
		CopyManager copyManager = new CopyManager();
		return copyManager.copyNodeElementList(copyList);
	}

	public static void clear() {
		copyList.clear();
		numberOfCopy = 0;
	}

	public static boolean canCopy() {
		if (copyList != null && !copyList.isEmpty()) {
			return true;
		}

		return false;
	}

	public static int getNumberOfCopy() {
		return numberOfCopy;
	}

	public Map<NodeElement, NodeElement> getNodeElementMap() {
		return nodeElementMap;
	}

	public NodeSet copyNodeElementList(NodeSet nodeElementList) {
		NodeSet copyList = new NodeSet();

		this.nodeElementMap = new HashMap<NodeElement, NodeElement>();
		Map<Column, Column> columnMap = new HashMap<Column, Column>();
		Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, ComplexUniqueKey>();

		// 選択されているノードのEditPartに対して処理を繰り返します
		for (NodeElement nodeElement : nodeElementList) {

			if (nodeElement instanceof ModelProperties) {
				// モデルプロパティの場合、何もしません
				continue;
			}

			// ノードを複製して、コピー情報に追加します
			NodeElement cloneNodeElement = (NodeElement) nodeElement.clone();
			copyList.addNodeElement(cloneNodeElement, false);

			nodeElementMap.put(nodeElement, cloneNodeElement);

			if (nodeElement instanceof ERTable) {
				// ノードがテーブルの場合
				// 列とインデックスと複合一意キーを複製します。
				copyColumnAndIndex((ERTable) nodeElement,
						(ERTable) cloneNodeElement, columnMap,
						complexUniqueKeyMap);

			} else if (nodeElement instanceof View) {
				// ノードがビューの場合
				// 列を複製します。
				copyColumn((View) nodeElement, (View) cloneNodeElement,
						columnMap);
			}
		}

		// 複製後のノードに対して、接続を作りなおします
		Map<ConnectionElement, ConnectionElement> connectionElementMap = new HashMap<ConnectionElement, ConnectionElement>();

		// 接続を張りなおします
		for (NodeElement nodeElement : nodeElementMap.keySet()) {
			NodeElement cloneNodeElement = nodeElementMap.get(nodeElement);

			// 複製元ノードに入ってくる接続を複製先に張りなおします
			replaceIncoming(nodeElement, cloneNodeElement,
					connectionElementMap, nodeElementMap);
		}

		// 外部キーの参照を作り直します
		for (NodeElement nodeElement : nodeElementMap.keySet()) {

			if (nodeElement instanceof ERTable) {
				ERTable table = (ERTable) nodeElement;

				// 複製元テーブルの列に対して処理を繰り返します
				for (Column column : table.getColumns()) {
					if (column instanceof NormalColumn) {
						NormalColumn oldColumn = (NormalColumn) column;

						// 外部キーの場合
						if (oldColumn.isForeignKey()) {
							NormalColumn newColumn = (NormalColumn) columnMap
									.get(oldColumn);
							newColumn.renewRelationList();

							for (Relation oldRelation : oldColumn
									.getRelationList()) {

								// 複製された関連の取得
								Relation newRelation = (Relation) connectionElementMap
										.get(oldRelation);

								if (newRelation != null) {
									// 関連も複製されている場合

									NormalColumn oldReferencedColumn = newRelation
											.getReferencedColumn();

									// ユニークキーを参照している場合
									if (oldReferencedColumn != null) {
										NormalColumn newReferencedColumn = (NormalColumn) columnMap
												.get(oldReferencedColumn);

										newRelation
												.setReferencedColumn(newReferencedColumn);

									}

									ComplexUniqueKey oldReferencedComplexUniqueKey = newRelation
											.getReferencedComplexUniqueKey();

									// 複合ユニークキーを参照している場合
									if (oldReferencedComplexUniqueKey != null) {
										ComplexUniqueKey newReferencedComplexUniqueKey = (ComplexUniqueKey) complexUniqueKeyMap
												.get(oldReferencedComplexUniqueKey);
										if (newReferencedComplexUniqueKey != null) {
											newRelation
													.setReferencedComplexUniqueKey(newReferencedComplexUniqueKey);
										}
									}

									NormalColumn targetReferencedColumn = null;

									for (NormalColumn referencedColumn : oldColumn
											.getReferencedColumnList()) {
										if (referencedColumn.getColumnHolder() == oldRelation
												.getSourceTableView()) {
											targetReferencedColumn = referencedColumn;
											break;
										}
									}
									NormalColumn newReferencedColumn = (NormalColumn) columnMap
											.get(targetReferencedColumn);

									newColumn.removeReference(oldRelation);
									newColumn.addReference(newReferencedColumn,
											newRelation);

								} else {
									// 複製先の列を外部キーではなく、通常の列に作り直します
									newColumn.removeReference(oldRelation);
								}
							}
						}
					}
				}

			}
		}
		copyList.setDirty();

		return copyList;
	}

	/**
	 * 複製元ノードに入ってくる接続を複製先に張りなおします
	 */
	private static void replaceIncoming(NodeElement from, NodeElement to,
			Map<ConnectionElement, ConnectionElement> connectionElementMap,
			Map<NodeElement, NodeElement> nodeElementMap) {
		List<ConnectionElement> cloneIncomings = new ArrayList<ConnectionElement>();

		// 複製元ノードに入ってくる接続に対して処理を繰り返します
		for (ConnectionElement incoming : from.getIncomings()) {
			NodeElement oldSource = incoming.getSource();

			// 接続元の複製を取得します
			NodeElement newSource = nodeElementMap.get(oldSource);

			// 接続元も複製されている場合
			if (newSource != null) {

				// 接続を複製します。
				ConnectionElement cloneIncoming = (ConnectionElement) incoming
						.clone();

				cloneIncoming.setSourceAndTarget(newSource, to);

				connectionElementMap.put(incoming, cloneIncoming);

				cloneIncomings.add(cloneIncoming);

				newSource.addOutgoing(cloneIncoming);
			}
		}

		to.setIncoming(cloneIncomings);
	}

	/**
	 * 列とインデックスの情報を複製します。
	 * 
	 * @param from
	 *            元のテーブル
	 * @param to
	 *            複製されたテーブル
	 * @param columnMap
	 *            キー：元の列、値：複製後の列
	 */
	private static void copyColumnAndIndex(ERTable from, ERTable to,
			Map<Column, Column> columnMap,
			Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
		copyColumn(from, to, columnMap);
		copyIndex(from, to, columnMap);
		copyComplexUniqueKey(from, to, columnMap, complexUniqueKeyMap);
	}

	private static void copyColumn(TableView from, TableView to,
			Map<Column, Column> columnMap) {
		// 複製後の列の一覧
		List<Column> cloneColumns = new ArrayList<Column>();

		// 元のテーブルの列に対して、処理を繰り返します。
		for (Column column : from.getColumns()) {

			Column cloneColumn = null;

			// 列を複製します。
			cloneColumn = (Column) column.clone();

			cloneColumns.add(cloneColumn);

			columnMap.put(column, cloneColumn);
		}

		// 複製後のテーブルに、複製後の列一覧を設定します。
		to.setColumns(cloneColumns, true);
	}

	private static void copyComplexUniqueKey(ERTable from, ERTable to,
			Map<Column, Column> columnMap,
			Map<ComplexUniqueKey, ComplexUniqueKey> complexUniqueKeyMap) {
		List<ComplexUniqueKey> cloneComplexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

		// 元のテーブルの複合一意キーに対して、処理を繰り返します。
		for (ComplexUniqueKey complexUniqueKey : from.getComplexUniqueKeyList()) {

			// 複合一意キーを複製します。
			ComplexUniqueKey cloneComplexUniqueKey = (ComplexUniqueKey) complexUniqueKey
					.clone();
			complexUniqueKeyMap.put(complexUniqueKey, cloneComplexUniqueKey);

			List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

			// 複製後の複合一意キーの列に対して、処理を繰り返します。
			for (NormalColumn column : cloneComplexUniqueKey.getColumnList()) {
				// 複製後の列を取得して、複製後の複合一意キーの列一覧に追加します。
				cloneColumns.add((NormalColumn) columnMap.get(column));
			}

			// 複製後の複合一意キーに、複製後の複合一意キーの列一覧を設定します。
			cloneComplexUniqueKey.setColumnList(cloneColumns);

			cloneComplexUniqueKeyList.add(cloneComplexUniqueKey);
		}

		// 複製後のテーブルに、複製後のインデックス一覧を設定します。
		to.setComplexUniqueKeyList(cloneComplexUniqueKeyList);
	}

	private static void copyIndex(ERTable from, ERTable to,
			Map<Column, Column> columnMap) {
		List<Index> cloneIndexes = new ArrayList<Index>();

		// 元のテーブルのインデックスに対して、処理を繰り返します。
		for (Index index : from.getIndexes()) {

			// インデックスを複製します。
			Index cloneIndex = (Index) index.clone();

			List<NormalColumn> cloneIndexColumns = new ArrayList<NormalColumn>();

			// 複製後のインデックスの列に対して、処理を繰り返します。
			for (NormalColumn indexColumn : cloneIndex.getColumns()) {
				// 複製後の列を取得して、複製後のインデックス列一覧に追加します。
				cloneIndexColumns
						.add((NormalColumn) columnMap.get(indexColumn));
			}

			// 複製後のインデックスに、複製後のインデックス列一覧を設定します。
			cloneIndex.setColumns(cloneIndexColumns);

			cloneIndexes.add(cloneIndex);
		}

		// 複製後のテーブルに、複製後のインデックス一覧を設定します。
		to.setIndexes(cloneIndexes);
	}

	public DiagramContents copy(DiagramContents originalDiagramContents) {
		DiagramContents copyDiagramContents = new DiagramContents();

		copyDiagramContents.setContents(this
				.copyNodeElementList(originalDiagramContents.getContents()));
		Map<NodeElement, NodeElement> nodeElementMap = this.getNodeElementMap();

		Settings settings = (Settings) originalDiagramContents.getSettings()
				.clone();
		setSettings(nodeElementMap, settings);
		copyDiagramContents.setSettings(settings);

		setColumnGroup(copyDiagramContents, originalDiagramContents);

		copyDiagramContents.setSequenceSet(originalDiagramContents
				.getSequenceSet().clone());
		copyDiagramContents.setTriggerSet(originalDiagramContents
				.getTriggerSet().clone());

		setWord(copyDiagramContents, originalDiagramContents);
		setTablespace(copyDiagramContents, originalDiagramContents);

		return copyDiagramContents;
	}

	private static void setSettings(Map<NodeElement, NodeElement> nodeElementMap,
			Settings settings) {
		for (Category category : settings.getCategorySetting()
				.getAllCategories()) {
			List<NodeElement> newContents = new ArrayList<NodeElement>();
			for (NodeElement nodeElement : category.getContents()) {
				newContents.add(nodeElementMap.get(nodeElement));
			}

			category.setContents(newContents);
		}
	}

	private static void setColumnGroup(DiagramContents copyDiagramContents,
			DiagramContents originalDiagramContents) {

		Map<ColumnGroup, ColumnGroup> columnGroupMap = new HashMap<ColumnGroup, ColumnGroup>();

		final GroupSet groupSet = copyDiagramContents.getGroups();
		for (ColumnGroup columnGroup : originalDiagramContents.getGroups().getGroupList()) {
			ColumnGroup newColumnGroup = (ColumnGroup) columnGroup.clone();
			groupSet.add(newColumnGroup, false);

			columnGroupMap.put(columnGroup, newColumnGroup);
		}
		groupSet.setDirty();

		for (TableView tableView : copyDiagramContents.getContents()
				.getTableViewList()) {
			List<Column> newColumns = new ArrayList<Column>();

			for (Column column : tableView.getColumns()) {
				if (column instanceof ColumnGroup) {
					newColumns.add(columnGroupMap.get((ColumnGroup) column));

				} else {
					newColumns.add(column);
				}
			}

			tableView.setColumns(newColumns, true);
		}
	}

	private static void setWord(DiagramContents copyDiagramContents,
			DiagramContents originalDiagramContents) {

		Map<Word, Word> wordMap = new IdentityHashMap<Word, Word>();
		Dictionary copyDictionary = copyDiagramContents.getDictionary();

		for (Word word : originalDiagramContents.getDictionary().getWordList()) {
			Word newWord = (Word) word.clone();
			wordMap.put(word, newWord);
		}

		for (TableView tableView : copyDiagramContents.getContents()
				.getTableViewList()) {
			for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
				Word oldWord = normalColumn.getWord();
				if (oldWord != null) {
					Word newWord = wordMap.get(oldWord);
					normalColumn.setWord(newWord);

					copyDictionary.add(normalColumn, false);
				}
			}
		}

		for (ColumnGroup columnGroup : copyDiagramContents.getGroups().getGroupList()) {
			for (NormalColumn normalColumn : columnGroup.getColumns()) {
				Word oldWord = normalColumn.getWord();
				if (oldWord != null) {
					Word newWord = wordMap.get(oldWord);
					normalColumn.setWord(newWord);

					copyDictionary.add(normalColumn, false);
				}
			}
		}

		copyDictionary.setDirty();
	}

	private static void setTablespace(DiagramContents copyDiagramContents,
			DiagramContents originalDiagramContents) {

		Map<Tablespace, Tablespace> tablespaceMap = new HashMap<Tablespace, Tablespace>();
		TablespaceSet copyTablespaceSet = copyDiagramContents
				.getTablespaceSet();

		for (Tablespace tablespace : originalDiagramContents.getTablespaceSet()) {
			Tablespace newTablespace = (Tablespace) tablespace.clone();
			tablespaceMap.put(tablespace, newTablespace);

			copyTablespaceSet.addTablespace(newTablespace, false);
		}
		copyTablespaceSet.setDirty();

		for (TableView tableView : copyDiagramContents.getContents()
				.getTableViewList()) {
			TableViewProperties tableProperties = tableView
					.getTableViewProperties();
			Tablespace oldTablespace = tableProperties.getTableSpace();

			Tablespace newTablespace = tablespaceMap.get(oldTablespace);
			tableProperties.setTableSpace(newTablespace);
		}

		TableViewProperties defaultTableProperties = copyDiagramContents
				.getSettings().getTableViewProperties();
		Tablespace oldDefaultTablespace = defaultTableProperties
				.getTableSpace();

		Tablespace newDefaultTablespace = tablespaceMap
				.get(oldDefaultTablespace);
		defaultTableProperties.setTableSpace(newDefaultTablespace);
	}
}
