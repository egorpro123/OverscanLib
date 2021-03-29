package ru.overscan.lib.face;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import ru.overscan.lib.face.Controls.LayoutAndDataView;

public class Control {
	static public final int NO_DATA = 0;
	static public final int EDIT_TEXT = 1;
	static public final int SPINNER = 2;
	static public final int BUTTON = 3;
	public String caption;
	public String name;
	protected Controls controls = null;
	int type; // 0 - edittext, 1-spinner;
	
	public Control(String caption) {
		this.caption = caption;
//		this.context = context;
		type = NO_DATA;
	}
	
	public void setControls(Controls controls) {
		this.controls = controls;
	}
	
	
	public View getView() {
		if (controls == null) return null;
		if (type == BUTTON) {
			Button b = new Button(controls.context);
			b.setText(caption);
			return b;
		}
		else return null;
	}
	
	
	public LayoutAndDataView getLayoutAndView() {
		if (controls == null) return null;
    	LinearLayout layout = FaceUtils.createLinearLayout(controls.context, 
    			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 
    			LinearLayout.HORIZONTAL, 0);
        TextView tv = new TextView(controls.context);
        if (Controls.mPaddingLeft != 0) 
        	tv.setPadding(DisplayUtils.densityPixels2pixels(controls.context, 
        			Controls.mPaddingLeft), 0, 0, 0);
        tv.setText(caption);
        layout.addView(tv);
//        return layout;
        return new LayoutAndDataView(layout, null);
	}	
	
	public void setValue(String value){
	}
}
