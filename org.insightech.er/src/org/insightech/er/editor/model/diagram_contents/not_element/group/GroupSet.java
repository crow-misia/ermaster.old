package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;

public class GroupSet extends AbstractModel {

	private static final long serialVersionUID = 6192280105150073360L;

	public static final String PROPERTY_CHANGE_GROUP_SET = "group_set";

	private static final Comparator<ColumnGroup> GROUP_ID_COMPARATOR = new Comparator<ColumnGroup>() {
		public int compare(ColumnGroup o1, ColumnGroup o2) {
			if (o1.getId() == null) {
				return o2.getId() == null ? 0 : -1;
			}
			if (o2.getId() == null) {
				return -1;
			}
			return o1.getId().compareTo(o2.getId());
		}
	};

	private String database;

	private final List<ColumnGroup> groups;

	public GroupSet() {
		this.groups = new ArrayList<ColumnGroup>();
	}

	public void add(ColumnGroup group, final boolean fire) {
		this.groups.add(group);
		Collections.sort(this.groups);

		if (fire) {
			setDirty();
		}
	}

	public void remove(ColumnGroup group, final boolean fire) {
		this.groups.remove(group);

		if (fire) {
			setDirty();
		}
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
	}

	public List<ColumnGroup> getGroupList() {
		return this.groups;
	}

	public List<ColumnGroup> getGroupListOrderId() {
		final List<ColumnGroup> retval = new ArrayList<ColumnGroup>(this.groups);
		Collections.sort(retval, GROUP_ID_COMPARATOR);
		return retval;
	}

	public void clear() {
		this.groups.clear();
	}

	public boolean contains(ColumnGroup group) {
		return this.groups.contains(group);
	}

	public ColumnGroup get(int index) {
		return this.groups.get(index);
	}

	public int indexOf(ColumnGroup group) {
		return this.groups.indexOf(group);
	}

	/**
	 * database を取得します.
	 * 
	 * @return database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * database を設定します.
	 * 
	 * @param database
	 *            database
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
}
