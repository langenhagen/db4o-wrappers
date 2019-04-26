package logic.db;
/**
 * @author Barn
 * Db_Ifc
 * This is the Interface for any AAL_LogicDB-Class and it provides
 * the main functions for a keyword-driven database.
 *
 * Version 091130
 */

import java.util.Vector;

public interface Db_Ifc {

	public void openDB();							// opens DB
	public void closeDB();							// closes DB
	public String getDBFilename();					// gets filename of DB
	public boolean store( String key, Object o);	// stores key and associated data
	public Object get( String key);					// gets data
	public Vector<DbObject> getAll();				// gets all data + keys
	public boolean delete( String key);				// deletes the specified object
	public boolean clear();							// clears the db
}
