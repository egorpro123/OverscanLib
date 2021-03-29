package ru.overscan.lib.face;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropdownControl extends DataControl {
//	public String dropdownList[];
	private HashMap<String,String> enumValues;
	
	public DropdownControl(String caption) {
		super(caption);
		type = SPINNER;
//		field.dataType = Field.ENUM_DATA;
	}
	
	public DropdownControl(String caption, String[] list) {
		this(caption);
		HashMap<String, String> map = new HashMap<String, String>(); 
		for (int i = 0; i < list.length; i++) {
//			Log.d(TAG, Integer.toString(i) + " " + list[i]);
			map.put(Integer.toString(i), list[i]);
		}
		setPossibleValues(map);
	}

	public DropdownControl(String caption, String[][] list) {
		this(caption);
		HashMap<String, String> map = new HashMap<String, String>(); 
		for (int i = 0; i < list.length; i++) {
//			Log.d(TAG, Integer.toString(i) + " " + list[i]);
			map.put(list[i][0], list[i][1]);
		}
		setPossibleValues(map);
	}

	public DropdownControl(String name, String caption, String[][] list) {
		this(caption, list);
		this.setName(name);
//		field.setValue(value);
	}
	
	public DropdownControl(String name, String caption, String[] list) {
		this(caption, list);
		this.setName(name);
//		field.setValue(value);
	}

	public DropdownControl(String caption, HashMap<String, String> map) {
		this(caption);
		setPossibleValues(map);
	}
	
	public DropdownControl(String name, String caption, 
			HashMap<String, String> map) {
		this(caption, map);
//		setPossibleValues(map);
		this.setName(name);
	}
			
	public void setPossibleValues(HashMap<String,String> vals) {
		enumValues = vals; 
//		field.setEnumValues(vals);
	}
	
	
	public View makeView(){
		if (controls == null) { 
			return null;
		}
		Spinner spinner = new Spinner(controls.context);
		ArrayList<String> list =
				new ArrayList<>(enumValues.values());
		Collections.sort(list);

	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
	    		controls.context, android.R.layout.simple_spinner_item, 
	            list.toArray(new String[0])); 
//	    spinnerArrayAdapter.setDropDownViewResource(
//	    		android.R.layout.simple_spinner_dropdown_item );
	    spinner.setAdapter(spinnerArrayAdapter);
	    return spinner;
	}
	
}
