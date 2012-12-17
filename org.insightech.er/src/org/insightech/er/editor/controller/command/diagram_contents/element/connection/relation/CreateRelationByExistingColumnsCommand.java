package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.element.relation.RelationByExistingColumnsDialog;

public class CreateRelationByExistingColumnsCommand extends
		AbstractCreateRelationCommand {

	private Relation relation;

	private List<NormalColumn> referencedColumnList;

	private List<NormalColumn> foreignKeyColumnList;

	private List<Word> wordList;

	public CreateRelationByExistingColumnsCommand() {
		super();
		this.wordList = new ArrayList<Word>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		ERTable sourceTable = (ERTable) this.source.getModel();
		TableView targetTable = (TableView) this.target.getModel();

		this.relation.setSource(sourceTable);
		this.relation.setTargetWithoutForeignKey(targetTable);

		final Dictionary dictionary = sourceTable.getDiagram().getDiagramContents().getDictionary();
		for (int i = 0, n = foreignKeyColumnList.size(); i < n; i++) {
			NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);
			this.wordList.add(foreignKeyColumn.getWord());

			dictionary.remove(foreignKeyColumn, false);

			foreignKeyColumn.addReference(referencedColumnList.get(i),
					this.relation);
			foreignKeyColumn.setWord(null);
		}

		targetTable.setDirty();
		dictionary.setDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		ERTable sourceTable = (ERTable) source.getModel();
		ERTable targetTable = (ERTable) target.getModel();

		this.relation.setSource(null);
		this.relation.setTargetWithoutForeignKey(null);

		final Dictionary dictionary = sourceTable.getDiagram().getDiagramContents().getDictionary();
		for (int i = 0, n = foreignKeyColumnList.size(); i < n; i++) {
			NormalColumn foreignKeyColumn = foreignKeyColumnList.get(i);
			foreignKeyColumn.removeReference(this.relation);
			foreignKeyColumn.setWord(wordList.get(i));

			dictionary.add(foreignKeyColumn, false);
		}

		targetTable.setDirty();
		dictionary.setDirty();
	}

	public boolean selectColumns() {
		if (this.target == null) {
			return false;
		}

		ERTable sourceTable = (ERTable) this.source.getModel();
		TableView targetTable = (TableView) this.target.getModel();

		Map<NormalColumn, List<NormalColumn>> referencedMap = new HashMap<NormalColumn, List<NormalColumn>>();
		Map<Relation, Set<NormalColumn>> foreignKeySetMap = new HashMap<Relation, Set<NormalColumn>>();

		for (NormalColumn normalColumn : targetTable.getNormalColumns()) {
			NormalColumn rootReferencedColumn = normalColumn.getRootReferencedColumn();
			if (rootReferencedColumn != null) {
				List<NormalColumn> foreignKeyList = referencedMap
						.get(rootReferencedColumn);

				if (foreignKeyList == null) {
					foreignKeyList = new ArrayList<NormalColumn>();
					referencedMap.put(rootReferencedColumn, foreignKeyList);
				}

				foreignKeyList.add(normalColumn);

				for (Relation relation : normalColumn.getRelationList()) {
					Set<NormalColumn> foreignKeySet = foreignKeySetMap
							.get(relation);
					if (foreignKeySet == null) {
						foreignKeySet = new HashSet<NormalColumn>();
						foreignKeySetMap.put(relation, foreignKeySet);
					}

					foreignKeySet.add(normalColumn);
				}
			}
		}

		List<NormalColumn> candidateForeignKeyColumns = new ArrayList<NormalColumn>();

		for (NormalColumn column : targetTable.getNormalColumns()) {
			if (!column.isForeignKey()) {
				candidateForeignKeyColumns.add(column);
			}
		}

		if (candidateForeignKeyColumns.isEmpty()) {
			Activator
					.showErrorDialog("error.no.candidate.of.foreign.key.exist");
			return false;
		}

		RelationByExistingColumnsDialog dialog = new RelationByExistingColumnsDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				sourceTable, candidateForeignKeyColumns, referencedMap,
				foreignKeySetMap);

		if (dialog.open() == IDialogConstants.OK_ID) {
			this.relation = new Relation(dialog.isReferenceForPK(), dialog
					.getReferencedComplexUniqueKey(), dialog
					.getReferencedColumn());
			this.referencedColumnList = dialog.getReferencedColumnList();
			this.foreignKeyColumnList = dialog.getForeignKeyColumnList();

		} else {
			return false;
		}

		return true;
	}
}
