package org.insightech.er.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.db.impl.access.AccessDBManager;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.h2.H2DBManager;
import org.insightech.er.db.impl.hsqldb.HSQLDBDBManager;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.sqlite.SQLiteDBManager;
import org.insightech.er.db.impl.sqlserver.SqlServerDBManager;
import org.insightech.er.db.impl.sqlserver2008.SqlServer2008DBManager;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.editor.model.ERDiagram;

public class DBManagerFactory {

	private static final Map<String, DBManager> DB_LIST = new HashMap<String, DBManager>();

	private static final List<String> DB_ID_LIST = new ArrayList<String>();

	static {
		addDB(new StandardSQLDBManager());
		addDB(new DB2DBManager());
		addDB(new HSQLDBDBManager());
		addDB(new H2DBManager());
		addDB(new AccessDBManager());
		addDB(new MySQLDBManager());
		addDB(new OracleDBManager());
		addDB(new PostgresDBManager());
		addDB(new SQLiteDBManager());
		addDB(new SqlServerDBManager());
		addDB(new SqlServer2008DBManager());
	}

	static void addDB(DBManager manager) {
		DB_LIST.put(manager.getId(), manager);
		DB_ID_LIST.add(manager.getId());
	}

	public static DBManager getDBManager(String database) {
		DBManager manager = DB_LIST.get(database);
		if (manager == null) {
			throw new IllegalArgumentException(
					ResourceString
						.getResourceString("error.database.is.not.supported")
							+ database);
		}
		return manager;
	}

	public static DBManager getDBManager(ERDiagram diagram) {
		return getDBManager(diagram.getDatabase());
	}

	public static List<String> getAllDBList() {
		return DB_ID_LIST;
	}

}
