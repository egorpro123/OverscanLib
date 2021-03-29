package ru.overscan.lib.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import ru.overscan.lib.data.Field;
import ru.overscan.lib.data.Record;
import ru.overscan.lib.data.Table;
import ru.overscan.lib.sys.ErrorCollector;

public class DBTable {
	final static String TAG = "DBTable";
	
	
	HashMap<String, DBField> fields;
	public String name, dbName, caption;
	String[] primaryKey;
//	LinkedList<String> primaryKey;
	LinkedList<String> fieldsOrder;
	HashMap<String, String[]> indexes;
	boolean isDefaultID;
	boolean _idOrIdDefaultPrimaryKey;
	Database database;
	SqlConditions sqlConditions;
		
	
	public DBTable(String name) {
		this(name, false);
	}

	public DBTable(String name, String caption) {
		this(name, false);
		this.caption = caption;
	}

	// если notMakeDefalutID=false добавляется идентификатор id с полем таблицы _id
	public DBTable(String name, boolean notMakeDefalutID) {
//		this.database = database;
//		database.addTable(this);
		this.name = name;
		this.dbName = name;
		fields = new HashMap<String, DBField>();
		fieldsOrder = new LinkedList<String>();
		if (notMakeDefalutID) isDefaultID = false; 
		else{
			isDefaultID = true;
			DBField f =	new DBField("id", Field.INT_DATA);
			f.dbName = BaseColumns._ID;
			f.autoincremented = true;
			this.addField(f);
			this.setPrimaryKey(new String[] {"id"});
		}
		_idOrIdDefaultPrimaryKey = true;
	}

	public void addField(String name) {
//		fields.put(name, new DBField(name));
//		fieldsOrder.add(name);
		this.addField(name, Field.STRING_DATA);
	}

	public void addField(String name, String caption) {
//		fields.put(name, new DBField(name));
//		fieldsOrder.add(name);
		this.addField(name, Field.STRING_DATA, Field.NORMAL_STATE, caption);
	}
	
	public void addField(String name, int type) {
		this.addField(name, type, Field.NORMAL_STATE, null);
	}

	public void addField(String name, int type, String caption) {
		this.addField(name, type, Field.NORMAL_STATE, caption);
	}
	
	public void addField(String name, int type, int state) {
//		DBField f = new DBField(name, type);
//		f.setState(state);
		this.addField(name, type, state, null);
	}

	public void addField(String name, int type, int state, String caption) {
		name = name.toLowerCase(Locale.getDefault());
		DBField f = new DBField(name, type);
		f.setState(state);
		f.setCaption(caption);
		if (_idOrIdDefaultPrimaryKey && (name.equals("id") || name.equals("_id"))) {
			setPrimaryKey(new String[]{name});
			f.dbName = BaseColumns._ID;
		} 
		this.addField(f);
	}
	
	public void addField(DBField field) {
		fields.put(field.name, field);
		fieldsOrder.add(field.name);
	}
	
	public DBField getField(String name) {
		return fields.get(name);
	}
	
	public LinkedList<DBField> getFields() {
		LinkedList<DBField> list = new LinkedList<DBField>();
		for (String s: fieldsOrder) list.add(fields.get(s));
		return list;
	}

	public LinkedList<DBField> getFieldsWithoutPrimaryKey() {
		LinkedList<DBField> list = new LinkedList<DBField>();
		List<String> keyList = null;
		if (primaryKey != null) keyList = Arrays.asList(primaryKey); 
		for (String s: fieldsOrder)
			if (keyList == null || !keyList.contains(s))
				list.add(fields.get(s));
		return list;
	}
	
	public void setDatabase (Database database) {
		this.database = database;
	}
	
	public Record initRec() {
		return getBufferRecord();
	}
	
	public Record getBufferRecord() {
		Record r = new Record();
		Field f;
		for (String n: fieldsOrder) {
			f = (Field) fields.get(n); 
			r.addField(f.name, f.type);
		}
		return r;
	}
	
