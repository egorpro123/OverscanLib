package ru.overscan.lib.data;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import ru.overscan.lib.net.NetUtils;
import ru.overscan.lib.net.ServerAnswer;
import ru.overscan.lib.net.ServerAnswerParsed;
import ru.overscan.lib.sys.ErrorCollector;
import ru.overscan.lib.sys.OverscanApplication;

abstract public class JsonReceiving<T> {
//	private static OverscanApplication application;	
	final static String TAG = "JsonReceiving";
	
	// вид данных, возвращаемых от сервера
	public static int TYPE_OBJECT = 1;
	public static int TYPE_ARRAY = 2;
	public static int TYPE_NODATA = 3; //д.б. передан OnFinish<Object> 

	boolean parseJsonAsync = true;
	boolean queryForbidden = false;
	
	
	boolean queryInProgress = false;
	
	private static final int SERVER_STATUS_UNAUTHORIZED = -2;
	private static final int SERVER_STATUS_UNEXPECTED_ERROR = -1;
	
	abstract public void setQueryParams(JSONObject obj) throws JSONException;

	abstract public T parseResultJson(Object obj) throws JSONException;
//	abstract public ServerAnswerParsed<T> parseResultJson(Object obj) throws JSONException;

	//	private static HashMap<String, ServerAnswerParsed<Object>> queryResults;
	
//	private ServerAnswerParsed<Object> queryResult;
	
//	private ServerAnswerParsed<T> result_;

	private OnFinish<T> onFinish;

	public interface OnFinish<T> {
	    public void handleResult(ServerAnswerParsed<T> parsed);
	}
	
	OverscanApplication application; 
//	Handler handler;
	String url;
	JSONObject params;
//	boolean isArray;
	int dataType;	
	
	QueryHandler<T> queryHandler;
	ParseHandler<T> parseHandler;
	
	public JsonReceiving(OverscanApplication app, OnFinish<T> h, String url,
			int dataType) {
		application = app;
		onFinish = h;
		this.url = url;
//		this.isArray = isArray;
		this.dataType = dataType;
		queryHandler = new QueryHandler<T>(this);
		parseHandler = new ParseHandler<T>(this);
	}
	
	
	public void setOnFinish(OnFinish<T> h) {
		onFinish = h;
	}

	public void addSpecialParam(String paramName, String value) {
		
	};
	
	public void start() {
		if (queryInProgress || queryForbidden) return;
		
		queryInProgress = true;
		ServerAnswerParsed<T> raw;
		boolean ok = true;

		raw = new ServerAnswerParsed<T>();
		raw.answer = new ServerAnswer();

		try {
			this.params = new JSONObject();
			setQueryParams(this.params);
//			if (this.params == null) return;
		} catch (JSONException e1) {
			//
			raw.answer = ServerAnswer.createApplicationError(
					"Ошибка при формировании JSON объекта с параметрами запроса", e1);
//			raw.answer.message = ServerAnswer.UNAUTHORIZED_MESSAGE;
			ok = false;
		}
		
		if (ok && !DataUtils.emptyString(OverscanApplication.access_token)) {
			try {
				params.put("access_token", OverscanApplication.access_token);
			} catch (JSONException e) {
				//
//				ErrorCollector.add("Ошибка при добавлении токена в данные запроса", e);
				raw.answer = ServerAnswer.createApplicationError(
						"Ошибка при добавлении токена в данные запроса", e);
				raw.answer.message = ServerAnswer.UNAUTHORIZED_MESSAGE;
				Toast.makeText(application,
					      raw.answer.message, Toast.LENGTH_SHORT).show();
				ok = false;
			}
		} else {
			ok = false;			
			raw.answer.status = ServerAnswer.UNAUTHORIZED;
			raw.answer.message = ServerAnswer.UNAUTHORIZED_MESSAGE;
//			return raw;
		}
		
//		if (application == null) {
//			raw = new ServerAnswerParsed<Object>(); 
//			raw.answer = ServerAnswer.createApplicationError( 
//					new IllegalStateException(JsonUtils.class.getSimpleName() +
//	                " is not initialized, call initialize(..) method first."));
//			return raw;
//		}
		if (ok && !NetUtils.isNetworkAvailable(application)) {
//			String msg = "Отсутствует сетевое подключение";
//			Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show(); //i18n
			raw.answer.status = ServerAnswer.NO_NETWORK;
			raw.answer.message = ServerAnswer.NO_NETWORK_MESSAGE;
			Toast.makeText(application,
				      raw.answer.message, Toast.LENGTH_SHORT).show();
			ok = false;
//			return raw;
		}
		
		if (ok) {
			Thread t = new Thread(this.new QueryRawJson(this));
//			Log.d(TAG, "before QueryRawJson start");
			t.start();
		} else {
//			result = raw;
			queryFinished(raw);
//			onFinish.handleResult(raw);
//			handler.sendEmptyMessage(0);
		}
		
	}
	
