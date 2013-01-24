package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class Environment implements Serializable, Cloneable {

	private static final long serialVersionUID = 2894497911334351672L;

	private String id;

	private String name;

	public Environment(String name) {
		this.name = name;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = StringUtils.isNumeric(id) ? id : null;
	}

	public static void setId(final Set<String> check, final Environment column) {
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

	/**
	 * name を取得します.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name を設定します.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Environment clone() {
		try {
			Environment environment = (Environment) super.clone();

			return environment;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