	private Record getBufferRecordWithoutPrimaryKey() {
		Record r = new Record();
		Field f;
		List<String> keyList = null;
		if (primaryKey != null) keyList = Arrays.asList(primaryKey);
		
		for (String n: fieldsOrder) {
			f = (Field) fields.get(n);			
			if (keyList == null || !keyList.contains(f.name))
				r.addField(f.name, f.type);
		}
		return r;
	}

	private DBField getFirstNoPrimaryKeyField() {
		DBField f;
		List<String> keyList = null;
		if (primaryKey != null) keyList = Arrays.asList(primaryKey);
		
		for (String n: fieldsOrder) {
			f = fields.get(n);			
			if (keyList == null || !keyList.contains(f.name))
				return f;
		}
		return null;
	}
	
	public Table getBufferTable() {
		Table t = new Table();
		Field f;
		for (String n: fieldsOrder) {
			f = (Field) fields.get(n); 
			t.addField(f.name, f.type);
//			t.addField((Field) fields.get(n));
		}
		t.primaryKey = primaryKey;
		return t;
	}
	
	public String[] getPrimaryKey() {
		return primaryKey;
	}
	
	private String stringsList2string(Iterable<String> list) {
		String res = "";
		for (String s: list) res += s + ", ";
		return res.substring(0, res.length() - 2);
	}
	
	
	public void collectError(String where, String err) {
		String s = "Таблица " + dbName + ". ";
		if (where != null) s += where + ". ";
		ErrorCollector.add(s + err);
	}
	
	public boolean okKey(String[] key, String funcName) {
		boolean ok = false;
		
		if (primaryKey == null) 
			collectError(funcName, "В таблице отсутствует первичный ключ.");
		else if (key.length != primaryKey.length) 
			collectError(funcName, "Переданный ключ "+ Arrays.toString(key) +
					"не соответствует табличному.");
		else ok = true;
		return ok;
	}

	public String makeSqlConditionByPrimaryKey() {
		if (primaryKey == null || primaryKey.length == 0) return null;
		String res = "";
		for (int i=0; i < primaryKey.length; i++) {
			res += fields.get(primaryKey[i]).dbName + " = ? and "; 
		}
		return res.substring(0, res.length() - 5);
	}
	
	// --------------- get - получить запись по ключу -------------------------------
	
	public String getValue(String id) {
		if (primaryKey == null || fields.size() <= primaryKey.length) return null;
//		public void set(String id, String fieldName) {
		DBField f = getFirstNoPrimaryKeyField();
		if (f == null) {
			collectError("getValue(" + id + ")", "не найдено неключевое поле");
			return null;
		}
		Record r = new Record();
		r.addField(f);
		get(new String[]{id}, r);
		return r.get(f.name);
	}

	public String getValue(String id, String fieldName) {
		if (primaryKey == null) return null;
		DBField f = fields.get(fieldName);
		if (f == null) {
			collectError("getValue(" + id + "," + fieldName + ")", 
					"попытка получить не существующее поле");
			return null;
		}
		Record r = new Record();
		r.addField(f);
		get(new String[]{id}, r);
		return r.get(f.name);
	}
	
// --------------- get - получить запись по ключу -------------------------------

	// get - получить запись по ключу, возращает Record без ключевых полей
	public Record get(String id) {
		return get(new String[]{id});
	}	
	
	// get - получить запись по ключу, возращает Record без ключевых полей
	public Record get(String[] key) {
		Record r = getBufferRecordWithoutPrimaryKey();
		this.get(key, r);
		return r;
	}
	