	private void queryFinished(ServerAnswerParsed<T> parsed) {
		queryInProgress = false;
		onFinish.handleResult(parsed);
	}

	public void addParam(JSONObject obj, String name, String value) throws JSONException {
		if (value != null) obj.put(name, value);
	}

	public void addTypedParam(JSONObject obj, String name, Field f) throws JSONException {
		if (f != null) {
			switch (f.type) {
	            case Field.DOUBLE_DATA:  obj.put(name, f.asDouble());
	            	break;
	            case Field.INT_DATA: obj.put(name, f.asInt());
	            	break;
	            default: obj.put(name, f.asString());
	            	break;
		    }
		}
	}
	
	public void setParseJsonAsync(boolean async){
		parseJsonAsync = async;
	}
	
	public void setQueryForbidden(boolean b){
		queryForbidden = b;
	}
	
	
//	private ServerAnswerParsed<T> typedResultFromObject(ServerAnswerParsed<Object> obj) {
//		ServerAnswerParsed<T> res = new ServerAnswerParsed<T>();
//		res.answer = obj.answer;
//		return res;
//	}
	
//	@SuppressLint("HandlerLeak")
	static class QueryHandler<T> extends Handler {
		WeakReference<JsonReceiving<T>> receiving;
		
		public QueryHandler(JsonReceiving<T> obj) {
			receiving = new WeakReference<JsonReceiving<T>>(obj);
		}

		@Override
		public void handleMessage(Message msg) {
			//
			JsonReceiving<T> obj = receiving.get();
			ServerAnswerParsed<Object> queryResult = (ServerAnswerParsed<Object>) msg.obj; 
			
			if (queryResult.answer.status == ServerAnswer.SUCCESS &&
					obj.dataType != JsonReceiving.TYPE_NODATA) {
				if (obj.parseJsonAsync) {
					Thread t = new Thread(new ParseRawJson<T>(obj, queryResult));
//					Thread t = new Thread(obj.new ParseRawJson(obj));
					t.start();
				} else {
					obj.queryFinished(obj.parseJson(obj, queryResult));
//					obj.onFinish.handleResult(obj.parseJson(obj));
				}
//				
//			obj.handler.sendEmptyMessage(0);
			} else {
				ServerAnswerParsed<T> res = new ServerAnswerParsed<T>();
				res.answer = queryResult.answer;
				obj.queryFinished(res);
//				obj.onFinish.handleResult(res);
//				obj.handler.sendEmptyMessage(0);
//				obj.startQuery();
			}
//			if (obj.queryResult.answer.status == ServerAnswer.SUCCESS)
//				obj.handler.sendEmptyMessage(0);
//			else {
//				obj.startQuery();
//			}
		}
		
	}

//	static class QueryHandler extends Handler {
//		WeakReference<JsonReceiving<T>> receiving;
//		
//		public QueryHandler(JsonReceiving<T> obj) {
//			receiving = new WeakReference<JsonReceiving<T>>(obj);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			//
//			JsonReceiving<T> obj = receiving.get();
//			if (obj.queryResult.answer.status == ServerAnswer.SUCCESS) {
//				if (parseJsonAsync) {
//					Thread t = new Thread(obj.new ParseRawJson(obj));
//					t.start();
//				} else {
//					obj.onFinish.handleResult(obj.parseJson(obj));
//				}
////				
////			obj.handler.sendEmptyMessage(0);
//			} else {
//				ServerAnswerParsed<T> res = new ServerAnswerParsed<T>();
//				res.answer = obj.queryResult.answer;
//				obj.onFinish.handleResult(res);
////				obj.handler.sendEmptyMessage(0);
////				obj.startQuery();
//			}
////			if (obj.queryResult.answer.status == ServerAnswer.SUCCESS)
////				obj.handler.sendEmptyMessage(0);
////			else {
////				obj.startQuery();
////			}
//		}
//		
//	}
	
	
	class QueryRawJson implements Runnable {
		JsonReceiving<T> receiving;
		
//		class Data {
//			String url;
//			JSONObject json; 
//			boolean isArray;
//			ServerAnswerParsed<Object> result;	
//		}

