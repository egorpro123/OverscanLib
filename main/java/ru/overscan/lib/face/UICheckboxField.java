package ru.overscan.lib.face;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by fizio on 16.05.2016.
 */
// значение поля должно быть строкой - true или false
public class UICheckboxField  extends UIField{
    //	int resourceId;
    public CheckBox checkBox;

    public UICheckboxField(String name, View parent, int resourceId) {
        super(name, parent.findViewById(resourceId));
//		setView(parent, resourceId);
        checkBox = (CheckBox) editView;
    }


    @Override
    protected void setValueToView(String s) {
        if (s != null && s.indexOf("true") >= 0)
            checkBox.setChecked(true);
        else checkBox.setChecked(false);
    }

//	public void setView(View parent, int resourceId) {
//	}

    @Override
    public String getValueFromView() {
        if (checkBox.isChecked()) return "true";
        else return "false";
    }

}

