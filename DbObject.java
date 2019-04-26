package logic.db;
/**
 * @author Barn
 * DbObject
 * This class is used to wrap objects to put them into db4o
 * with the ability of a string-identifier on an arbitrary element
 *
 * Version 091130
 */
public class DbObject {

	private String key;		// string-identifier
	private Object data;	// object to store

	/**
	 * Constructor
	 * @param key
	 * the string-value to identify the wrapped object
	 * @param data
	 * the object to store
	 */
	public DbObject( String key, Object data){
		this.key = key;
		this.data = data;
	}

	/**
	 * Gets the keyword of the dbObject
	 * @return
	 * the string-identifier
	 */
	public String getKey(){ return key; }

	/**
	 * Gets the data of the dbObject
	 * @return
	 * the data Object
	 */
	public Object getData(){ return data; }

	/**
	 * Sets the keyword of the dbObject
	 * @param key
	 * the new keyword as a string
	 */
	public void setKey( String key){ this.key = key; }

	/**
	 * Sets the data of the dbObject
	 * @param data
	 * the new generic object to store
	 */
	public void setData( Object data){ this.data = data; }

	/**
	 * Gets the data of the dbObject.
	 * This method is only a duplicate of getData()
	 * provided as a shortcut.
	 * @return
	 * the data Object
	 */
	public Object d(){ return getData(); }

}