		public QueryRawJson(JsonReceiving<T> receiving) {
			this.receiving = receiving;
		}
		

		@Override
		public void run() {
			//
			Log.d(TAG, "in QueryRawJson params: " + receiving.params.toString());
			ServerAnswerParsed<Object> raw;
			ServerAnswerParsed<String> queryRes = 
					NetUtils.httpJsonQuery(OverscanApplication.serverUrl + "/" + url, 
							receiving.params.toString()); 
//			Log.d(TAG, "in QueryRawJson query res: " + "url " + OverscanApplication.serverUrl + "/" + url + ", " +
//					"queryRes.answer " + queryRes.answer.status + " " +
//					queryRes.answer.message + ", " +
//					queryRes.rec);
			if (queryRes.answer.status == ServerAnswer.SUCCESS) {
				raw = receiving.partlyParseServerAnswer(queryRes.rec);
			} else {
				raw = new ServerAnswerParsed<Object>(); 
				raw.answer = queryRes.answer;
			}
//			receiving.queryResult = raw;
			receiving.queryHandler.sendMessage(
					receiving.queryHandler.obtainMessage(0, (Object) raw));
		}
		
	}


	static class ParseHandler<T> extends Handler {
		WeakReference<JsonReceiving<T>> receiving;
		
		public ParseHandler(JsonReceiving<T> obj) {
			receiving = new WeakReference<JsonReceiving<T>>(obj);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			JsonReceiving<T> obj = receiving.get();
			obj.queryFinished((ServerAnswerParsed<T>) msg.obj);
		}
		
	}


	private ServerAnswerParsed<T> parseJson(JsonReceiving<T> receiving,
			ServerAnswerParsed<Object> toParse){
		ServerAnswerParsed<T> parsed = new ServerAnswerParsed<T> ();
		parsed.answer = new ServerAnswer();
		parsed.answer.status = ServerAnswer.SUCCESS;

		try {
			parsed.rec = receiving.parseResultJson(toParse.rec);
//			parsed.rec = (ServerAnswerParsed<T>) 
//					receiving.parseResultJson(receiving.queryResult.rec);
		} catch (JSONException e) {
			parsed.answer = ServerAnswer.createServerError(e);
	        parsed.rec = null;
		}
		return parsed;
	}


	static class ParseRawJson<T> implements Runnable {
		JsonReceiving<T> receiving;
		ServerAnswerParsed<Object> toParse;
		
		public ParseRawJson(JsonReceiving<T> receiving, ServerAnswerParsed<Object> toParse) {
			this.receiving = receiving;
			this.toParse = toParse;
		}

