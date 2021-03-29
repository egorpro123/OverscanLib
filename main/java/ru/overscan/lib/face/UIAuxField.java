package ru.overscan.lib.face;

import android.view.View;

/**
 * Created by fizio on 21.05.2016.
 */
public class UIAuxField extends UIField {

    String value;

    public UIAuxField(String name) {
        super(name, null);
    }

    @Override
    protected void setValueToView(String s) {
        value = s;
    }

    @Override
    protected String getValueFromView() {
        return value;
    }

    @Override
    public void setVisibility(boolean visible){
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
