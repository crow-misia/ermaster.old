package org.insightech.er.editor.view.figure.table.style;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.IndexFigure;
import org.insightech.er.editor.view.figure.table.column.IndexHeaderFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class AbstractStyleSupport implements StyleSupport {

	private final TableFigure tableFigure;

	public AbstractStyleSupport(TableFigure tableFigure) {
		super();
		this.tableFigure = tableFigure;
	}

	public void init() {
		this.init(this.tableFigure);
	}

	protected abstract void init(TableFigure tableFigure);

	public void createTitleBar() {
		Figure top = new Figure();
		this.tableFigure.add(top, BorderLayout.TOP);

		this.initTitleBar(top);
	}

	abstract protected void initTitleBar(Figure top);

	protected Color getTextColor() {
		return this.tableFigure.getTextColor();
	}

	public void createContentArea(IFigure content) {
		initContentArea(content);

		this.tableFigure.add(content, BorderLayout.CENTER);
	}

	protected static void initContentArea(IFigure columns) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(true);
		layout.setSpacing(0);

		columns.setBorder(new MarginBorder(0, 2, 2, 2));
		columns.setLayoutManager(layout);

		columns.setBackgroundColor(null);
		columns.setOpaque(false);
	}

	public void createFooter() {
	}

	protected static String getColumnText(int viewMode, String physicalName,
			String logicalName, String type, boolean isNotNull,
			boolean uniqueKey, boolean detail, boolean displayType) {
		StringBuilder text = new StringBuilder();

		String name = null;
		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
			name = physicalName;

		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
			name = logicalName;

		} else {
			name = logicalName + "/" + physicalName;
		}

		if (name != null) {
			text.append(name);
		}

		if (displayType) {
			text.append(" ");

			text.append(type);
		}

		if (detail) {
			if (isNotNull && uniqueKey) {
				text.append(" (UNN)");

			} else if (isNotNull) {
				text.append(" (NN)");

			} else if (uniqueKey) {
				text.append(" (U)");
			}
		}

		return text.toString();
	}

	protected static Label createColumnLabel() {
		Label label = new Label();
		label.setBorder(new MarginBorder(new Insets(3, 5, 3, 5)));
		label.setLabelAlignment(PositionConstants.LEFT);

		return label;
	}

	protected static void setColumnFigureColor(IFigure figure,
			boolean isSelectedReferenced, boolean isSelectedForeignKey,
			boolean isAdded, boolean isUpdated, boolean isRemoved) {
		if (isAdded) {
			figure.setBackgroundColor(Resources.ADDED_COLOR);
		} else if (isUpdated) {
			figure.setBackgroundColor(Resources.UPDATED_COLOR);
		} else if (isRemoved) {
			figure.setBackgroundColor(Resources.REMOVED_COLOR);
		}

		if (isSelectedReferenced && isSelectedForeignKey) {
			figure
					.setBackgroundColor(Resources.SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN);

		} else if (isSelectedReferenced) {
			figure.setBackgroundColor(Resources.SELECTED_REFERENCED_COLUMN);

		} else if (isSelectedForeignKey) {
			figure.setBackgroundColor(Resources.SELECTED_FOREIGNKEY_COLUMN);

		}

		figure.setOpaque(true);
	}

	public void adjustBounds(Rectangle rect) {
	}

	protected TableFigure getTableFigure() {
		return tableFigure;
	}

	public void addColumn(NormalColumnFigure figure, int viewMode,
			String physicalName, String logicalName, String type,
			boolean primaryKey, boolean foreignKey, boolean isNotNull,
			boolean uniqueKey, boolean displayKey, boolean displayDetail,
			boolean displayType, boolean isSelectedReferenced,
			boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated,
			boolean isRemoved) {
		Label label = createColumnLabel();
		label.setForegroundColor(this.getTextColor());

		StringBuilder text = new StringBuilder();
		text.append(getColumnText(viewMode, physicalName, logicalName,
				type, isNotNull, uniqueKey, displayDetail, displayType));

		if (displayKey) {
			if (primaryKey && foreignKey) {
				label.setForegroundColor(ColorConstants.blue);

				text.append(" (PFK)");

			} else if (primaryKey) {
				label.setForegroundColor(ColorConstants.red);

				text.append(" (PK)");

			} else if (foreignKey) {
				label.setForegroundColor(ColorConstants.darkGreen);

				text.append(" (FK)");
			}
		}

		label.setText(text.toString());

		setColumnFigureColor(figure, isSelectedReferenced,
				isSelectedForeignKey, isAdded, isUpdated, isRemoved);

		figure.add(label);
	}

	public void addColumnGroup(GroupColumnFigure figure, int viewMode,
			String name, boolean isAdded, boolean isUpdated, boolean isRemoved) {

		Label label = createColumnLabel();

		label.setForegroundColor(this.getTextColor());

		StringBuilder text = new StringBuilder();
		text.append(name);
		text.append(" (GROUP)");

		setColumnFigureColor(figure, false, false, isAdded,
				isUpdated, isRemoved);

		label.setText(text.toString());

		figure.add(label);
	}

	public void addIndex(IndexFigure figure, int viewMode, String name, boolean displayIcon, boolean isAdded, boolean isUpdated, boolean isRemoved) {
		Label label = createColumnLabel();

		label.setForegroundColor(this.getTextColor());

		StringBuilder text = new StringBuilder();
		text.append(name);
		text.append(" (INDEX)");

		setColumnFigureColor(figure, false, false, isAdded,
				isUpdated, isRemoved);

		label.setText(text.toString());

		figure.add(label);
	}

	public void addIndexHeader(IndexHeaderFigure figure) {
		// do nothing.
	}
}
