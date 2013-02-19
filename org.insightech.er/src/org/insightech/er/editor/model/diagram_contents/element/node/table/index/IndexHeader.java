package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;

public final class IndexHeader extends AbstractModel implements ObjectModel {

	private static final long serialVersionUID = -1071894911784864112L;

	public static final IndexHeader INSTANCE = new IndexHeader();

	private IndexHeader() {
	}

	public String getName() {
		return null;
	}

	@Override
	public IndexHeader clone() {
		return INSTANCE;
	}

	public String getDescription() {
		return null;
	}
	
	public String getObjectType() {
		return "indexHeader";
	}

}
