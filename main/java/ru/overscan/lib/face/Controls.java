package ru.overscan.lib.face;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import ru.overscan.lib.R;
import ru.overscan.lib.data.DataUtils;
import ru.overscan.lib.data.Record;
import ru.overscan.lib.data.SparseArrayIterator;

public class Controls implements Iterable<Control>  {
	final static int CONTAINER_DEFAULT = 0;
	final static int CONTAINER_SAVE = 1;
	
//	private final static String TAG = Controls.class.getSimpleName();
	private final static String TAG = "Controls";
	static int mPaddingLeft;
	boolean forEdit;
	
	int size = 0;
	int containerType = CONTAINER_DEFAULT;
	SparseArray<Control> controls;
	HashMap<String, Integer> posByNames;
	Context context;	
	View mainLayout;
	ViewGroup controlsLayout;
	ButtonClickListener buttonClickListener;
	
	View.OnClickListener internalButtonOnClickListener;
	LayoutAndDataViews currentLayoutAndDataViews;

	public Controls(Context context) {
		this.context = context;
//		controls = new ArrayMap<String, Control>();
//		controls = new LinkedHashMap<String, Control>();
		controls = new SparseArray<Control>();
		posByNames = new HashMap<String, Integer>();
		mPaddingLeft = 10;
		size = 0;
		forEdit = true;
		currentLayoutAndDataViews = null;
		internalButtonOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
            	if (v.getTag() != null) {
            		if (buttonClickListener != null)
            			buttonClickListener.onClick((String)v.getTag());
            	}
                // Perform action on click
            }
        };
	}

		
	abstract static class ButtonClickListener{
		public abstract void onClick(String tag);
	}
	
	public void setButtonClickListener(ButtonClickListener l) {
		buttonClickListener = l;
	}
	
	public void setValue(String fldName, String value) {
		Log.d(TAG, Integer.toString(posByNames.get(fldName)));
		controls.get(posByNames.get(fldName)).setValue(value);
	}
	
	public void makeNotEditable() {
		forEdit = false;
	}
	
	public static class LayoutAndDataViews{
		View layout;
//		ViewGroup layout;
		HashMap<String, View> views;
		
		public LayoutAndDataViews(View layout){
//		public LayoutAndDataViews(ViewGroup layout){
			this.layout = layout;
			views = new HashMap<String, View>();
		}
		
		public void setDataView(String fldName, View view){
			views.put(fldName, view);
		}
	}
	
	public static class LayoutAndDataView{
		ViewGroup layout;
		View view;
		
		public LayoutAndDataView(ViewGroup layout, View view){
			this.layout = layout;
			this.view = view;
		} 
	}
	
	public void addControl(String caption) {
		this.addControl(new Control(caption));
	}
	
	public void addDataControl(String caption) {
		this.addControl(new DataControl(caption, caption));
	}
	
	public void addDataControl(String name, String caption) {
		this.addControl(new DataControl(caption, name));
	}

	public void addControl(Control c) {
		c.controls = this;
		controls.put(size, c);
		if (c.name == null) c.name = c.caption;
		posByNames.put(c.name, size);
		size++;
	}
	
	public void setConteinerType(int type) {
		containerType = type;
	}

	public View getControlsView(){
//	public ViewGroup makeControlsView(){
		if (currentLayoutAndDataViews != null) 
			return currentLayoutAndDataViews.layout;
		if (context == null) return null;
		currentLayoutAndDataViews = getLayoutAndDataViews();
		return currentLayoutAndDataViews.layout;
	}
	
	
	private void makeContainer() {
		controlsLayout = FaceUtils.createLinearLayout(context, LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, LinearLayout.VERTICAL, 0);
		if (containerType == CONTAINER_DEFAULT) {
	        mainLayout = controlsLayout;
		}
		else if (containerType == CONTAINER_SAVE) {
			//!!! main change to frame layout
			
			LinearLayout main = FaceUtils.createLinearLayout(context, LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT, LinearLayout.VERTICAL, 0);
			LinearLayout middle = FaceUtils.createLinearLayout(context, LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT, LinearLayout.VERTICAL, 1.0f);
			ScrollView v = new ScrollView(context);
			// без нижних 2х строки не позиционируется
			v.setLayoutParams(FaceUtils.createLayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.MATCH_PARENT, 0));
			v.setFillViewport(true);
			middle.addView(v);			

			((LinearLayout) controlsLayout).setGravity(Gravity.CENTER_VERTICAL);
			v.addView(controlsLayout);
			
			LinearLayout bottom = FaceUtils.createLinearLayout(context, LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL, 0);
			bottom.setGravity(Gravity.CENTER);
//			bottom.addView(new Button(context));
//			bottom.addView(new Button(context));
			bottom.addView(createButton(
					context.getResources().getString(R.string.save_button), 
					"control:save"));
			bottom.addView(createButton(
					context.getResources().getString(R.string.cancel_button), 
					"control:cancel"));
			
			main.addView(middle);
			main.addView(bottom);
			mainLayout = main;
		}
	}
	
	public Button createButton(String text, String tag) {
		Button b;
		b = new Button(context);
		b.setText(text);
		b.setTag(tag);
		b.setOnClickListener(internalButtonOnClickListener);
		return b;
	}

	public LayoutAndDataViews getLayoutAndDataViews(){
		return getLayoutAndDataViews(false);
	}
	
	public LayoutAndDataViews createNewLayoutAndDataViews(){
		return getLayoutAndDataViews(true);
	}
	
	public LayoutAndDataViews getLayoutAndDataViews(boolean createNew){
		if (!createNew && currentLayoutAndDataViews != null) 
			return currentLayoutAndDataViews;
		
		if (context == null) return null;
		makeContainer();
        
        LayoutAndDataViews lavs = new LayoutAndDataViews(mainLayout);
        LayoutAndDataView lav;
        Control c;
        for (int i = 0; i < controls.size(); i++) {
        	c = controls.get(i);
        	lav = c.getLayoutAndView();
        	controlsLayout.addView(lav.layout);
        	lavs.setDataView(c.name, lav.view);
        }
        if (!createNew) currentLayoutAndDataViews = lavs;
       	return lavs;
	}
	
	public void setDatas(Record data) {
		if (currentLayoutAndDataViews != null)
			setDatas(currentLayoutAndDataViews.views, data);
	}
	
	public void setDatas(HashMap<String, View> views, Record data) {
		String[] names = data.getNames();
		for (int i=0; i < names.length; i++) {
//			Log.d(TAG, "data.get(names[i]) " + names[i] + " - " + data.get(names[i]));
//		   ((EditText) views.get(names[i])).setText(data.get(names[i]));
			if (views.containsKey(names[i]))
				((TextView) views.get(names[i])).setText(data.get(names[i]));
		}
	}

	public void getDatas(Record data) {
		if (currentLayoutAndDataViews != null)
			getDatas(currentLayoutAndDataViews.views, data);
	}
	
	public void getDatas(HashMap<String, View> views, Record data) {
		String[] names = data.getNames();
		data.clear();
		for (int i=0; i < names.length; i++) {
			if (views.containsKey(names[i]))
				data.put(names[i],
						((TextView) views.get(names[i])).getText().toString());
		}
	}
	
	public String getDataViewValue(String name) {
		return ((TextView) currentLayoutAndDataViews.views.get(name)).
				getText().toString();
	}

	public boolean existDataView(String name) {
		return currentLayoutAndDataViews.views.containsKey(name);
	}
	
	public boolean datasDiffer(Record data) {
		if (currentLayoutAndDataViews == null) 
			throw new IllegalStateException("Отсутствует шаблон и представления данных"); 
		String[] names = data.getNames();
		for (int i=0; i < names.length; i++) {
			if (existDataView(names[i]))
				if (DataUtils.stringsDiffer(data.get(names[i]),
						getDataViewValue(names[i]))) return true; 
		}
		return false;
	}

	@Override
	public Iterator<Control> iterator() {
		//
		return new SparseArrayIterator<Control>(controls);
	}
	
}

