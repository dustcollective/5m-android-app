package com.m5.android.avicola.db;

import android.database.Cursor;

public abstract class CursorExecutor<T> {

    public T execute() {
        Cursor c = null;
        try {
            c = createCursor();
            return doExecute(c);
        } finally {
            if (c != null) {
                c.close();
            }
        }

    }

    public abstract Cursor createCursor();
    public abstract T doExecute(Cursor c);

}
