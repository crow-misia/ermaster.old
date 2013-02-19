package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Format;

public class TableHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public TableHtmlReportPageGenerator(Map<Object, String> idMap) {
		super(idMap);
	}

	public String getType() {
		return "table";
	}

	@SuppressWarnings("unchecked")
	public List<Object> getObjectList(ERDiagram diagram) {
		return (List) diagram.getDiagramContents().getContents().getTableSet()
				.getList();
	}

	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException {
		ERTable table = (ERTable) object;

		String description = table.getDescription();

		List<NormalColumn> normalColumnList = table.getExpandedColumns();

		String attributeTable = this.generateAttributeTable(diagram,
				normalColumnList);

		List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();
		for (NormalColumn normalColumn : normalColumnList) {
			if (normalColumn.isForeignKey()) {
				foreignKeyList.add(normalColumn);
			}
		}

		String foreignKeyTable = this.generateForeignKeyTable(foreignKeyList);

		List<NormalColumn> referencedKeyList = new ArrayList<NormalColumn>();
		for (Relation relation : table.getOutgoingRelations()) {
			referencedKeyList.addAll(relation.getForeignKeyColumns());
		}

		String referencedKeyTable = this
				.generateReferenceKeyTable(referencedKeyList);

		String complexUniqueKeyMatrix = generateComplexUniqueKeyMatrix(
				table.getComplexUniqueKeyList(), normalColumnList);

		List<Index> indexList = table.getIndexes();

		String indexSummaryTable = this.generateIndexSummaryTable(indexList);

		String indexMatrix = generateIndexMatrix(indexList,
				normalColumnList);

		String attributeDetailTable = this.generateAttributeDetailTable(
				diagram, normalColumnList);

		return new String[] { Format.null2blank(description),
				Format.null2blank(table.getPhysicalName()),
				Format.null2blank(table.getConstraint()), attributeTable,
				foreignKeyTable, referencedKeyTable, complexUniqueKeyMatrix,
				indexSummaryTable, indexMatrix, attributeDetailTable };
	}

	public String getObjectName(Object object) {
		ERTable table = (ERTable) object;

		return table.getName();
	}

	@Override
	public String getObjectSummary(Object object) {
		ERTable table = (ERTable) object;

		return table.getDescription();
	}
}
