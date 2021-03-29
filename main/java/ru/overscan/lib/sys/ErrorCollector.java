package ru.overscan.lib.sys;

import android.util.Log;
import android.util.SparseArray;
import ru.overscan.lib.net.ServerAnswer;

public class ErrorCollector {
	static final String TAG = "ErrorCollector";
	
	static SparseArray<ErrorDatas> errors;
	static int errorsCounter;
	static final int MAX_ERRORS_AMOUNT = 20;
	
	static {
		errors = new SparseArray<ErrorDatas>();
		errorsCounter = 0;
	}
	
	static public class ErrorDatas {
		String msg;
		Throwable err;
	}
	
	public static void add(String error) {
		ErrorDatas e = new ErrorDatas();
		e.msg = error;
		addErrorDatas(e);
	}

	public static void add(Throwable error) {
		ErrorDatas e = new ErrorDatas();
		e.msg = error.getMessage();
		e.err = error;
		addErrorDatas(e);
	}

	public static void add(String message, Throwable error) {
		ErrorDatas e = new ErrorDatas();
		e.msg = message;
		e.err = error;
		addErrorDatas(e);
	}
	
	private static void addErrorDatas(ErrorDatas data) {
		if (errorsCounter >= MAX_ERRORS_AMOUNT) return;
		errors.put(errorsCounter, data);
		errorsCounter ++;
		Log.d(TAG, data.msg + (data.err == null ? "" : " Error" + data.err.getMessage()));
	}
	
	public static void clear() {
		errors.clear();
		errorsCounter = 0;
	}	
	
}
