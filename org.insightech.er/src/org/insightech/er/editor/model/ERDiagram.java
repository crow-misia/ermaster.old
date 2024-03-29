package org.insightech.er.editor.model;

import java.util.List;
import java.util.Locale;

import org.eclipse.draw2d.geometry.Point;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;

public class ERDiagram extends ViewableModel {

	private static final long serialVersionUID = 8729319470770699498L;

	public static final String PROPERTY_CHANGE_ALL = "all";

	public static final String PROPERTY_CHANGE_DATABASE = "database";

	public static final String PROPERTY_CHANGE_SETTINGS = "settings";

	private ChangeTrackingList changeTrackingList;

	private DiagramContents diagramContents;

	private transient ERDiagramMultiPageEditor editor;

	private int[] defaultColor;

	private boolean tooltip;

	private boolean disableSelectColumn;

	private Category currentCategory;

	private int currentCategoryIndex;

	private double zoom = 1.0d;

	private int x;

	private int y;

	private DBSetting dbSetting;

	private PageSetting pageSetting;

	public Point mousePoint = new Point();

	public ERDiagram(String database) {
		this.diagramContents = new DiagramContents();
		this.diagramContents.getSettings().setDatabase(database);
		this.pageSetting = new PageSetting();

		this.setDefaultColor(128, 128, 192);
		this.setColor(255, 255, 255);
	}

	public void init() {
		this.diagramContents.setColumnGroups(GlobalGroupSet.load());

		Settings settings = this.getDiagramContents().getSettings();

		if (Locale.JAPANESE.getLanguage().equals(
				Locale.getDefault().getLanguage())) {
			settings.getTranslationSetting().setUse(true);
			settings.getTranslationSetting().selectDefault();
		}

		settings.getModelProperties().init();
	}

	public void addNewContent(NodeElement element, final boolean contentFire, final boolean dictionaryFire) {
		element.setColor(this.defaultColor);
		element.setFontName(this.getFontName());
		element.setFontSize(this.getFontSize());

		this.addContent(element, contentFire, dictionaryFire);
	}

	public void addContent(NodeElement element, final boolean contentFire, final boolean dictionaryFire) {
		element.setDiagram(this);

		this.diagramContents.getContents().addNodeElement(element, true);

		if (this.editor != null) {
			Category category = this.editor.getCurrentPageCategory();
			if (category != null) {
				category.getContents().add(element);
			}
		}

		final Dictionary dictionary = this.getDiagramContents().getDictionary();
		if (element instanceof TableView) {
			for (final NormalColumn normalColumn : ((TableView) element)
					.getExpandedColumns()) {
				dictionary.add(normalColumn, false);
			}
		}
		if (dictionaryFire) {
			dictionary.setDirty();
		}

		if (contentFire) {
			setDirtyForContent();
		}
	}

	public void removeContent(NodeElement element, final boolean contentFire, final boolean dictionaryFire) {
		this.diagramContents.getContents().remove(element, true);

		if (element instanceof TableView) {
			this.diagramContents.getDictionary().remove((TableView) element, dictionaryFire);
		}

		for (Category category : this.diagramContents.getSettings()
				.getCategorySetting().getAllCategories()) {
			category.getContents().remove(element);
		}

		if (contentFire) {
			setDirtyForContent();
		}
	}

	public void replaceContents(DiagramContents newDiagramContents, final boolean fire) {
		this.diagramContents = newDiagramContents;
		if (fire) {
			setDirtyForContent();
		}
	}

	public void changeAll() {
		changeAll(null);
	}

	public void changeAll(List<NodeElement> nodeElementList) {
		this.firePropertyChange(PROPERTY_CHANGE_ALL, null, nodeElementList);
	}

	public void setDatabase(String str) {
		String oldDatabase = getDatabase();

		this.getDiagramContents().getSettings().setDatabase(str);

		if (str != null && !str.equals(oldDatabase)) {
			this.firePropertyChange(PROPERTY_CHANGE_DATABASE, oldDatabase,
					getDatabase());
			this.changeAll();
		}
	}

	public String getDatabase() {
		return this.getDiagramContents().getSettings().getDatabase();
	}

	public DBManager getDBManager() {
		return DBManagerFactory.getDBManager(this);
	}

