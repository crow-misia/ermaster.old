package org.insightech.er.editor.model.diagram_contents.not_element.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class SequenceSet extends AbstractModel implements ObjectListModel,
		Iterable<Sequence> {

	private static final long serialVersionUID = -120487815554383179L;

	public static final String PROPERTY_CHANGE_SEQUENCE_SET = "SequenceSet";

	private List<Sequence> sequenceList;

	public SequenceSet() {
		this.sequenceList = new ArrayList<Sequence>();
	}

	public void addSequence(Sequence sequence, final boolean fire) {
		this.sequenceList.add(sequence);
		Collections.sort(this.sequenceList);

		if (fire) {
			setDirty();
		}
	}

	public int remove(Sequence sequence, final boolean fire) {
		int index = this.sequenceList.indexOf(sequence);
		this.sequenceList.remove(index);
		if (fire) {
			setDirty();
		}

		return index;
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_SEQUENCE_SET, null, null);
	}

	public boolean contains(String name) {
		for (Sequence sequence : sequenceList) {
			if (name.equalsIgnoreCase(sequence.getName())) {
				return true;
			}
		}

		return false;
	}

	public Sequence get(String name) {
		for (Sequence sequence : sequenceList) {
			if (name.equalsIgnoreCase(sequence.getName())) {
				return sequence;
			}
		}

		return null;
	}

	public List<Sequence> getSequenceList() {
		return this.sequenceList;
	}

	public Iterator<Sequence> iterator() {
		return this.sequenceList.iterator();
	}

	@Override
	public SequenceSet clone() {
		SequenceSet sequenceSet = (SequenceSet) super.clone();
		List<Sequence> newSequenceList = new ArrayList<Sequence>(sequenceList.size());

		for (Sequence sequence : sequenceList) {
			Sequence newSequence = (Sequence) sequence.clone();
			newSequenceList.add(newSequence);
		}

		sequenceSet.sequenceList = newSequenceList;

		return sequenceSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString
				.getResourceString("label.object.type.sequence_list");
	}

	public String getObjectType() {
		return "list";
	}
}