		@Override
		public void run() {
			ServerAnswerParsed<T> data = receiving.parseJson(receiving, toParse);
			receiving.parseHandler.sendMessage(
					receiving.parseHandler.obtainMessage(0, (Object) data));
		}

		
	}

	
//	class ParseRawJson implements Runnable {
//		JsonReceiving<T> receiving;
//		
//		public ParseRawJson(JsonReceiving<T> receiving) {
//			this.receiving = receiving;
//		}
//
//		@Override
//		public void run() {
//			//
////			ServerAnswerParsed<T> parsed = new ServerAnswerParsed<T> ();
////			parsed.answer = new ServerAnswer();
////			parsed.answer.status = ServerAnswer.SUCCESS;
////
////			try {
////				parsed = receiving.parseResultJson(receiving.queryResult.rec);
////			} catch (JSONException e) {
////				parsed.answer = ServerAnswer.createServerError(e);
////		        parsed.rec = null;
////			}
////			receiving.result_ = parsed;
//			receiving.result_ = parseJson(receiving);
////			receiving.onFinish.handleResult(receiving);
//			receiving.parseHandler.sendEmptyMessage(0);
//		}
//
////		@Override
////		protected void onPostExecute(ServerAnswerParsed<Object> parsed) {
////			if (onFinish != null) onFinish.handleResult(parsed);
////		}
//
//		
//	}
	
	private ServerAnswerParsed<Object> partlyParseServerAnswer(String rawStr) {
		ServerAnswer answer = new ServerAnswer();
		ServerAnswerParsed<Object> parsed = new ServerAnswerParsed<Object>();
		if (rawStr == null || rawStr.equals("") || rawStr.equals("null")) {
			answer.status = ServerAnswer.UNKNOWN_ERROR;
			ErrorCollector.add("Получена NULL или пустая строка от сервера - " +
					rawStr + ".");
		} else {
			try {
				JSONObject obj = new JSONObject(rawStr);
				if (obj.isNull("status")){
					answer.status = ServerAnswer.UNKNOWN_ERROR;
					answer.message = ServerAnswer.SERVER_ERROR_MESSAGE;
				}
				else if (obj.getInt("status") < 0) {
					int status = obj.getInt("status");
					if (status == SERVER_STATUS_UNAUTHORIZED )
					  answer.status = ServerAnswer.UNAUTHORIZED;
					else answer.status = ServerAnswer.UNKNOWN_ERROR;
					answer.originalStatus = status;
					if (!obj.isNull("msg")) answer.message = obj.getString("msg");
					else answer.message = ServerAnswer.SERVER_ERROR_MESSAGE;
				} else {
					int status = obj.getInt("status");
					answer.originalStatus = status;
					if (status == 0) answer.status = ServerAnswer.NOT_FOUND;
					else {
						answer.status = ServerAnswer.SUCCESS;
						
						if (dataType == TYPE_OBJECT)
							parsed.rec = obj.getJSONObject("data");
						else if (dataType == TYPE_ARRAY) 
							parsed.rec = obj.getJSONArray("data");
//						if (isArray) parsed.rec = obj.getJSONArray("data");
//						else parsed.rec = obj.getJSONObject("data");
					}
				}
			} catch (JSONException e) {
//				answer = ServerAnswer.createServerError(e);
				ErrorCollector.add("Ошибка при разборе ответа сервера", e);
				answer.status = ServerAnswer.UNKNOWN_ERROR;
	        }
		}
		if (answer.status == ServerAnswer.UNKNOWN_ERROR && 
				(answer.message == null || answer.message.equals(""))) {
			answer.message = ServerAnswer.SERVER_ERROR_MESSAGE; 			
		}
		parsed.answer = answer;
		return parsed;
	}
	
	
}	
//	private static ServerAnswerParsed<Object> getRawJsonObject(String url,
//			JSONObject json, boolean isArray) {
//		ServerAnswerParsed<Object> raw;
//		
//		if (application.access_token != null && !application.access_token.equals("")) {
//			try {
//				json.put("access_token", application.access_token);
//			} catch (JSONException e) {
//				//
//				ErrorCollector.add("Ошибка при добавлении токена в данные запроса", e);
//			}
//		}
//		
//		if (application == null) {
//			raw = new ServerAnswerParsed<Object>(); 
//			raw.answer = ServerAnswer.createApplicationError( 
//					new IllegalStateException(JsonUtils.class.getSimpleName() +
//	                " is not initialized, call initialize(..) method first."));
//			return raw;
//		}
//		if (!NetUtils.isNetworkAvailable(application)) {
//			String msg = "Отсутствует сетевое подключение";
////			Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show(); //i18n
//			raw = new ServerAnswerParsed<Object>();
//			raw.answer = new ServerAnswer();
//			raw.answer.status = ServerAnswer.NO_NETWORK;
//			raw.answer.message = msg;
//			return raw;
//		}
//		
//		RawGetJsonObject getJsonObject = new RawGetJsonObject();
//		RawGetJsonObject.Data data = getJsonObject.new Data();
//		data.url = url;
//		data.json = json;
//		data.isArray = isArray;
//		getJsonObject.execute(data);
//		return data.result;
//		
////		ServerAnswerParsed<String> queryRes = 
////				NetUtils.httpJsonQuery(url, json.toString()); 
////		if (queryRes.answer.status == ServerAnswer.SUCCESS) {
////			raw = JsonUtils.partlyParseServerAnswer(queryRes.rec, isArray);
////		} else {
////			raw = new ServerAnswerParsed<Object>(); 
////			raw.answer = queryRes.answer;
////		}
////		return raw;
//	}
	
	
//	public static ServerAnswerParsed<JSONObject> parseServerAnswer(String rawStr) {
//		ServerAnswer answer = new ServerAnswer();
//		ServerAnswerParsed<JSONObject> parsed = new ServerAnswerParsed<JSONObject>();
//		if (rawStr == null || rawStr.equals("") || rawStr.equals("null")) {
//			answer.status = ServerAnswer.ERROR;
//			ErrorCollector.add("Получена NULL или пустая строка от сервера - " + 
//			 rawStr + ".");
//		}
//		else {
//			try {
//				JSONObject obj = new JSONObject(rawStr);
//				if (obj.isNull("ok")) {
//					answer.status = ServerAnswer.ERROR;
//					if (!obj.isNull("msg")) answer.message = obj.getString("msg");
//				}
//				else {
//					boolean ok = obj.getBoolean("ok");
//					if (!ok) answer.status = ServerAnswer.NOT_FOUND;
//					else {
//						answer.status = ServerAnswer.SUCCESS;
//						parsed.rec = obj.getJSONObject("rec"); 
//					}
//				}
//			} catch (JSONException e) {
//				ErrorCollector.add("Ошибка при разборе ответа сервера", e);
//				answer.status = ServerAnswer.ERROR;
//	        }
//		}
//		if (answer.status == ServerAnswer.ERROR && 
//				(answer.message == null || answer.message.equals(""))) {
//			answer.message = ServerAnswer.SERVER_ERROR_MESSAGE; 			
//		}
//		parsed.answer = answer;
//		return parsed;
//	}
		

	
	