	public void restoreDatabase(String str) {
		this.getDiagramContents().getSettings().setDatabase(str);
	}

	public void setSettings(Settings settings, final boolean fire) {
		this.getDiagramContents().setSettings(settings);
		this.editor.initCategoryPages();

		change();
		if (fire) {
			setDirtyForContent();
		}
	}

	public void setCurrentCategoryPageName() {
		this.editor.setCurrentCategoryPageName();
	}
	
	public void addCategory(Category category, final boolean fire) {
		category.setColor(this.defaultColor);
		this.getDiagramContents().getSettings().getCategorySetting()
				.addCategoryAsSelected(category);
		this.editor.initCategoryPages();
		if (fire) {
			setDirtyForContent();
		}
	}

	public void removeCategory(Category category, final boolean fire) {
		this.getDiagramContents().getSettings().getCategorySetting()
				.removeCategory(category);
		this.editor.initCategoryPages();
		if (fire) {
			setDirtyForContent();
		}
	}

	public void restoreCategories(final boolean fire) {
		this.editor.initCategoryPages();
		if (fire) {
			setDirtyForContent();
		}
	}

	public void change() {
		this.firePropertyChange(PROPERTY_CHANGE_SETTINGS, null, null);
	}

	public ChangeTrackingList getChangeTrackingList() {
		if (this.changeTrackingList == null) {
			this.changeTrackingList = new ChangeTrackingList();
		}
		return changeTrackingList;
	}

	public DiagramContents getDiagramContents() {
		return this.diagramContents;
	}

	public void setEditor(ERDiagramMultiPageEditor editor) {
		this.editor = editor;
	}

	public int[] getDefaultColor() {
		return defaultColor;
	}

	public final void setDefaultColor(int red, int green, int blue) {
		this.defaultColor = new int[3];
		this.defaultColor[0] = red;
		this.defaultColor[1] = green;
		this.defaultColor[2] = blue;
	}

	public void setCurrentCategory(Category currentCategory,
			int currentCategoryIndex) {
		this.currentCategory = currentCategory;
		this.currentCategoryIndex = currentCategoryIndex;
		this.changeAll();
	}

	public Category getCurrentCategory() {
		return currentCategory;
	}

	public int getCurrentCategoryIndex() {
		return currentCategoryIndex;
	}

	public boolean isTooltip() {
		return tooltip;
	}

	public void setTooltip(boolean tooltip) {
		this.tooltip = tooltip;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * dbSetting を取得します.
	 * 
	 * @return dbSetting
	 */
	public DBSetting getDbSetting() {
		return dbSetting;
	}

	/**
	 * dbSetting を設定します.
	 * 
	 * @param dbSetting
	 *            dbSetting
	 */
	public void setDbSetting(DBSetting dbSetting) {
		this.dbSetting = dbSetting;
	}

	/**
	 * pageSetting を取得します.
	 * 
	 * @return pageSetting
	 */
	public PageSetting getPageSetting() {
		return pageSetting;
	}

	/**
	 * pageSetting を設定します.
	 * 
	 * @param pageSetting
	 *            pageSetting
	 */
	public void setPageSetting(PageSetting pageSetting) {
		this.pageSetting = pageSetting;
	}

	/**
	 * editor を取得します.
	 * 
	 * @return editor
	 */
	public ERDiagramMultiPageEditor getEditor() {
		return editor;
	}

	public String filter(String str) {
		if (str == null) {
			return str;
		}

		Settings settings = this.getDiagramContents().getSettings();

		if (settings.isCapital()) {
			return str.toUpperCase();
		}

		return str;
	}

	/**
	 * disableSelectColumn を取得します.
	 * 
	 * @return disableSelectColumn
	 */
	public boolean isDisableSelectColumn() {
		return disableSelectColumn;
	}

	/**
	 * disableSelectColumn を設定します.
	 * 
	 * @param disableSelectColumn
	 *            disableSelectColumn
	 */
	public void setDisableSelectColumn(boolean disableSelectColumn) {
		this.disableSelectColumn = disableSelectColumn;
	}

	public void setDirtyForContent() {
		this.firePropertyChange(NodeSet.PROPERTY_CHANGE_CONTENTS, null, null);
	}
}
