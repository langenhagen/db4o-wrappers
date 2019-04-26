package logic.db;
/**
 * @author Barn
 * Db_Fast
 * This class represents a file-based database for any kind of Objects,
 * each one associated with an unique String-identifier.
 *
 * Db_Fast provides methods for storing, deleting and retreiving data
 * with keywords from a db4o database (www.db4o.com).
 *
 * Db_Fast is the fast equivalent of the class Db_Std,
 * which additionally calls the methods openDB() and closeDB().
 * However, when security and simplicity are a bigger matter than Speed,
 * it is recommended to use Db_Std.
 *
 * Caution: this Class needs several classes of the db4o-library!
 *
 * db4o is an open source database library for file-based object-databases
 * and needs to be referenced in this this project.
 * The db4o-Version I used when implementing was 7.4 (db4o-7.4-java5.jar)
 *
 * Version 091215
 */

import java.util.Vector;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class Db_Fast implements Db_Ifc {
	private ObjectContainer db;
	private String filename;

	/**
	 * Standard constructor. Creates an instance of a database
	 * and sets it up with the filename 'AAL_LogicDB.db' in the
	 * working directory. If no such DB file exists, it will be
	 * created, otherwise the existing one is used. The filename
	 * cannot be changed afterwards.
	 */
	public Db_Fast(){ filename = "AAL_LogicDB.db"; }

	/**
	 * Second constructor. Creates an instance of a database
	 * and sets it up with a specified filename in working dir
	 * or with an absolute path. If no such DB file exists, it will
	 * be created, otherwise the existing one is used.
	 * @param filename
	 * the name or path of the DB file with or without the Ending ".db".
	 * It can't be changed afterwards.
	 */
	public Db_Fast( String filename){
		if( filename.lastIndexOf(".db") != filename.length() -3)
			filename = filename.concat(".db");

		this.filename = filename;
	}

	/**
	 * Opens the database and makes it ready to work.
	 * The DB needs to be open when methods are used!
	 */
	public void openDB(){ db = Db4o.openFile(filename); }

	/**
	 * Closes an open database.
	 */
	public void closeDB(){ db.close(); }

	/**
	 * Gets the filename of the AAL_LogicDB-Object
	 * @return
	 * the appropriate filename or path.
	 */
	public String getDBFilename(){ return filename; }

	/**
	 * Stores an object with a special identification key
	 * into the database, if not already an dbObject
	 * with the same key exists.
	 * @param key
	 * the keyword to the data associated with. Because for
	 * identifying, the keyword must be unique in this DB.
	 * @param o
	 * the data object you want to store in the db.
	 * Note: it is strongly recommended not to pass an Object of
	 * the type DbObject as the argument, because of internal use
	 * of class informations.
	 * @return
	 * if successful returns true.
	 * otherwise returns false.
	 */
	public boolean store( String key, Object o){
		boolean ret = false;

		try{
			// if key is unique
			DbObject tmp = new DbObject( key, null);
			if( db.queryByExample(tmp).size() == 0){
				db.store( new DbObject(key, o));
				ret = true;
			}
		}catch(Exception e){
			System.err.println("AAL_LogicDB: Couldn't store element into DB: " + e.getClass());
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Gets the object with the specified keyword.
	 * @param key
	 * the keyword as a string.
	 * @return
	 * returns an Object from the DB.
	 * returns null if the key is not associated with
	 * any data or some error occured.
	 */
	@SuppressWarnings("unchecked")
	public Object get( String key){
		Object ret = null;
		try{
			DbObject tmp = new DbObject(key, null);
			ObjectSet result = db.queryByExample(tmp);

			// if key is valid
			if( result.size() > 0)
				ret = ((DbObject)result.next()).getData();
		}catch( Exception e){
			System.err.println("AAL_LogicDB: Couldn't get element from DB: " + e.getClass());
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Retrieves all elements of the database.
	 * @return
	 * Returns an Vector of DbObjects of all Elements
	 * of the DB or null in case of error.
	 */
	public Vector<DbObject> getAll(){
		ObjectSet<DbObject> tmp;
		Vector<DbObject> ret = new Vector<DbObject>();

		try{
			tmp = db.queryByExample(null);

			// Put elements into vector
			for(int i=0; i<tmp.size(); i++){
				Object o = tmp.get(i);
				if( o.getClass() != DbObject.class)
					continue;
				ret.add(tmp.get(i));
			}
		}catch( Exception e){
			System.err.println("AAL_LogicDB: Couldn't get all keys from DB: " + e.getClass());
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Removes the object with the specified keyword from the DB.
	 * @param key
	 * the keyword as a string.
	 * @return
	 * returns true, if an object has been deleted.
	 * returns false, there is no keyword associated with
	 * any data or some error occured.
	 */
	@SuppressWarnings("unchecked")
	public boolean delete( String key){
		boolean ret = false;

		try{
			DbObject tmp = new DbObject(key, null);
			ObjectSet result = db.queryByExample(tmp);
			DbObject found = null;

			// if key is valid
			if( result.size() > 0){
				found = (DbObject)result.next();
				db.delete(found);
				ret = true;
			}
		}catch( Exception e){
			System.err.println("AAL_LogicDB: Couldn't delete element from DB: " + e.getClass());
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * Removes all elements from the DB.
	 * @return
	 * Returns true if removing was successful.
	 * Returns false if an error occured.
	 */
	@SuppressWarnings("unchecked")
	public boolean clear(){
		boolean ret = false;

		try{
			ObjectSet results = db.queryByExample(null);
			while( results.hasNext())
				db.delete(results.next());
			ret = true;
		}catch(Exception e){
			System.err.println("Couldn't clear DB: " + e.getClass());
			e.printStackTrace();
		}
		return ret;
	}

}