//	public static ChangingField parseChangingField(JSONObject comObj, 
//			String fieldName) {
//		if (comObj == null) return null;
//		try {
//			JSONObject obj = comObj.getJSONObject(fieldName);
//			if (obj == null) return null;
//			ChangingField data = new ChangingField();
//			data.current = parseValueDatas(obj.getJSONObject("v"));
//			JSONArray array = obj.getJSONArray("c");
//			if (array != null) {
//				data.lastChanges = new SparseArray<Change>();
//				for (int i = 0; i < array.length(); i++) {
//					data.lastChanges.put(i, parseValueDatas(array.getJSONObject(i)));
//				}
//			}
//			return data;
//		} catch (JSONException e) {
//			return null;
//		}
//	}
//	
//	public static Change parseValueDatas(JSONObject obj) 
//			throws JSONException {
//		if (obj == null) return null;
//		Change c = new Change();
//		c.cid = obj.getLong("cid");
//		c.value = obj.getString("v");
//		c.user = obj.getString("u");
//		c.qp = obj.getInt("qp");
//		c.qn = obj.getInt("qn");
//		c.dt = obj.getLong("dt");
//		return c;
//	}
//	
//	public static ServerAnswerParsed<ArrayList<EntityName>> 
//	 		parseNamesList(JSONArray array) {
//		ServerAnswerParsed<ArrayList<EntityName>> parsed = 
//				new ServerAnswerParsed<ArrayList<EntityName>> ();
//		ArrayList<EntityName> names = new ArrayList<EntityName>();
//		parsed.rec = names;
//		parsed.answer = new ServerAnswer();
//		parsed.answer.status = ServerAnswer.SUCCESS;
//		
//		EntityName name;
//		JSONObject obj;
//		if (array != null) {			
//			try {
//				for (int i = 0; i < array.length(); i++) {
//					obj = array.getJSONObject(i);
//					name = new EntityName();
//					name.id = obj.getLong("id");
//					name.name = obj.getString("name");
//					names.add(name);
//				}
//			} catch (JSONException e) {
//				parsed.answer = ServerAnswer.createServerError(e);
//	            parsed.rec = null;
//				//
////				e.printStackTrace();
//			}
//		}		
//		return parsed;
//	}
//
//	public static ServerAnswerParsed<Table> parseUniRecs(JSONArray array, String[] fields) {
//		ServerAnswerParsed<Table> parsed = new ServerAnswerParsed<Table> ();
//		Table res = new Table();
//		for (int i = 0; i < fields.length; i++) {
//			res.addField(fields[i]);
//		}
//		parsed.rec = res;
//		parsed.answer = new ServerAnswer();
//		parsed.answer.status = ServerAnswer.SUCCESS;
//
//		JSONObject obj;
//		if (array != null) {			
////			String[] names = fields.getNames();
//			try {
//				for (int i = 0; i < array.length(); i++) {
//					obj = array.getJSONObject(i);
//					for (int j = 0; j < fields.length; j++) {
//						if (obj.isNull(fields[j])) res.put(fields[j], "");
//						else res.put(fields[j], obj.getString(fields[j]));
//					}
//					res.addRec();
//				}
//			} catch (JSONException e) {
//				parsed.answer = ServerAnswer.createServerError(e);
//		        parsed.rec = null;
//				//
//		//		e.printStackTrace();
//			}
//		}		
//		return parsed;
//	}

	
//	public static ServerAnswerParsed<Commodity> parseCommodity(JSONObject obj) {
//	ServerAnswerParsed<Commodity> recv = new ServerAnswerParsed<Commodity>();
//	Commodity commodity = new Commodity();
//	recv.rec = commodity;
//	recv.answer.status = ServerAnswer.SUCCESS;
//	
//	try {
////		JSONObject obj = new JSONObject(rawStr);
//		commodity.id = obj.getLong("id");
//		commodity.barcode = obj.getString("bar");
//		
//		commodity.caption = parseFieldDatas(obj, "cap");
//		commodity.unitAmount = parseFieldDatas(obj, "am");
//		commodity.measured = parseFieldDatas(obj, "ms");
//		commodity.leaved = parseFieldDatas(obj, "lv");
//		commodity.priced = parseFieldDatas(obj, "pr");
//		
//	} catch (JSONException e) {
//		recv.answer.status = ServerAnswer.ERROR;
//		recv.answer.message = BAD_SERVER_ANSWER;
//		ErrorCollector.add(BAD_SERVER_ANSWER + " - " + obj.toString(), e);
//        recv.rec = null;
//    }
//	return recv;
//}
//

