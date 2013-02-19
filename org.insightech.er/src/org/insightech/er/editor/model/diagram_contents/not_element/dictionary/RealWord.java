package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import org.insightech.er.db.sqltype.SqlType;

public class RealWord extends Word {

    private static final long serialVersionUID = -1983348253197344183L;

	private String physicalName;

	private String logicalName;

	private SqlType type;

	private TypeData typeData;

	private String description;

	public RealWord(String physicalName, String logicalName, SqlType type,
			TypeData typeData, String description, String database) {
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.setType(type, typeData, database);
		this.description = description;
	}

	public RealWord(Word word) {
		this.physicalName = word.getPhysicalName();
		this.logicalName = word.getLogicalName();
		this.type = word.getType();
		this.typeData = new TypeData(word.getTypeData());
		this.description = word.getDescription();
	}

	@Override
	public String getLogicalName() {
		return logicalName;
	}

	@Override
	public String getPhysicalName() {
		return physicalName;
	}

	@Override
	public SqlType getType() {
		return type;
	}

	@Override
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	@Override
	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	@Override
	public void setType(SqlType type, TypeData typeData, String database) {
		this.type = type;
		this.typeData = new TypeData(typeData);;

		if (type != null && type.isNeedLength(database)) {
			if (this.typeData.getLength() == null) {
				this.typeData.setLength(0);
			}
		} else {
			this.typeData.setLength(null);
		}

		if (type != null && type.isNeedDecimal(database)) {
			if (this.typeData.getDecimal() == null) {
				this.typeData.setDecimal(0);
			}
		} else {
			this.typeData.setDecimal(null);
		}

	}

	@Override
	protected void setType(SqlType type) {
		this.type = type;
	}

	@Override
	public TypeData getTypeData() {
		return typeData;
	}

	@Override
	protected void setTypeData(TypeData typeData) {
		this.typeData = typeData;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}
}
