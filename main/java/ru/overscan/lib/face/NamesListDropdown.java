package ru.overscan.lib.face;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Entity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import ru.overscan.lib.data.DataUtils;
import ru.overscan.lib.data.EntityName;
import ru.overscan.lib.data.FilteredAdapter;
import ru.overscan.lib.data.JsonReceiving;
import ru.overscan.lib.net.ServerAnswer;
import ru.overscan.lib.net.ServerAnswerParsed;
import ru.overscan.lib.sys.OverscanApplication;

public class NamesListDropdown implements JsonReceiving.OnFinish<ArrayList<EntityName>>,
		AdapterView.OnItemClickListener{
	final static String TAG = "NamesListDropdown";
	final static boolean selectEmptyOnClear = true;
	String queryFilter;
	String prevFilter;
	FilteredAdapter<EntityName> adapter;
//	ListView listView;
	EditText filterEdit;
	JsonReceiving<ArrayList<EntityName>> receiving;
//	String resUrl;
	ListPopupWindow popup;
	RenewHandle renewHandle;
	
	long prevChangeTime;
//	Params params;
//	PopupWindow popup;

	private OnSelect onSelect;
	public interface OnSelect {
	    public void select(EntityName name);
	}	
	
	
//	public static class Params {
//		String tab;
//		String filter;   // заполняется автоматически из filterEdit
//		String innerUrl;
//		String idField;
//		String nameField;
//		Table equals;
//		
//		public Params(){}
//		
//		public Params(String innerUrl, String filter){
//			this.innerUrl = innerUrl;
//			this.filter = filter;
//		}
//	}
	
	public NamesListDropdown(EditText filterEdit, 
			JsonReceiving<ArrayList<EntityName>> receiving,
			OnSelect h) {
		this.receiving = receiving;
		receiving.setOnFinish(this);
//		Params p = new Params();
//		p.innerUrl = resUrl;
		onSelect = h;
		Init(filterEdit);
	}

//	public NamesListDropdown(EditText filterEdit, Params params, 
//			OnSelect h) {
//		onSelect = h;
//		Init(filterEdit, params);
//	}
	
	private void Init(EditText filterEdit) {
		this.filterEdit = filterEdit;
//		this.params = params;
		queryFilter = "";
		prevFilter = "";
//		this.resUrl = resUrl;
		
		filterEdit.addTextChangedListener(filterTextWatcher);

		popup = new ListPopupWindow(filterEdit.getContext());
		popup.setAnchorView(filterEdit);
//        popup.setFocusable(true);
//        popup.setWidth(250);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        
        popup.setOnItemClickListener(this);
        prevFilter = filterEdit.getText().toString();
        renewHandle = new RenewHandle(this);
	}
	
	
	private DataSetObserver dataObserver = new DataSetObserver() {
		public void onChanged () {
			if (adapter != null) {
				if (adapter.getCount() == 0) hidePopup();
				else showPopup();
			}			
		}
	};
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
			popup.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
			renewHandle.send(s.toString().trim());
//	    	renew(s.toString().trim());
//			Log.d(TAG, "textChanged... " + s);
	    }

	};
	
	
	
	static class RenewHandle extends Handler {
		WeakReference<NamesListDropdown> namesList;
		
		RenewHandle(NamesListDropdown obj) {
			namesList = new WeakReference<NamesListDropdown>(obj);
		}
		
		void send(String s){
			removeMessages(0);
			Message msg = obtainMessage(0);
			Bundle b = new Bundle();
			b.putString("filter", s);
			msg.setData(b);
			sendMessageDelayed(msg, 1000);
		}
		
		
		@Override
		public void handleMessage(Message msg) {
			NamesListDropdown obj = namesList.get();
			obj.renew(msg.getData().getString("filter"));
		}
		
	}
	
	
