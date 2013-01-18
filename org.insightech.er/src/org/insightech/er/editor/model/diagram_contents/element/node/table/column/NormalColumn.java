package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.RealWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class NormalColumn extends Column {

	private static final long serialVersionUID = -3177788331933357906L;

	private String id;

	private Word word;

	private String foreignKeyPhysicalName;

	private String foreignKeyLogicalName;

	private String foreignKeyDescription;

	private boolean notNull;

	private boolean primaryKey;

	private boolean uniqueKey;

	private boolean autoIncrement;

	private String defaultValue;

	private String constraint;

	private String uniqueKeyName;

	private Sequence autoIncrementSetting;

	private String characterSet;

	private String collation;

	/** 親が2つある外部キーは、通常ないが、親の大元が同じ参照キーの場合はありえる. */
	private List<NormalColumn> referencedColumnList;

	private List<Relation> relationList;

	public NormalColumn(Word word, boolean notNull, boolean primaryKey,
			boolean uniqueKey, boolean autoIncrement, String defaultValue,
			String constraint, String uniqueKeyName, String characterSet,
			String collation) {
		this.referencedColumnList = new ArrayList<NormalColumn>();
		this.relationList = new ArrayList<Relation>();
		
		this.word = word;

		this.init(notNull, primaryKey, uniqueKey, autoIncrement, defaultValue,
				constraint, uniqueKeyName, characterSet, collation);
	}

	protected NormalColumn(NormalColumn from) {
		this.referencedColumnList = new ArrayList<NormalColumn>(from.referencedColumnList);
		this.relationList = new ArrayList<Relation>(from.relationList);

		this.foreignKeyPhysicalName = from.foreignKeyPhysicalName;
		this.foreignKeyLogicalName = from.foreignKeyLogicalName;
		this.foreignKeyDescription = from.foreignKeyDescription;

		this.init(from.notNull, from.primaryKey, from.uniqueKey,
				from.autoIncrement, from.defaultValue, from.constraint,
				from.uniqueKeyName, from.characterSet, from.collation);

		this.word = from.word;

		if (from.autoIncrementSetting == null) {
			from.autoIncrementSetting = null;
		} else {
			this.autoIncrementSetting = (Sequence) from.autoIncrementSetting
					.clone();
		}
	}

	/**
	 * 外部キーを作成します
	 * 
	 * @param from
	 * @param referencedColumn
	 * @param relation
	 * @param primaryKey
	 *            主キーかどうか
	 */
	public NormalColumn(NormalColumn from, NormalColumn referencedColumn,
			Relation relation, boolean primaryKey) {
		this.referencedColumnList = new ArrayList<NormalColumn>();
		this.relationList = new ArrayList<Relation>();
		this.referencedColumnList.add(referencedColumn);
		this.relationList.add(relation);

		this.word = null;

		copyData(from, this);

		this.primaryKey = primaryKey;
		this.autoIncrement = false;
	}

	protected void init(boolean notNull, boolean primaryKey, boolean uniqueKey,
			boolean autoIncrement, String defaultValue, String constraint,
			String uniqueKeyName, String characterSet, String collation) {

		this.notNull = notNull;
		this.primaryKey = primaryKey;
		this.uniqueKey = uniqueKey;
		this.autoIncrement = autoIncrement;

		this.defaultValue = defaultValue;
		this.constraint = constraint;

		this.uniqueKeyName = uniqueKeyName;

		this.characterSet = characterSet;
		this.collation = collation;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = StringUtils.isNumeric(id) ? id : null;
	}

	public static void setId(final Set<String> check, final NormalColumn column) {
		String id = column.id;
		while (id == null) {
			id = Integer.toString(RandomUtils.nextInt());
			if (check.add(id)) {
				column.id = id;
				break;
			}
			id = null;
		}
	}

	public NormalColumn getFirstReferencedColumn() {
		if (this.referencedColumnList.isEmpty()) {
			return null;
		}
		return this.referencedColumnList.get(0);
	}

	public NormalColumn getReferencedColumn(Relation relation) {
		for (NormalColumn referencedColumn : this.referencedColumnList) {
			if (referencedColumn.getColumnHolder() == relation
					.getSourceTableView()) {
				return referencedColumn;
			}
		}
		return null;
	}

	public String getLogicalName() {
		final NormalColumn first = this.getFirstReferencedColumn();
		if (first == null) {
			return this.word.getLogicalName();
		}
		if (StringUtils.isEmpty(this.foreignKeyLogicalName)) {
			return first.getLogicalName();
		}
		return this.foreignKeyLogicalName;
	}

	public String getPhysicalName() {
		final NormalColumn first = this.getFirstReferencedColumn();
		if (first == null) {
			return this.word.getPhysicalName();
		}
		if (StringUtils.isEmpty(this.foreignKeyPhysicalName)) {
			return first.getPhysicalName();
		}
		return this.foreignKeyPhysicalName;
	}

	public String getDescription() {
		final NormalColumn first = this.getFirstReferencedColumn();
		if (first == null) {
			return this.word.getDescription();
		}
		if (StringUtils.isEmpty(this.foreignKeyDescription)) {
			return first.getDescription();
		}
		return this.foreignKeyDescription;
	}

	public String getForeignKeyLogicalName() {
		return this.foreignKeyLogicalName;
	}

	public String getForeignKeyPhysicalName() {
		return foreignKeyPhysicalName;
	}

	public String getForeignKeyDescription() {
		return foreignKeyDescription;
	}

	public SqlType getType() {
		final NormalColumn first = this.getFirstReferencedColumn();
		if (first == null) {
			return word.getType();
		}
		SqlType type = this.getFirstReferencedColumn().getType();

		if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(type)) {
			return SqlType.valueOfId(SqlType.SQL_TYPE_ID_INTEGER);
		}
		if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL).equals(type)) {
			return SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_INT);
		}

		return type;
	}

	public TypeData getTypeData() {
		final NormalColumn first = this.getFirstReferencedColumn();
		if (first == null) {
			return this.word.getTypeData();
		}
		return first.getTypeData();
	}

	public boolean isNotNull() {
		return this.notNull;
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public boolean isUniqueKey() {
		return this.uniqueKey;
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String getConstraint() {
		return this.constraint;
	}

	public String getUniqueKeyName() {
		return uniqueKeyName;
	}

	
	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.getLogicalName();
	}

	public NormalColumn getRootReferencedColumn() {
		NormalColumn root = this;
		NormalColumn next;

		while ((next = root.getFirstReferencedColumn()) != null) {
			root = next;
		}

		return root;
	}

	public List<Relation> getOutgoingRelationList() {
		List<Relation> outgoingRelationList = new ArrayList<Relation>();

		ColumnHolder columnHolder = this.getColumnHolder();

		if (columnHolder instanceof ERTable) {
			ERTable table = (ERTable) columnHolder;

			for (Relation relation : table.getOutgoingRelations()) {
				if (relation.isReferenceForPK()) {
					if (this.isPrimaryKey()) {
						outgoingRelationList.add(relation);
					}
				} else {
					if (this == relation.getReferencedColumn()) {
						outgoingRelationList.add(relation);
					}
				}
			}
		}

		return outgoingRelationList;
	}

	public List<NormalColumn> getForeignKeyList() {
		List<NormalColumn> foreignKeyList = new ArrayList<NormalColumn>();

		ColumnHolder columnHolder = this.getColumnHolder();

		if (columnHolder instanceof ERTable) {
			ERTable table = (ERTable) columnHolder;

			for (Relation relation : table.getOutgoingRelations()) {
				boolean found = false;
				for (final NormalColumn column : relation.getTargetTableView()
						.getNormalColumns()) {
					if (column.isForeignKey()) {
						for (NormalColumn referencedColumn : column.referencedColumnList) {
							if (referencedColumn == this) {
								foreignKeyList.add(column);
								found = true;
								break;
							}
						}

						if (found) {
							break;
						}
					}
				}
			}
		}

		return foreignKeyList;
	}

	public List<Relation> getRelationList() {
		return relationList;
	}

	public void addReference(NormalColumn referencedColumn, Relation relation) {
		this.foreignKeyDescription = this.getDescription();
		this.foreignKeyLogicalName = this.getLogicalName();
		this.foreignKeyPhysicalName = this.getPhysicalName();

		this.referencedColumnList.add(referencedColumn);

		this.relationList.add(relation);

		copyData(this, this);

		this.word = null;
	}

	public void renewRelationList() {
		this.relationList = new ArrayList<Relation>(this.relationList);
	}

	public void removeReference(Relation relation) {
		this.relationList.remove(relation);

		if (relationList.isEmpty()) {
			NormalColumn temp = this.getFirstReferencedColumn();
			while (temp.isForeignKey()) {
				temp = temp.getFirstReferencedColumn();
			}

			this.word = temp.getWord();
			if (!this.getPhysicalName().equals(this.word.getPhysicalName())
					|| !this.getLogicalName().equals(this.word.getLogicalName())
					|| !this.getDescription().equals(this.word.getDescription())) {
				this.word = new RealWord(this.word);

				this.word.setPhysicalName(this.getPhysicalName());
				this.word.setLogicalName(this.getLogicalName());
				this.word.setDescription(this.getDescription());
			}

			this.foreignKeyDescription = null;
			this.foreignKeyLogicalName = null;
			this.foreignKeyPhysicalName = null;

			this.referencedColumnList.clear();

			copyData(this, this);

		} else {
			for (NormalColumn referencedColumn : this.referencedColumnList) {
				if (referencedColumn.getColumnHolder() == relation
						.getSourceTableView()) {
					this.referencedColumnList.remove(referencedColumn);
					break;
				}
			}
		}
	}

	public boolean isForeignKey() {
		return !this.relationList.isEmpty();
	}

	public boolean isRefered() {
		if (!(this.getColumnHolder() instanceof ERTable)) {
			return false;
		}

		boolean isRefered = false;

		ERTable table = (ERTable) this.getColumnHolder();

		for (Relation relation : table.getOutgoingRelations()) {
			if (!relation.isReferenceForPK()) {
				for (NormalColumn foreignKeyColumn : relation
						.getForeignKeyColumns()) {

					for (NormalColumn referencedColumn : foreignKeyColumn.referencedColumnList) {
						if (referencedColumn == this) {
							isRefered = true;
							break;
						}
					}

					if (isRefered) {
						break;
					}
				}

				if (isRefered) {
					break;
				}

			}
		}

		return isRefered;
	}

	public boolean isReferedStrictly() {
		if (!(this.getColumnHolder() instanceof ERTable)) {
			return false;
		}

		boolean isRefered = false;

		ERTable table = (ERTable) this.getColumnHolder();

		for (Relation relation : table.getOutgoingRelations()) {
			if (!relation.isReferenceForPK()) {
				for (NormalColumn foreignKeyColumn : relation
						.getForeignKeyColumns()) {

					for (NormalColumn referencedColumn : foreignKeyColumn.referencedColumnList) {
						if (referencedColumn == this) {
							isRefered = true;
							break;
						}
					}

					if (isRefered) {
						break;
					}
				}

				if (isRefered) {
					break;
				}

			} else {
				if (this.isPrimaryKey()) {
					isRefered = true;
					break;
				}
			}
		}

		return isRefered;
	}

	public Word getWord() {
		return this.word;
	}

	public boolean isFullTextIndexable() {
		return this.getType().isFullTextIndexable();
	}

	public static void copyData(NormalColumn from, NormalColumn to) {
		to.init(from.isNotNull(), from.isPrimaryKey(), from.isUniqueKey(), from
				.isAutoIncrement(), from.getDefaultValue(), from
				.getConstraint(), from.uniqueKeyName, from.characterSet,
				from.collation);

		if (from.autoIncrementSetting == null) {
			to.autoIncrementSetting = null;
		} else {
			to.autoIncrementSetting = (Sequence) from.autoIncrementSetting.clone();
		}

		if (to.isForeignKey()) {
			NormalColumn firstReferencedColumn = to.getFirstReferencedColumn();

			if (firstReferencedColumn.getPhysicalName() == null) {
				to.foreignKeyPhysicalName = from.getPhysicalName();

			} else {
				if (from.foreignKeyPhysicalName != null
						&& !firstReferencedColumn.getPhysicalName().equals(
								from.foreignKeyPhysicalName)) {
					to.foreignKeyPhysicalName = from.foreignKeyPhysicalName;

				} else if (!firstReferencedColumn.getPhysicalName().equals(
						from.getPhysicalName())) {
					to.foreignKeyPhysicalName = from.getPhysicalName();

				} else {
					to.foreignKeyPhysicalName = null;
				}
			}

			if (firstReferencedColumn.getLogicalName() == null) {
				to.foreignKeyLogicalName = from.getLogicalName();

			} else {
				if (from.foreignKeyLogicalName != null
						&& !firstReferencedColumn.getLogicalName().equals(
								from.foreignKeyLogicalName)) {
					to.foreignKeyLogicalName = from.foreignKeyLogicalName;

				} else if (!firstReferencedColumn.getLogicalName().equals(
						from.getLogicalName())) {
					to.foreignKeyLogicalName = from.getLogicalName();

				} else {
					to.foreignKeyLogicalName = null;

				}
			}

			if (firstReferencedColumn.getDescription() == null) {
				to.foreignKeyDescription = from.getDescription();

			} else {
				if (from.foreignKeyDescription != null
						&& !firstReferencedColumn.getDescription().equals(
								from.foreignKeyDescription)) {
					to.foreignKeyDescription = from.foreignKeyDescription;

				} else if (!firstReferencedColumn.getDescription().equals(
						from.getDescription())) {
					to.foreignKeyDescription = from.getDescription();

				} else {
					to.foreignKeyDescription = null;
				}
			}

		} else {
			from.word.copyTo(to.word);
		}

		to.setColumnHolder(from.getColumnHolder());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(", physicalName:" + this.getPhysicalName());
		sb.append(", logicalName:" + this.getLogicalName());

		return sb.toString();
	}

	public void setForeignKeyPhysicalName(String physicalName) {
		this.foreignKeyPhysicalName = physicalName;
	}

	public void setForeignKeyLogicalName(String logicalName) {
		this.foreignKeyLogicalName = logicalName;
	}

	public void setForeignKeyDescription(String description) {
		this.foreignKeyDescription = description;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public void setUniqueKeyName(String uniqueKeyName) {
		this.uniqueKeyName = uniqueKeyName;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public void setUniqueKey(boolean uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public Sequence getAutoIncrementSetting() {
		return autoIncrementSetting;
	}

	public void setAutoIncrementSetting(Sequence autoIncrementSetting) {
		this.autoIncrementSetting = autoIncrementSetting;
	}

	public void copyForeikeyData(NormalColumn to) {
		to.setConstraint(this.getConstraint());
		to.setForeignKeyDescription(this.getForeignKeyDescription());
		to.setForeignKeyLogicalName(this.getForeignKeyLogicalName());
		to.setForeignKeyPhysicalName(this.getForeignKeyPhysicalName());
		to.setNotNull(this.isNotNull());
		to.setUniqueKey(this.isUniqueKey());
		to.setPrimaryKey(this.isPrimaryKey());
		to.setAutoIncrement(this.isAutoIncrement());
		to.setCharacterSet(this.getCharacterSet());
		to.setCollation(this.getCollation());
	}

	public List<NormalColumn> getReferencedColumnList() {
		return referencedColumnList;
	}

	@Override
	public NormalColumn clone() {
		NormalColumn clone = (NormalColumn) super.clone();

		clone.relationList = new ArrayList<Relation>(this.relationList);
		clone.referencedColumnList = new ArrayList<NormalColumn>(
				this.referencedColumnList);

		return clone;
	}

}
