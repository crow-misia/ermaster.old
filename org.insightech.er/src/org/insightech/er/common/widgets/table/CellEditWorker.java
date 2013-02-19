package org.insightech.er.common.widgets.table;

import java.io.Serializable;

public interface CellEditWorker extends Serializable {

	public void addNewRow();

	public void changeRowNum();

	public boolean isModified(int row, int column);

}
