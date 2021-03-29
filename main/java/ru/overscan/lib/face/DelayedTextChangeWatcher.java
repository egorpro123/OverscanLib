package ru.overscan.lib.face;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

abstract public class DelayedTextChangeWatcher implements TextWatcher {

//	OnChange onChange;
//	interface OnChange {
//		public void handle(String s);
//	}
	abstract public void onChangeAfterDelay(String s);

	public void onChange(String s){
		
	};
	
	EditText target;
	int delay;
	ChangeHandler changeHandler;
//	String prevText;
	
	public DelayedTextChangeWatcher(int delay) {
		this.delay = delay;
//		onChange = h;
		changeHandler = new ChangeHandler(this);
//		target.addTextChangedListener(new TextWatcher() {
//
//			
//		});
//		private TextWatcher filterTextWatcher = new TextWatcher()
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//
		onChange(s.toString());
		changeHandler.send(s.toString());
//		String text = s.toString().trim();
//		if (DataUtils.stringsDiffer(text, prevText)) {
//			changeHandler.send(s.toString());
//		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		//
		
	}
	
	
	static class ChangeHandler extends Handler {
		WeakReference<DelayedTextChangeWatcher> watcher;
		
		ChangeHandler(DelayedTextChangeWatcher w) {
			watcher = new WeakReference<DelayedTextChangeWatcher>(w);
		}
		
		void send(String s){
			removeMessages(0);
			Message msg = obtainMessage(0);
			Bundle b = new Bundle();
			b.putString("string", s);
			msg.setData(b);
			sendMessageDelayed(msg, watcher.get().delay);
		}
		
		
		@Override
		public void handleMessage(Message msg) {
			DelayedTextChangeWatcher w = watcher.get();
			w.onChangeAfterDelay(msg.getData().getString("string"));
//			if (b.onChange != null) 
//				b.onChange.handle(msg.getData().getString("string"));
		}
		
	}
	
}


//public class DelayedChangeHandler {
//
//	OnChange onChange;
//	interface OnChange {
//		public void handle(String s);
//	}
//	
//	EditText target;
//	int delay;
//	ChangeHandler changeHandler;
//	
//	public DelayedChangeHandler(EditText target, int delay, OnChange h) {
//		this.delay = delay;
//		onChange = h;
//		changeHandler = new ChangeHandler(this);
//		target.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				//
//				
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				//
//				changeHandler.send(s.toString());
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				//
//				
//			}
//			
//		});
////		private TextWatcher filterTextWatcher = new TextWatcher()
//	}
//	
//	static class ChangeHandler extends Handler {
//		WeakReference<DelayedChangeHandler> boss;
//		
//		ChangeHandler(DelayedChangeHandler b) {
//			boss = new WeakReference<DelayedChangeHandler>(b);
//		}
//		
//		void send(String s){
//			removeMessages(0);
//			Message msg = obtainMessage(0);
//			Bundle b = new Bundle();
//			b.putString("string", s);
//			msg.setData(b);
//			sendMessageDelayed(msg, boss.get().delay);
//		}
//		
//		
//		@Override
//		public void handleMessage(Message msg) {
//			DelayedChangeHandler b = boss.get();
//			if (b.onChange != null) 
//				b.onChange.handle(msg.getData().getString("string"));
//		}
//		
//	}
//	
//}

