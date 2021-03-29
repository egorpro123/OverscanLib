package ru.overscan.lib.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import ru.overscan.lib.sys.ErrorCollector;

public class HttpQuery extends AsyncTask<Void, Void, String> {
	private static final String TAG = HttpQuery.class.getSimpleName();
	private String urlString;
	private boolean usePost;
	private String sendMessage;
	
	private Handler handler;
    public interface Handler {
        public void handleResult(String result);
    }	

	public HttpQuery(String url, Handler h){
		urlString = url;
		handler = h;
		usePost = false;
	}
	
	public HttpQuery(String url, Handler h, boolean usePost){
		this(url, h);
		this.usePost = usePost;
	}
	

	@Override
	protected String doInBackground(Void... params) {
		ServerAnswerParsed<String> ans = NetUtils.httpQuery(urlString, usePost);
		return ans.rec;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (handler != null) handler.handleResult(result);
	}	

}

// -- test --

//if (NetUtils.isNetworkAvailable(this)) {
//new HttpQuery("http://www.ya.ru", new HttpQuery.Handler() {
//	@Override
//	public void handleResult(String result) {
//		showText(result);
//	}
//}).execute();
////String s = HttpResource.postReceive("http://www.ya.ru"); 
////Log.d(TAG, s);
//
//}
//else showText("Включите сеть");


