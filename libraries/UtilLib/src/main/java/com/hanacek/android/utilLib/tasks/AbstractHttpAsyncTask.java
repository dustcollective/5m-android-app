package com.hanacek.android.utilLib.tasks;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.hanacek.android.utilLib.util.Log;
import com.hanacek.android.utilLib.util.Util;

/**
 * see file://doc/AsyncTask.png
 */
abstract public class AbstractHttpAsyncTask<RESULT> extends AbstractAsyncTask<RESULT> {

    private String requestMethod = "GET";
    private List<NameValuePair> postParams;
    private Map<String, List<String>> responseHeaders;
    protected String url;
    protected HttpAsyncTaskConfiguration httpAsyncTaskConfiguration;
    protected long expiresAt;

    public AbstractHttpAsyncTask() {
        this.httpAsyncTaskConfiguration = new HttpAsyncTaskConfiguration.Builder().build();
    }

    public AbstractHttpAsyncTask(String url) {
        this.url = url;
        this.httpAsyncTaskConfiguration = new HttpAsyncTaskConfiguration.Builder().build();
    }

    public AbstractHttpAsyncTask(String url, HttpAsyncTaskConfiguration httpAsyncTaskConfiguration) {
        this.url = url;
        this.httpAsyncTaskConfiguration = httpAsyncTaskConfiguration;
    }

    public AbstractHttpAsyncTask setUrl(String url) {
        this.url = url;
        return this;
    }
    
