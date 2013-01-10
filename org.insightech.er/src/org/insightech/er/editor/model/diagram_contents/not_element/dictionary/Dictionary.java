package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Dictionary extends AbstractModel {

	private static final long serialVersionUID = -4476318682977312216L;

	public static final String PROPERTY_CHANGE_DICTIONARY = "dictionary";

	private final Map<Word, Set<NormalColumn>> wordMap;
	private final Map<UniqueWord, Set<NormalColumn>> uniqueWordMap;

	public Dictionary() {
		this.wordMap = new IdentityHashMap<Word, Set<NormalColumn>>();
		this.uniqueWordMap = new HashMap<UniqueWord, Set<NormalColumn>>();
	}

	public void add(NormalColumn column, final boolean fire) {
		Word word = column.getWord();

		if (word == null) {
			return;
		}
		
		// for Word
		Set<NormalColumn> useColumns = this.wordMap.get(word);
		if (useColumns == null) {
			useColumns = new HashSet<NormalColumn>();
			this.wordMap.put(word, useColumns);
		}
		useColumns.add(column);

		if (fire) {
			setDirty();
		}
	}

	public void remove(NormalColumn column, final boolean fire) {
		Word word = column.getWord();

		if (word == null) {
			return;
		}
		
		// for Word
		Set<NormalColumn> useColumns = this.wordMap.get(word);
		if (useColumns != null) {
			useColumns.remove(column);
			if (useColumns.isEmpty()) {
				this.wordMap.remove(word);
			}
		}
		
		if (fire) {
			setDirty();
		}
	}

	public void remove(TableView tableView, final boolean fire) {
		for (NormalColumn normalColumn : tableView.getNormalColumns()) {
			this.remove(normalColumn, false);
		}
		if (fire) {
			setDirty();
		}
	}
	
	public void setDirty() {
		// for UniqueWord
		createUniqueWordMap();

		this.firePropertyChange(PROPERTY_CHANGE_DICTIONARY, null, null);
	}
	
	private void createUniqueWordMap() {
		Set<NormalColumn> useColumns;
		this.uniqueWordMap.clear();
		for (final Map.Entry<Word, Set<NormalColumn>> entry : this.wordMap.entrySet()) {
			final UniqueWord key = entry.getKey().getUniqueWord();
			useColumns = this.uniqueWordMap.get(key);
			if (useColumns == null) {
				useColumns = new HashSet<NormalColumn>(entry.getValue());
				this.uniqueWordMap.put(key, useColumns);
			} else {
				useColumns.addAll(entry.getValue());
			}
		}
	}

	public void clear() {
		this.wordMap.clear();
		this.uniqueWordMap.clear();
	}

	public List<UniqueWord> getUniqueWordList() {
		return new ArrayList<UniqueWord>(this.uniqueWordMap.keySet());
	}

	public List<Word> getWordList() {
		List<Word> list = new ArrayList<Word>(this.wordMap.keySet());

		Collections.sort(list);

		return list;
	}

	public Collection<NormalColumn> getColumnList(Word word) {
		if (word instanceof UniqueWord) {
			return this.uniqueWordMap.get(word);
		}
		return this.wordMap.get(word);
	}

	public void copyTo(Word from, Word to, final boolean fire) {
		from.copyTo(to);

		if (fire) {
			setDirty();
		}
	}
}
