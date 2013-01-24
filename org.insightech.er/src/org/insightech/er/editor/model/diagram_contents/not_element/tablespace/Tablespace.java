package org.insightech.er.editor.model.diagram_contents.not_element.tablespace;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.settings.Environment;

public class Tablespace extends AbstractModel implements ObjectModel,
		Comparable<Tablespace> {

	private static final long serialVersionUID = 1861168804265437031L;

	private String id;

	private String name;

	private Map<Environment, TablespaceProperties> propertiesMap = new HashMap<Environment, TablespaceProperties>();

	public int compareTo(Tablespace other) {
		return this.name.toUpperCase().compareTo(other.name.toUpperCase());
	}

	public void copyTo(Tablespace to) {
		to.name = name;

		to.propertiesMap = new HashMap<Environment, TablespaceProperties>();
		for (Map.Entry<Environment, TablespaceProperties> entry : this.propertiesMap
				.entrySet()) {
			to.propertiesMap.put(entry.getKey(), entry.getValue().clone());
		}
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = StringUtils.isNumeric(id) ? id : null;
	}

	public static void setId(final Set<String> check, final Tablespace column) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TablespaceProperties getProperties(Environment environment,
			ERDiagram diagram) {
		return DBManagerFactory.getDBManager(diagram)
				.checkTablespaceProperties(this.propertiesMap.get(environment));
	}

	public void putProperties(Environment environment,
			TablespaceProperties tablespaceProperties) {
		this.propertiesMap.put(environment, tablespaceProperties);
	}

	/**
	 * propertiesMap を取得します.
	 * 
	 * @return propertiesMap
	 */
	public Map<Environment, TablespaceProperties> getPropertiesMap() {
		return propertiesMap;
	}

	@Override
	public Tablespace clone() {
		Tablespace clone = (Tablespace) super.clone();

		this.copyTo(clone);

		return clone;
	}

	public String getDescription() {
		return "";
	}

	public String getObjectType() {
		return "tablespace";
	}

}