	private void get(String[] key, Record r) {
		if (!okKey(key, "get()")) return;
				
//		String sql = "select " + stringsList2string(fieldsOrder) +
//				" from " + dbName + " where " + makeSqlConditionByPrimaryKey();
		String sql = "select " + dbFieldNamesStringByRecord(r) +
				" from " + dbName + " where " + makeSqlConditionByPrimaryKey();
//		Log.d(TAG, "GET: " + sql);
		Cursor c = database.open().rawQuery(sql, key);
		int q = c.getCount();
		if (q == 0) collectError("get()", "Не найдена запись по ключу " +
				Arrays.toString(key));
		else if (q > 1) collectError("get()", "Найдено больше одной записи по ключу " +
				Arrays.toString(key));
		else {
			c.moveToFirst();
			r.clear();
			DBField f;
			String[] names = r.getNames();
			for (int i = 0; i < names.length; i++) {
				f = fields.get(names[i]);
				r.put(f.name, c.getString(c.getColumnIndex(f.dbName)));				
			}
////			int ind;
//			for (String name: fieldsOrder) {
//				f = fields.get(name);
////				ind = c.getColumnIndex(f.dbName); 
//				r.put(f.name, c.getString(c.getColumnIndex(f.dbName)));
////				if (f.dataType == STRING_DATA) r.put(f.name, c.getString(ind);
////				else if (f.dataType == INT_DATA) r.put(f.name, c.getString(ind);
//				
//			}
		}
		database.close();
	}
	
// -----------------------------------------------------------------------------------
	
	public int[] getFieldsCursorIndexes(Cursor c, String[] names) {
//		String[] names = table.getNames();
		int[] inds = new int[names.length];
		for (int i = 0; i < names.length; i++)
			inds[i] = c.getColumnIndex(fields.get(names[i]).dbName);
		return inds;
	}

	
// --- получить несколько записей ------------------------------------------------------
// таблицу нужно создать с полями самому или получить так getBufferTable()

	public void getMany(SqlConditions conditions, Table table) {
		getMany(conditions, table, 0, 0);
	}	
	
	public void getMany(Table table) {
		getMany(sqlConditions, table, 0, 0);
	}
	
	public void getMany(Table table, int fromPosition, int quantity) {
		getMany(sqlConditions, table, fromPosition, quantity);
	}
	
	public void getMany(SqlConditions conditions, 
			Table table, int fromPosition, int quantity) {
		String sql = "SELECT " + dbFieldNamesStringByTable(table) +
				" FROM " + dbName;
		if (conditions != null) {
			sql += " WHERE " + conditions.getWherePart();
			String ord = conditions.getOrderbyPart();
			if (ord != null) sql += " ORDER BY " + ord;
		}
		if (quantity > 0) sql += " LIMIT " + quantity;
		if (fromPosition > 0) sql += " OFFSET " + fromPosition;
		Cursor c = database.open().rawQuery(sql, 
				conditions == null ? null : conditions.getWhereValues());
		String[] names = table.getNames();
		int[] inds = getFieldsCursorIndexes(c, names);
//		int[] inds = new int[names.length];
//		for (int i = 0; i < names.length; i++)
//			inds[i] = c.getColumnIndex(names[i]);
		int pos = fromPosition;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			for (int i = 0; i < inds.length; i++)
				table.put(names[i], c.getString(inds[i]));
			table.setRec(pos++);
//		    String name = ;
//		    table.put(name, value);
		}
		database.close();
	}

// -----------------------------------------------------------------------------
	
//	public void getMany(String sql, String[] condValues, Table table, 
//			int fromPosition, int quantity, int skip) {
//		Cursor c = database.open().rawQuery(sql, condValues);
//		
//		if (quantity > 0) sql += " LIMIT " + quantity;
//		if (skip > 0) sql += " OFFSET " + skip;
//		
//		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//		    String name = cur.getString(nameColumn);
//		    table.put(name, value);
//		}
//		database.close();
//	}
	
	private String dbFieldNamesStringByTable(Table t) {
		String r = "";
		String[] names = t.getNames();
		for (int i = 0; i < names.length; i++) 
			r += fields.get(names[i]).dbName + ", ";
		if (r.length() < 2) return null;
		else return r.substring(0, r.length() - 2);
	}
	
	private String dbFieldNamesStringByRecord(Record t) {
		String r = "";
		String[] names = t.getNames();
		for (int i = 0; i < names.length; i++) 
			r += fields.get(names[i]).dbName + ", ";
		if (r.length() < 2) return null;
		else return r.substring(0, r.length() - 2);
	}

	
