package org.insightech.er.common.widgets.table;

import java.io.Serializable;

public interface HeaderClickListener extends Serializable {

	public void onHeaderClick(int column);
}
