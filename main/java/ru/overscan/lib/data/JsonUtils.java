package ru.overscan.lib.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;
import ru.overscan.lib.net.NetUtils;
import ru.overscan.lib.net.ServerAnswer;
import ru.overscan.lib.net.ServerAnswerParsed;
import ru.overscan.lib.sys.ErrorCollector;
import ru.overscan.lib.sys.OverscanApplication;

public class JsonUtils {
	private static OverscanApplication application;
	
	private static final int SERVER_STATUS_UNAUTHORIZED = -2;
	private static final int SERVER_STATUS_UNEXPECTED_ERROR = -1;

	public static synchronized void initialize(OverscanApplication app) {
		application = app;
    }
	
	public static ServerAnswerParsed<JSONObject> getRawJsonFromServer(String url,
			JSONObject json) {
		ServerAnswerParsed<JSONObject> parsed = new ServerAnswerParsed<JSONObject>();
		ServerAnswerParsed<Object> obj = getRawJsonObject(url, json, true);
		parsed.answer = obj.answer;
		parsed.rec = (JSONObject) obj.rec;
		return parsed;
////		return JsonUtils.parseServerAnswer(NetUtils.httpJsonQuery(url, json));
//		ServerAnswerParsed<JSONObject> parsed = new ServerAnswerParsed<JSONObject>();
//		ServerAnswerParsed<String> queryRes = NetUtils.httpJsonQuery(url, json);
//		if (queryRes.answer.status == ServerAnswer.SUCCESS) {
//			ServerAnswerParsed<Object> answer = 
//					JsonUtils.parseServerAnswer(queryRes.rec, false);
//			parsed.answer = answer.answer;
//			parsed.rec = (JSONObject) answer.rec;
//		} 
//		else parsed.answer = queryRes.answer;
//		return parsed;
////		String res = NetUtils.httpJsonQuery(App.serverUrl + "/commodity", 
////				obj.toString());
////		ServerAnswerParsed<JSONObject> sa = JsonUtils.parseServerAnswer(res);
////		if (sa.answer.status == ServerAnswer.SUCCESS) {
////			commodity = JsonUtils.parseCommodity(sa.rec);
////		} else {
////			commodity.answer = sa.answer;
////		}
	}


	public static ServerAnswerParsed<JSONArray> getRawJsonArrayFromServer(String url,
			JSONObject json) {
		ServerAnswerParsed<JSONArray> parsed = new ServerAnswerParsed<JSONArray>();		
		ServerAnswerParsed<Object> obj = getRawJsonObject(url, json, true);
		parsed.answer = obj.answer;
		parsed.rec = (JSONArray) obj.rec;
		return parsed;
//		ServerAnswerParsed<JSONArray> parsed = new ServerAnswerParsed<JSONArray>();
//		ServerAnswerParsed<String> queryRes = NetUtils.httpJsonQuery(url, json); 
//		if (queryRes.answer.status == ServerAnswer.SUCCESS) {
//			ServerAnswerParsed<Object> answer = 
//				JsonUtils.parseServerAnswer(queryRes.rec, true);
//			parsed.answer = answer.answer;
//			parsed.rec = (JSONArray) answer.rec;
//		}
//		else parsed.answer = queryRes.answer;
//		return parsed;
	}

