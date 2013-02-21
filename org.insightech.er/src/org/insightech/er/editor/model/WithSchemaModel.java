package org.insightech.er.editor.model;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.SupportFunctions;
import org.insightech.er.util.Format;

public abstract class WithSchemaModel extends AbstractModel implements
        ObjectModel, Comparable<WithSchemaModel> {

	private static final long serialVersionUID = -7450893485538582071L;

	private String schema = null;

	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getNameWithSchema(String database) {
		if (this.schema == null) {
			return Format.null2blank(this.name);
		}

		DBManager dbManager = DBManagerFactory.getDBManager(database);

		if (dbManager.isSupported(SupportFunctions.SCHEMA)) {
			return this.schema + "." + Format.null2blank(this.name);
		}
		return Format.null2blank(this.name);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof WithSchemaModel)) {
			return false;
		}
		final WithSchemaModel m = (WithSchemaModel) o;

		return StringUtils.equalsIgnoreCase(schema, m.schema) &&
		        StringUtils.equalsIgnoreCase(name, m.name);
	}

	public int compareTo(final WithSchemaModel other) {
		int compareTo = 0;

		compareTo = Format.null2blank(this.schema).toUpperCase().compareTo(
		        Format.null2blank(other.schema).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = Format.null2blank(this.name).toUpperCase().compareTo(
		        Format.null2blank(other.name).toUpperCase());

		if (compareTo != 0) {
			return compareTo;
		}

		return compareTo;
	}

	@Override
	public int hashCode() {
		int h = StringUtils.isBlank(schema) ? 0 : schema.toUpperCase().hashCode();
		return h * 37 + (StringUtils.isBlank(name) ? 0 : name.toUpperCase().hashCode());
	}
}
