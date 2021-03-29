package ru.overscan.lib.data;

import java.util.ArrayList;
import java.util.HashMap;

import ru.overscan.lib.face.FieldsObserver;

public class Field {
	public static final int STRING_DATA = 0;
	public static final int INT_DATA = 1;
	public static final int DATE_TIME_DATA = 2;
	public static final int BOOLEAN_DATA = 3;
	//	  public static final int ENUM_DATA = 4;
	public static final int DOUBLE_DATA = 4;
	//	  public static final int NULL_DATA = 5;

	public static final int NORMAL_STATE = 0;
	public static final int NOT_NULL_STATE = 1;

	public String name;
	public int type;
	private int state;
	private String defaultValue;
	//	  private String initialValue;
	private String value;
//	public FieldsObserver.OnBasicFieldChangedListener onBasicFieldChangedListener;

	public Field(String name, int type){
	  this.name = name;
	  this.type = type;
	}

	public Field(String name){
	  this(name, STRING_DATA);
	}

	public Field(String name, String value){
	  this(name, STRING_DATA);
	  this.value = value;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String asString(){
		return getValue();
	}
	
	public double asDouble(){
		return Double.parseDouble(getValue());
	}

	public int asInt(){
		return Integer.parseInt(getValue());
	}

	public long asLong(){
		return Long.parseLong(getValue());
	}

    public boolean asBoolean(){
		String s = getValue();
		return s != null && s.indexOf("true") >= 0;
	}

//	public void dependentChanged(ArrayList<String> initializedFields){

}

