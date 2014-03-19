package com.m5.android.avicola.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.hanacek.android.utilLib.util.Log;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;

import java.util.ArrayList;

public class DbProvider extends ContentProvider {

	private String authority;

	public static final int CODE_FAVORITES = 1;

	/**
	 * Uri matcher to decode incoming URIs.
	 */
    protected UriMatcher mUriMatcher;
	
	private DatabaseHelper databaseHelper;
	
	public DbProvider() {}

    protected void init() {
        authority = AppContext.modConfig().getString(AbstractModConfig.Strings.DATABASE_AUTHORITY);

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(authority, FavoriteTable.TABLE_NAME, CODE_FAVORITES);
    }
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int count;

        switch (mUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                count = db.delete(FavoriteTable.TABLE_NAME, where, whereArgs);
                Log.debug("DB - removed rows from favorites table: " + count);
                break;
            default:
                throw new IllegalArgumentException("DB - Unknown URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();

		long rowId = 0;
		String tableName = "";
		Uri responseUri = null;
		switch (mUriMatcher.match(uri)) {
		case CODE_FAVORITES:
		    tableName = FavoriteTable.TABLE_NAME;
		    responseUri = FavoriteTable.CONTENT_URI_DEFAULT;
		    break;
        default:
            throw new SQLException("DB - Failed to insert row, uri not supported " + uri);
		}
		
		try {
            rowId = db.insertOrThrow(tableName, null, values);
        }
        catch (SQLiteConstraintException e) {
            throw e;
        }

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(responseUri, rowId);
            Log.debug("DB - row inserted, response uri"+ noteUri);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        
        throw new SQLException("DB - Failed to insert row into, rowId invalid");
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Cursor c = null;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try {
	        switch (mUriMatcher.match(uri)) {
	            case CODE_FAVORITES:
	            	qb.setTables(FavoriteTable.TABLE_NAME);
	                break;
	            default:
	                throw new IllegalArgumentException("DB - Unknown URI " + uri);
	        }
	
	    	c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	        
	    	
	    	
	        //testing
	        //c.moveToFirst();
	        //u.u(c.getInt(c.getColumnIndex("w_expires_at_ts")));  
	        
	        c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		catch (Exception e) {
			Log.error(e);
			if (db.inTransaction()) {
        		db.endTransaction();
        	}
		}
		
        return c;
	}
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) {
	    
	    ContentProviderResult[] contentProviderResults = null;
	    
	    SQLiteDatabase db = databaseHelper.getWritableDatabase();
	    db.beginTransaction();
	    try {
	        contentProviderResults = super.applyBatch(operations);
	        db.setTransactionSuccessful();
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
	    } finally {
	        db.endTransaction();
	    }
	    
	    return contentProviderResults;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int count = 0;

//        switch (mUriMatcher.match(uri)) {
//            default:
//                throw new IllegalArgumentException("Unknown URI " + uri);
//        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;

	}

}
