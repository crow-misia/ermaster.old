package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Format;

public class SequenceHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public SequenceHtmlReportPageGenerator(Map<Object, String> idMap) {
		super(idMap);
	}

	public String getType() {
		return "sequence";
	}

	@SuppressWarnings("unchecked")
	public List<Object> getObjectList(ERDiagram diagram) {
		return (List) diagram.getDiagramContents().getSequenceSet()
				.getSequenceList();
	}

	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object) {
		Sequence sequence = (Sequence) object;

		return new String[] { Format.null2blank(sequence.getDescription()),
				getValue(sequence.getIncrement()),
				getValue(sequence.getMinValue()),
				getValue(sequence.getMaxValue()),
				getValue(sequence.getStart()),
				getValue(sequence.getCache()),
				String.valueOf(sequence.isCycle()).toUpperCase() };
	}

	private static String getValue(Number value) {
		if (value == null) {
			return "";
		}
		return String.valueOf(value);
	}

	public String getObjectName(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getName();
	}

	@Override
	public String getObjectSummary(Object object) {
		Sequence sequence = (Sequence) object;

		return sequence.getDescription();
	}
}
