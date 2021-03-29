package ru.overscan.lib.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class UniversalContentProvider extends ContentProvider {
	private static final String TAG = "CommoditiesProvider";
	
	UriMatcher uriMatcher;
	ProviderManager providerManager;	
	ProviderContent providerContent;
	
	public UniversalContentProvider(ProviderContent c) {
		providerContent = c;
	}

	@Override
	public boolean onCreate() {
        providerManager = new ProviderManager();
        
        providerManager.setProviderContent(providerContent);
//        providerManager.setProviderContent(
//        		new CommoditiesProviderContent());

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        providerManager.makeAllUri(uriMatcher);
		//
		return false;
	}
	
	private void checkUriMatch(int m, Uri uri) {
		if (m <= 0 || m > providerManager.uriedResourceCounter)
			throw new IllegalArgumentException("Unknown URI " + uri);		
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int m = uriMatcher.match(uri);
		checkUriMatch(m, uri);
		Cursor c = providerManager.getResourceQueryCursor(m,
				new ProviderManager.QueryParam(uri, projection, 
						selection, selectionArgs, sortOrder));
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		int m = uriMatcher.match(uri);
		checkUriMatch(m, uri);
		return providerManager.getResourceContentType(m);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) throws IllegalArgumentException {
		int m = uriMatcher.match(uri);
		checkUriMatch(m, uri);
		Uri u = providerManager.resourceInsert(m, uri, values);
		if (u != null) getContext().getContentResolver().notifyChange(u, null);
		return u;
	}
	
	private int updateOrDelete(Uri uri, boolean doUpdate,
			ContentValues values, String selection, String[] selectionArgs) {
		int m = uriMatcher.match(uri);
		int cnt;
		checkUriMatch(m, uri);
		if (doUpdate)
		  cnt = providerManager.resourceUpdate(m, values,
				new ProviderManager.QueryParam(uri, null, selection, selectionArgs, null));
		else cnt = providerManager.resourceDelete(m, 
				new ProviderManager.QueryParam(uri, null, selection, selectionArgs, null));
		getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return updateOrDelete(uri, false, null, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return updateOrDelete(uri, true, values, selection, selectionArgs);
	}

}
