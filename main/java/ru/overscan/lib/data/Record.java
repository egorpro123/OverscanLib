package ru.overscan.lib.data;

import java.util.Arrays;
import java.util.HashMap;

import android.util.SparseArray;

public class Record {
//	final static String TAG = "Record";
	
	private SparseArray<Field> fields;
	public int count;
	private HashMap<String,Integer> posByNames;
//	private boolean paranoid;
//	private String[] values;
//	private boolean initializing = true;
	
	
	public Record() {
		fields = new SparseArray<Field>();
		count = 0;
		posByNames = new HashMap<String,Integer>();
//		paranoid = true;
	}
	
	public void addField(String name) {
		this.addField(new Field(name));
	}

	public void addField(String name, int type) {
		this.addField(new Field(name, type));
	}
	
	public void addField(String name, String value) {
		this.addField(new Field(name, value));
	}
	
	public void addField(Field f) {
		fields.put(count, f);
		posByNames.put(f.name, count);
		count ++;
//		initializing = true;
	}

	private int getPos(String name) {
		return posByNames.get(name);
	}
	
	public boolean hasField(String name) {
		return posByNames.containsKey(name);
	}

	public Field getField(String name) {
		return fields.get(getPos(name));
	}

	public Field getField(int ind) {
		return fields.get(ind);
	}
	
//	public void prepareState() {
//		values = new String[count];
//		initializing = false;
//	};
	
	public void put(String name, String value) {
//		if (initializing) prepareState();
//		values[getPos(name)] = value;
		fields.get(getPos(name)).setValue(value);
	}

	public void put(int index, String value) {
//		if (initializing) prepareState();
//		values[getPos(name)] = value;
		fields.get(index).setValue(value);
	}
	
	public String get(String name) {
//		if (initializing) prepareState();
//		Log.d(TAG, Integer.toString(getPos(name)));
//		Log.d(TAG, values[getPos(name)]);
//		return values[getPos(name)];
		return fields.get(getPos(name)).getValue();
	}
	
	public String[] getValuesAsArray() {
		String[] values = new String[count];
		for (int i=0; i<count; i++) values[i] = fields.get(i).getValue();
		return values;
	}

	public void setValuesAsArray(String[] v) {
		for (int i=0; i<((v.length > count) ? count : v.length); i++) 
			fields.get(i).setValue(v[i]);
	}
	
	public void clear() {
		for (int i=0; i<count; i++) fields.get(i).setValue(null);
	}

	@Override
	public String toString() {
		String s = "";
		//
//		String[] keys = posByNames.keySet().toArray(new String[0]);
		String[] keys = getNames();
//		Arrays.sort(keys); 
		for(int i = 0; i < keys.length; i++) { 
			s = s + keys[i] + "=" + get(keys[i]) + 
					(i < keys.length - 1 ? ", " : "");
		}
		return s;
	}
		
	public String[] getNames(){
		String[] names = new String[count];
//		String[] names;
		for (int i = 0; i < count; i++) {
			names[i] = getField(i).name;
		}
		return names;
//		return posByNames.keySet().toArray(new String[0]);
	}
	
}

// test
//	Record r = new Record();
//	r.addField("first");
//	r.addField(new Field("second"));
//	r.put("first", "first value");
//	r.get("second");

