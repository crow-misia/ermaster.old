package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class TableListSheetGenerator extends AbstractSheetGenerator {

	protected static final String[] FIND_KEYWORDS_OF_TABLE = {
		KEYWORD_LOGICAL_TABLE_NAME, KEYWORD_PHYSICAL_TABLE_NAME };

	private ColumnTemplate columnTemplate;

	private void clear() {
		this.columnTemplate = null;
	}

	public void setAllColumnsData(HSSFWorkbook workbook, HSSFSheet sheet,
			ERDiagram diagram) {
		this.clear();

		CellLocation cellLocation = POIUtils.findCell(sheet,
				FIND_KEYWORDS_OF_TABLE);

		if (cellLocation != null) {
			int rowNum = cellLocation.r;
			HSSFRow templateRow = sheet.getRow(rowNum);

			if (this.columnTemplate == null) {
				this.columnTemplate = loadColumnTemplate(workbook, sheet,
						cellLocation);
			}

			int order = 1;
			final String database = diagram.getDatabase();

			for (ERTable table : diagram.getDiagramContents().getContents()
					.getTableSet()) {

				if (diagram.getCurrentCategory() != null
						&& !diagram.getCurrentCategory().contains(table)) {
					continue;
				}

				HSSFRow row = POIUtils.insertRow(sheet, rowNum++);
				setColumnData(this.keywordsValueMap, columnTemplate,
						row, table, null, null, database, order);
				order++;
			}

			setCellStyle(columnTemplate, sheet, cellLocation.r, rowNum
					- cellLocation.r, templateRow.getFirstCellNum());
		}
	}

	@Override
	protected String getKeywordValue(
			final Map<String, String> keywordsValueMap,
			final TableView tableView, final NormalColumn normalColumn, final Word word,
			final String database, final String keyword) {
		Object obj = null;

		if (KEYWORD_LOGICAL_TABLE_NAME.equals(keyword)) {
			obj = tableView.getLogicalName();

		} else if (KEYWORD_PHYSICAL_TABLE_NAME.equals(keyword)) {
			obj = tableView.getPhysicalName();

		} else if (KEYWORD_TABLE_DESCRIPTION.equals(keyword)) {
			obj = tableView.getDescription();
		}
		
		return getValue(keywordsValueMap, keyword, obj);
	}

	public String getSheetName() {
		String name = this.keywordsValueMap.get(KEYWORD_SHEET_NAME);

		if (name == null) {
			name = "List of Tables";
		}

		return name;
	}

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {
		String name = this.getSheetName();
		HSSFSheet newSheet = createNewSheet(workbook, sheetNo, name,
				sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), new ColumnSet());

		this.setAllColumnsData(workbook, newSheet, diagram);
		monitor.worked(1);
	}

	@Override
	public String getTemplateSheetName() {
		return "tablelist_template";
	}

	@Override
	public int getKeywordsColumnNo() {
		return 40;
	}

	@Override
	public String[] getKeywords() {
		return new String[] {
				KEYWORD_LOGICAL_TABLE_NAME, KEYWORD_PHYSICAL_TABLE_NAME, KEYWORD_TABLE_DESCRIPTION,
				KEYWORD_ORDER,
				KEYWORD_SHEET_NAME };
	}

	@Override
	public int count(ERDiagram diagram) {
		return 1;
	}

}
