package ru.overscan.lib.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
	static final int DEFAULT_CONNECT_TIMEOUT = 15000; 
	static final int DEFAULT_READ_TIMEOUT = 10000; 
	
	public static boolean isNetworkAvailable(Context context) {
//		return false;
	    ConnectivityManager manager = (ConnectivityManager) 
	        context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = manager.getActiveNetworkInfo();
	    if (info != null && info.isConnected()) return true;
	    else return false;
	}


	public static ServerAnswerParsed<String> httpJsonQuery(String urlStr, String sendJson) {
		return rawHttpQuery(urlStr, sendJson, true);
	}
	
	public static ServerAnswerParsed<String> httpQuery(String urlStr, boolean usePost) {
		return rawHttpQuery(urlStr, null, usePost);
	}
	
//	private static String rawHttpQuery(String urlStr, String sendJson,
//			boolean usePost) {
	private static ServerAnswerParsed<String> rawHttpQuery(String urlStr, String sendJson,
			boolean usePost) {
		
//		Authenticator.setDefault(new Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("username", "password".toCharArray());
//			}
//		});		
		
		ServerAnswerParsed<String> response = new ServerAnswerParsed<String>();
		URL url;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e1) {
			//
			response.answer = ServerAnswer.createApplicationError(e1);
			return response;
		}
//		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			//
			response.answer = ServerAnswer.createServerError(e1);
			return response;
		}
		response.answer = new ServerAnswer();
		try {		
			con.setUseCaches(false);
			con.setReadTimeout(DEFAULT_READ_TIMEOUT);
			con.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
			con.setDoInput(true);
			if (sendJson != null && !sendJson.equals("")) {
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type","application/json;charset=utf-8");
				writeStringAndCloseStream(sendJson, con.getOutputStream());
//				OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
//				out.write(sendMessage);
//				out.close();
			} else if (usePost) con.setRequestMethod("POST");
//			con.setRequestMethod("GET");
//			con.setDoOutput(true);
//			con.connect();
			InputStream in = con.getInputStream();			
			int code = con.getResponseCode();
//			if (code == HttpURLConnection.HTTP_UNAUTHORIZED)
//				response.answer.status = ServerAnswer.UNAUTHORIZED;
//			else 
			if (code == HttpURLConnection.HTTP_OK) {
				response.answer.status = ServerAnswer.SUCCESS;
				response.rec = readContent(in); 
			}
		} catch (FileNotFoundException e) {
			response.answer.status = ServerAnswer.BAD_URL;
		} catch (Exception e) {
			response.answer = ServerAnswer.createServerError(e);
//			Log.e(TAG, e.getMessage());
//			ErrorCollector.add(e);
//			response = null;
//			e.printStackTrace();
		} finally {
           con.disconnect();
		}		
		return response;
	}
//	Uri.Builder builder = new Uri.Builder()
//    .appendQueryParameter("firstParam", paramValue1)
//    .appendQueryParameter("secondParam", paramValue2)
//    .appendQueryParameter("thirdParam", paramValue3);
//String query = builder.build().getEncodedQuery();

	public static void writeStringAndCloseStream(String s, OutputStream stream) 
			throws IOException {
		//OutputStream os = con.getOutputStream();
//		BufferedWriter writer = new BufferedWriter(
//        new OutputStreamWriter(URLEncoder.encode(stream,"UTF-8"), "UTF-8"));
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(stream, "UTF-8"));
		writer.write(s);
		writer.flush();
		writer.close();
		stream.close();		
	}

	
	// Reads an InputStream and converts it to a String.
	public static String readContent(InputStream stream) 
			throws IOException, UnsupportedEncodingException {
//		String line;
//		String response = null;
//        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//        while ((line = br.readLine()) != null) response += line;
//        return response;
        
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer("");
        String s;
        while ((s = reader.readLine()) != null) {
            buffer.append(s);
        }
        reader.close();
        return buffer.toString();
	}	
	
	
}