//public static ChangingField parseOneField(JSONObject obj, String jsonName) {
//	try {
//		ChangingField fd = 
//				parseFieldDatas(obj.getJSONObject(jsonName), localName);
//		if (fd != null)	{
//			fd.name = localName;
//			data.put(counter++, fd);
//		}
//	} catch (JSONException e) {
//		ErrorCollector.add(e);
////        e.printStackTrace();
////		data.put(counter++, null);
//    }
//	return counter;
//}
//
	
//	public static synchronized void initialize(OverscanApplication app) {
//	application = app;
//}

//public ServerAnswerParsed<JSONObject> getRawJsonFromServer(String url,
//		JSONObject json) {
//	ServerAnswerParsed<JSONObject> parsed = new ServerAnswerParsed<JSONObject>();
//	ServerAnswerParsed<Object> obj = getRawJsonObject(url, json, true);
//	parsed.answer = obj.answer;
//	parsed.rec = (JSONObject) obj.rec;
//	return parsed;
//////	return JsonUtils.parseServerAnswer(NetUtils.httpJsonQuery(url, json));
////	ServerAnswerParsed<JSONObject> parsed = new ServerAnswerParsed<JSONObject>();
////	ServerAnswerParsed<String> queryRes = NetUtils.httpJsonQuery(url, json);
////	if (queryRes.answer.status == ServerAnswer.SUCCESS) {
////		ServerAnswerParsed<Object> answer = 
////				JsonUtils.parseServerAnswer(queryRes.rec, false);
////		parsed.answer = answer.answer;
////		parsed.rec = (JSONObject) answer.rec;
////	} 
////	else parsed.answer = queryRes.answer;
////	return parsed;
//////	String res = NetUtils.httpJsonQuery(App.serverUrl + "/commodity", 
//////			obj.toString());
//////	ServerAnswerParsed<JSONObject> sa = JsonUtils.parseServerAnswer(res);
//////	if (sa.answer.status == ServerAnswer.SUCCESS) {
//////		commodity = JsonUtils.parseCommodity(sa.rec);
//////	} else {
//////		commodity.answer = sa.answer;
//////	}
//}
//
//
//public ServerAnswerParsed<JSONArray> getRawJsonArrayFromServer(String url,
//		JSONObject json) {
//	ServerAnswerParsed<JSONArray> parsed = new ServerAnswerParsed<JSONArray>();		
//	ServerAnswerParsed<Object> obj = getRawJsonObject(url, json, true);
//	parsed.answer = obj.answer;
//	parsed.rec = (JSONArray) obj.rec;
//	return parsed;
////	ServerAnswerParsed<JSONArray> parsed = new ServerAnswerParsed<JSONArray>();
////	ServerAnswerParsed<String> queryRes = NetUtils.httpJsonQuery(url, json); 
////	if (queryRes.answer.status == ServerAnswer.SUCCESS) {
////		ServerAnswerParsed<Object> answer = 
////			JsonUtils.parseServerAnswer(queryRes.rec, true);
////		parsed.answer = answer.answer;
////		parsed.rec = (JSONArray) answer.rec;
////	}
////	else parsed.answer = queryRes.answer;
////	return parsed;
//	
//}



