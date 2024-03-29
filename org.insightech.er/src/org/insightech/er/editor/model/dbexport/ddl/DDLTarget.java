package org.insightech.er.editor.model.dbexport.ddl;

import java.io.Serializable;

public class DDLTarget implements Serializable, Cloneable {

	private static final long serialVersionUID = 8212409392159961699L;

	public boolean dropTablespace = true;

	public boolean dropSequence = true;

	public boolean dropTrigger = true;

	public boolean dropView = true;

	public boolean dropIndex = true;

	public boolean dropTable = true;

	public boolean createTablespace = true;

	public boolean createSequence = true;

	public boolean createTrigger = true;

	public boolean createView = true;

	public boolean createIndex = true;

	public boolean createTable = true;

	public boolean createForeignKey = true;

	public boolean createComment = true;

	public boolean inlineTableComment = true;

	public boolean inlineColumnComment = true;

	public boolean commentValueDescription = true;

	public boolean commentValueLogicalName = false;

	public boolean commentValueLogicalNameDescription = false;

	public boolean commentReplaceLineFeed = false;

	public String commentReplaceString;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DDLTarget other = (DDLTarget) obj;
		if (commentReplaceLineFeed != other.commentReplaceLineFeed)
			return false;
		if (commentReplaceString == null) {
			if (other.commentReplaceString != null)
				return false;
		} else if (!commentReplaceString.equals(other.commentReplaceString))
			return false;
		if (commentValueDescription != other.commentValueDescription)
			return false;
		if (commentValueLogicalName != other.commentValueLogicalName)
			return false;
		if (commentValueLogicalNameDescription != other.commentValueLogicalNameDescription)
			return false;
		if (createComment != other.createComment)
			return false;
		if (createForeignKey != other.createForeignKey)
			return false;
		if (createIndex != other.createIndex)
			return false;
		if (createSequence != other.createSequence)
			return false;
		if (createTable != other.createTable)
			return false;
		if (createTablespace != other.createTablespace)
			return false;
		if (createTrigger != other.createTrigger)
			return false;
		if (createView != other.createView)
			return false;
		if (dropIndex != other.dropIndex)
			return false;
		if (dropSequence != other.dropSequence)
			return false;
		if (dropTable != other.dropTable)
			return false;
		if (dropTablespace != other.dropTablespace)
			return false;
		if (dropTrigger != other.dropTrigger)
			return false;
		if (dropView != other.dropView)
			return false;
		if (inlineColumnComment != other.inlineColumnComment)
			return false;
		if (inlineTableComment != other.inlineTableComment)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int h = 0;
		h = h * 37 + (commentReplaceLineFeed ? 1231 : 1237);
		h = h * 37 + (commentReplaceString == null ? 0 : commentReplaceString.hashCode());
		h = h * 37 + (commentValueDescription ? 1231 : 1237);
		h = h * 37 + (commentValueLogicalName ? 1231 : 1237);
		h = h * 37 + (commentValueLogicalNameDescription ? 1231 : 1237);
		h = h * 37 + (createComment ? 1231 : 1237);
		h = h * 37 + (createForeignKey ? 1231 : 1237);
		h = h * 37 + (createIndex ? 1231 : 1237);
		h = h * 37 + (createSequence ? 1231 : 1237);
		h = h * 37 + (createTable ? 1231 : 1237);
		h = h * 37 + (createTablespace ? 1231 : 1237);
		h = h * 37 + (createTrigger ? 1231 : 1237);
		h = h * 37 + (createView ? 1231 : 1237);
		h = h * 37 + (dropIndex ? 1231 : 1237);
		h = h * 37 + (dropSequence ? 1231 : 1237);
		h = h * 37 + (dropTable ? 1231 : 1237);
		h = h * 37 + (dropTablespace ? 1231 : 1237);
		h = h * 37 + (dropTrigger ? 1231 : 1237);
		h = h * 37 + (dropView ? 1231 : 1237);
		h = h * 37 + (inlineColumnComment ? 1231 : 1237);
		return h * 37 + (inlineTableComment ? 1231 : 1237);
	}

	@Override
	public DDLTarget clone() {
		try {
			return (DDLTarget) super.clone();

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
