package ru.overscan.lib.db;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import ru.overscan.lib.data.Record;
import ru.overscan.lib.data.Table;
import ru.overscan.lib.sys.ErrorCollector;

public class DBTableAsync {
	
	static final int RETURNS_RECORD = 0;
	static final int RETURNS_TABLE = 1;
	static final int RETURNS_OBJECT = 2;
	static final int JUST_FINISHED = 3;
	
	public interface OnRecordReady {
		public void handleRecord(Record rec);
	} 
	OnRecordReady onRecordReady;

	public interface OnTableReady {
		public void handleTable(Table tab);
	} 
	OnTableReady onTableReady;

	public interface OnObjectReady {
		public void handleObject(Object obj);
	} 
	OnObjectReady onObjectReady;
	
	public interface OnFinished {
		public void handle();
	}
	OnFinished onFinished;
	
//	private SparseArray<Record> recordResults;
//	private SparseArray<Table> tableResults;
	
	
	QueryHandler queryHandler;
	DBTable dbTable;
	
	
	public DBTableAsync(String name) {
		dbTable = Database.getInstance().getTable(name);
		if (dbTable == null) throw new Error("Not found table " + name);
//		super(name);
//		dbTable = new DBTable(name);
		//
		init();
	}

//	public DBTableAsync(String name, boolean noMakeDefaultIdField) {
//		super(name, noMakeDefaultIdField);
//		init();
////		dbTable = new DBTable(name);
//		//
//	}
	
	private void init(){
		queryHandler = new QueryHandler(this);
	}

// ------------ получение значения ---------------------------------
	
	public String getValue(String id) {
		Thread t = new Thread(this.new DBQuery(DBQuery.GET_QUERY_VALUE, id));
		t.start();
		return null;
	}

	public String getValue(String id, String fieldName) {
		Thread t = new Thread(this.new DBQuery(
				DBQuery.GET_QUERY_FIELD_VALUE, id, fieldName));
		t.start();
		return null;		
	}
		
	
// ------------ получение записи ---------------------------------
	
	public Record get(String id) {
		get(new String[] {id});
		return null;
	}
	
	public Record get(String[] key) {
		Thread t = new Thread(this.new DBQuery(DBQuery.GET_QUERY, key));
		t.start();
		return null;
	}

// ------------ обновление или вставка ----------------------------

	public int set(String id, String value) {
		Thread t = new Thread(this.new DBQuery(DBQuery.SET_QUERY_VALUE, id, value));
		t.start();
		return 0;
	}

	public int set(String id, String[] values) {
		Thread t = new Thread(this.new DBQuery(DBQuery.SET_QUERY_VALUES, id, values));
		t.start();
		return 0;
	}
	
	public int set(String id, Record r) {
		set(new String[] {id}, r);
		return 0;
	}
	
	public int set(String[] key, Record r) {
		Thread t = new Thread(this.new DBQuery(DBQuery.SET_QUERY, key, r));
		t.start();		
		return 0;
	}
	
// ---------------------------------------------------------------

	class DBQuery implements Runnable {
		static final int GET_QUERY = 0; 
		static final int SET_QUERY = 1; 
		static final int INSERT_QUERY = 2; 
		static final int DELETE_QUERY = 3; 
		static final int DELETE_ALL_QUERY = 4; 
		static final int GET_QUERY_VALUE = 5; 
		static final int GET_QUERY_FIELD_VALUE = 6; 
		static final int SET_QUERY_VALUE = 7; 
		static final int SET_QUERY_VALUES = 8; 

		
		String[] key;
		String[] values;
		Record rec;
		int queryType;
		String val2;
		String val1;
		
		DBQuery(int queryType, String[] key) {
			this.queryType = queryType;
			this.key = key;
		}

		DBQuery(int queryType, String[] key, Record r) {
			this.queryType = queryType;
			this.key = key;
			this.rec = r;
		}
		
		DBQuery(int queryType, String val1, String[] values) {
			this.queryType = queryType;
			this.val1 = val1;
			this.values = values;
		}
		
		DBQuery(int queryType, String val1) {
			this.val1 = val1;
			this.queryType = queryType;
		}

		DBQuery(int queryType, String val1, String val2) {
			this.queryType = queryType;
			this.val1 = val1;
			this.val2 = val2;
		}
		
		@Override
		public void run() {
			//
//			int key = getNextKey(recordResults);
			Message msg = null;
			switch (this.queryType) {
				case DBQuery.GET_QUERY:
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_RECORD, 
							dbTable.get(key));
					break;
				case DBQuery.GET_QUERY_VALUE:
//					String s = DBTable.super.getValue(id);
//					msg = queryHandler.obtainMessage(DBTable.RETURNS_OBJECT, (Object)s);
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_OBJECT, 
							dbTable.getValue(val1));
					break;
				case DBQuery.GET_QUERY_FIELD_VALUE:
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_OBJECT, 
							dbTable.getValue(val1, val2));
					break;
				case DBQuery.SET_QUERY_VALUE:
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_OBJECT, 
							dbTable.set(val1, val2));
					break;
				case DBQuery.SET_QUERY_VALUES:
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_OBJECT, 
							dbTable.set(val1, values));
					break;
				case DBQuery.SET_QUERY:
					msg = queryHandler.obtainMessage(DBTableAsync.RETURNS_OBJECT, 
							dbTable.set(key, rec));
					break;
			
			}
			if (msg != null && !queryHandler.sendMessage(msg))
				ErrorCollector.add("DBTable: Не помещено сообщение в очередь");
			
		}
		
	}
	
	
	static class QueryHandler extends Handler {
		WeakReference<DBTableAsync> tab;
		
		QueryHandler(DBTableAsync t) {
			tab = new WeakReference<DBTableAsync>(t);
		}

		@Override
		public void handleMessage(Message msg) {
			DBTableAsync t = tab.get();
			switch (msg.what) {
	            case DBTableAsync.RETURNS_RECORD: 
	            	if (t.onRecordReady != null) 
	            		t.onRecordReady.handleRecord((Record) msg.obj);
	            	break;			
	            case DBTableAsync.RETURNS_TABLE: 
	            	if (t.onTableReady != null) 
	            		t.onTableReady.handleTable((Table) msg.obj);
	            	break;			
	            case DBTableAsync.RETURNS_OBJECT: 
	            	if (t.onObjectReady != null) 
	            		t.onObjectReady.handleObject(msg.obj);
	            	break;			
	            case DBTableAsync.JUST_FINISHED: 
	            	if (t.onTableReady != null) 
	            		t.onFinished.handle();
	            	break;			
			}
		}
		
	}
	
	

//	synchronized private int getNextKey(SparseArray<?> arr) {
//		int i = 0;
//		while(i < 1000){
//		  if (arr.indexOfKey(i) < 0) return i;
//		  else i++;
//		}
//		if (i == 1000) 
//			ErrorCollector.add("Достигнут макс.придел (1000) ключей в классе DBTable");
//		return i;
//	}
	public void setOnRecordReady(OnRecordReady onRecordReady) {
		this.onRecordReady = onRecordReady;
	}

	public void setOnTableReady(OnTableReady onTableReady) {
		this.onTableReady = onTableReady;
	}

	public void setOnObjectReady(OnObjectReady onObjectReady) {
		this.onObjectReady = onObjectReady;		
	}
	
	public void setOnFinished(OnFinished onFinished) {
		this.onFinished = onFinished;
	}
	
}
