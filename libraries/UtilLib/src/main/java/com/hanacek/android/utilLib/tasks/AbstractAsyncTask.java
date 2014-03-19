package com.hanacek.android.utilLib.tasks;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.concurrent.Executor;

import com.hanacek.android.utilLib.util.Log;

/**
 * see file://doc/AsyncTask.png
 */
abstract public class AbstractAsyncTask<RESULT> extends AsyncTask<Object, Void, RESULT> {
    
    protected AbstractAsyncTaskInterface mInterface;
    private boolean showProgressBar;
    private boolean isNullResponseSuccess;
    private ThreadPerTaskExecutor threadPerTaskExecutor;
    
    protected FailHolder failed;
    protected boolean taskDone;
    
    protected class FailHolder {
        final public Throwable throwable;
        final public byte[] response;
        final public boolean isFailedConnection;
        final public int responseCode;

        public FailHolder(Throwable throwable, byte[] response, boolean isFailedConnection, int responseCode) {
            this.throwable = throwable;
            this.response = response;
            this.isFailedConnection = isFailedConnection;
            this.responseCode = responseCode;
        }

        public FailHolder(Throwable throwable, byte[] response, boolean isFailedConnection) {
            this.throwable = throwable;
            this.response = response;
            this.isFailedConnection = isFailedConnection;
            this.responseCode = 0;
        }

        public FailHolder(Throwable throwable) {
            this.throwable = throwable;
            this.response = null;
            this.isFailedConnection = false;
            this.responseCode = 0;
        }
    }

    /**
     * if progress bar should be shown onPreExecute and hidden onPostExecute
     */
    public AbstractAsyncTask<RESULT> setShowProgressBar(AbstractAsyncTaskInterface abstractAsyncTaskInterface) {
        this.mInterface = abstractAsyncTaskInterface;
        this.showProgressBar = true;
        return this;
    }

    /**
     * default behavior is that null response is considered as error and is handled in onFailed, in case this is set
     * to true, it is handled in onSuccess
     */
    public AbstractAsyncTask<RESULT> setIsNullResponseSuccess() {
        this.isNullResponseSuccess = true;
        return this;
    }
    
    public void extendedOnPreExecute(){}
    
    @Override
    final protected void onPreExecute() {
        if (taskDone) return;

        if (showProgressBar && mInterface != null && !mInterface.isUnbound()) {
            mInterface.showProgressBar();
        }
        
        extendedOnPreExecute();
        
        super.onPreExecute();
    }

    @Override
    final protected RESULT doInBackground(Object... params) {
        if (taskDone) return null;
        return extendedDoInBackground();
    }

    abstract protected RESULT extendedDoInBackground();

    @Override
    final protected void onPostExecute(RESULT result) {
        extendedOnPostExecuteStarts(result);
        
        //no need to deliver, context is gone
        if (mInterface != null && mInterface.isUnbound()) {
            return;
        }

        if ((result == null && !isNullResponseSuccess) || failed != null) {
            onFailed(failed, result);
            if (failed != null && failed.throwable != null) {
                Log.error(failed.throwable);
            }
        }
        else {
            onSuccess(result);
        }
        
        if (mInterface != null && showProgressBar) {
            mInterface.hideProgressBar();
        }
        
        extendedOnPostExecuteEnds(result);
    }

    public void extendedOnPostExecuteStarts(RESULT result){}
    public void extendedOnPostExecuteEnds(RESULT result){}
    public void onSuccess(RESULT result) {}
    public void onFailed(FailHolder failHolder, RESULT result) {}

    private class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }
    
    /**
     * do not block the one thread that all async tasks are using on some android versions
     */
    public void executeOnSeparateThread() {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            new SeparateThread(new Handler()).start();
        }
        else {
            if (threadPerTaskExecutor == null) {
                threadPerTaskExecutor = new ThreadPerTaskExecutor();
            }
            
            executeOnExecutor(threadPerTaskExecutor);
        }
    }
    
    /**
     * if we need to run the async task logic be we do not want to run it outside of caller thread
     * 
     * this is just a fake run that doesnt go properly through the complete async task lifecycle
     */
    public void executeOnCurrentThread() {
        onPreExecute();
        final RESULT res = doInBackground();
        onPostExecute(res);
    }
    
    /**
     * because before android sdk 11 executeOnExecutor() doesnt exist this try to handle the async task
     * in a different thread to not block the main async task thread
     */
    private class SeparateThread extends Thread {
        
        private Handler handler;
        
        public SeparateThread(Handler handler) {
            this.handler = handler;
        }
        
        @Override
        public void run() {
            this.handler.post(new Runnable() { 
                @Override
                public void run() {
                    onPreExecute();
                }
            });
            final RESULT res = doInBackground();
            this.handler.post(new Runnable() { 
                @Override
                public void run() {
                    onPostExecute(res);
                }
            });
        }
    }
}
