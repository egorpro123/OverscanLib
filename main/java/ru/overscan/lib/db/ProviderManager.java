package ru.overscan.lib.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;
import ru.overscan.lib.data.Field;

public class ProviderManager {
	final private static String TAG = "ProviderManager"; 
	
	//	AUTHORITY "ru.overscan.overscan.provider.CommoditiesProvider"
	public int uriedResourceCounter = 0;	
	public SparseArray<UriedResource> uriedResources;	
	public int resourceGroupCounter = 0;	
	public SparseArray<ResourceGroup> resourceGroups;
	public ProviderContent providerContent;
	
	public ProviderManager(){
		uriedResources = new SparseArray<UriedResource>();
		resourceGroups = new SparseArray<ResourceGroup>();
	}
	
//	public ProviderManager(String authority, String org_name){
//		AUTHORITY = authority;
//		ORG_NAME = org_name;
//		uriedResources = new SparseArray<UriedResource>();
//		resourceGroups = new SparseArray<ResourceGroup>();
//	}
	
	public void setProviderContent(ProviderContent pc) {
		providerContent = pc;
		pc.createContent(this);
	}
	
	public Cursor getResourceQueryCursor(int uriedResourceId, QueryParam qp) {
		UriedResource sr = uriedResources.get(uriedResourceId); 
		return sr.group.getQueryCursor(sr.groupResourceId, qp);
	}

	void makeAllUri(UriMatcher uriMatcher) {
		for(int i = 0; i < resourceGroups.size(); i++) 
			resourceGroups.valueAt(i).makeUri(uriMatcher, this);
	}
	
	public Uri resourceInsert(int uriedResourceId, Uri uri, ContentValues values) 
			throws IllegalArgumentException {
		UriedResource sr = uriedResources.get(uriedResourceId);
		String s = sr.group.insert(sr.groupResourceId, uri, values);
		if (s == null) return null;
		else return Uri.parse(s); 
	}

	public int resourceDelete(int uriedResourceId, QueryParam qp) {
		UriedResource sr = uriedResources.get(uriedResourceId);
		return sr.group.delete(sr.groupResourceId, qp);
	}

	public int resourceUpdate(int uriedResourceId, ContentValues values, 
			QueryParam qp) {
		UriedResource sr = uriedResources.get(uriedResourceId);
		return sr.group.update(sr.groupResourceId, values, qp);
	}	
	
	static boolean emptyContentValue(ContentValues cv, String key) {
		return !cv.containsKey(key) || cv.getAsString(key).trim().equals("");
	}
	
	static String getUriSegment(Uri uri, int index) {
		try {
			return uri.getPathSegments().get(index);
		}
		catch (Exception e) {
			return null;
		}
	}
	
//	static void checkState() {
//		if (AUTHORITY == null || ORG_NAME == null)
//			throw new IllegalStateException(ProviderManager.class.getSimpleName() +
//                " is not initialized, call initialize(..) method first.");
//	}
	

	//--------------------------------------------

	public static class SqlWhere {
		String where;
		String[] args;
		
		public SqlWhere(String pWhere, String[] pArgs) {
			where = pWhere;
			args = pArgs;
		}
	}
	
	
//	public static SqlWhere getSqlWhere(int settedUriId, Uri uri) {
//		UriedResource sr = uriedResources.get(settedUriId, null);
//		if (sr == null) return null;
//		else return sr.table.makeWhere(sr.tableResourceId, uri);
//	};
	
	//--------------------------------------------

	public static class UriedResource {
		int id;
		ResourceGroup group;
		String groupResourceId;
		
		public UriedResource(int id, ResourceGroup g, String tableResourceId) {
			this.id = id;
			group = g;
			this.groupResourceId = tableResourceId;
		}
	}	
	
	
	public String getResourceContentType(int uriedResourceId) {
		UriedResource sr = uriedResources.get(uriedResourceId); 
		return sr.group.getContentType(sr.groupResourceId);
	}
	
	//--------------------------------------------
	
	public static class QueryParam {
		Uri uri; 
		String[] projection; 
		String selection;
		String[] selectionArgs; 
		String sortOrder;
		
		public QueryParam(Uri uri, String[] projection, 
				String selection, String[] selectionArgs, String sortOrder) {
			this.uri = uri;
			this.projection = projection; 
			this.selection = selection;
			this.selectionArgs = selectionArgs; 
			this.sortOrder = sortOrder;			
		}
	}

	
	//--------------------------------------------
	
	public void addResourceGroup(ResourceGroup rg) {
		resourceGroupCounter++;
		resourceGroups.append(resourceGroupCounter, rg);
	}
		

	public static class UriValue {
		public int pos;
		public String fieldName;
		
