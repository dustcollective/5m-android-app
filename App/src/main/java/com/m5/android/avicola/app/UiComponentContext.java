package com.m5.android.avicola.app;

import android.app.Activity;
import android.content.ContextWrapper;
import android.view.View;

import com.hanacek.android.utilLib.tasks.AbstractAsyncTaskInterface;

/**
 * init onCreate activity/fragment
 */
public class UiComponentContext extends ContextWrapper implements AbstractAsyncTaskInterface {

	protected Activity context;
	private boolean isUnbound = false;
	private View progressBar;
	
	public UiComponentContext(Activity context) {
	    super(context);
		init(context);
	}
	
	private void init(Activity context) {
	    this.context = context;
	}
	
	public void setProgressBar(View progressBar) {
	    this.progressBar = progressBar;
	}
	
	public void showProgressBar() {
	    if (this.progressBar != null) {
	        this.progressBar.setVisibility(View.VISIBLE);
	    }
	}
	
	public void hideProgressBar() {
	    if (this.progressBar != null) {
            this.progressBar.setVisibility(View.GONE);
        }
	}
    
	public boolean isUnbound() {
	    return this.isUnbound;
	}
	
	public void unbind() {
	    this.isUnbound = true;
	}
}
