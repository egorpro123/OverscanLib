package ru.overscan.lib.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSelectionPattern {
	ArrayList<String> patterns;
	boolean caseSensitive;
	
	public StringSelectionPattern(String s){
		this(s, false);
	}

	
	public StringSelectionPattern(String str, boolean caseSensitive){
		this.caseSensitive = caseSensitive;
		patterns = new ArrayList<String>();
		if (!caseSensitive) str = str.toLowerCase();
		
//		ArrayList<String> frags = new ArrayList<String>();
				
		Pattern p = Pattern.compile("\"(.*?)\"");
		Matcher m = p.matcher(str);
		boolean b = false;
		while (m.find()) {
			patterns.add(m.group(1));
			b = true;
		}
		if (b) str = str.replaceAll("\"(.*?)\"", "");
		String [] a = str.split("\\s+");
		for (int i = 0; i < a.length; i++) {
			patterns.add(a[i]);
		}
//		String str = "";
//		for (String s: frags) str = str + "(?=.*" + s + ")";
//		if (!caseSensitive) str = "(?i)" + str;
//		pattern = Pattern.compile(str);
		
//		Pattern.compile(regularExpression).matcher(input).matches()
	}
	
	public boolean fit(String str) {
		if (!caseSensitive) str = str.toLowerCase();
		for (String s: patterns)
			if (str.indexOf(s) < 0) return false;
			else str = str.replaceFirst(s, "");
		return true;
//		boolean b = pattern.matcher(s).matches();
//		return pattern.matcher(s).matches();
	}

//	public boolean fit(String str) {
//		if (!caseSensitive) str = str.toLowerCase();
//		for (String s: patterns)
//			if (str.indexOf(s) < 0) return false;
//		return true;
////		boolean b = pattern.matcher(s).matches();
////		return pattern.matcher(s).matches();
//	}
	
}
