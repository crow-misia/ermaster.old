package org.insightech.er.editor.persistent.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.oracle.OracleTableProperties;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.RealWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLLoader {

	private ERDiagram diagram;

	private String database;

	private static final ThreadLocal<SimpleDateFormat> DATEFORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private static class LoadContext {
		private Map<String, NodeElement> nodeElementMap;

		private Map<String, NormalColumn> columnMap;

		private Map<String, ComplexUniqueKey> complexUniqueKeyMap;

		private Map<NormalColumn, String[]> columnRelationMap;

		private Map<NormalColumn, String[]> columnReferencedColumnMap;

		private Map<String, ColumnGroup> columnGroupMap;

		private Map<Relation, String> referencedColumnMap;

		private Map<Relation, String> referencedComplexUniqueKeyMap;

		private Map<ConnectionElement, String> connectionSourceMap;

		private Map<ConnectionElement, String> connectionTargetMap;

		private Map<String, ConnectionElement> connectionMap;

		private Map<String, Word> wordMap;

		private Map<String, Tablespace> tablespaceMap;

		private Map<String, Environment> environmentMap;

		private Map<UniqueWord, Word> uniqueWordMap;

		private Dictionary dictionary;

		private LoadContext(Dictionary dictionary) {
			this.nodeElementMap = new HashMap<String, NodeElement>();
			this.columnMap = new HashMap<String, NormalColumn>();
			this.complexUniqueKeyMap = new HashMap<String, ComplexUniqueKey>();
			this.columnRelationMap = new HashMap<NormalColumn, String[]>();
			this.columnReferencedColumnMap = new HashMap<NormalColumn, String[]>();
			this.columnGroupMap = new HashMap<String, ColumnGroup>();
			this.referencedColumnMap = new HashMap<Relation, String>();
			this.referencedComplexUniqueKeyMap = new HashMap<Relation, String>();
			this.connectionMap = new HashMap<String, ConnectionElement>();
			this.connectionSourceMap = new HashMap<ConnectionElement, String>();
			this.connectionTargetMap = new HashMap<ConnectionElement, String>();
			this.wordMap = new HashMap<String, Word>();
			this.tablespaceMap = new HashMap<String, Tablespace>();
			this.environmentMap = new HashMap<String, Environment>();
			this.uniqueWordMap = new HashMap<UniqueWord, Word>();

			this.dictionary = dictionary;
			this.dictionary.clear();
		}

		private void resolve() {
			for (ConnectionElement connection : this.connectionSourceMap
					.keySet()) {
				String id = this.connectionSourceMap.get(connection);

				NodeElement nodeElement = this.nodeElementMap.get(id);
				connection.setSource(nodeElement, true);
			}

			for (ConnectionElement connection : this.connectionTargetMap
					.keySet()) {
				String id = this.connectionTargetMap.get(connection);

				NodeElement nodeElement = this.nodeElementMap.get(id);
				connection.setTarget(nodeElement, true);
			}

			for (Relation relation : this.referencedColumnMap.keySet()) {
				String id = this.referencedColumnMap.get(relation);

				NormalColumn column = this.columnMap.get(id);
				relation.setReferencedColumn(column);
			}

			for (Relation relation : this.referencedComplexUniqueKeyMap
					.keySet()) {
				String id = this.referencedComplexUniqueKeyMap.get(relation);

				ComplexUniqueKey complexUniqueKey = this.complexUniqueKeyMap
						.get(id);
				relation.setReferencedComplexUniqueKey(complexUniqueKey);
			}

			Set<NormalColumn> foreignKeyColumnSet = this.columnReferencedColumnMap
					.keySet();

			while (!foreignKeyColumnSet.isEmpty()) {
				NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator()
						.next();
				reduce(foreignKeyColumnSet, foreignKeyColumn);
			}
		}

		private void reduce(Set<NormalColumn> foreignKeyColumnSet,
				NormalColumn foreignKeyColumn) {
			
			
			String[] referencedColumnIds = this.columnReferencedColumnMap
					.get(foreignKeyColumn);

			String[] relationIds = this.columnRelationMap.get(foreignKeyColumn);

			List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();

			if (referencedColumnIds != null) {
				for (String referencedColumnId : referencedColumnIds) {
					NormalColumn referencedColumn = this.columnMap
							.get(referencedColumnId);
					referencedColumnList.add(referencedColumn);

					if (foreignKeyColumnSet.contains(referencedColumn) && foreignKeyColumn != referencedColumn) {
						reduce(foreignKeyColumnSet, referencedColumn);
					}
				}
			}

			if (relationIds != null) {
				for (String relationId : relationIds) {
					Relation relation = (Relation) this.connectionMap
							.get(relationId);
					for (NormalColumn referencedColumn : referencedColumnList) {
						if (referencedColumn.getColumnHolder() == relation
								.getSourceTableView()) {
							foreignKeyColumn.addReference(referencedColumn,
									relation);
							break;
						}
					}
				}
			}
			
			foreignKeyColumnSet.remove(foreignKeyColumn);
		}
	}

	public ERDiagram load(InputStream in) throws Exception {
		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document document = parser.parse(in);

		Node root = document.getFirstChild();

		while (root.getNodeType() == Node.COMMENT_NODE) {
			document.removeChild(root);
			root = document.getFirstChild();
		}

		load((Element) root);

		return this.diagram;
	}

	private static String getStringValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);
		node = node.getFirstChild();
		if (node == null) {
			return "";
		}

		return node.getNodeValue();
	}

	private static String[] getTagValues(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		final int n = nodeList.getLength();
		String[] values = new String[n];

		for (int i = 0; i < n; i++) {
			Node node = nodeList.item(i);
			node = node.getFirstChild();
			if (node == null) {
				values[i] = null;
			} else {
				values[i] = node.getNodeValue();
			}
		}

		return values;
	}

	private static boolean getBooleanValue(Element element, String tagname) {
		return getBooleanValue(element, tagname, false);
	}

	private static boolean getBooleanValue(Element element, String tagname,
			boolean defaultValue) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return defaultValue;
		}

		Node node = nodeList.item(0);

		String value = node.getFirstChild().getNodeValue();

		return Boolean.parseBoolean(value);
	}

	private static int getIntValue(Element element, String tagname) {
		return getIntValue(element, tagname, 0);
	}

	private static int getIntValue(Element element, String tagname, int defaultValue) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return defaultValue;
		}

		Node node = nodeList.item(0);
		node = node.getFirstChild();
		if (node == null) {
			return defaultValue;
		}

		String value = node.getNodeValue();

		return Integer.parseInt(value);
	}

	private static Integer getIntegerValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return Integer.valueOf(value);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Long getLongValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return Long.valueOf(value);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static BigDecimal getBigDecimalValue(Element element, String tagname) {
		String value = getStringValue(element, tagname);

		try {
			return new BigDecimal(value);
		} catch (Exception e) {
		}

		return null;
	}

	private static double getDoubleValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return 0;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return 0;
		}

		String value = node.getFirstChild().getNodeValue();

		return Double.parseDouble(value);
	}

	private static Date getDateValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return DATEFORMAT.get().parse(value);
		} catch (ParseException e) {
			return null;
		}
	}

	private static Element getElement(Element element, String tagname) {
		NodeList nodeList = element.getChildNodes();

		if (nodeList.getLength() == 0) {
			return null;
		}

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element) nodeList.item(i);
				if (ele.getTagName().equals(tagname)) {
					return ele;
				}
			}
		}

		return null;
	}

	private void load(Element root) {
		Element settings = getElement(root, "settings");
		this.database = loadDatabase(settings);

		this.diagram = new ERDiagram(this.database);

		loadDBSetting(this.diagram, root);
		loadPageSetting(this.diagram, root);

		loadColor(this.diagram, root);
		loadDefaultColor(this.diagram, root);
		loadFont(this.diagram, root);

		DiagramContents diagramContents = this.diagram.getDiagramContents();
		this.loadDiagramContents(diagramContents, root);

		int categoryIndex = getIntValue(root, "category_index");
		this.diagram.setCurrentCategory(null, categoryIndex);

		double zoom = getDoubleValue(root, "zoom");
		this.diagram.setZoom(zoom);

		int x = getIntValue(root, "x");
		int y = getIntValue(root, "y");
		this.diagram.setLocation(x, y);

		this.loadChangeTrackingList(this.diagram.getChangeTrackingList(), root);

		this.diagram.getDiagramContents().getSettings().getTranslationSetting()
				.load();
	}

	private static String loadDatabase(Element settingsElement) {
		String database = getStringValue(settingsElement, "database");
		if (database == null) {
			database = DBManagerFactory.getAllDBList().get(0);
		}

		return database;
	}

	private void loadDiagramContents(DiagramContents diagramContents,
			Element parent) {
		Dictionary dictionary = diagramContents.getDictionary();

		LoadContext context = new LoadContext(dictionary);

		this.loadDictionary(dictionary, parent, context);

		Settings settings = diagramContents.getSettings();
		loadEnvironmentSetting(settings.getEnvironmentSetting(), parent,
				context);

		this.loadTablespaceSet(diagramContents.getTablespaceSet(), parent,
				context);

		GroupSet columnGroups = diagramContents.getGroups();
		columnGroups.clear();

		this.loadColumnGroups(columnGroups, parent, context);
		this.loadContents(diagramContents.getContents(), parent, context);
		loadTestDataList(diagramContents.getTestDataList(), parent,
				context);
		loadSequenceSet(diagramContents.getSequenceSet(), parent);
		loadTriggerSet(diagramContents.getTriggerSet(), parent);

		loadSettings(settings, parent, context);

		context.resolve();
	}

	private static void loadSequenceSet(SequenceSet sequenceSet, Element parent) {
		Element element = getElement(parent, "sequence_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("sequence");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element sequenceElemnt = (Element) nodeList.item(i);
				Sequence sequence = loadSequence(sequenceElemnt);

				sequenceSet.addSequence(sequence, false);
			}
			sequenceSet.setDirty();
		}
	}

	private static Sequence loadSequence(Element element) {
		Sequence sequence = new Sequence();

		sequence.setName(getStringValue(element, "name"));
		sequence.setSchema(getStringValue(element, "schema"));
		sequence.setIncrement(getIntegerValue(element, "increment"));
		sequence.setMinValue(getLongValue(element, "min_value"));
		sequence.setMaxValue(getBigDecimalValue(element, "max_value"));
		sequence.setStart(getLongValue(element, "start"));
		sequence.setCache(getIntegerValue(element, "cache"));
		sequence.setCycle(getBooleanValue(element, "cycle"));
		sequence.setOrder(getBooleanValue(element, "order"));
		sequence.setDescription(getStringValue(element, "description"));
		sequence.setDataType(getStringValue(element, "data_type"));
		sequence.setDecimalSize(getIntValue(element, "decimal_size"));

		return sequence;
	}

	private static void loadTriggerSet(TriggerSet triggerSet, Element parent) {
		Element element = getElement(parent, "trigger_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("trigger");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element triggerElemnt = (Element) nodeList.item(i);
				Trigger trigger = loadTrigger(triggerElemnt);

				triggerSet.addTrigger(trigger, false);
			}
			triggerSet.setDirty();
		}
	}

	private static Trigger loadTrigger(Element element) {
		Trigger trigger = new Trigger();

		trigger.setName(getStringValue(element, "name"));
		trigger.setSchema(getStringValue(element, "schema"));
		trigger.setSql(getStringValue(element, "sql"));
		trigger.setDescription(getStringValue(element, "description"));

		return trigger;
	}

	private void loadTablespaceSet(TablespaceSet tablespaceSet, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "tablespace_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("tablespace");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element tablespaceElemnt = (Element) nodeList.item(i);
				Tablespace tablespace = this.loadTablespace(tablespaceElemnt,
						context);
				tablespaceSet.addTablespace(tablespace, false);
			}
			tablespaceSet.setDirty();
		}
	}

	private Tablespace loadTablespace(Element element, LoadContext context) {
		String id = getStringValue(element, "id");

		Tablespace tablespace = new Tablespace();
		tablespace.setId(id);
		tablespace.setName(getStringValue(element, "name"));

		NodeList nodeList = element.getElementsByTagName("properties");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propertiesElemnt = (Element) nodeList.item(i);

			String environmentId = getStringValue(propertiesElemnt,
					"environment_id");
			Environment environment = context.environmentMap.get(environmentId);

			TablespaceProperties tablespaceProperties = null;

			if (DB2DBManager.ID.equals(this.database)) {
				tablespaceProperties = loadTablespacePropertiesDB2(propertiesElemnt);

			} else if (MySQLDBManager.ID.equals(this.database)) {
				tablespaceProperties = loadTablespacePropertiesMySQL(propertiesElemnt);

			} else if (OracleDBManager.ID.equals(this.database)) {
				tablespaceProperties = loadTablespacePropertiesOracle(propertiesElemnt);

			} else if (PostgresDBManager.ID.equals(this.database)) {
				tablespaceProperties = loadTablespacePropertiesPostgres(propertiesElemnt);

			}

			tablespace.putProperties(environment, tablespaceProperties);
		}

		if (id != null) {
			context.tablespaceMap.put(id, tablespace);
		}

		return tablespace;
	}

	private static TablespaceProperties loadTablespacePropertiesDB2(Element element) {
		DB2TablespaceProperties properties = new DB2TablespaceProperties();

		properties.setBufferPoolName(getStringValue(element,
				"buffer_pool_name"));
		properties.setContainer(getStringValue(element, "container"));
		// properties.setContainerDevicePath(this.getStringValue(element,
		// "container_device_path"));
		// properties.setContainerDirectoryPath(this.getStringValue(element,
		// "container_directory_path"));
		// properties.setContainerFilePath(this.getStringValue(element,
		// "container_file_path"));
		// properties.setContainerPageNum(this.getStringValue(element,
		// "container_page_num"));
		properties.setExtentSize(getStringValue(element, "extent_size"));
		properties.setManagedBy(getStringValue(element, "managed_by"));
		properties.setPageSize(getStringValue(element, "page_size"));
		properties.setPrefetchSize(getStringValue(element, "prefetch_size"));
		properties.setType(getStringValue(element, "type"));

		return properties;
	}

	private static TablespaceProperties loadTablespacePropertiesMySQL(Element element) {
		MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

		properties.setDataFile(getStringValue(element, "data_file"));
		properties.setEngine(getStringValue(element, "engine"));
		properties.setExtentSize(getStringValue(element, "extent_size"));
		properties.setInitialSize(getStringValue(element, "initial_size"));
		properties.setLogFileGroup(getStringValue(element,
				"log_file_group"));

		return properties;
	}

	private static TablespaceProperties loadTablespacePropertiesOracle(Element element) {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();

		properties.setAutoExtend(getBooleanValue(element, "auto_extend"));
		properties.setAutoExtendMaxSize(getStringValue(element,
				"auto_extend_max_size"));
		properties.setAutoExtendSize(getStringValue(element,
				"auto_extend_size"));
		properties.setAutoSegmentSpaceManagement(getBooleanValue(element,
				"auto_segment_space_management"));
		properties.setDataFile(getStringValue(element, "data_file"));
		properties.setFileSize(getStringValue(element, "file_size"));
		properties.setInitial(getStringValue(element, "initial"));
		properties.setLogging(getBooleanValue(element, "logging"));
		properties.setMaxExtents(getStringValue(element, "max_extents"));
		properties.setMinExtents(getStringValue(element, "min_extents"));
		properties.setMinimumExtentSize(getStringValue(element,
				"minimum_extent_size"));
		properties.setNext(getStringValue(element, "next"));
		properties.setOffline(getBooleanValue(element, "offline"));
		properties.setPctIncrease(getStringValue(element, "pct_increase"));
		properties.setTemporary(getBooleanValue(element, "temporary"));

		return properties;
	}

	private static TablespaceProperties loadTablespacePropertiesPostgres(
			Element element) {
		PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

		properties.setLocation(getStringValue(element, "location"));
		properties.setOwner(getStringValue(element, "owner"));

		return properties;
	}

	private void loadChangeTrackingList(ChangeTrackingList changeTrackingList,
			Element parent) {
		Element element = getElement(parent, "change_tracking_list");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("change_tracking");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element changeTrackingElemnt = (Element) nodeList.item(i);
				ChangeTracking changeTracking = this
						.loadChangeTracking(changeTrackingElemnt);

				changeTrackingList.addChangeTracking(changeTracking);
			}
		}
	}

	private ChangeTracking loadChangeTracking(Element element) {
		DiagramContents diagramContents = new DiagramContents();

		loadDiagramContents(diagramContents, element);

		ChangeTracking changeTracking = new ChangeTracking(diagramContents);

		changeTracking.setComment(getStringValue(element, "comment"));
		changeTracking.setUpdatedDate(getDateValue(element, "updated_date"));

		return changeTracking;
	}

	private void loadColumnGroups(GroupSet columnGroups, Element parent,
			LoadContext context) {

		Element element = getElement(parent, "column_groups");

		NodeList nodeList = element.getElementsByTagName("column_group");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element columnGroupElement = (Element) nodeList.item(i);

			ColumnGroup columnGroup = new ColumnGroup();
			
			final String id = getStringValue(columnGroupElement, "id");

			columnGroup.setId(id);
			columnGroup.setGroupName(getStringValue(columnGroupElement,
					"group_name"));

			List<Column> columns = this
					.loadColumns(columnGroupElement, context);
			for (Column column : columns) {
				columnGroup.addColumn((NormalColumn) column);
			}

			columnGroups.add(columnGroup, false);

			context.columnGroupMap.put(id, columnGroup);
		}
		columnGroups.setDirty();
	}

	private static void loadTestDataList(List<TestData> testDataList, Element parent,
			LoadContext context) {

		Element element = getElement(parent, "test_data_list");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("test_data");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element testDataElement = (Element) nodeList.item(i);

				TestData testData = new TestData();
				loadTestData(testData, testDataElement, context);
				testDataList.add(testData);
			}
		}
	}

	private static void loadTestData(TestData testData, Element element,
			LoadContext context) {

		testData.setName(getStringValue(element, "name"));
		testData.setExportOrder(getIntValue(element, "export_order"));

		NodeList nodeList = element.getElementsByTagName("table_test_data");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element tableTestDataElement = (Element) nodeList.item(i);

			TableTestData tableTestData = new TableTestData();

			String tableId = getStringValue(tableTestDataElement,
					"table_id");
			ERTable table = (ERTable) context.nodeElementMap.get(tableId);
			if (table != null) {
				loadDirectTestData(tableTestData.getDirectTestData(),
						tableTestDataElement, context);
				loadRepeatTestData(tableTestData.getRepeatTestData(),
						tableTestDataElement, context);

				testData.putTableTestData(table, tableTestData);
			}
		}

	}

	private static void loadDirectTestData(DirectTestData directTestData,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "direct_test_data");

		NodeList nodeList = element.getElementsByTagName("data");

		List<Map<NormalColumn, String>> dataList = directTestData.getDataList();
		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element dataElement = (Element) nodeList.item(i);

			NodeList columnNodeList = dataElement
					.getElementsByTagName("column_data");

			Map<NormalColumn, String> data = new HashMap<NormalColumn, String>();

			for (int j = 0, m = columnNodeList.getLength(); j < m; j++) {
				Element columnDataElement = (Element) columnNodeList.item(j);

				String columnId = getStringValue(columnDataElement,
						"column_id");
				NormalColumn column = context.columnMap.get(columnId);

				String value = getStringValue(columnDataElement, "value");

				data.put(column, value);
			}

			dataList.add(data);
		}
	}

	private static void loadRepeatTestData(RepeatTestData repeatTestData,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "repeat_test_data");

		int testDataNum = getIntegerValue(element, "test_data_num");
		repeatTestData.setTestDataNum(testDataNum);

		Element dataDefListElement = getElement(element, "data_def_list");

		NodeList nodeList = dataDefListElement.getElementsByTagName("data_def");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element dataDefElement = (Element) nodeList.item(i);

			String columnId = getStringValue(dataDefElement, "column_id");
			NormalColumn column = context.columnMap.get(columnId);

			RepeatTestDataDef dataDef = new RepeatTestDataDef();

			dataDef.setType(getStringValue(dataDefElement, "type"));
			dataDef.setRepeatNum(getIntValue(dataDefElement, "repeat_num"));
			dataDef.setTemplate(getStringValue(dataDefElement, "template"));
			dataDef.setFrom(getStringValue(dataDefElement, "from"));
			dataDef.setTo(getStringValue(dataDefElement, "to"));
			dataDef.setIncrement(getStringValue(dataDefElement, "increment"));
			dataDef.setSelects(getTagValues(dataDefElement, "select"));

			Element modifiedValuesElement = getElement(dataDefElement,
					"modified_values");
			if (modifiedValuesElement != null) {
				NodeList modifiedValueNodeList = modifiedValuesElement
						.getElementsByTagName("modified_value");

				for (int j = 0, m = modifiedValueNodeList.getLength(); j < m; j++) {
					Element modifiedValueNode = (Element) modifiedValueNodeList
							.item(j);

					Integer row = getIntValue(modifiedValueNode, "row");
					String value = getStringValue(modifiedValueNode,
							"value");

					dataDef.setModifiedValue(row, value);
				}
			}

			repeatTestData.setDataDef(column, dataDef);
		}
	}

	private void loadDictionary(Dictionary dictionary, Element parent,
			LoadContext context) {

		Element element = getElement(parent, "dictionary");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("word");

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				Element wordElement = (Element) nodeList.item(i);

				this.loadWord(dictionary, wordElement, context);
			}
		}
	}

	private Word loadWord(Dictionary dictionary, Element element, LoadContext context) {

		String id = getStringValue(element, "id");

		String type = getStringValue(element, "type");

		TypeData typeData = new TypeData(
				getIntegerValue(element, "length"), getIntegerValue(
						element, "decimal"), getBooleanValue(element,
						"array"), getIntegerValue(element,
						"array_dimension"), getBooleanValue(element,
						"unsigned"), getStringValue(element, "args"),
						getStringValue(element, "unit"));

		Word word = new RealWord(Format.null2blank(getStringValue(element,
				"physical_name")), Format.null2blank(getStringValue(
				element, "logical_name")), SqlType.valueOfId(type), typeData,
				Format.null2blank(getStringValue(element, "description")),
				this.database);

		word.getUniqueWord().setId(id);
		dictionary.add(word.getUniqueWord());

		context.wordMap.put(id, word);

		return word;
	}

	private List<Column> loadColumns(Element parent, LoadContext context) {
		List<Column> columns = new ArrayList<Column>();

		Element element = getElement(parent, "columns");

		NodeList groupList = element.getChildNodes();

		for (int i = 0, n = groupList.getLength(); i < n; i++) {
			if (groupList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) groupList.item(i);

			if ("column_group".equals(columnElement.getTagName())) {
				ColumnGroup column = loadColumnGroup(columnElement,
						context);
				columns.add(column);

			} else if ("normal_column".equals(columnElement.getTagName())) {
				NormalColumn column = this.loadNormalColumn(columnElement,
						context);
				columns.add(column);
			}
		}

		return columns;
	}

	private static ColumnGroup loadColumnGroup(Element element, LoadContext context) {
		String key = element.getFirstChild().getNodeValue();

		return context.columnGroupMap.get(key);
	}

	private NormalColumn loadNormalColumn(Element element, LoadContext context) {

		String id = getStringValue(element, "id");

		String wordId = getStringValue(element, "word_id");

		Word word = context.wordMap.get(wordId);

		UniqueWord uniqueWord;
		if (word == null) {
			String type = getStringValue(element, "type");

			word = new RealWord(getStringValue(element, "physical_name"),
					getStringValue(element, "logical_name"),
					SqlType.valueOfId(type), new TypeData(null, null, false,
							null, false, null, null), getStringValue(element,
							"description"), database);

			uniqueWord = word.getUniqueWord();
			uniqueWord.setId(wordId);

			final Word tmp = context.uniqueWordMap.get(uniqueWord);
			if (tmp == null) {
				context.uniqueWordMap.put(uniqueWord, word);
			} else {
				word = tmp;
			}
		}

		final NormalColumn normalColumn = new NormalColumn(word,
				getBooleanValue(element, "not_null"),
				getBooleanValue(element, "primary_key"),
				getBooleanValue(element, "unique_key"),
				getBooleanValue(element, "auto_increment"),
				getStringValue(element, "default_value"),
				getStringValue(element, "constraint"),
				getStringValue(element, "unique_key_name"),
				getStringValue(element, "character_set"),
				getStringValue(element, "collation"));

		Element autoIncrementSettingElement = getElement(element,
				"sequence");
		if (autoIncrementSettingElement != null) {
			Sequence autoIncrementSetting = loadSequence(autoIncrementSettingElement);
			normalColumn.setAutoIncrementSetting(autoIncrementSetting);
		}

		boolean isForeignKey = false;

		String[] relationIds = getTagValues(element, "relation");
		context.columnRelationMap.put(normalColumn, relationIds);

		String[] referencedColumnIds = getTagValues(element,
				"referenced_column");

		if (referencedColumnIds.length != 0) {
			context.columnReferencedColumnMap.put(normalColumn,
					referencedColumnIds);
			isForeignKey = true;
		}

		if (!isForeignKey) {
			context.dictionary.add(normalColumn, true);
		}

		if (id != null && !"null".equals(id)) {
			normalColumn.setId(id);
			context.columnMap.put(id, normalColumn);
		}

		return normalColumn;
	}

	private static void loadSettings(Settings settings, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "settings");

		if (element != null) {
			settings.setDatabase(loadDatabase(element));
			settings.setCapital(getBooleanValue(element, "capital"));
			settings.setTableStyle(Format.null2blank(getStringValue(
					element, "table_style")));

			settings.setNotation(getStringValue(element, "notation"));
			settings.setNotationLevel(getIntValue(element,
					"notation_level"));
			settings.setNotationDependence(getBooleanValue(element,
					"notation_dependence", true));
			settings.setNotationExpandGroup(getBooleanValue(element,
					"notation_expand_group", true));
			settings.setNotationIndex(getBooleanValue(element,
					"notation_index", true));

			settings.setViewMode(getIntValue(element, "view_mode"));
			settings.setOutlineViewMode(getIntValue(element,
					"outline_view_mode"));
			settings.setViewOrderBy(getIntValue(element, "view_order_by"));

			settings.setAutoImeChange(getBooleanValue(element,
					"auto_ime_change"));
			settings.setValidatePhysicalName(getBooleanValue(element,
					"validate_physical_name", true));
			settings.setUseBezierCurve(getBooleanValue(element,
					"use_bezier_curve"));
			settings.setSuspendValidator(getBooleanValue(element,
					"suspend_validator"));
			settings.setCheckUsedWord(getBooleanValue(element,
					"check_used_word", true));

			ExportSetting exportSetting = settings.getExportSetting();
			loadExportSetting(exportSetting, element, context);

			CategorySetting categorySetting = settings.getCategorySetting();
			loadCategorySetting(categorySetting, element, context);

			TranslationSetting translationSetting = settings
					.getTranslationSetting();
			loadTranslationSetting(translationSetting, element, context);

			ModelProperties modelProperties = settings.getModelProperties();
			loadModelProperties(modelProperties, element);

			loadTableProperties(
					(TableProperties) settings.getTableViewProperties(),
					element, context);

		}
	}

	private static void loadExportSetting(ExportSetting exportSetting, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "export_setting");

		if (element != null) {
			exportSetting.setCategoryNameToExport(getStringValue(
					element, "category_name_to_export"));
			exportSetting.setDdlOutput(getStringValue(
					element, "ddl_output"));
			exportSetting.setExcelOutput(getStringValue(
					element, "excel_output"));
			exportSetting.setExcelTemplate(getStringValue(
					element, "excel_template"));
			exportSetting.setImageOutput(getStringValue(
					element, "image_output"));
			exportSetting.setPutERDiagramOnExcel(getBooleanValue(
					element, "put_diagram_on_excel"));
			exportSetting.setUseLogicalNameAsSheet(getBooleanValue(
					element, "use_logical_name_as_sheet"));
			exportSetting.setOpenAfterSaved(getBooleanValue(
					element, "open_after_saved"));

			exportSetting.getDdlTarget().createComment = getBooleanValue(
					element, "create_comment");
			exportSetting.getDdlTarget().createForeignKey = getBooleanValue(
					element, "create_foreignKey");
			exportSetting.getDdlTarget().createIndex = getBooleanValue(
					element, "create_index");
			exportSetting.getDdlTarget().createSequence = getBooleanValue(
					element, "create_sequence");
			exportSetting.getDdlTarget().createTable = getBooleanValue(
					element, "create_table");
			exportSetting.getDdlTarget().createTablespace = getBooleanValue(
					element, "create_tablespace");
			exportSetting.getDdlTarget().createTrigger = getBooleanValue(
					element, "create_trigger");
			exportSetting.getDdlTarget().createView = getBooleanValue(
					element, "create_view");

			exportSetting.getDdlTarget().dropIndex = getBooleanValue(
					element, "drop_index");
			exportSetting.getDdlTarget().dropSequence = getBooleanValue(
					element, "drop_sequence");
			exportSetting.getDdlTarget().dropTable = getBooleanValue(
					element, "drop_table");
			exportSetting.getDdlTarget().dropTablespace = getBooleanValue(
					element, "drop_tablespace");
			exportSetting.getDdlTarget().dropTrigger = getBooleanValue(
					element, "drop_trigger");
			exportSetting.getDdlTarget().dropView = getBooleanValue(
					element, "drop_view");

			exportSetting.getDdlTarget().inlineColumnComment = getBooleanValue(
					element, "inline_column_comment");
			exportSetting.getDdlTarget().inlineTableComment = getBooleanValue(
					element, "inline_table_comment");

			exportSetting.getDdlTarget().commentValueDescription = getBooleanValue(
					element, "comment_value_description");
			exportSetting.getDdlTarget().commentValueLogicalName = getBooleanValue(
					element, "comment_value_logical_name");
			exportSetting.getDdlTarget().commentValueLogicalNameDescription = getBooleanValue(
					element, "comment_value_logical_name_description");
			exportSetting.getDdlTarget().commentReplaceLineFeed = getBooleanValue(
					element, "comment_replace_line_feed");
			exportSetting.getDdlTarget().commentReplaceString = getStringValue(
					element, "comment_replace_string");
			loadExportJavaSetting(exportSetting.getExportJavaSetting(),
					element, context);
			loadExportTestDataSetting(
					exportSetting.getExportTestDataSetting(), element, context);
		}
	}

	private static void loadExportJavaSetting(ExportJavaSetting exportJavaSetting,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "export_java_setting");

		if (element != null) {
			exportJavaSetting.setJavaOutput(getStringValue(
					element, "java_output"));
			exportJavaSetting.setPackageName(Format.null2blank(getStringValue(
					element, "package_name")));
			exportJavaSetting.setClassNameSuffix(Format.null2blank(getStringValue(
					element, "class_name_suffix")));
			exportJavaSetting.setSrcFileEncoding(getStringValue(
					element, "src_file_encoding"));
			exportJavaSetting.setWithHibernate(getBooleanValue(
					element, "with_hibernate"));
		}
	}

	private static void loadExportTestDataSetting(
			ExportTestDataSetting exportTestDataSetting, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "export_testdata_setting");

		if (element != null) {
			exportTestDataSetting.setExportFileEncoding(getStringValue(
					element, "file_encoding"));
			exportTestDataSetting.setExportFilePath(getStringValue(
					element, "file_path"));
			exportTestDataSetting.setExportFormat(getIntValue(
					element, "format"));
		}
	}

	private static void loadCategorySetting(CategorySetting categorySetting,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "category_settings");
		categorySetting.setFreeLayout(getBooleanValue(
				element, "free_layout"));
		categorySetting.setShowReferredTables(getBooleanValue(
				element, "show_referred_tables"));

		Element categoriesElement = getElement(element, "categories");

		NodeList nodeList = categoriesElement.getChildNodes();

		List<Category> selectedCategories = new ArrayList<Category>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element categoryElement = (Element) nodeList.item(i);

			Category category = new Category();

			loadNodeElement(category, categoryElement, context);
			category.setName(getStringValue(categoryElement, "name"));
			boolean isSelected = getBooleanValue(categoryElement,
					"selected");

			String[] keys = getTagValues(categoryElement, "node_element");

			List<NodeElement> nodeElementList = new ArrayList<NodeElement>();

			for (String key : keys) {
				NodeElement nodeElement = context.nodeElementMap.get(key);
				if (nodeElement != null) {
					nodeElementList.add(nodeElement);
				}
			}

			category.setContents(nodeElementList);
			categorySetting.addCategory(category);

			if (isSelected) {
				selectedCategories.add(category);
			}
		}

		categorySetting.setSelectedCategories(selectedCategories);
	}

	private static void loadTranslationSetting(TranslationSetting translationSetting,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "translation_settings");
		if (element != null) {
			translationSetting.setUse(getBooleanValue(element, "use"));

			Element translationsElement = getElement(element,
					"translations");

			NodeList nodeList = translationsElement.getChildNodes();

			List<String> selectedTranslations = new ArrayList<String>();

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element translationElement = (Element) nodeList.item(i);

				selectedTranslations.add(getStringValue(
						translationElement, "name"));
			}

			translationSetting.setSelectedTranslations(selectedTranslations);
		}
	}

	private static void loadEnvironmentSetting(EnvironmentSetting environmentSetting,
			Element parent, LoadContext context) {
		Element settingElement = getElement(parent, "settings");
		Element element = XMLLoader
				.getElement(settingElement, "environment_setting");

		List<Environment> environmentList = new ArrayList<Environment>();

		if (element != null) {
			NodeList nodeList = element.getChildNodes();

			for (int i = 0, n = nodeList.getLength(); i < n; i++) {
				if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element environmentElement = (Element) nodeList.item(i);

				String id = getStringValue(environmentElement, "id");
				String name = getStringValue(environmentElement, "name");
				Environment environment = new Environment(name);
				environment.setId(id);

				environmentList.add(environment);
				context.environmentMap.put(id, environment);
			}
		}

		if (environmentList.isEmpty()) {
			Environment environment = new Environment(
					ResourceString.getResourceString("label.default"));
			environmentList.add(environment);
			context.environmentMap.put("", environment);
		}

		environmentSetting.setEnvironments(environmentList);
	}

	private static void loadModelProperties(ModelProperties modelProperties,
			Element parent) {
		Element element = getElement(parent, "model_properties");

		loadLocation(modelProperties, element);
		loadColor(modelProperties, element);

        modelProperties.setFormatVersion(getIntValue(element, "format_version", 1));

        modelProperties.setDisplay(getBooleanValue(element, "display"), false);
		modelProperties.setCreationDate(getDateValue(element, "creation_date"));
		modelProperties.setUpdatedDate(getDateValue(element, "updated_date"), false);

		NodeList nodeList = element.getElementsByTagName("model_property");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propertyElement = (Element) nodeList.item(i);

			NameValue nameValue = new NameValue(getStringValue(
					propertyElement, "name"), getStringValue(
					propertyElement, "value"));

			modelProperties.addProperty(nameValue);
		}
		
		modelProperties.formatUpgrade();
		
		modelProperties.setDirty();
	}

	private static void loadLocation(NodeElement nodeElement, Element element) {

		int x = getIntValue(element, "x");
		int y = getIntValue(element, "y");
		int width = getIntValue(element, "width");
		int height = getIntValue(element, "height");

		nodeElement.setLocation(new Location(x, y, width, height));
	}

	private static void loadFont(ViewableModel viewableModel, Element element) {
		String fontName = getStringValue(element, "font_name");
		int fontSize = getIntValue(element, "font_size");

		viewableModel.setFontName(fontName);
		viewableModel.setFontSize(fontSize);
	}

	private void loadContents(NodeSet contents, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "contents");

		NodeList nodeList = element.getChildNodes();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Node node = nodeList.item(i);

			if ("table".equals(node.getNodeName())) {
				ERTable table = this.loadTable((Element) node, context);
				contents.addNodeElement(table, false);

			} else if ("view".equals(node.getNodeName())) {
				View view = this.loadView((Element) node, context);
				contents.addNodeElement(view, false);

			} else if ("note".equals(node.getNodeName())) {
				Note note = loadNote((Element) node, context);
				contents.addNodeElement(note, false);

			} else if ("image".equals(node.getNodeName())) {
				InsertedImage insertedImage = loadInsertedImage(
						(Element) node, context);
				contents.addNodeElement(insertedImage, false);
			}
		}
		contents.setDirty();
	}

	private ERTable loadTable(Element element, LoadContext context) {
		ERTable table = new ERTable();

		table.setDiagram(this.diagram);

		loadNodeElement(table, element, context);
		table.setPhysicalName(getStringValue(element, "physical_name"), false);
		table.setLogicalName(getStringValue(element, "logical_name"), false);
		table.setDescription(getStringValue(element, "description"));
		table.setConstraint(getStringValue(element, "constraint"));
		table.setPrimaryKeyName(XMLLoader
				.getStringValue(element, "primary_key_name"));
		table.setOption(getStringValue(element, "option"));

		List<Column> columns = this.loadColumns(element, context);
		table.setColumns(columns, false);

		List<Index> indexes = loadIndexes(element, table, context);
		table.setIndexes(indexes);

		List<ComplexUniqueKey> complexUniqueKeyList =
				loadComplexUniqueKeyList(element, table, context);
		table.setComplexUniqueKeyList(complexUniqueKeyList);

		loadTableProperties(
				(TableProperties) table.getTableViewProperties(), element,
				context);

		table.setDirty();

		return table;
	}

	private View loadView(Element element, LoadContext context) {
		View view = new View();

		view.setDiagram(this.diagram);

		loadNodeElement(view, element, context);
		view.setPhysicalName(getStringValue(element, "physical_name"), false);
		view.setLogicalName(getStringValue(element, "logical_name"), false);
		view.setDescription(getStringValue(element, "description"));
		view.setSql(getStringValue(element, "sql"));

		List<Column> columns = this.loadColumns(element, context);
		view.setColumns(columns, false);

		view.setDirty();

		loadViewProperties((ViewProperties) view.getTableViewProperties(),
				element, context);

		return view;
	}

	private static List<Index> loadIndexes(Element parent, ERTable table,
			LoadContext context) {
		List<Index> indexes = new ArrayList<Index>();

		Element element = getElement(parent, "indexes");
		if (element == null) {
			return indexes; 
		}

		NodeList nodeList = element.getChildNodes();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element indexElement = (Element) nodeList.item(i);

			String type = getStringValue(indexElement, "type");
			if ("null".equals(type)) {
				type = null;
			}

			Index index = new Index(table, getStringValue(indexElement,
					"name"), getBooleanValue(indexElement, "non_unique"),
					getBooleanValue(indexElement, "bitmap"),
					type, getStringValue(indexElement, "description"));

			index.setFullText(getBooleanValue(indexElement, "full_text"));

			loadIndexColumns(index, indexElement, context);

			indexes.add(index);
		}

		return indexes;
	}

	private static void loadIndexColumns(Index index, Element parent,
			LoadContext context) {
		Element element = getElement(parent, "columns");
		NodeList nodeList = element.getChildNodes();

		List<Boolean> descs = new ArrayList<Boolean>();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) nodeList.item(i);

			String id = getStringValue(columnElement, "id");
			NormalColumn column = context.columnMap.get(id);

			Boolean desc = new Boolean(getBooleanValue(columnElement,
					"desc"));

			index.addColumn(column);
			descs.add(desc);
		}

		index.setDescs(descs);
	}

	private static List<ComplexUniqueKey> loadComplexUniqueKeyList(Element parent,
			ERTable table, LoadContext context) {
		List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

		Element element = getElement(parent, "complex_unique_key_list");
		if (element == null) {
			return complexUniqueKeyList;
		}

		NodeList nodeList = element.getChildNodes();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element complexUniqueKeyElement = (Element) nodeList.item(i);

			String id = getStringValue(complexUniqueKeyElement, "id");
			String name = getStringValue(complexUniqueKeyElement, "name");

			ComplexUniqueKey complexUniqueKey = new ComplexUniqueKey(name);
			complexUniqueKey.setId(id);

			loadComplexUniqueKeyColumns(complexUniqueKey,
					complexUniqueKeyElement, context);

			complexUniqueKeyList.add(complexUniqueKey);

			context.complexUniqueKeyMap.put(id, complexUniqueKey);
		}

		return complexUniqueKeyList;
	}

	private static void loadComplexUniqueKeyColumns(ComplexUniqueKey complexUniqueKey,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "columns");
		NodeList nodeList = element.getChildNodes();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) nodeList.item(i);

			String id = getStringValue(columnElement, "id");
			NormalColumn column = context.columnMap.get(id);

			complexUniqueKey.addColumn(column);
		}
	}

	private static void loadTableProperties(TableProperties tableProperties,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "table_properties");

		String tablespaceId = getStringValue(element, "tablespace_id");
		Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
		tableProperties.setTableSpace(tablespace);

		tableProperties.setSchema(getStringValue(element, "schema"));

		if (tableProperties instanceof MySQLTableProperties) {
			loadTablePropertiesMySQL(
					(MySQLTableProperties) tableProperties, element);

		} else if (tableProperties instanceof PostgresTableProperties) {
			loadTablePropertiesPostgres(
					(PostgresTableProperties) tableProperties, element);

		} else if (tableProperties instanceof OracleTableProperties) {
			loadTablePropertiesOracle(
					(OracleTableProperties) tableProperties, element);

		}
	}

	private static void loadTablePropertiesMySQL(MySQLTableProperties tableProperties,
			Element element) {

		tableProperties.setCharacterSet(getStringValue(element,
				"character_set"));
		tableProperties.setCollation(getStringValue(element, "collation"));
		tableProperties.setStorageEngine(getStringValue(element,
				"storage_engine"));
		tableProperties.setPrimaryKeyLengthOfText(getIntegerValue(element,
				"primary_key_length_of_text"));
	}

	private static void loadTablePropertiesPostgres(
			PostgresTableProperties tableProperties, Element element) {
		tableProperties.setWithoutOIDs(getBooleanValue(element,
				"without_oids"));
	}

	private static void loadTablePropertiesOracle(OracleTableProperties tableProperties,
			Element element) {

		tableProperties.setCharacterSet(getStringValue(element,
				"character_set"));
	}

	private static void loadViewProperties(ViewProperties viewProperties,
			Element parent, LoadContext context) {
		Element element = getElement(parent, "view_properties");

		if (element != null) {
			String tablespaceId = getStringValue(element, "tablespace_id");
			Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
			viewProperties.setTableSpace(tablespace);

			viewProperties.setSchema(getStringValue(element, "schema"));
		}
	}

	private static Note loadNote(Element element, LoadContext context) {
		Note note = new Note();

		note.setText(getStringValue(element, "text"));
		loadNodeElement(note, element, context);

		return note;
	}

	private static InsertedImage loadInsertedImage(Element element, LoadContext context) {
		InsertedImage insertedImage = new InsertedImage();

		insertedImage
				.setBase64EncodedData(getStringValue(element, "data"));
		insertedImage.setHue(getIntValue(element, "hue"));
		insertedImage.setSaturation(getIntValue(element, "saturation"));
		insertedImage.setBrightness(getIntValue(element, "brightness"));
		insertedImage.setAlpha(getIntValue(element, "alpha", 255));
		insertedImage.setFixAspectRatio(getBooleanValue(element,
				"fix_aspect_ratio"));

		loadNodeElement(insertedImage, element, context);

		return insertedImage;
	}

	private static void loadNodeElement(NodeElement nodeElement, Element element,
			LoadContext context) {
		String id = getStringValue(element, "id");

		nodeElement.setId("null".equals(id) ? null : id);

		loadLocation(nodeElement, element);
		loadColor(nodeElement, element);
		loadFont(nodeElement, element);

		context.nodeElementMap.put(id, nodeElement);

		loadConnections(element, context);
	}

	private static void loadConnections(Element parent, LoadContext context) {
		Element element = getElement(parent, "connections");

		if (element == null) {
			return;
		}

		NodeList nodeList = element.getChildNodes();

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element connectionElement = (Element) nodeList.item(i);

			if ("relation".equals(connectionElement.getTagName())) {
				loadRelation(connectionElement, context);

			} else if ("comment_connection".equals(connectionElement
					.getTagName())) {
				loadCommentConnection(connectionElement, context);
			}
		}
	}

	private static void loadRelation(Element element, LoadContext context) {
		boolean referenceForPK = getBooleanValue(element,
				"reference_for_pk");
		Relation connection = new Relation(referenceForPK, null, null);

		load(connection, element, context);

		connection.setChildCardinality(getStringValue(element,
				"child_cardinality"));
		connection.setParentCardinality(getStringValue(element,
				"parent_cardinality"));
		connection.setName(getStringValue(element, "name"));
		connection.setOnDeleteAction(getStringValue(element,
				"on_delete_action"));
		connection.setOnUpdateAction(getStringValue(element,
				"on_update_action"));

		String referencedComplexUniqueKeyId = getStringValue(element,
				"referenced_complex_unique_key");
		if (!"null".equals(referencedComplexUniqueKeyId)) {
			context.referencedComplexUniqueKeyMap.put(connection,
					referencedComplexUniqueKeyId);
		}
		String referencedColumnId = getStringValue(element,
				"referenced_column");
		if (referencedColumnId != null && !"null".equals(referencedColumnId)) {
			context.referencedColumnMap.put(connection, referencedColumnId);
		}
	}

	private static void loadCommentConnection(Element element, LoadContext context) {
		CommentConnection connection = new CommentConnection();

		load(connection, element, context);
	}

	private static void load(ConnectionElement connection, Element element,
			LoadContext context) {
		String id = getStringValue(element, "id");

		connection.setId(id);
		context.connectionMap.put(id, connection);

		String source = getStringValue(element, "source");
		String target = getStringValue(element, "target");

		context.connectionSourceMap.put(connection, source);
		context.connectionTargetMap.put(connection, target);

		connection.setSourceLocationp(getIntValue(element, "source_xp", -1),
				getIntValue(element, "source_yp", -1));
		connection.setTargetLocationp(getIntValue(element, "target_xp", -1),
				getIntValue(element, "target_yp", -1));

		NodeList nodeList = element.getElementsByTagName("bendpoint");

		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element bendPointElement = (Element) nodeList.item(i);

			Bendpoint bendpoint = new Bendpoint(getIntValue(
					bendPointElement, "x"), getIntValue(bendPointElement,
					"y"));

			bendpoint.setRelative(getBooleanValue(bendPointElement,
					"relative"));

			connection.addBendpoint(i, bendpoint, false);
		}
		connection.setDirtyForBendpoint();
	}

	private static void loadDBSetting(ERDiagram diagram, Element element) {
		Element dbSettingElement = getElement(element, "dbsetting");

		if (dbSettingElement != null) {
			String dbsystem = getStringValue(element, "dbsystem");
			String server = getStringValue(element, "server");
			int port = getIntValue(element, "port");
			String database = getStringValue(element, "database");
			String user = getStringValue(element, "user");
			String password = getStringValue(element, "password");
			boolean useDefaultDriver = getBooleanValue(element,
					"use_default_driver", true);
			if (StandardSQLDBManager.ID.equals(dbsystem)) {
				useDefaultDriver = false;
			}

			String url = getStringValue(element, "url");
			String driverClassName = getStringValue(element,
					"driver_class_name");

			DBSetting dbSetting = new DBSetting(dbsystem, server, port,
					database, user, password, useDefaultDriver, url,
					driverClassName);
			diagram.setDbSetting(dbSetting);
		}
	}

	private static void loadPageSetting(ERDiagram diagram, Element element) {
		Element dbSettingElement = getElement(element, "page_setting");

		if (dbSettingElement != null) {
			boolean directionHorizontal = getBooleanValue(element,
					"direction_horizontal");
			int scale = getIntValue(element, "scale");
			String paperSize = getStringValue(element, "paper_size");
			int topMargin = getIntValue(element, "top_margin");
			int leftMargin = getIntValue(element, "left_margin");
			int bottomMargin = getIntValue(element, "bottom_margin");
			int rightMargin = getIntValue(element, "right_margin");

			PageSetting pageSetting = new PageSetting(directionHorizontal,
					scale, paperSize, topMargin, rightMargin, bottomMargin,
					leftMargin);
			diagram.setPageSetting(pageSetting);
		}
	}

	private static void loadColor(ViewableModel model, Element element) {
		int[] rgb = new int[] { 255, 255, 255 };
		Element color = getElement(element, "color");

		if (color != null) {
			rgb[0] = getIntValue(color, "r");
			rgb[1] = getIntValue(color, "g");
			rgb[2] = getIntValue(color, "b");
		}

		model.setColor(rgb[0], rgb[1], rgb[2]);
	}

	private static void loadDefaultColor(ERDiagram diagram, Element element) {
		int[] rgb = new int[] { 255, 255, 255 };
		Element color = getElement(element, "default_color");

		if (color != null) {
			rgb[0] = getIntValue(color, "r");
			rgb[1] = getIntValue(color, "g");
			rgb[2] = getIntValue(color, "b");
		}

		diagram.setDefaultColor(rgb[0], rgb[1], rgb[2]);
	}
}
