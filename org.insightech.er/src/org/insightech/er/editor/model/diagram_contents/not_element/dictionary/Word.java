package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.Comparator;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.util.Format;

public abstract class Word extends AbstractModel implements ObjectModel,
		Comparable<Word> {

    private static final long serialVersionUID = 5551978409260110063L;

	private static final Comparator<Word> WITHOUT_NAME_COMPARATOR = new WordWithoutNameComparator();

	public static final Comparator<Word> PHYSICAL_NAME_COMPARATOR = new WordPhysicalNameComparator();

	public static final Comparator<Word> LOGICAL_NAME_COMPARATOR = new WordLogicalNameComparator();

	public abstract String getLogicalName();

	public abstract String getPhysicalName();

	public abstract SqlType getType();

	public abstract void setLogicalName(String logicalName);

	public abstract void setPhysicalName(String physicalName);

	public abstract void setType(SqlType type, TypeData typeData, String database);

	protected abstract void setType(SqlType type);

	public abstract TypeData getTypeData();

	protected abstract void setTypeData(TypeData typeData);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public void copyTo(Word to) {
		to.setPhysicalName(this.getPhysicalName());
		to.setLogicalName(this.getLogicalName());
		to.setDescription(this.getDescription());
		to.setType(this.getType());
		to.setTypeData(new TypeData(this.getTypeData()));
	}

	public final int compareTo(Word o) {
		return PHYSICAL_NAME_COMPARATOR.compare(this, o);
	}

	public final String getName() {
		return this.getLogicalName();
	}

	public final String getObjectType() {
		return "word";
	}

	private UniqueWord uniqueWord;
	public UniqueWord getUniqueWord() {
		if (this.uniqueWord == null) {
			if (this instanceof UniqueWord) {
				this.uniqueWord = (UniqueWord) this;
			} else {
				this.uniqueWord = new UniqueWord(this);
			}
		}
		return this.uniqueWord;
	}

	private static class WordWithoutNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			if (o1.getType() == null) {
				if (o2.getType() != null) {
					return 1;
				}
			} else {
				if (o2.getType() == null) {
					return -1;
				}
				int value = o1.getType().getId().compareTo(o2.getType().getId());
				if (value != 0) {
					return value;
				}
			}

			if (o1.getTypeData() == null) {
				if (o2.getTypeData() != null) {
					return 1;
				}
			} else {
				if (o2.getTypeData() == null) {
					return -1;
				}
				int value = o1.getTypeData().compareTo(o2.getTypeData());
				if (value != 0) {
					return value;
				}
			}

			int value = Format.null2blank(o1.getDescription()).compareTo(
					Format.null2blank(o2.getDescription()));
			if (value != 0) {
				return value;
			}

			return 0;
		}

	}

	private static class WordPhysicalNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int value = 0;

			value = Format.null2blank(o1.getPhysicalName()).toUpperCase().compareTo(
					Format.null2blank(o2.getPhysicalName()).toUpperCase());
			if (value != 0) {
				return value;
			}

			value = Format.null2blank(o1.getLogicalName()).toUpperCase().compareTo(
					Format.null2blank(o2.getLogicalName()).toUpperCase());
			if (value != 0) {
				return value;
			}

			return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
		}

	}

	private static class WordLogicalNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int value = 0;

			value = Format.null2blank(o1.getLogicalName()).toUpperCase().compareTo(
					Format.null2blank(o2.getLogicalName()).toUpperCase());
			if (value != 0) {
				return value;
			}

			value = Format.null2blank(o1.getPhysicalName()).toUpperCase().compareTo(
					Format.null2blank(o2.getPhysicalName()).toUpperCase());
			if (value != 0) {
				return value;
			}

			return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
		}

	}

}
