package ru.overscan.lib.face;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.overscan.lib.data.DataUtils;
import ru.overscan.lib.data.Field;

public class UITextField extends UIField{
//	int resourceId;
	public EditText edit;

	public UITextField(String name, View parent, int resourceId) {
		super(name, parent.findViewById(resourceId));
//		setView(parent, resourceId);
        edit = (EditText) editView;
	}


    @Override
    protected void setValueToView(String s) {
        if (edit != null) edit.setText(s);
    }

//	public void setView(View parent, int resourceId) {
//	}

	@Override
	public String getValueFromView() {
		return edit.getEditableText().toString().trim();
	}

}
