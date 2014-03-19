package com.m5.android.avicola.db.dao;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;

import com.m5.android.avicola.db.CursorExecutor;
import com.m5.android.avicola.db.FavoriteTable;
import com.m5.android.avicola.model.Content;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDao extends AbstractDao {

    public FavoriteDao(Context context) {
        super(context);
    }

    public Long insert(final Content content) {
        return ContentUris.parseId(getContext().getContentResolver().insert(FavoriteTable.CONTENT_URI_DEFAULT, FavoriteTable.insertRow(content)));
    }

    public Content find(final String id) {
        return new CursorExecutor<Content>() {
            @Override
            public Cursor createCursor() {
                return getContext().getContentResolver().query(FavoriteTable.CONTENT_URI_DEFAULT, FavoriteTable.SELECT_ALL_PROJECTION,
                        FavoriteTable.CN_ID + " = ?", new String[]{id + ""}, null);
            }

            @Override
            public Content doExecute(Cursor c) {
                if (c.moveToNext()) {
                    return FavoriteTable.fromCursor(c);
                }
                return null;
            }
        }.execute();
    }

    public void delete(final String id) {
        getContext().getContentResolver().delete(FavoriteTable.CONTENT_URI_DEFAULT, FavoriteTable.CN_ID + " = ?", new String[]{id});
    }

    public List<Content> getAll() {
        return new CursorExecutor<List<Content>>() {
            @Override
            public Cursor createCursor() {
                return getContext().getContentResolver().query(
                        FavoriteTable.CONTENT_URI_DEFAULT,
                        FavoriteTable.SELECT_ALL_PROJECTION, null, null,
                        FavoriteTable.DEFAULT_SORT_ORDER);
            }

            @Override
            public List<Content> doExecute(Cursor c) {
                List<Content> data = new ArrayList<Content>();
                while (c.moveToNext()) {
                    Content content = FavoriteTable.fromCursor(c);
                    data.add(content);
                }
                return data;
            }
        }.execute();
    }
}