// ------------ получить все записи -----------------------------------------
	
	public void getAll(Table table) {
		String sql = "SELECT " + dbFieldNamesStringByTable(table) + " FROM " + dbName;
		Cursor c = database.open().rawQuery(sql, new String[]{});
		String[] names = table.getNames();
		int[] inds = getFieldsCursorIndexes(c, names);
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			for (int i = 0; i < inds.length; i++) {
//				Log.d(TAG, "-" + names[i] + "-");
//				Log.d(TAG, "string " + c.getString(inds[i]));
//				Log.d(TAG, "integer " + c.getInt(inds[i]));
				table.put(names[i], c.getString(inds[i]));
			}
			table.addRec();
		}
		database.close();
	}

// ------------ удалить все записи -----------------------------------------
	
	public void deleteAll() {
		database.open().execSQL("delete from " + dbName);
		database.close();		
	}

// ------------------------------------------------------------------------
	
	private ContentValues makeContentValuesByRecord(Record r, boolean excludeKey) {
		ContentValues values = new ContentValues();
		String names[] = r.getNames();
		List<String> list = Arrays.asList(primaryKey);
		for (int i = 0; i < names.length; i++) {
			if (!excludeKey || list.indexOf(names[i]) < 0)
//				Log.d(TAG, "ContentValues: name: "+fields.get(names[i]).dbName+ ", value: "+ r.get(names[i]));
				values.put(fields.get(names[i]).dbName, r.get(names[i]));
		}
		return values;
	}

// ------------ вставка записи  -----------------------------------------
	
	public void insert(Record r) {
		database.open().insert(dbName, null, makeContentValuesByRecord(r, false));
		database.close();
	}
	
// ------------ обновляет или вставляет записи --------------------------

	// обновляет первое неключевое поле (сделано для таблиц из 2х колонок - ключ, значение)
	public int set(String id, String value) {
		if (primaryKey == null || fields.size() <= primaryKey.length) return 0;
//		public void set(String id, String fieldName) {
		DBField f = getFirstNoPrimaryKeyField();
		if (f == null) {
			collectError("set(id,value)", "не найдено неключевое поле");
			return 0;
		}
		Record r = new Record();
		f.setValue(value);
		r.addField(f);
//		r.addField(fieldName);
//		r.put(0, value);
		return this.set(new String[] {id}, r);
	}

	public int set(String id, String[] values) {
		Record r = getBufferRecordWithoutPrimaryKey();
		String[] names = r.getNames();
		for (int i = 0; i < names.length; i++) {
			if (i < values.length) r.put(names[i], values[i]);
		}
		return this.set(new String[] {id}, r);
	}
	
	public int set(String id, Record r) {
		return this.set(new String[] {id}, r);
	}
	
	public int set(String[] key, Record r) {
		if (!okKey(key, "set()")) return -1;

//		Log.d(TAG, "makeSqlConditionByPrimaryKey(): " + makeSqlConditionByPrimaryKey() + 
//				", key: "+ key[0]);
		int n = database.open().update(dbName, makeContentValuesByRecord(r, true), 
				makeSqlConditionByPrimaryKey(), key);
//		Log.d(TAG, "updated " + Integer.toString(n) + " records");
		if (n == 0) {
			if (primaryKey != null && !r.hasField(primaryKey[0])) {
				DBField f;
				for (int i = 0; i < primaryKey.length; i++) {
					f = getField(primaryKey[i]);
					f.setValue(key[i]);
					r.addField(f);
				}
			}
			insert(r);
			n = 1;
		}
		database.close();
		return n;
	}

// ------------ удаление записи -----------------------------------------
	
	public void delete(String id) {
		this.delete(new String[] {id});
	}

	
	public void delete(String[] key) {
		
//		Log.d(TAG, makeSqlConditionByPrimaryKey());
//		Log.d(TAG, Arrays.toString(key));
		database.open().delete(dbName, makeSqlConditionByPrimaryKey(), key);
		database.close();
	}
	
// ----------------------------------------------------------------------
	
	
	public void addIndex(String name, String[] fields) {
		if (indexes == null) indexes = new HashMap<String, String[]>();
//		LinkedList<String> list = new LinkedList<String>();
//		for (int i = 0; i < fields.length; i++) list.add(fields[i]);
		indexes.put(name, fields);
	}
	
