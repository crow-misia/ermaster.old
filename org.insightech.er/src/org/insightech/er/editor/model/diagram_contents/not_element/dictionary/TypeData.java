package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class TypeData implements Serializable, Comparable<TypeData> {

	private static final long serialVersionUID = 811961394466466597L;

	private Integer length;

	private Integer decimal;

	private boolean array;

	private Integer arrayDimension;

	private boolean unsigned;

	private String args;
	
	private String unit;

	public TypeData(Integer length, Integer decimal, boolean array,
			Integer arrayDimension, boolean unsigned, String args,
			String unit) {
		super();
		this.length = length;
		this.decimal = decimal;
		this.array = array;
		this.arrayDimension = arrayDimension;
		this.unsigned = unsigned;
		this.args = StringUtils.trimToNull(args);
		this.unit = unit;
	}

	public TypeData(TypeData from) {
		super();
		this.length = from.length;
		this.decimal = from.decimal;
		this.array = from.array;
		this.arrayDimension = from.arrayDimension;
		this.unsigned = from.unsigned;
		this.args = from.args;
		this.unit = from.unit;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getDecimal() {
		return decimal;
	}

	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public Integer getArrayDimension() {
		return arrayDimension;
	}

	public void setArrayDimension(Integer arrayDimension) {
		this.arrayDimension = arrayDimension;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = StringUtils.trimToNull(args);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int compareTo(TypeData o) {
		if (o == null) {
			return -1;
		}

		if (this.length == null) {
			if (o.length != null) {
				return 1;
			}
		} else {
			if (o.length == null) {
				return -1;
			}
			int value = this.length.compareTo(o.length);
			if (value != 0) {
				return value;
			}
		}

		if (this.decimal == null) {
			if (o.decimal != null) {
				return 1;
			}
		} else {
			if (o.decimal == null) {
				return -1;
			}
			int value = this.decimal.compareTo(o.decimal);
			if (value != 0) {
				return value;
			}
		}

		if (this.array != o.array) {
			if (this.array) {
				return -1;
			}

			return 1;
		}

		if (this.arrayDimension == null) {
			if (o.arrayDimension != null) {
				return 1;
			}
		} else {
			if (o.arrayDimension == null) {
				return -1;
			}
			int value = this.arrayDimension.compareTo(o.arrayDimension);
			if (value != 0) {
				return value;
			}
		}

		if (this.unsigned != o.unsigned) {
			if (this.unsigned) {
				return 1;
			} else {
				return -1;
			}
		}

		if (this.args == null) {
			if (o.args != null) {
				return 1;
			}
		} else {
			if (o.args == null) {
				return -1;
			}
			int value = this.args.compareTo(o.args);
			if (value != 0) {
				return value;
			}
		}
		
		if (this.unit == null) {
			if (o.unit != null) {
				return 1;
			}
		} else {
			if (o.unit == null) {
				return -1;
			}
			int value = this.unit.compareTo(o.unit);
			if (value != 0) {
				return value;
			}
		}

		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((decimal == null) ? 0 : decimal.hashCode());
		result = prime * result + (array ? 1231 : 1237);
		result = prime * result
				+ ((arrayDimension == null) ? 0 : arrayDimension.hashCode());
		result = prime * result + (unsigned ? 1231 : 1237);
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		return prime * result + ((unit == null) ? 0 : unit.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeData other = (TypeData) obj;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (decimal == null) {
			if (other.decimal != null)
				return false;
		} else if (!decimal.equals(other.decimal))
			return false;
		if (array != other.array)
			return false;
		if (arrayDimension == null) {
			if (other.arrayDimension != null)
				return false;
		} else if (!arrayDimension.equals(other.arrayDimension))
			return false;
		if (unsigned != other.unsigned)
			return false;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

}
