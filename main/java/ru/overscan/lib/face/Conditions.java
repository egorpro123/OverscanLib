package ru.overscan.lib.face;

import java.util.HashMap;
import java.util.Set;

import android.widget.Button;
import ru.overscan.lib.sys.ErrorCollector;

public class Conditions {
	public static final int DISABLED = 1;
	public static final int UNCHECKED = 2;
	public static final int REFUSED = 3;
	public static final int ACCEPTED = 4;

//	public static final int ENABLED = 1;
//	public static final int CHECKED = 2;
//	public static final int ACCEPTED = 4;
	
	HashMap<String, Integer> store;
	Button okButton;
	Boolean prevOkState = null;
	
	public Conditions(){
		store = new HashMap<String, Integer>();
	}
	
//	private class Condition {
////		static final int NONE = 0;
//		
//		HashSet<int>
//		
////		boolean checked;
////		boolean unaccepted;		
////		boolean enabled;
//		
//		Condition(int cond) {
//			this.checked = checked;
//			this.unaccepted = unaccepted;
//			this.enabled = enabled;
//		}
//
//		Condition(boolean enabled, boolean unaccepted) {
//			this.checked = checked;
//			this.unaccepted = unaccepted;
//			this.enabled = enabled;
//		}
//
//		Condition(boolean enabled, boolean unaccepted) {
//			this.checked = checked;
//			this.unaccepted = unaccepted;
//			this.enabled = enabled;
//		}
//	}

	public void addCondition(String name) {
		addCondition(name, DISABLED);
	}
	
	public void addCondition(String name, int condition) {		
		store.put(name, condition);
	}

	// add button after all addConditon
	public void addOkButton(Button b) {		
		okButton = b;
		checkTotalOkState(isOk());
	}
	
	public boolean isOk() {
		Set<String> keys = store.keySet();
		for (String key: keys) {
			if (!isOkCondition(store.get(key))) return false;
		}
		return true;
	}
	
	private boolean exist(String name){
		if (store.containsKey(name)) return true;
		else {
			ErrorCollector.add("Отсутствует условие - " + name);
			return false;
		}
	}

	public boolean isOk(String name) {
		return isOkCondition(store.get(name));
	}
	
	private boolean isOkCondition(int state) {
		return state == DISABLED || state == ACCEPTED;
	}
	
	private void setState(String name, int state){
		if (exist(name)) {
			store.put(name, state);
			checkTotalOkState(state == DISABLED || state == ACCEPTED);
		}
	} 
	
	private void checkTotalOkState(boolean ok) {
		if (prevOkState == null || prevOkState != ok) {
			prevOkState = ok;
			reflectInterface(ok);
		}		
	}
	
	public void reflectInterface(boolean ok){
		if (okButton != null) {
			okButton.setEnabled(ok);
		}
	}

	public void enable(String name) {
		uncheck(name);
	}

	public void disable(String name) {
		setState(name, DISABLED);
//		if (exist(name)) store.put(name, DISABLED);
	}
	
	public void uncheck(String name) {
		setState(name, UNCHECKED);
//		if (exist(name)) store.put(name, UNCHECKED);
	}

	public void accept(String name) {
		setState(name, ACCEPTED);
//		if (exist(name)) store.put(name, ACCEPTED);
	}
	
	public void refuse(String name) {
		setState(name, REFUSED);
//		if (exist(name)) store.put(name, REFUSED);
	}
		
}


//private boolean isOkCondition(int state) {
//	if ((state & ENABLED) != 0 && 
//			((state & CHECKED) == 0 || (state & ACCEPTED) == 0)) return false;
//	else return true;
//}
//
//public void enable(String name) {
//	set(name, ENABLED);
//}
//
//public void disable(String name) {
//	unset(name, ENABLED);
//}
//
//public void uncheck(String name) {
//	set(name, ENABLED);
//	unset(name, CHECKED);
//}
//
//public void accept(String name) {
//	set(name, ENABLED | CHECKED | ACCEPTED);
//}
//
//public void unaccept(String name) {
//	set(name, ENABLED | CHECKED);
//	unset(name, ACCEPTED);
//}
//
//private void set(String name, int cond) {
//	store.put(name, store.get(name) | cond);
//}
//
//private void unset(String name, int cond) {
//	store.put(name, store.get(name) & ~cond);
//}
//
