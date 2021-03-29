package ru.overscan.lib.db;

abstract public class ProviderContent {
	public String AUTHORITY = null;
	public String ORG_NAME = null;
	
	public ProviderContent(String authority, String org_name){
		AUTHORITY = authority;
		ORG_NAME = org_name;
	}

	abstract public void createContent(ProviderManager pm);
}
