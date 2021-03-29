package ru.overscan.lib.db;

import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



// после создания таблиц и полей, выховите init()

public class Database {
	static final String TAG = "MyDatabase";
	
	private static Database instance;	
//	private String name;
//	private int version;
	
	HashMap<String, DBTable> tables;
	Context context;
	OpenHelper openHelper;
	int openCounter;
	SQLiteDatabase openedDB;
	public boolean testing;
	
	private ContentCreator contentCreator;
	
    public interface ContentCreator {    	
    	public String getName();
    	public int getVersion();
        public void makeContent(Database db);
        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
    
    public static synchronized Database getInstance() {
		if (instance == null)
			throw new IllegalStateException(Database.class.getSimpleName() +
                " is not initialized, call initialize(..) method first.");
		else return instance;
//         if (instance == null) {
//             instance = new TestDB(context);
//         }
//         return instance;
    }	

    public static Database initialize(Context context, ContentCreator content) {
        if (instance == null) {
//            instance = handler.createDB(context);
            instance = new Database(context, content);
        }
        return instance;
   }	

	

//	public Database(Context context, ContentCreator handler) {
//		this(context, "overscan.db", 1, handler);
//		//
//	}
//	public Database(Context context, String name, int version,
//			ContentCreator handler) {

	public Database(Context context, ContentCreator creator) {
//		this.name = "overscan.db";
//		this.name = handler.getName();
//		this.version = handler.getVersion();
		this.context = context;
		contentCreator = creator;
		openCounter = 0;
		tables = new HashMap<String, DBTable>();
		testing = true;
		contentCreator.makeContent(this);
		//
	}
	
	public void init() {
		openHelper = new OpenHelper(context);		
	}
	
	public void addTable(DBTable t) {
		t.setDatabase(this);
		tables.put(t.name, t);
	}

	public DBTable getTable(String name) {
		return tables.get(name);
	}
	
	
	// возможно при массовых операциях с базой (например через объект DBTable) 
	// имеет смысл вначале сделать отдельный open(), в конце работы close()
	public synchronized SQLiteDatabase open() {
		if (openHelper != null) {
			openCounter++;
			if(openCounter == 1) openedDB = openHelper.getWritableDatabase();
			return openedDB;
		}
		else return null;
    }	

    public synchronized void close() {
		if (openHelper != null && openCounter > 0) {
	        openCounter--;
	        if (openCounter == 0) openedDB.close();
		}
    }
    
//	public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		
//	}
    
    
	
	class OpenHelper extends SQLiteOpenHelper {
		
		public OpenHelper(Context context) {
			super(context, contentCreator.getName(), null, contentCreator.getVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//
			String[] sqls;
			for (DBTable t: tables.values()) {
				sqls = t.makeCreateSqls();
				for(int i = 0; i < sqls.length; i++) {
					Log.d(TAG, sqls[i]);
					db.execSQL(sqls[i]);
				}
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//
	         // Recreates the database with a new version
			if (testing) {
				dropAllTables(db);
				onCreate(db);
			}
			else contentCreator.upgrade(db, oldVersion, newVersion);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//
			if (testing) {
				dropAllTables(db);
//	         // Recreates the database with a new version
				onCreate(db);
			}
		}
	}
		
	
	private void dropAllTables(SQLiteDatabase db) {
		String sql;
		for (DBTable t: tables.values()) {
			sql = t.makeDeleteSql();
			Log.d(TAG, sql);
			db.execSQL(sql);
		}
	}

	
}
