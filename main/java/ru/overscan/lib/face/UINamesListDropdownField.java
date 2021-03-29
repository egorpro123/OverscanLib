package ru.overscan.lib.face;

import java.util.ArrayList;

import android.view.View;

import ru.overscan.lib.data.DataUtils;
import ru.overscan.lib.data.EntityName;
import ru.overscan.lib.data.JsonReceiving;

public class UINamesListDropdownField extends UITextField{

	public UINamesListDropdownField(String name, View parent, int id) {
		super(name, parent, id);
		//
	}
	
	String id;
//		EditText edit;
//		LocalityNamesReceiving namesReceiving;
	NamesListDropdown dropdown;
//		NamesListDropdown.OnSelect onSelect;
	
	public void setInitialValue(String id, String shown) {
		initialValue = id;
		this.id = id;
		edit.setText(shown);			
	}
	
//	@Override
//	public void setValue(String s) {
//		id = s;
//	}
	
	@Override
	public String getValue() {
		return id;
	}

//	@Override
//	public void setValue(String s) {
//		setValue(s, true);
////		if (edit != null) edit.setText(s);
////        if (observer != null) observer.notify(name);
//	}

	@Override
	public void setValue(String s, boolean observerNotify) {
		id = s;
//		setValueToView(s);
		if (observer1 != null)
			if (observerNotify) observer1.notify(name);
			else observer1.changed(name);
	}


	public void setShownValue(String s) {
		setValueToView(s);
	}

	public String getShownValue() {
		return getValueFromView();
	}
//	public String getShownValue() {
//		return super.getValue();
//	}

	public void initDropdown(JsonReceiving<ArrayList<EntityName>> receiving,
			NamesListDropdown.OnSelect h) {
		dropdown = new NamesListDropdown(edit, receiving, h); 
	}
	
	@Override
	public boolean isChanged() {
		return DataUtils.stringsDiffer(id, initialValue);
	}
		
}