// ----------- установить первичный ключ --------------------------------	
	public void setPrimaryKey(String[] key) {
		isDefaultID = false;
		primaryKey = key;
//		if (primaryKey == null) primaryKey = new LinkedList<String>();
//		else primaryKey.clear();
//		for (int i = 0; i < key.length; i++) primaryKey.add(key[i]);
	}
	
	private String makeFieldCreateDef(String name) {
		DBField f = fields.get(name);
		
		String s = f.dbName + " " + f.dbTypeName(f.type);
		if (primaryKey != null && primaryKey.length == 1 && primaryKey[0].equals(name))
			s += " PRIMARY KEY NOT NULL";
		else if (f.getState() == Field.NOT_NULL_STATE) s += " NOT NULL";
		if (f.autoincremented) s += " AUTOINCREMENT";
		return s;
	}
	
	public String[] makeCreateSqls() {
		String[] sqls;
		int q = 1;
		if (indexes != null) q += indexes.size();
		sqls = new String[q];
		String s = "CREATE TABLE " + dbName + " (";
		for (String n: fieldsOrder) s += makeFieldCreateDef(n) + ", ";
		
		if (primaryKey != null && primaryKey.length > 1) {
			String r = Arrays.toString(primaryKey);
			s += "PRIMARY KEY (" + r.substring(1, r.length()-1) +")";
		}
		else s = s.substring(0, s.length()-2);
		s += ");";
		sqls[0] = s;
		
		if (indexes != null) {
			int i = 1;
			for(String k: indexes.keySet()) {
				s = Arrays.toString(indexes.get(k));
				sqls[i++] = "CREATE INDEX index_" + dbName + "_" + k + " ON " + dbName + 
						"(" + s.substring(1, s.length()-1) +");";
//						LinkedList<String>
//			CREATE INDEX index_price_commodity_id on price(commodity_id);
			}
		}
		return sqls;
	}		
