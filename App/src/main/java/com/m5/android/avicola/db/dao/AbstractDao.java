package com.m5.android.avicola.db.dao;

import android.content.Context;

public class AbstractDao {

    private Context context;

    public AbstractDao(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
