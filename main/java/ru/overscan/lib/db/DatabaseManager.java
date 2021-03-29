package ru.overscan.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {
	private static SQLiteOpenHelper mHelper;
	private static int mOpenCounter = 0;
	private static SQLiteDatabase mDatabase;
	
	
	public static synchronized void initialize(SQLiteOpenHelper helper) {
        if (mHelper == null) {
            mHelper = helper;
        }
    }
	
//	public static synchronized void initialize(Context ctx) {
//        if (mHelper == null) {
//            mHelper = new DBHelper(ctx);
//        }
//    }
	
	private static boolean isStateOk() {
		if (mHelper == null)
			throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                " is not initialized, call initialize(..) method first.");
		else return true;
	}
	

	public static synchronized SQLiteDatabase openDB() {
		if (DatabaseManager.isStateOk()) {
			mOpenCounter++;
			if(mOpenCounter == 1) mDatabase = mHelper.getWritableDatabase();
			return mDatabase;
		}
		else return null;
    }	

//	public synchronized SQLiteDatabase openReadable() {
//        mOpenCounter++;
//        if(mOpenCounter == 1) {
//            // Opening new database
//            mDatabase = getReadableDatabase();
//        }
//        return mDatabase;
//    }
	
    public static synchronized void closeDB() {
		if (isStateOk()) {
	        mOpenCounter--;
	        if(mOpenCounter == 0) mDatabase.close();
		}
    }

}
