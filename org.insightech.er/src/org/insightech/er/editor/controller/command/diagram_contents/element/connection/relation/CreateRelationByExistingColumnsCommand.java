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

public final class CreateRelationByExistingColumnsCommand extends
		AbstractCreateRelationCommand {

	private Relation relation;

	private List<NormalColumn> referencedColumnList;

	private List<NormalColumn> foreignKeyColumnList;

	private final List<Word> wordList;

	public CreateRelationByExistingColumnsCommand() {
		super();
		this.wordList = new ArrayList<Word>();
	}

	@Override
	protected void doExecute() {
		ERTable sourceTable = this.getSourceModel();
		TableView targetTable = this.getTargetModel();

		this.relation.setSource(sourceTable, false);
		this.relation.setTargetWithoutForeignKey(targetTable, true);

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

	@Override
	protected void doUndo() {
		ERTable sourceTable = this.getSourceModel();
		TableView targetTable = this.getTargetModel();

		this.relation.setSource(null, false);
		this.relation.setTargetWithoutForeignKey(null, true);

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

		ERTable sourceTable = this.getSourceModel();
		TableView targetTable = this.getTargetModel();

		Map<NormalColumn, List<NormalColumn>> referencedMap = new HashMap<NormalColumn, List<NormalColumn>>();
		Map<Relation, Set<NormalColumn>> foreignKeySetMap = new HashMap<Relation, Set<NormalColumn>>();

		for (final NormalColumn normalColumn : targetTable.getNormalColumns()) {
			NormalColumn rootReferencedColumn = normalColumn.getRootReferencedColumn();

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
