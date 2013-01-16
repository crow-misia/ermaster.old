package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.Comparator;

import org.insightech.er.db.sqltype.SqlType;

public class UniqueWord extends Word {

	private static final long serialVersionUID = 6795589487175949331L;
	
	public static final Comparator<UniqueWord> WORD_ID_COMPARATOR = new WordIdComparator();

	private final Word word;

	private String id;

	protected UniqueWord(Word word) {
		this.word = word;
	}

	public Word getWord() {
		return word;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String getLogicalName() {
		return word.getLogicalName();
	}

	@Override
	public String getPhysicalName() {
		return word.getPhysicalName();
	}

	@Override
	public SqlType getType() {
		return word.getType();
	}

	@Override
	public void setLogicalName(String logicalName) {
		word.setLogicalName(logicalName);
	}

	@Override
	public void setPhysicalName(String physicalName) {
		word.setPhysicalName(physicalName);
	}

	@Override
	public void setType(SqlType type, TypeData typeData, String database) {
		word.setType(type, typeData, database);
	}

	@Override
	protected void setType(SqlType type) {
		word.setType(type);
	}

	@Override
	public TypeData getTypeData() {
		return word.getTypeData();
	}

	@Override
	protected void setTypeData(TypeData typeData) {
		word.setTypeData(typeData);
	}

	@Override
	public String getDescription() {
		return word.getDescription();
	}

	@Override
	public void setDescription(String description) {
		word.setDescription(description);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME
				* result
				+ ((this.getTypeData() == null) ? 0 : this.getTypeData()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getDescription() == null) ? 0 : this.getDescription()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getLogicalName() == null) ? 0 : this.getLogicalName()
						.hashCode());
		result = PRIME
				* result
				+ ((this.getPhysicalName() == null) ? 0 : this
						.getPhysicalName().hashCode());
		result = PRIME * result
				+ ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Word other = (Word) obj;
		if (getTypeData() == null) {
			if (other.getTypeData() != null)
				return false;
		} else if (!getTypeData().equals(other.getTypeData()))
			return false;
		if (getDescription() == null) {
			if (other.getDescription() != null)
				return false;
		} else if (!getDescription().equals(other.getDescription()))
			return false;
		if (getLogicalName() == null) {
			if (other.getLogicalName() != null)
				return false;
		} else if (!getLogicalName().equals(other.getLogicalName()))
			return false;
		if (getPhysicalName() == null) {
			if (other.getPhysicalName() != null)
				return false;
		} else if (!getPhysicalName().equals(other.getPhysicalName()))
			return false;
		if (this.getType() == null) {
			if (other.getType() != null)
				return false;
		} else if (!this.getType().equals(other.getType()))
			return false;
		return true;
	}

	private static class WordIdComparator implements Comparator<UniqueWord> {

		public int compare(UniqueWord o1, UniqueWord o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			if (o1.id == null) {
				if (o2.id != null) {
					return 1;
				}
			} else {
				if (o2.id == null) {
					return -1;
				}
				int value = o1.id.compareTo(o2.id);
				if (value != 0) {
					return value;
				}
			}

			return 0;
		}

	}
}
