package org.insightech.er.db;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.impl.access.AccessEclipseDBManager;
import org.insightech.er.db.impl.db2.DB2EclipseDBManager;
import org.insightech.er.db.impl.h2.H2EclipseDBManager;
import org.insightech.er.db.impl.hsqldb.HSQLDBEclipseDBManager;
import org.insightech.er.db.impl.mysql.MySQLEclipseDBManager;
import org.insightech.er.db.impl.oracle.OracleEclipseDBManager;
import org.insightech.er.db.impl.postgres.PostgresEclipseDBManager;
import org.insightech.er.db.impl.sqlite.SQLiteEclipseDBManager;
import org.insightech.er.db.impl.sqlserver.SqlServerEclipseDBManager;
import org.insightech.er.db.impl.sqlserver2008.SqlServer2008EclipseDBManager;
import org.insightech.er.db.impl.standard_sql.StandardSQLEclipseDBManager;
import org.insightech.er.editor.model.ERDiagram;

public class EclipseDBManagerFactory {

	private static final List<EclipseDBManager> DB_LIST = new ArrayList<EclipseDBManager>();

	static {
		addDB(new StandardSQLEclipseDBManager());
		addDB(new DB2EclipseDBManager());
		addDB(new HSQLDBEclipseDBManager());
		addDB(new H2EclipseDBManager());
		addDB(new AccessEclipseDBManager());
		addDB(new MySQLEclipseDBManager());
		addDB(new OracleEclipseDBManager());
		addDB(new PostgresEclipseDBManager());
		addDB(new SQLiteEclipseDBManager());
		addDB(new SqlServerEclipseDBManager());
		addDB(new SqlServer2008EclipseDBManager());
	}

	static void addDB(EclipseDBManager manager) {
		DB_LIST.add(manager);
	}

	public static EclipseDBManager getEclipseDBManager(String database) {
		for (EclipseDBManager manager : DB_LIST) {
			if (manager.getId().equals(database)) {
				return manager;
			}
		}

		throw new IllegalArgumentException(
				ResourceString
						.getResourceString("error.database.is.not.supported")
						+ database);
	}

	public static EclipseDBManager getEclipseDBManager(ERDiagram diagram) {
		return getEclipseDBManager(diagram.getDatabase());
	}

}
