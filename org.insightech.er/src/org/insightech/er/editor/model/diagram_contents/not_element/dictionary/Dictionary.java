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
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class Dictionary extends AbstractModel {

	private static final long serialVersionUID = -4476318682977312216L;

	public static final String PROPERTY_CHANGE_DICTIONARY = "dictionary";

	private final Map<Word, Set<NormalColumn>> wordMap;
	private final Map<UniqueWord, Set<NormalColumn>> uniqueWordMap;
	private final Map<UniqueWord, String> idMap;

	public Dictionary() {
		this.wordMap = new IdentityHashMap<Word, Set<NormalColumn>>();
		this.uniqueWordMap = new HashMap<UniqueWord, Set<NormalColumn>>();
		this.idMap = new WeakHashMap<UniqueWord, String>();
	}

	public void add(final UniqueWord word) {
		Set<NormalColumn> columns = this.uniqueWordMap.get(word);
		if (columns == null) {
			columns = new HashSet<NormalColumn>();
			this.uniqueWordMap.put(word, columns);
		}

		// ID割当てを行う (チェック用マップも登録する)
		UniqueWord.setId(idMap, word.getUniqueWord());
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

			UniqueWord.setId(idMap, word.getUniqueWord());
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
		for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
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

	public List<UniqueWord> getUniqueWordListOrderId() {
		final List<UniqueWord> retval = new ArrayList<UniqueWord>(this.uniqueWordMap.keySet());

		Collections.sort(retval, UniqueWord.WORD_ID_COMPARATOR);
		return retval;
	}

	public List<Word> getWordList() {
		List<Word> list = new ArrayList<Word>(this.wordMap.keySet());

		Collections.sort(list);

		return list;
	}

	public List<Word> getUniqueWordList(final String filterString) {
		final List<UniqueWord> list;
		if (StringUtils.isEmpty(filterString)) {
			list = getUniqueWordList();
		} else {
			final Set<UniqueWord> wordList = this.uniqueWordMap.keySet();
			list = new ArrayList<UniqueWord>(wordList.size());
			for (final UniqueWord word : wordList) {
				if (word.getName().startsWith(filterString)) {
					list.add(word);
				}
			}
		}
		
		final List<Word> retval = new ArrayList<Word>(list.size());
		for (final UniqueWord uw : list) {
			retval.add(uw.getWord());
		}

		return retval;
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