		public UriValue(int _pos, String _fieldName) {
			pos = _pos;
			fieldName = _fieldName;
		}		
	}
	
	
	//--------------------------------------------

	public static class Resource {
		public String id;
		public int contentType;
		public String uri;
		public List<UriValue> uriValues;
//		public int globalId;
		
		public Resource(String id, String uri, int contentType) {
			this.id = id;
			this.uri = uri;
			this.contentType = contentType;
			uriValues = null;
		}
		
		public Resource(String id, String uri, int contentType,
				List<UriValue> uriValues) {
			this.id = id;
			this.uri = uri;
			this.contentType = contentType;
			this.uriValues = uriValues;
		}		
	} 

	//--------------------------------------------

	
	public static class ResourceGroup {
		// параметр создания полей по умолчанию
		static public final int FIELD_ID_AND_NAME = 1;
		static public final int FIELD_NAME_ONLY = 2;
		static public final int FIELD_ID_ONLY = 3;
		static public final int FIELD_NO_STANDART = 4;

		// параметр создания ресурсов по умолчанию, 
		// где: DIR - ресурс списка recs, ITEM - ресурс одной записи recs/#
		static public final int RESOURCE_DIR_AND_ITEM = 1;
		static public final int RESOURCE_ONLY_DIR = 2;
		static public final int RESOURCE_ONLY_ITEM = 3;
		static public final int RESOURCE_NO_STANDART = 4;

		// тип группы ресурсов
		static public final int RG_TABLE = 1;
		static public final int RG_QUERY = 2;
		
		// типы контента
		static public final int DIR = 1;
		static public final int ITEM = 2;

// зарезервированное имя ресурса списка (в виде: objects) DIR 
// зарезервированное имя ресурса одного элемента (в виде: objects/#) ITEM

		public String tableName; 
		public String uriBase;
		String contentName;
		String orderBy;
		int fieldState; 
		int resourceState;
		public int rgType;
//		int resCounter = 0;
		HashMap<String, Resource> resources;
		HashMap<String, ResourceField> fields;
		ProviderContent providerContent;
		
//		public static class Field {
//			public static final int NORMAL_STATE = 0;
//			public static final int NOT_NULL_STATE = 1;
////			public static final int ONLY_QUERY = 3;
//			
//			public String id;
//			public String name;
//			public int state;
//			
//			public Field(String _id, String _name) {
//				this(_id, _name, NORMAL_STATE);
//			}
//
//			public Field(String _id, String _name, int _state) {
//				id = _id;
//				name = _name;
//				state = _state;
//			}
//		} 

		public static class ResourceField extends Field {
		
			public String id;
		
			public ResourceField(String id, String name) {
				super(name);
				this.id = id;
			}

			public ResourceField(String id, String name, int dataType, int state) {
				super(name, dataType);
				setState(state);
				this.id = id;
			}

			public ResourceField(String id, String name, int state) {
				super(name);
				setState(state);
				this.id = id;
			}
		} 
		
		//--------------------------------------------
		
		
		public ResourceGroup(ProviderContent pc, String tableAndContentName, String uriBase) {
			this(pc, tableAndContentName, tableAndContentName, uriBase,
					FIELD_ID_AND_NAME, RESOURCE_DIR_AND_ITEM, RG_TABLE);
		}

		public ResourceGroup(ProviderContent pc, String tableName, String contentName, 
				String uriBase, int fieldState, int resourceState) {
			this(pc, tableName, contentName, uriBase, fieldState, 
					resourceState, RG_TABLE);
		}		
		
		public ResourceGroup(ProviderContent pc, String tableName, 
				String contentName, String uriBase,
				int fieldState, int resourceState, int rgType) {
//			checkState();
			this.tableName = tableName; 
			this.uriBase = uriBase;
			this.contentName = contentName;
			this.fieldState = fieldState; 
			this.resourceState = resourceState;
			this.rgType = rgType;
			providerContent = pc;
			resources = new HashMap<String, Resource>();
			fields = new HashMap<String, ResourceField>();
			addStandartFields();
			addStandartResources();
		}		

	    public String getUriString() {
	    	return "content://" + providerContent.AUTHORITY + "/" + uriBase; 
	    }
	    
	    public String getFullUriString(Resource r) {
	    	return "content://" + providerContent.AUTHORITY + "/" + r.uri;
	    }
		
	    public Uri getContentUri() {
	    	return Uri.parse(getUriString()); 
	    }
	    
	    public String getContentType(Resource r){
	    	if (r.contentType == DIR)
	    		return "vnd.android.cursor.dir/vnd." + 
	    			providerContent.ORG_NAME + "." + contentName;
	    	else return "vnd.android.cursor.item/vnd." + 
	    			providerContent.ORG_NAME +"." + contentName;
	    }
	    
