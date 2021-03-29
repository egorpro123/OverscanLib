package ru.overscan.lib.db;

import java.util.Locale;

import ru.overscan.lib.data.Field;

public class DBField extends Field {
	String dbName;
	int len = -1;
	boolean autoincremented;
	public String caption;

	public DBField(String name) {
		this(name, STRING_DATA);
		//
	}

	public DBField(String name, int type) {
		super(name, type);
		super.name = name.toLowerCase(Locale.ENGLISH);
		dbName = super.name;
		autoincremented = false;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	public void setDBName(String name) {
		dbName = name;	
	}	  
	  
	public String dbTypeName(int type){
		switch (type) {
    		case STRING_DATA: return "TEXT";
        	case INT_DATA: return "INTEGER";
        	case DATE_TIME_DATA: return "INTEGER";
        	case BOOLEAN_DATA: return "INTEGER";
        	case DOUBLE_DATA: return "REAL";
//        	case NULL_DATA: return "NULL";
        	default: return "INVALID_TYPE";
		}
	}
	
}

//Each value stored in an SQLite database (or manipulated by the database engine) has one of the following storage classes:
//
//NULL. The value is a NULL value.
//INTEGER. The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending on the magnitude of the value.
//REAL. The value is a floating point value, stored as an 8-byte IEEE floating point number.
//TEXT. The value is a text string, stored using the database encoding (UTF-8, UTF-16BE or UTF-16LE).
//BLOB. The value is a blob of data, stored exactly as it was input.