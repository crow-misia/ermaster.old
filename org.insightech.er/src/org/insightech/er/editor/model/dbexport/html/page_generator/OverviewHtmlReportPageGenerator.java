package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class OverviewHtmlReportPageGenerator {

	private Map<Object, String> idMap;

	public OverviewHtmlReportPageGenerator(Map<Object, String> idMap) {
		this.idMap = idMap;
	}

	public String getObjectId(Object object) {
		String id = idMap.get(object);

		if (id == null) {
			id = String.valueOf(idMap.size());
			this.idMap.put(object, id);
		}

		return id;
	}

	public static String generateFrame(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_template.html");

		Object[] args = { generateFrameTable(htmlReportPageGeneratorList) };
		return MessageFormat.format(template, args);
	}

	private static String generateFrameTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-frame_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateSummary(String imageSrc,
			Map<TableView, Location> tableLocationMap,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_template.html");

		Object[] args = { this.generateImage(imageSrc, tableLocationMap),
				generateSummaryTable(htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private String generateImage(String imageSrc,
			Map<TableView, Location> tableLocationMap) throws IOException {
		if (imageSrc == null) {
			return "";
		}

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_image_template.html");

		Object[] args = { imageSrc, this.generateImageMap(tableLocationMap) };

		return MessageFormat.format(template, args);
	}

	private String generateImageMap(Map<TableView, Location> tableLocationMap)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		if (tableLocationMap != null) {
			String template = ExportToHtmlManager
					.getTemplate("overview/overview-summary_image_map_template.html");

			for (Map.Entry<TableView, Location> entry : tableLocationMap
					.entrySet()) {
				Location location = entry.getValue();

				Object[] args = { String.valueOf(location.x),
						String.valueOf(location.y),
						String.valueOf(location.x + location.width),
						String.valueOf(location.y + location.height),
						entry.getKey().getObjectType(),
						this.getObjectId(entry.getKey()) };
				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	private static String generateSummaryTable(
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("overview/overview-summary_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			Object[] args = { pageGenerator.getType(),
					pageGenerator.getPageTitle() };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public static String generateAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("allclasses_template.html");

		Object[] args = { generateAllClassesTable(diagram,
				htmlReportPageGeneratorList) };

		return MessageFormat.format(template, args);
	}

	private static String generateAllClassesTable(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("allclasses_row_template.html");

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {

			for (Object object : pageGenerator.getObjectList(diagram)) {
				Object[] args = {
						pageGenerator.getType() + "/"
								+ pageGenerator.getObjectId(object) + ".html",
						pageGenerator.getObjectName(object) };
				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	public static int countAllClasses(ERDiagram diagram,
			List<HtmlReportPageGenerator> htmlReportPageGeneratorList) {
		int count = 0;

		for (HtmlReportPageGenerator pageGenerator : htmlReportPageGeneratorList) {
			count += pageGenerator.getObjectList(diagram).size();
		}

		return count;
	}
}
