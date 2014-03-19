package com.hanacek.android.utilLib.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

abstract public class AbstractAdapter<LIST_ITEM> extends BaseAdapter {
    
    protected List<? extends LIST_ITEM> items;
    protected Context context;
    
    abstract protected long extendedGetItemId(int position);
    
    public AbstractAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<? extends LIST_ITEM> items) {
        this.items = items;
        notifyDataSetChanged();
    }
    
    public void clearItems() {
        this.items = new ArrayList<LIST_ITEM>();
        notifyDataSetChanged();
    }
    
    public boolean clearItem(LIST_ITEM item) {
        if (items == null) {
            return false;
        }
        
        boolean res = items.remove(item);
        if (res) {
            notifyDataSetChanged();
        }
        
        return res;
    }
    
    public List<? extends LIST_ITEM> getItems() {
        return this.items;
    }
    
    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size() ;
    }

    @Override
    public LIST_ITEM getItem(int position) {
        return (items == null || items.size() <= position) ? null : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (items == null || items.size() <= position) ? -1 : extendedGetItemId(position);
    }
}
