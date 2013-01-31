package org.insightech.er.editor.view.figure.table.style;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.IndexFigure;
import org.insightech.er.editor.view.figure.table.column.IndexHeaderFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public interface StyleSupport {

	void init();

    void setDependence(final Boolean dependence);

	void createTitleBar();

	void createContentArea(IFigure content);

	void createFooter();

	void setName(String name);

	void setFont(Font font, Font titleFont);

	void adjustBounds(Rectangle rect);

	void addColumn(NormalColumnFigure figure, int viewMode,
			String physicalName, String logicalName, String type,
			boolean primaryKey, boolean foreignKey, boolean isNotNull,
			boolean uniqueKey, boolean displayKey, boolean displayDetail,
			boolean displayType, boolean isSelectedReferenced,
			boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated,
			boolean isRemoved);

	void addColumnGroup(GroupColumnFigure figure, int viewMode,
			String name, boolean isAdded, boolean isUpdated, boolean isRemoved);

	void addIndex(IndexFigure figure, int viewMode,
			String name, boolean displayIcon, boolean isAdded, boolean isUpdated, boolean isRemoved);

	void addIndexHeader(IndexHeaderFigure figure);
}
