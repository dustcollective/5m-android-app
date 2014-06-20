package com.m5.android.avicola.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;
import com.m5.android.avicola.model.Contact;
import com.m5.android.avicola.model.Content;

public class FavoriteTable {

    public static final String TABLE_NAME = "favorites";

    private static final Uri CONTENT_URI = Uri.parse("content://" + AppContext.modConfig().getString(AbstractModConfig.Strings.DATABASE_AUTHORITY));

    public static final Uri CONTENT_URI_DEFAULT = Uri.withAppendedPath(CONTENT_URI, TABLE_NAME);

    public static final String CN_LOCAL_UID = "uid";
    public static final String CN_ID = "ci";
    public static final String CN_NEWS_ID = "cn";
    public static final String CN_TYPE = "ctype";
    public static final String CN_HEADLINE = "ch";
    public static final String CN_LINK = "clink";
    public static final String CN_SNIPPET = "cs";
    public static final String CN_BODY = "cb";
    public static final String CN_COUNTRY = "cc";
    public static final String CN_TERRITORY = "cte";
    public static final String CN_THUMBNAIL = "ct";
    public static final String CN_LOCATION = "cl";
    public static final String CN_EVENT_ID = "cei";
    public static final String CN_DATE = "cd";
    public static final String CN_START = "cstart";
    public static final String CN_END = "ce";
    public static final String CN_CONTACT_EMAIL = "cce";
    public static final String CN_CONTACT_NAME = "ccn";
    public static final String CN_CALENDAR_ID = "calId";

    public static final String[] SELECT_ALL_PROJECTION = new String[]{
            CN_BODY, CN_CONTACT_EMAIL, CN_CONTACT_NAME, CN_COUNTRY, CN_DATE, CN_END, CN_EVENT_ID, CN_HEADLINE, CN_ID, CN_LINK, CN_LOCATION, CN_NEWS_ID,
            CN_SNIPPET, CN_START, CN_TERRITORY, CN_THUMBNAIL, CN_TYPE, CN_LOCAL_UID, CN_CALENDAR_ID
    };

    public static final String DEFAULT_SORT_ORDER = CN_ID + " ASC";

    public static ContentValues insertRow(Content item) {
        ContentValues values = new ContentValues();
        values.put(CN_BODY, item.body);

        if (item.contact != null) {
            values.put(CN_CONTACT_EMAIL, item.contact.email);
            values.put(CN_CONTACT_NAME, item.contact.name);
        }

        values.put(CN_COUNTRY, item.country);
        values.put(CN_DATE, item.date);
        values.put(CN_END, item.end);
        values.put(CN_EVENT_ID, item.eventId);
        values.put(CN_HEADLINE, item.headline);
        values.put(CN_ID, item.id);
        values.put(CN_LINK, item.link);
        values.put(CN_LOCATION, item.location);
        values.put(CN_NEWS_ID, item.newsId);
        values.put(CN_SNIPPET, item.snippet);
        values.put(CN_START, item.start);
        values.put(CN_CALENDAR_ID, item.calendarId);

        if (item.territory != null) {
            values.put(CN_TERRITORY, item.territory.name());
        }

        values.put(CN_THUMBNAIL, item.thumbnail);
        values.put(CN_TYPE, item.type.name());

        return values;
    }

    public static Content fromCursor(Cursor c) {
        Content content = new Content();
        content.contact = new Contact();

        if (c.getColumnIndex(FavoriteTable.CN_LOCAL_UID) != -1) {
            content.localUid = c.getLong(c.getColumnIndex(FavoriteTable.CN_LOCAL_UID));
        }

        if (c.getColumnIndex(FavoriteTable.CN_CALENDAR_ID) != -1) {
            content.calendarId = c.getLong(c.getColumnIndex(FavoriteTable.CN_CALENDAR_ID));
        }

        if (c.getColumnIndex(FavoriteTable.CN_TYPE) != -1) {
            content.type = Content.Type.valueOf(c.getString(c.getColumnIndex(FavoriteTable.CN_TYPE)));
        }

        if (c.getColumnIndex(FavoriteTable.CN_THUMBNAIL) != -1) {
            content.thumbnail = c.getString(c.getColumnIndex(FavoriteTable.CN_THUMBNAIL));
        }

        if (c.getColumnIndex(FavoriteTable.CN_TERRITORY) != -1) {
            final String territoryTemp = c.getString(c.getColumnIndex(FavoriteTable.CN_TERRITORY));
            if (territoryTemp != null) {
                content.territory = Content.Territory.valueOf(territoryTemp);
            }
        }

        if (c.getColumnIndex(FavoriteTable.CN_BODY) != -1) {
            content.body = c.getString(c.getColumnIndex(FavoriteTable.CN_BODY));
        }

        if (c.getColumnIndex(FavoriteTable.CN_CONTACT_EMAIL) != -1) {
            content.contact.email = c.getString(c.getColumnIndex(FavoriteTable.CN_CONTACT_EMAIL));
        }

        if (c.getColumnIndex(FavoriteTable.CN_CONTACT_NAME) != -1) {
            content.contact.name = c.getString(c.getColumnIndex(FavoriteTable.CN_CONTACT_NAME));
        }

        if (c.getColumnIndex(FavoriteTable.CN_COUNTRY) != -1) {
            content.country = c.getString(c.getColumnIndex(FavoriteTable.CN_COUNTRY));
        }

        if (c.getColumnIndex(FavoriteTable.CN_DATE) != -1) {
            content.date = c.getLong(c.getColumnIndex(FavoriteTable.CN_DATE));
        }

        if (c.getColumnIndex(FavoriteTable.CN_END) != -1) {
            content.end = c.getLong(c.getColumnIndex(FavoriteTable.CN_END));
        }

        if (c.getColumnIndex(FavoriteTable.CN_EVENT_ID) != -1) {
            content.eventId = c.getString(c.getColumnIndex(FavoriteTable.CN_EVENT_ID));
        }

        if (c.getColumnIndex(FavoriteTable.CN_HEADLINE) != -1) {
            content.headline = c.getString(c.getColumnIndex(FavoriteTable.CN_HEADLINE));
        }

        if (c.getColumnIndex(FavoriteTable.CN_ID) != -1) {
            content.id = c.getString(c.getColumnIndex(FavoriteTable.CN_ID));
        }

        if (c.getColumnIndex(FavoriteTable.CN_LINK) != -1) {
            content.link = c.getString(c.getColumnIndex(FavoriteTable.CN_LINK));
        }

        if (c.getColumnIndex(FavoriteTable.CN_LOCATION) != -1) {
            content.location = c.getString(c.getColumnIndex(FavoriteTable.CN_LOCATION));
        }

        if (c.getColumnIndex(FavoriteTable.CN_NEWS_ID) != -1) {
            content.newsId = c.getString(c.getColumnIndex(FavoriteTable.CN_NEWS_ID));
        }

        if (c.getColumnIndex(FavoriteTable.CN_SNIPPET) != -1) {
            content.snippet = c.getString(c.getColumnIndex(FavoriteTable.CN_SNIPPET));
        }

        if (c.getColumnIndex(FavoriteTable.CN_START) != -1) {
            content.start = c.getLong(c.getColumnIndex(FavoriteTable.CN_START));
        }

        return content;
    }
}