	    public String getContentType(String tableResourceId){
	    	return getContentType(getResource(tableResourceId));
	    }	    
	    
	    private String makeResRest(String resRest) {	    	
			if (resRest == null || resRest.equals(""))
			   return uriBase;
			else return uriBase + "/" + resRest;
	    }
	    
	    // resRest это часть которая идет после имени ресурса, например # в objects/#
		public void addResource(String resID, String resRest, int contentType) {
			resources.put(resID, new Resource(resID, makeResRest(resRest), 
					contentType));
		}
		
		public void addResource(String resID, String resRest, int contentType,
				String[] fieldNames) {
			List<UriValue> vals = new ArrayList<UriValue>();
			int counter = 0;
			
			if (fieldNames.length > 0) {
				String[] a = resRest.split("/");
			  	for (int i = 0; i < a.length; i++) {
			  		if (a[i].equals("*") || a[i].equals("#")) {
			  			if (fieldNames.length >= counter) {
			  				vals.add(new UriValue(i + 1, fieldNames[counter]));
			  				counter++;
			  			}
				  		if (fieldNames.length < counter) break;
			  		}
			  	}
			}
			
			resources.put(resID, new Resource(resID, makeResRest(resRest), 
					contentType, vals));
		}
		
		public Resource getResource(String resID) {
			return resources.get(resID);
		}
		
		public void addField(String id, String name) {
			fields.put(id, new ResourceField(id, name));
		}
		
		public void addField(String id, String name, int state) {
			fields.put(id, new ResourceField(id, name, state));
		}

		public void addField(String id) {
			fields.put(id, new ResourceField(id, id.toLowerCase()));
		}

		public void addField(String id, int state) {
			fields.put(id, new ResourceField(id, id.toLowerCase(), state));
		}
		
		public Field getField(String id) {
			return fields.get(id);
		}
				
		private void addStandartFields() {
			if (fieldState == FIELD_ID_AND_NAME || fieldState == FIELD_ID_ONLY)
				addField("ID", BaseColumns._ID, Field.NOT_NULL_STATE);
			if (fieldState == FIELD_ID_AND_NAME || fieldState == FIELD_NAME_ONLY) {
				addField("NAME", "name", Field.NOT_NULL_STATE);
				orderBy = "name";
			}
		}

		private void addStandartResources() {
			if (resourceState == RESOURCE_DIR_AND_ITEM || 
					resourceState == RESOURCE_ONLY_DIR)
				addResource("DIR", "", DIR);
			if (resourceState == RESOURCE_DIR_AND_ITEM || 
					resourceState == RESOURCE_ONLY_ITEM)
				addResource("ITEM", "#", ITEM);
		}
		
		public void makeUri(UriMatcher uriMatcher, ProviderManager pm) {
//			Resource r;
//			for (int i=0; i < resources.size()-1; i++) {
			for (Resource r: resources.values()) {
				pm.uriedResourceCounter++;	
				pm.uriedResources.put(pm.uriedResourceCounter, 
						new UriedResource(pm.uriedResourceCounter, this, r.id));
				
				uriMatcher.addURI(providerContent.AUTHORITY, 
						r.uri, pm.uriedResourceCounter);
				
//				r.setGlobalID(uriedResourceCounter);
//				resources.setValueAt(i, r);
			}
		}
		
		public SqlWhere makeIdWhere(Resource r, Uri uri) {
			if (r.contentType == DIR) return new SqlWhere(null, null);
			else if (r.contentType == ITEM) {
				Log.d(TAG, "uri " + uri);
				Log.d(TAG, "segment " + getUriSegment(uri, 1));
//				Resource r = resources.get(sr.tableResourceId);
				return new SqlWhere(fields.get("ID").name + "=?" , new String[] {
					getUriSegment(uri, 1)
//					uri.getPathSegments().get(1)
				});
			}
			else return null;
		};

		
		public SqlWhere makeWhereByUri(Resource r, Uri uri) {
			String s = "";
			String[] vals = new String[r.uriValues.size()];
			int i = 0;
			
			for (UriValue v: r.uriValues) {
				s = s + getField(v.fieldName).name + "=? and ";
				vals[i++] = uri.getPathSegments().get(v.pos);
			}
			return new SqlWhere(s.substring(0, s.length()-5), vals);
		}
		
//		public static class UriValue {
//			public int pos;
//			public String fieldName;
//			
//			public UriValue(int _pos, String _fieldName) {
//				pos = _pos;
//				fieldName = _fieldName;
//			}		
//		}
//			public List<UriValue> uriValues;
		
