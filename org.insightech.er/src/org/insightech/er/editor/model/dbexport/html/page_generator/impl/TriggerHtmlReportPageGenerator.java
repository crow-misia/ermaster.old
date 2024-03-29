package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.Format;

public class TriggerHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public TriggerHtmlReportPageGenerator(Map<Object, String> idMap) {
		super(idMap);
	}

	public String getType() {
		return "trigger";
	}

	@SuppressWarnings("unchecked")
	public List<Object> getObjectList(ERDiagram diagram) {
		return (List) diagram.getDiagramContents().getTriggerSet()
				.getTriggerList();
	}

	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object) {
		Trigger trigger = (Trigger) object;

		String description = Format.null2blank(trigger.getDescription());
		String sql = Format.null2blank(trigger.getSql());

		return new String[] { description, sql };
	}

	public String getObjectName(Object object) {
		Trigger trigger = (Trigger) object;

		return trigger.getName();
	}

	@Override
	public String getObjectSummary(Object object) {
		Trigger trigger = (Trigger) object;

		return trigger.getDescription();
	}
}
