package com.hanacek.android.utilLib.calendar;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import com.hanacek.android.utilLib.util.Log;

import java.util.Calendar;

public class CalendarUtils {

    public static Bundle[] getCalendar(Context c) {
        String projection[] = {CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};
        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"), projection, null, null, null);

        if (managedCursor.getCount() > 0) {
            Bundle[] calendars = new Bundle[managedCursor.getCount()];
            int i = 0;
            while (managedCursor.moveToNext()) {
                calendars[i] = new Bundle();
                calendars[i].putString(CalendarContract.Calendars._ID, managedCursor.getString(managedCursor.getColumnIndex(projection[0])));
                calendars[i].putString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, managedCursor.getString(managedCursor.getColumnIndex(projection[1])));

                Log.debug("Calendar " + i + ", id: " + calendars[i].getString(CalendarContract.Calendars._ID) +
                        ", display name: " + calendars[i].getString(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));

                i++;
            }
            managedCursor.close();

            return calendars;
        }

        return null;
    }

    @TargetApi(14)
    public static long addEvent(Context context, Calendar beginTime, Calendar endTime, String title, String description, String timezone, String location) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return -1;
        }

        long calID = 1;
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        return Long.parseLong(uri.getLastPathSegment());
    }

    @TargetApi(14)
    public static void removeEvent(Context context, long eventId) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return;
        }

        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
        Log.debug("Calendar rows removed: " + rows);
    }
}
