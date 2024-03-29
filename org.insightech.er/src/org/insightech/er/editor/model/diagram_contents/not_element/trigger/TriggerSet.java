package org.insightech.er.editor.model.diagram_contents.not_element.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class TriggerSet extends AbstractModel implements ObjectListModel,
		Iterable<Trigger> {

	private static final long serialVersionUID = -5072692633977593382L;

	public static final String PROPERTY_CHANGE_TRIGGER_SET = "TriggerSet";

	private List<Trigger> triggerList;

	public TriggerSet() {
		this.triggerList = new ArrayList<Trigger>();
	}

	public void addTrigger(Trigger trigger, final boolean fire) {
		this.triggerList.add(trigger);
		Collections.sort(this.triggerList);

		if (fire) {
			setDirty();
		}
	}

	public int remove(Trigger trigger, final boolean fire) {
		int index = this.triggerList.indexOf(trigger);
		this.triggerList.remove(index);
		if (fire) {
			setDirty();
		}

		return index;
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_TRIGGER_SET, null, null);
	}

	public boolean contains(String name) {
		for (Trigger trigger : triggerList) {
			if (name.equalsIgnoreCase(trigger.getName())) {
				return true;
			}
		}

		return false;
	}

	public Trigger get(String name) {
		for (Trigger trigger : triggerList) {
			if (name.equalsIgnoreCase(trigger.getName())) {
				return trigger;
			}
		}

		return null;
	}

	public List<Trigger> getTriggerList() {
		return this.triggerList;
	}

	public Iterator<Trigger> iterator() {
		return this.triggerList.iterator();
	}

	@Override
	public TriggerSet clone() {
		TriggerSet triggerSet = (TriggerSet) super.clone();
		List<Trigger> newTriggerList = new ArrayList<Trigger>(triggerList.size());

		for (Trigger trigger : triggerList) {
			Trigger newTrigger = (Trigger) trigger.clone();
			newTriggerList.add(newTrigger);
		}

		triggerSet.triggerList = newTriggerList;

		return triggerSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString
				.getResourceString("label.object.type.trigger_list");
	}

	public String getObjectType() {
		return "list";
	}
}