	private static ServerAnswerParsed<Object> getRawJsonObject(String url,
			JSONObject json, boolean isArray) {
		ServerAnswerParsed<Object> raw;
		
		if (application.access_token != null && !application.access_token.equals("")) {
			try {
				json.put("access_token", application.access_token);
			} catch (JSONException e) {
				//
				ErrorCollector.add("Ошибка при добавлении токена в данные запроса", e);
			}
		}
		
		if (application == null) {
			raw = new ServerAnswerParsed<Object>(); 
			raw.answer = ServerAnswer.createApplicationError( 
					new IllegalStateException(JsonUtils.class.getSimpleName() +
	                " is not initialized, call initialize(..) method first."));
			return raw;
		}
		if (!NetUtils.isNetworkAvailable(application)) {
			String msg = "Отсутствует сетевое подключение";
//			Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show(); //i18n
			raw = new ServerAnswerParsed<Object>();
			raw.answer = new ServerAnswer();
			raw.answer.status = ServerAnswer.NO_NETWORK;
			raw.answer.message = msg;
			return raw;
		}
		ServerAnswerParsed<String> queryRes = 
				NetUtils.httpJsonQuery(url, json.toString()); 
		if (queryRes.answer.status == ServerAnswer.SUCCESS) {
			raw = JsonUtils.partlyParseServerAnswer(queryRes.rec, isArray);
//			parsed.answer = answer.answer;
//			parsed.rec = (JSONArray) answer.rec;
		} else {
			raw = new ServerAnswerParsed<Object>(); 
			raw.answer = queryRes.answer;
		}
		return raw;
	}
	
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
		

	private static ServerAnswerParsed<Object> partlyParseServerAnswer(String rawStr, 
			boolean isArray) {
		ServerAnswer answer = new ServerAnswer();
		ServerAnswerParsed<Object> parsed = new ServerAnswerParsed<Object>();
		if (rawStr == null || rawStr.equals("") || rawStr.equals("null")) {
			answer.status = ServerAnswer.UNKNOWN_ERROR;
			ErrorCollector.add("Получена NULL или пустая строка от сервера - " + 
			 rawStr + ".");
		}
		else {
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
						if (isArray) parsed.rec = obj.getJSONArray("data");
						else parsed.rec = obj.getJSONObject("data");
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
	
	public static ArrayList<EntityName>	parseNamesList(JSONArray array) throws JSONException {
//		ServerAnswerParsed<ArrayList<EntityName>> parsed = 
//				new ServerAnswerParsed<ArrayList<EntityName>> ();
		ArrayList<EntityName> names = new ArrayList<EntityName>();
//		parsed.rec = names;
//		parsed.answer = new ServerAnswer();
//		parsed.answer.status = ServerAnswer.SUCCESS;
		
		EntityName name;
		JSONObject obj;
		if (array != null) {			
//			try {
			for (int i = 0; i < array.length(); i++) {
				obj = array.getJSONObject(i);
				name = new EntityName();
				name.id = obj.getString("id");
				name.name = obj.getString("name");
				names.add(name);
			}
//			} catch (JSONException e) {
//				parsed.answer = ServerAnswer.createServerError(e);
//	            parsed.rec = null;
//				//
////				e.printStackTrace();
//			}
		}		
		return names;
	}

	public static Table parseUniRecs(JSONArray array, String[] fields) throws JSONException {
//	ServerAnswerParsed<Table> parsed = new ServerAnswerParsed<Table> ();
	ru.overscan.lib.data.Table res = new Table();
	for (int i = 0; i < fields.length; i++) {
		res.addField(fields[i]);
	}
//	parsed.rec = res;
//	parsed.answer = new ServerAnswer();
//	parsed.answer.status = ServerAnswer.SUCCESS;

	if (array != null) {			
		JSONObject obj;
		for (int i = 0; i < array.length(); i++) {
			obj = array.getJSONObject(i);
			for (int j = 0; j < fields.length; j++) {
				if (obj.isNull(fields[j])) res.put(fields[j], "");
				else res.put(fields[j], obj.getString(fields[j]));
			}
			res.addRec();
		}
	}
	return res;
//		String[] names = fields.getNames();
//		try {
//			for (int i = 0; i < array.length(); i++) {
//				obj = array.getJSONObject(i);
//				for (int j = 0; j < fields.length; j++) {
//					if (obj.isNull(fields[j])) res.put(fields[j], "");
//					else res.put(fields[j], obj.getString(fields[j]));
//				}
//				res.addRec();
//			}
//		} catch (JSONException e) {
//			parsed.answer = ServerAnswer.createServerError(e);
//	        parsed.rec = null;
//			//
//	//		e.printStackTrace();
//		}
//	}		
//	return parsed;
}
	
	
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
	
}


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
