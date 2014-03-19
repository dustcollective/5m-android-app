package com.m5.android.avicola.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	DatabaseHelper(Context context) {
    	super(context, AppContext.modConfig().getString(AbstractModConfig.Strings.DATABASE_NAME), null,
                AppContext.modConfig().getInt(AbstractModConfig.Integers.DATABASE_VERSION));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	String sql = "CREATE TABLE " + FavoriteTable.TABLE_NAME + " (" +
    	        FavoriteTable.CN_LOCAL_UID + " INTEGER PRIMARY KEY, " +
    			FavoriteTable.CN_CONTACT_NAME + " TEXT, " +
                FavoriteTable.CN_CONTACT_EMAIL + " TEXT, " +
                FavoriteTable.CN_COUNTRY + " TEXT, " +
                FavoriteTable.CN_BODY + " TEXT, " +
                FavoriteTable.CN_TERRITORY + " TEXT, " +
                FavoriteTable.CN_THUMBNAIL + " TEXT, " +
                FavoriteTable.CN_EVENT_ID + " TEXT, " +
                FavoriteTable.CN_HEADLINE + " TEXT, " +
                FavoriteTable.CN_ID + " TEXT, " +
                FavoriteTable.CN_LINK + " TEXT, " +
                FavoriteTable.CN_LOCATION + " TEXT, " +
                FavoriteTable.CN_NEWS_ID + " TEXT, " +
                FavoriteTable.CN_SNIPPET + " TEXT, " +
                FavoriteTable.CN_TYPE + " TEXT, " +
    			FavoriteTable.CN_START + " INTEGER, " +
                FavoriteTable.CN_END + " INTEGER, " +
                FavoriteTable.CN_DATE + " INTEGER" +
				")";

    	db.execSQL(sql);
    	

    	sql = "CREATE INDEX IF NOT EXISTS favorites_id_index ON " + FavoriteTable.TABLE_NAME + " (" + FavoriteTable.CN_ID + ")";
    	db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	switch(oldVersion) {
    		case 1:
    	}
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

   