//	public boolean TimeForRenew() {
//		long newTime = DataUtils.getCurrentDateTime();
//		boolean ok = (newTime - prevChangeTime > 1000);
//		prevChangeTime = newTime;
//		return ok;
//	}
	
	
	public void renew(String filter) {
		
		if (!DataUtils.stringsDiffer(prevFilter, filter)) return;
//		Log.d(TAG, "renew... " + filter);
		prevFilter = filter;
		if (DataUtils.emptyString(filter) || filter.length() <3) {
//			if (DataUtils.emptyString(filter) && (onSelect != null)) {
//				onSelect.select(new EntityName());
//			}

			hidePopup();
//			adapter = null;
//			if (popup.isShowing()) popup.dismiss();
//			listView.setAdapter(null);
//			Log.d(TAG, "hide popup on renew");
		}
		else {
			if (adapter == null || filterChangedForQuery(filter)) {
				Log.d(TAG, "get from server... filter "+ filter + ", queryFilter " + queryFilter);
				receiving.addSpecialParam("filter", filter);
				receiving.start();
//				params.filter = filter;
//				(new NamesListReceiving(params, this)).start();
//				(new NamesListReceiving(params, this)).execute();
				queryFilter = filter;
			}
			else {
				Log.d(TAG, "update adapter...");
				adapter.getFilter().filter(filter);
//				Log.d(TAG, "filter adapter");
//				if (adapter.getCount() == 0) hidePopup();
//				else showPopup();
			}
		}				
	}
	
	public String getPopupInfo() {
//		getInputMethodMode()
		return "getSoftInputMode() " + popup.getSoftInputMode() + ", " +
			"getInputMethodMode() " + popup.getInputMethodMode() + ", " +
			"isModal() " + Boolean.toString(popup.isModal());
	
	}

	public void hidePopup() {
//		Log.d(TAG, getPopupInfo() + " try hide popup");
//		Log.d(TAG, "try hide popup");
		if (popup.isShowing()) {
//			Log.d(TAG, "real hide popup");
			popup.dismiss();		
		}
	}
	
	public void showPopup() {
//		Log.d(TAG, getPopupInfo() + " try show popup");
//		Log.d(TAG, "try show popup");
		if (!popup.isShowing()) { 
//			Log.d(TAG, "real show popup");
			popup.show();
		}
//		if (!popup.isShowing()) popup.showAsDropDown(filterEdit, 0, -5);
	}
	
	private boolean filterChangedForQuery(String filter) {
		return filter.indexOf(queryFilter) < 0;
	}

	@Override
	public void handleResult(ServerAnswerParsed<ArrayList<EntityName>> parsed) {
		//
		if (parsed.answer.status == ServerAnswer.NO_NETWORK)
		  Toast.makeText(OverscanApplication.getInstance(), parsed.answer.message, 
				  Toast.LENGTH_SHORT).show();

		if (parsed.answer.status != ServerAnswer.SUCCESS) return;
//		adapter = new FilteredAdapter<EntityName>(filterEdit.getContext(), parsed.rec, 
//				android.R.layout.simple_spinner_item);
		if (adapter != null) adapter.unregisterDataSetObserver(dataObserver);
		
		adapter = new FilteredAdapter<EntityName>(filterEdit.getContext(), parsed.rec, 
				android.R.layout.simple_spinner_dropdown_item);
		adapter.registerDataSetObserver(dataObserver);
		popup.setAdapter(adapter);
//		Log.d(TAG, "new adapter");
//		listView.setAdapter(adapter);
		showPopup();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//
//		Log.d(TAG, adapter.getItem(position).id + " " +
//				adapter.getItem(position).name);
		prevFilter = adapter.getItem(position).name;
//		queryFilter = prevFilter;
		if (onSelect != null) {
			EntityName name = adapter.getItem(position);
			filterEdit.setText(name.name);
			onSelect.select(name);
//			onSelect.select(adapter.getItem(position));
		}
		else filterEdit.setText(prevFilter);

		hidePopup();
	}

}
