package ru.overscan.lib.data;

import android.util.Log;
import android.util.SparseArray;

public class Table {
	final static String TAG = "Table";
	
	Record buffer;
//	SparseArray<String[]> recs1;
	int currentSize;
	public String[] primaryKey;
	
	Data currentDatas;
	
	public class Data {
		SparseArray<String[]> recs;
		
		public Data(){
			recs = new SparseArray<String[]>();
		}
	} 	
	
	public Table() {
		init(new Record());
	};

	public Table(String[] fields) {
		Record r = new Record();
		for (int i = 0; i < fields.length; i++) {
			r.addField(fields[i]);
		}
		init(r);
	};
	
	public Table(Record r) {
		init(r);
	};
	
	private void init(Record r){
		buffer = r;
		currentDatas = this.new Data();
		currentSize = 0;		
	}
	
	public int size() {
		return currentSize;
	}
	
	public void addField(String name) {
		buffer.addField(name);
	}
	
	public void addField(String name, int type) {
		buffer.addField(name, type);
	}
	
	public void addField(Field f) {
		buffer.addField(f);
	}
	
	public void put(String name, String value) {
		buffer.put(name, value);
	}

	public String get(String name) {
		return buffer.get(name);
	}
	
	public void addRec() {
		currentDatas.recs.put(currentSize, buffer.getValuesAsArray());
//		recs.put(currentSize, buffer.getValuesAsArray());
		currentSize++;
	}

	public void setRec(int pos) {
		currentDatas.recs.put(pos, buffer.getValuesAsArray());
		if (currentSize < pos + 1) currentSize = pos + 1; 
	}
	
	public void chooseRec(int pos) {
		if (pos < currentSize) {
//			buffer.setValuesAsArray(recs.get(pos));
			try {
				buffer.setValuesAsArray(currentDatas.recs.get(pos));
			} 
			catch(Exception e) {
				buffer.clear();
			}
		}
		else buffer.clear();
	}

	public String[] getRecAsArray(int pos) {
		if (pos < currentSize) {
//			buffer.setValuesAsArray(recs.get(pos));
			return currentDatas.recs.get(pos);
		}
		else return null;
	}
	
	public void clear(){
		currentDatas.recs.clear();
//		recs.clear();
		currentSize = 0;
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
	
	// возвращает набор подобных строковых полей (с такими же именами полей) 
	public Record getFieldsBuffer() {		
		Record r = new Record();
		String[] names = buffer.getNames();
		for (int i=0; i < names.length; i++) {
			r.addField(new Field(names[i]));
		}
		return r;
	}

	public String[] getNames() {		
		return buffer.getNames();
	}

	public Field getField(String name) {
		return buffer.getField(name);
	}
	
}
