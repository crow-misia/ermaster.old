package org.insightech.er.editor.model.diagram_contents.element.node.note;

import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.Format;

/**
 * ノートのモデル
 * 
 * @author nakajima
 * 
 */
public class Note extends NodeElement implements Comparable<Note> {

	private static final long serialVersionUID = -8810455349879962852L;

	public static final String PROPERTY_CHANGE_NOTE = "note";

	private String text;

	/**
	 * ノートの本文を返却します。
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * ノートの本文を設定します。
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;

		this.firePropertyChange(PROPERTY_CHANGE_NOTE, null, null);
	}

	@Override
	public List<NodeElement> getReferringElementList() {
		List<NodeElement> referringElementList = super.getReferringElementList();

		for (ConnectionElement connectionElement : this.getIncomings()) {
			NodeElement sourceElement = connectionElement.getSource();
			referringElementList.add(sourceElement);
		}

		return referringElementList;
	}

	public String getDescription() {
		return "";
	}

	public int compareTo(Note other) {
		return Format.null2blank(this.text)
				.compareTo(Format.null2blank(other.text));
	}

	public String getName() {
		String name = text;
		if (name == null) {
			name = "";

		} else if (name.length() > 20) {
			name = name.substring(0, 20);
		}

		return name;
	}

	public String getObjectType() {
		return "note";
	}
}
