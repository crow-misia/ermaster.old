package org.insightech.er.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.NameValue;

public class ModelProperties extends NodeElement implements Cloneable {

	private static final long serialVersionUID = 5311013351131568260L;

	public static final int FORMAT_VERSION = 2;
	
	public static final String PROPERTY_CHANGE_MODEL_PROPERTIES = "model_properties";

	public static final String KEY_PROJECT_NAME = ResourceString.getResourceString("label.project.name");

    public static final String KEY_MODEL_NAME = ResourceString.getResourceString("label.model.name");

    public static final String KEY_VERSION = ResourceString.getResourceString("label.version");

    public static final String KEY_COMPANY_NAME = ResourceString.getResourceString("label.company.name");

    public static final String KEY_DEPARTMENT_NAME = ResourceString.getResourceString("label.department.name");

    public static final String KEY_AUTHOR = ResourceString.getResourceString("label.author");

    public static final String KEY_UPDATER = ResourceString.getResourceString("label.updater");

    private boolean display;

	private List<NameValue> properties;

	private Date creationDate;

	private Date updatedDate;

    private int formatVersion;

    public ModelProperties() {
		this.creationDate = new Date();
		this.updatedDate = new Date();

		this.setLocation(new Location(50, 50, -1, -1));

		this.properties = new ArrayList<NameValue>();

		this.formatVersion = FORMAT_VERSION;
	}

	public void init() {
		properties.add(new NameValue(KEY_PROJECT_NAME, ""));
		properties.add(new NameValue(KEY_MODEL_NAME, ""));
		properties.add(new NameValue(KEY_VERSION, ""));
		properties.add(new NameValue(KEY_COMPANY_NAME, ""));
        properties.add(new NameValue(KEY_DEPARTMENT_NAME, ""));
		properties.add(new NameValue(KEY_AUTHOR, ""));
        properties.add(new NameValue(KEY_UPDATER, ""));
	}

	public void clear() {
		this.properties.clear();
	}

	public List<NameValue> getProperties() {
		return properties;
	}

	public void addProperty(NameValue property) {
		this.properties.add(property);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public Map<String, String> getMap() {
	    final Map<String, String> retval = new HashMap<String, String>();
	    
	    for (final NameValue n : this.properties) {
	        retval.put(n.getName(), n.getValue());
	    }
	    
	    return retval;
	}

	public void setUpdatedDate(Date updatedDate, final boolean fire) {
		this.updatedDate = updatedDate;

		if (fire) {
			setDirty();
		}
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display, final boolean fire) {
		this.display = display;

		if (fire) {
			setDirty();
		}
	}

    public int getFormatVersion() {
        return this.formatVersion;
    }

    public void setFormatVersion(final int version) {
        this.formatVersion = version;
    }

    @Override
	public void setLocation(Location location) {
		location.width = -1;
		location.height = -1;

		super.setLocation(location);
	}

	@Override
	public ModelProperties clone() {
		final ModelProperties clone = (ModelProperties) super.clone();

		final List<NameValue> list = new ArrayList<NameValue>(this.properties.size());

		for (final NameValue nameValue : this.properties) {
			list.add(nameValue.clone());
		}

		clone.properties = list;

		return clone;
	}

	public void setProperties(List<NameValue> properties, final boolean fire) {
		this.properties = properties;

		if (fire) {
			setDirty();
		}
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "model_properties";
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	public void formatUpgrade() {
	    switch (this.formatVersion) {
	    case 1:
	        // version → バージョン(Ja)
	        for (int i = 0; i < this.properties.size(); i++) {
	            final NameValue v = this.properties.get(i);

	            if (StringUtils.equals(v.getName(), "version")) {
	                v.setName(ModelProperties.KEY_VERSION);
	            } else if (StringUtils.equals(v.getName(), KEY_COMPANY_NAME)) {
	                this.properties.add(i + 1, new NameValue(KEY_DEPARTMENT_NAME, ""));
                } else if (StringUtils.equals(v.getName(), KEY_AUTHOR)) {
                    this.properties.add(i + 1, new NameValue(KEY_UPDATER, ""));
	            }
	        }
	        break;
	    }

	    this.formatVersion = FORMAT_VERSION;
	}
}