		private SqlWhere makeSqlWhere(Resource r, QueryParam qp) {
			if (qp.selection != null) {
				return new SqlWhere(qp.selection, qp.selectionArgs);
			} else {
				if (rgType == RG_TABLE) return makeIdWhere(r, qp.uri);
				else return makeWhereByUri(r, qp.uri);
			}
		}
		
		
		public Cursor getQueryCursor(String tableResourceId, QueryParam qp) {
			Resource r = getResource(tableResourceId);
			if (r.contentType == DIR || r.contentType == ITEM) {
				SQLiteDatabase db = DatabaseManager.openDB();
//				String selection;
//				String[] selectionArgs;
				String order;
				SqlWhere sw = makeSqlWhere(r, qp);
//				selection = sw.where;
//				selectionArgs = sw.args;
//				if (qp.selection != null) {
//					selection = qp.selection;
//					selectionArgs = qp.selectionArgs;
//				} else {
//					SqlWhere sw;
//					if (drType == RG_TABLE) sw = makeWhere(r, qp.uri);
//					else sw = makeWhereByUri(r, qp.uri);
//					selection = sw.where;
//					selectionArgs = sw.args;
//				}
				if (qp.sortOrder != null) order = qp.sortOrder;
				else order = orderBy;
				Cursor c = db.query(tableName, qp.projection, 
						sw.where, sw.args, null, null, order);
				DatabaseManager.closeDB();
				return c;
			}
			else return null;
		};
		
		
		public String insert(String resourceId, Uri uri, ContentValues vals) 
				throws IllegalArgumentException {
			Resource r = getResource(resourceId);
			if (rgType == RG_TABLE) 
				throw new IllegalArgumentException("Ресурс - " + getFullUriString(r) + " не для вставки");
			if (r.id.equals("DIR") || r.id.equals("ITEM")) {
				for (ResourceField f: fields.values()) {
					if (f.id == "ID") continue;
					if (f.getState() == Field.NOT_NULL_STATE && emptyContentValue(vals, f.name)) 
						throw new IllegalArgumentException("Ресурс - " + getFullUriString(r) + 
							" при вставке поле " + f.name + " не должно быть пустым");
				}
				if (r.id.equals("ITEM")) {
					String s = getUriSegment(uri, 1);
				    if (s != null && emptyContentValue(vals, getField("ID").name))
				    	vals.put(getField("ID").name, Integer.parseInt(s));
				}
				SQLiteDatabase db = DatabaseManager.openDB();
				Long l = db.insert(tableName, null, vals);
				DatabaseManager.closeDB();
				return getUriString() + "/" +Long.toString(l);
			}
			else return null;
		}

		
		public int updateOrDelete(String resourceId, boolean doUpdate,
				ContentValues values, QueryParam qp) throws IllegalArgumentException {
			Resource r = getResource(resourceId);
			if (rgType == RG_TABLE) 
				throw new IllegalArgumentException("Ресурс - " + getFullUriString(r) + 
						" только для запросов");
			else {
				SQLiteDatabase db = DatabaseManager.openDB();
				int cnt;
				SqlWhere sw = makeSqlWhere(r, qp);
				if (doUpdate)
				  cnt = db.update(tableName, values, sw.where, sw.args);
				else cnt = db.delete(tableName, sw.where, sw.args);
//				update(String table, ContentValues values, String whereClause, String[] whereArgs)				
				DatabaseManager.closeDB();
				return cnt;
			}			
		}
		
		// в QueryParam имеет значение только uri, where, whereArgs
		public int delete(String resourceId, QueryParam qp) 
				throws IllegalArgumentException {
			return updateOrDelete(resourceId, false, null, qp);
//			Resource r = getResource(resourceId);
//			if (drType == RG_TABLE) 
//				throw new IllegalArgumentException("Ресурс - " + getFullUriString(r) + 
//						" только для запросов");
//			else {
//				SQLiteDatabase db = DatabaseManager.openDB();
//				SqlWhere sw = makeSqlWhere(r, qp);
//				int cnt = db.delete(tableName, sw.where, sw.args);
//				DatabaseManager.closeDB();
//				return cnt;
//			}
		}
		
		// в QueryParam имеет значение только uri, where, whereArgs
		public int update(String resourceId, ContentValues values, QueryParam qp) 
				throws IllegalArgumentException {
			return updateOrDelete(resourceId, true, values, qp);
//			Resource r = getResource(resourceId);
//			if (drType == RG_TABLE) 
//				throw new IllegalArgumentException("Ресурс - " + getFullUriString(r) + 
//						" только для запросов");
//			else {
//				SQLiteDatabase db = DatabaseManager.openDB();
//				SqlWhere sw = makeSqlWhere(r, qp);
//				int cnt = db.update(tableName, values, sw.where, sw.args);
////				update(String table, ContentValues values, String whereClause, String[] whereArgs)				
//				DatabaseManager.closeDB();
//				return cnt;
//			}
		} 

	} 
	
}
