package ru.overscan.lib.face;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import ru.overscan.lib.data.Field;
import ru.overscan.lib.face.Controls.LayoutAndDataView;

public class DataControl extends Control {
	Field field;

	public DataControl(String caption, String name) {
		super(caption);
		type = EDIT_TEXT;
		this.name = name;
		field = new Field(name);
	}
	
	public DataControl(String caption) {
		this(caption, caption);
	}
	
	
	public void setName(String name){
		field.name = name;
	}
			
	public void setValue(String value){
		field.setValue(value);
	}
	
	public void setType(int type){
		field.type = type;
	}
	

	public LayoutAndDataView getLayoutAndView() {
		if (controls == null) return null;
		LayoutAndDataView lav = super.getLayoutAndView();
		View v = getView();
        lav.layout.addView(v);
		return new LayoutAndDataView(lav.layout, v);
	}	
	
	
	
	public View getView() {
		if (controls == null) return null;
		EditText et = new EditText(controls.context);
	    if (field.getValue() != null) et.setText(field.getValue());
	    if (!controls.forEdit) {
	    	et.setClickable(false);
	    	et.setFocusable(false);
	    }
	    return et;
	}
	
}
