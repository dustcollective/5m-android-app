package com.hanacek.android.utilLib.tasks;

public interface AbstractAsyncTaskInterface {
    public void showProgressBar();
    public void hideProgressBar();
    public boolean isUnbound();
}