// может пойдет для универсальных изменяемых полей

//public static SparseArray<ChangingField> 
//parseChangingField(String rawStr) {
//SparseArray<ChangingField> data = new SparseArray<ChangingField>();
//int counter = 0;
//
//try {
//JSONObject obj = new JSONObject(rawStr);
//
//
//counter = parseOneField(data, counter, obj, "bar", "barcode");
//
//counter = parseOneField(data, counter, obj, "cap", "caption");
//counter = parseOneField(data, counter, obj, "bar", "barcode");
//counter = parseOneField(data, counter, obj, "leaved", "leaved");
//counter = parseOneField(data, counter, obj, "measured", "measured");
//counter = parseOneField(data, counter, obj, "priced", "priced");
//} catch (JSONException e) {
//e.printStackTrace();
//data = null;
//}
//return data;
//}
//
//public static int parseOneField(SparseArray<ChangingField> data,
//int counter, JSONObject obj, String jsonName, String localName) {
//try {
//ChangingField fd = 
//		parseFieldDatas(obj.getJSONObject(jsonName), localName);
//if (fd != null)	{
//	fd.name = localName;
//	data.put(counter++, fd);
//}
//} catch (JSONException e) {
//;
////e.printStackTrace();
////data.put(counter++, null);
//}
//return counter;
//}
//

//		@Override
//protected Void doInBackground(Data... data) {
//	if (data.length == 0) return null;
//	RawGetJsonObject.Data params = data[0];
//	ServerAnswerParsed<Object> raw;
//	ServerAnswerParsed<String> queryRes = 
//			NetUtils.httpJsonQuery(params.url, params.json.toString()); 
//	if (queryRes.answer.status == ServerAnswer.SUCCESS) {
//		raw = JsonUtils.partlyParseServerAnswer(queryRes.rec, params.isArray);
//	} else {
//		raw = new ServerAnswerParsed<Object>(); 
//		raw.answer = queryRes.answer;
//	}
//	params.result = raw;
//	return null;
//}