    public AbstractHttpAsyncTask<RESULT> setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }
    
    public AbstractHttpAsyncTask<RESULT> setPostRequestMethod(List<NameValuePair> postParams) {
        this.postParams = postParams;
        this.requestMethod = "POST";
        return this;
    }
    
    protected Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }
    
    protected void extendedPreDoInBackground(String url) {}
    protected abstract RESULT extendedPostDoInBackground(byte[] result);
    protected void extendedPreConnect(HttpURLConnection httpUrlConnection) {}
    protected void extendedPostConnect(HttpURLConnection httpUrlConnection, String url) {}

    /**
     * override this if static expiration of cache is needed (replace the date provided in response headers)
     * 
     * @param expiresAt timestamp in milliseconds
     */
    public AbstractHttpAsyncTask<RESULT> setExpiresAt(long expiresAt){
        this.expiresAt = expiresAt;
        return this;
    }

    public String extendableGetCacheKey() {
        return url;
    }

    @Override
    protected RESULT extendedDoInBackground() {
        byte[] result;
        if (httpAsyncTaskConfiguration.cache != null) {
            result = httpAsyncTaskConfiguration.cache.load(extendableGetCacheKey(), false);
            if (result != null) {
                return extendedPostDoInBackground(result);
            }
        }
        
        extendedPreDoInBackground(url);
        result = doConnection(url);
        
        if (result != null) {
            if (httpAsyncTaskConfiguration.cache != null) {
                if (httpAsyncTaskConfiguration.useHeaderExpiresAt && expiresAt == 0 && responseHeaders != null) {
                    List<String> expires = responseHeaders.get("Expires");
                    if (expires != null && expires.size() > 0) {
                        String expireDate = expires.get(0);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                        try {
                            expiresAt = sdf.parse(expireDate).getTime();
                        }
                        catch (Exception e) {
                            Log.error("Could not parse expire header", e);
                        }
                    }
                }

                if (expiresAt == 0) {
                    expiresAt = System.currentTimeMillis() + httpAsyncTaskConfiguration.cachePeriod;
                }

                saveToCache(extendableGetCacheKey(), result, expiresAt);
            }
            
            return extendedPostDoInBackground(result);
        }
        
        return null;
    }

    protected void saveToCache(String cacheKey, byte[] result, long expiresAt) {
        httpAsyncTaskConfiguration.cache.save(cacheKey, result, expiresAt);
    }
    
    /**
     * if the handling of connection needs to be overwritten
     * 
     * @param httpURLConnection
     * @return true if the handling was overwritten and inputstream from connection was consumed
     *      that will result in null response inside onSuccess
     */
    protected boolean extendedHandleResult(HttpURLConnection httpURLConnection) {
        return false;
    }
    
    protected byte[] getConnectionResponse(HttpURLConnection conn) throws Exception {
        return Util.stream2Bytes(conn.getInputStream());
    }

    private void logHeaders(Map<String, List<String>> headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            Log.debug("headers key : " + entry.getKey());
            for (String val : entry.getValue()) {
                Log.debug("headers value : " + val);
            }
        }
    }

    /**
     * @param url
     * @return byte[]
     */
    protected byte[] doConnection(String url) {
        HttpURLConnection conn = null;
        try {
            Log.debug("connect to: " + url);

            URL urlURL = new URL(url);
            conn = (HttpURLConnection) urlURL.openConnection();
            for (Map.Entry<String, String> entry : httpAsyncTaskConfiguration.requestProperty.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //conn.setRequestProperty("User-Agent", AppContext.getUserAgent());
            //logHeaders(conn.getRequestProperties());

            extendedPreConnect(conn);
            
            conn.setDoInput(true);
            conn.setConnectTimeout(httpAsyncTaskConfiguration.connectionTimeout);
            conn.setReadTimeout(httpAsyncTaskConfiguration.readTimeout);
            conn.setRequestMethod(requestMethod);

            if ("POST".equals(requestMethod) && postParams != null) {
                conn.setDoOutput(true);
                writePostParams(conn);
            }
            else {
                conn.connect();
            }
            extendedPostConnect(conn, url);
            this.responseHeaders = conn.getHeaderFields();

            if ("HEAD".equals(conn.getRequestMethod())) {
                return null;
            }
            else {
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    byte[] bytes = Util.stream2Bytes(conn.getErrorStream());
                    failed = new FailHolder(null, bytes, false, conn.getResponseCode());
                    Log.warn("response code is "+conn.getResponseCode());
                    if (bytes != null) {
                        Log.debug(new String(bytes, "UTF-8"));
                    }
                    return null;
                }
                
                return handleConnectionResponse(conn);
            }
        }
        catch (UnknownHostException e) {
            return handleException(e, true);
        }
        catch (SocketTimeoutException e) {
            return handleException(e, true);
        }
        catch (ConnectException e) {
            return handleException(e, true);
        }
        catch (Exception e) {
            return handleException(e, false);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private byte[] handleConnectionResponse(HttpURLConnection conn) throws Exception {
        if (extendedHandleResult(conn)) {
            return null;
        }

        return getConnectionResponse(conn);
    }
    
    private byte[] handleException(Exception e, boolean isConnectionFail) {
        Log.error(e);
        byte[] result = null;
        if (httpAsyncTaskConfiguration.cache != null) {
            result = httpAsyncTaskConfiguration.cache.load(extendableGetCacheKey(), true);
        }
        failed = new FailHolder(e, result, isConnectionFail);
        return result;
    }
    
    final public void onFailed(FailHolder failHolder, RESULT result) {
        if (failHolder != null && failHolder.isFailedConnection) {
            if (result == null) {
                Log.debug("Connection problem - no content in cache");
                onConnectionProblemWithoutViableContent(failHolder);
            }
            else {
                Log.debug("Connection problem - found content in cache (Fallback)");
                onConnectionProblemWithCacheFallback();
                onSuccess(result);
                return;
            }
        }

        extendedOnFailed(failHolder);
    }

    protected void writePostParams(HttpURLConnection httpUrlConnection) throws Exception {
        OutputStream output = null;
        
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(postParams, httpAsyncTaskConfiguration.jsonEncoding);
            
            output = httpUrlConnection.getOutputStream();    
            urlEncodedFormEntity.writeTo(output);
        } finally {
            if (output != null) {
                try { 
                    output.close(); 
                } catch (IOException e) {
                    Log.error(e);
                }
            }
        }
    }

    /**
     * When there are connection problems and no content was found in cache.
     *
     * @param failHolder
     */
    protected void onConnectionProblemWithoutViableContent(FailHolder failHolder) {}

    /**
     * When there are connection problems but some viable content was found in cache. Might be expired that depends on settings of the async task.
     */
    protected void onConnectionProblemWithCacheFallback() {}

    protected void extendedOnFailed(FailHolder failHolder) {}

}