//		CREATE TABLE something (
//				  column1 INTEGER NOT NULL,
//				  column2 INTEGER NOT NULL,
//				  value,
//				  PRIMARY KEY ( column1, column2)
//				);
	
	public String makeDeleteSql(){
		return "DROP TABLE IF EXISTS " + dbName;
//		"DROP TABLE IF EXISTS shop
	}

	public SqlConditions addWhere(String name, int type, String value) {
		if (sqlConditions == null) sqlConditions = new SqlConditions(this);
		sqlConditions.addWhere(name, type, value);
		return sqlConditions;
	}
	
	public SqlConditions addOrderBy(String name, int type) {
		if (sqlConditions == null) sqlConditions = new SqlConditions(this);
		sqlConditions.addOrderBy(name, type);
		return sqlConditions;
	}
	
	
	public static class Where {
		public static final int EQUAL = 1;
		public static final int LESS = 2;
		public static final int MORE = 3;
		public static final int LESS_OR_EQUAL = 4;
		public static final int MORE_OR_EQUAL = 5;
		public static final int LIKE = 6;
		public static final int IS_NULL = 7;
		public static final int IS_NOT_NULL = 8;
		
		public String name;
		public int type;
		public String value;
		
		public Where(String name, int type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}				
		
		public String whereOperatorString() {
			switch (type) {
				case EQUAL: return "=";
				case LESS: return "<";
				case MORE: return ">";
				case LESS_OR_EQUAL: return "<=";
				case MORE_OR_EQUAL: return ">=";
				case LIKE: return "LIKE";
				case IS_NULL: return "ISNULL";
				case IS_NOT_NULL: return "NOTNULL";
				default: return "INVALID_WHERE_OPERATOR";
			}
		}		
		
		public boolean needValue() {
			return type != IS_NULL && type != IS_NOT_NULL; 
		}
	}

	public static class OrderBy {
		public final static int ASC = 1;
		public final static int DESC = 2;
		public String name;
		public int type;
		
		public OrderBy(String name, int type) {
			this.name = name;
			this.type = type;
		}
		
		public static String getTypeString(int type) {
			if (type == ASC) return "ASC";
			else if (type == DESC) return "DESC";
			else return "ILLEGAL_ORDER_BY_TYPE";
		}
	}
	
	public static class SqlConditions {
		HashMap<String, Where> whereFields;
		LinkedList<String> whereOrder;
		HashMap<String, OrderBy> orderByFields;
		LinkedList<String> orderByOrder;
		boolean forwardDirection;
		DBTable table;
		HashMap<String, Field> prevValues;
		
		public SqlConditions(DBTable table) {
			this.table = table;
			whereFields = new HashMap<String, Where>();
			whereOrder = new LinkedList<String>();
			orderByFields = new HashMap<String, OrderBy>();
			orderByOrder = new LinkedList<String>();
			forwardDirection = true;
		}
		
		public void setForwardDirection(boolean forward){
			forwardDirection = forward;
		}

		public SqlConditions addWhere(String name, int type, String value) {
			whereFields.put(name, new Where(name, type, value));
			whereOrder.add(name);
			return this;
		}
		
		public SqlConditions addOrderBy(String name, int type) {
			orderByFields.put(name, new OrderBy(name, type));
			orderByOrder.add(name);
			return this;
		}
		
		public void setPrevValues(HashMap<String, Field> values) {
			prevValues = values;
		}

		public String getWherePart() {
			String r = "";
			Where wc;
			for (String s: whereOrder) {
				wc = whereFields.get(s);
				if (!wc.needValue() || wc.value != null) {
					r += table.getField(wc.name).dbName + " " + 
							wc.whereOperatorString();
					if (wc.needValue()) r += " ?";
					r += " AND ";
				}
			}
			if (r.length() < 5) return r;
			else return r.substring(0, r.length()-5);
		}

		public String getOrderbyPart() {
			String r = "";
			String cond = null;
			OrderBy oc;
			for (String s: orderByOrder) {
				oc = orderByFields.get(s);
				r += table.getField(oc.name).dbName + ", ";  
				cond = OrderBy.getTypeString(oc.type);
			}
			if (r == null || r.length() <= 2) return r;
			else return r.substring(0, r.length() - 2) + " " + cond;
		}
		
		public String[] getWhereValues(){
			LinkedList<String> list = new LinkedList<String>();
			Where wc;
			for (String s: whereOrder) {
				wc = whereFields.get(s);
				if (!wc.needValue() || wc.value != null) {
					list.add(wc.value);
				}
			}
			return list.toArray(new String[] {});
		}
		
	}
	
}





// test

//DBTable tab = new DBTable("post");
//tab.addField("field2");
//tab.addField("field3");
//tab.addIndex("name", new String[] {"field2", "field3"});
//
//String[] sqls = tab.makeCreateSqls(); 
//for (int i = 0; i < sqls.length; i++) showText(sqls[i]);
//
//tab.setPrimaryKey(new String[] {"id", "field3"});
//sqls = tab.makeCreateSqls(); 
//for (int i = 0; i < sqls.length; i++) showText(sqls[i]);

// ------------ test 2 -----------------

//Database db = new Database(this, "test.db", 3);
//db.testing = true;
//
//DBTable tab = new DBTable("user");
//tab.addField("fio");
//tab.addField("address");
//tab.addField("dep_id", Field.INT_DATA);
//tab.addIndex("fio", new String[] {"fio"});
//db.addTable(tab);
//
//db.init();

//tab.deleteAll();
//Record r = tab.getBufferRecord(); 
//for (int i = 1; i < 6; i++) {
//	r.put("id", ""+i);
//	r.put("fio", "fio"+i);
//	r.put("address", "adr"+i);
//	tab.insert(r);
//}
//tab.delete("2");
//r.clear();
//r.put("fio", "check fio");
//tab.set("4", r);


//Table t = tab.getBufferTable();
//showText(Arrays.toString(t.getNames()));
//tab.getAll(t);
//for (int i = 0; i < t.size(); i++) {
//	t.chooseRec(i);
//	showText("rec" + i + " id " + t.get("id") + ", fio " + t.get("fio") + 
//			", address " + t.get("address"));
//}



