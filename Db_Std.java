package logic.db;
/**
 * @author Barn
 * Db_Std
 * This class represents a file-based database for any kind of Objects,
 * each one associated with an unique identifier.
 *
 * AAL_LogicDB provides methods for storing, deleting and retrieving data
 * with keywords from a db4o database (www.db4o.com).
 *
 * However, when speed is a matter, the use of Db_Fast, which
 * does not automatically take care of opening and closing the DB,
 * is recommended.
 *
 * Caution: this Class needs several classes of the db4o-library!
 *
 * db4o is an open source database library for file-based object-databases
 * and needs to be referenced in this this project.
 * The db4o-Version I used while implementing was 7.4 (db4o-7.4-java5.jar)
 *
 * Version 091215
 */

import java.util.Vector;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class Db_Std implements Db_Ifc {
	private ObjectContainer db;
	private String filename;

	/**
	 * Standard constructor. Creates an instance of a database
	 * and sets it up with the filename 'AAL_LogicDB.db' in the
	 * working directory. If no such DB file exists, it will be
	 * created, otherwise the existing one is used. The filename
	 * cannot be changed afterwards.
	 */
	public Db_Std(){
		filename = "AAL_LogicDB.db";
		db = Db4o.openFile("AAL_LogicDB.db");
		db.close();
	}

	/**
	 * Second constructor. Creates an instance of a database
	 * and sets it up with a specified filename in working dir
	 * or with an absolute path. If no such DB file exists, it will
	 * be created, otherwise the existing one is used.
	 * @param filename
	 * the name or path of the DB file with or without the Ending ".db".
	 * It can't be changed afterwards.
	 */
	public Db_Std( String filename){
		if( filename.lastIndexOf(".db") != filename.length() -3)
			filename = filename.concat(".db");

		this.filename = filename;
		db = Db4o.openFile(filename);
		db.close();
	}

	/**
	 * Opens the database and makes it ready.
	 * This method is not necessary in AAL_LogicDB
	 * and has no functionality, but for
	 * compatibility reasons it is implemented.
	 */
	public void openDB(){ }

	/**
	 * Closes an open database.
	 * This method is not necessary in AAL_LogicDB
	 * and has no functionality, but for
	 * compatibility reasons it is implemented.
	 */
	public void closeDB(){ }

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
		db = Db4o.openFile(filename);
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
		db.close();
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
		db = Db4o.openFile(filename);
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
		db.close();
		return ret;
	}

	/**
	 * Retrieves all elements of the database.
	 * @return
	 * Returns an Vector of DbObjects of all Elements
	 * of the DB or null in case of error.
	 */
	public Vector<DbObject> getAll(){
		db = Db4o.openFile(filename);
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
		db.close();
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
		db = Db4o.openFile(filename);
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
		db.close();
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
		db = Db4o.openFile(filename);
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
		db.close();
		return ret;
	}

}
