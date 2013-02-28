package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
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

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = StringUtils.isNumeric(id) ? id : null;
	}

	public static void setId(final Map<UniqueWord, String> check, final UniqueWord word) {
		// 既にIDの割当てがある場合、再利用する
		String id = check.get(word);
		if (id == null && word.id != null) {
			check.put(word, word.id);
			return;
		}
		if (id != null) {
			word.id = id;
			return;
		}

		while (id == null) {
			id = Integer.toString(RandomUtils.nextInt());
			for (final Map.Entry<UniqueWord, String> entry : check.entrySet()) {
				if (StringUtils.equalsIgnoreCase(id, entry.getValue())) {
					id = null;
					break;
				}
			}
		}
		check.put(word, id);
		word.id = id;
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

	public String getDescription() {
		return word.getDescription();
	}

	@Override
	public void setDescription(String description) {
		word.setDescription(description);
	}

	@Override
	public void copyTo(Word to) {
		super.copyTo(to);
		if (to instanceof UniqueWord) {
			((UniqueWord) to).id = id;
		}
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
		return PRIME * result
				+ ((this.getType() == null) ? 0 : this.getType().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Word)) {
			return false;
		}
		final Word other = (Word) obj;
		return equals(this, other);
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