//--------------------------

//public static class SqlCondition {
//	static final int EQUAL = 1;
//	static final int LESS = 2;
//	static final int MORE = 3;
//	static final int LESS_OR_EQUAL = 4;
//	static final int MORE_OR_EQUAL = 5;
//	static final int LIKE = 6;
//	//условия для where и order by, зависят от направления прохождения по записям, 
//	//даже если полей несколько этот тип у них должен быть одинаковым
//	static final int ASC_LOOK_DIRECTION = 7; // по возрастанию 
//	static final int DESC_LOOK_DIRECTION = 8; // по убыванию
//	//условия для order by, 
//	//даже если полей несколько этот тип у них должен быть одинаковым
//	static final int ASC = 9; // по возрастанию 
//	static final int DESC = 10; // по убыванию
//	String name;
//	int type;
//	
//	public SqlCondition(String name, int type){
//		this.name = name;
//		this.type = type;
//	}
//	
//	public SqlCondition(String name){
//		this(name, EQUAL);
//	}
//	
//	public boolean isWhereType() {
//		return type != ASC && type != DESC;
//	}
//
//	public boolean isOrderByType() {
//		return type == ASC || type == DESC ||
//				type == ASC_LOOK_DIRECTION || type == DESC_LOOK_DIRECTION;
//	}
//	
//	public String whereConditionString(boolean forwardDirection) {
//		switch (type) {
//			case EQUAL: return "=";
//			case LESS: return "<";
//			case MORE: return ">";
//			case LESS_OR_EQUAL: return "<=";
//			case MORE_OR_EQUAL: return ">=";
//			case LIKE: return "LIKE";
//			case ASC: return ""; 
//			case DESC: return ""; 
//			case ASC_LOOK_DIRECTION: 
//				if (forwardDirection) return ">";
//				else return "<";
//			case DESC_LOOK_DIRECTION: 
//				if (forwardDirection) return "<";
//				else return ">";
////    		case NULL_DATA: return "NULL";
//			default: return "INVALID_CONDITION_TYPE";
//		}
//	}
//	
//	public String orderByCondition(boolean forwardDirection) {
//		if ((type==ASC) || (type == ASC_LOOK_DIRECTION && forwardDirection) || 
//				(type == DESC_LOOK_DIRECTION && !forwardDirection)) return "ASC";
//		else return "DESC";
//	}
//}
//
//public static class SqlConditions {
//	HashMap<String, SqlCondition> hash;
//	LinkedList<String> order;
//	boolean forwardDirection;
//	DBTable table;
//	
//	public SqlConditions(DBTable table) {
//		this.table = table;
//		hash = new HashMap<String, SqlCondition>();
//		order = new LinkedList<String>();
//		forwardDirection = true;
//	}
//	
//	public void setForwardDirection(boolean forward){
//		forwardDirection = forward;
//	}
//	
//	public SqlConditions add(String name, int type) {
//		hash.put(name, new SqlCondition(name, type));
//		order.add(name);
//		return this;
//	}
//	
//	public SqlConditions add(String name) {
//		this.add(name, SqlCondition.EQUAL);
//		return this;
//	}
//
//	public String getWherePart() {
//		String r = "";
//		SqlCondition sc;
//		for (String s: order) {
//			sc = hash.get(s);
//			if (sc.isWhereType())
//				r += table.getField(sc.name).dbName + " " + 
//						sc.whereConditionString(forwardDirection) + " ? AND ";
//		}
//		if (r.length() < 5) return r;
//		else return r.substring(0, r.length()-5);
//	}
//
//	public String getOrderbyPart() {
//		String r = null;
//		String cond = null;
//		SqlCondition sc;
//		for (String s: order) {
//			sc = hash.get(s);
//			if (sc.isOrderByType())
//				r += table.getField(sc.name).dbName + ", ";  
//				cond = sc.orderByCondition(forwardDirection);
//			}
//		}
//		if (r == null) return null;
//		else return r.substring(0, r.length() - 2) + " " + cond;
//	}
//	
//}


