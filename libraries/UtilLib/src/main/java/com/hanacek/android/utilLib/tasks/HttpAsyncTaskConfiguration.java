package com.hanacek.android.utilLib.tasks;

import java.util.HashMap;
import java.util.Map;

import com.hanacek.android.utilLib.cache.Cache;

/**
 * Builder
 */
public class HttpAsyncTaskConfiguration {

    protected long cachePeriod = 180000;
    protected String jsonEncoding = "UTF-8";
    protected int connectionTimeout = 7000;
    protected int readTimeout = 15000;
    protected boolean useHeaderExpiresAt = true;
    protected Cache cache;
    protected Map<String, String> requestProperty = new HashMap<String, String>();

    protected HttpAsyncTaskConfiguration() {

    }

    public static class Builder {

        private HttpAsyncTaskConfiguration asyncTaskConfiguration;

        public Builder() {
            this.asyncTaskConfiguration = new HttpAsyncTaskConfiguration();
        }

        public Builder setCachePeriod(long val) {
            this.asyncTaskConfiguration.cachePeriod = val;
            return this;
        }

        public Builder setJsonEncoding(String val) {
            this.asyncTaskConfiguration.jsonEncoding = val;
            return this;
        }

        public Builder setConnectionTimeout(int val) {
            this.asyncTaskConfiguration.connectionTimeout = val;
            return this;
        }

        public Builder setReadTimeout(int val) {
            this.asyncTaskConfiguration.readTimeout = val;
            return this;
        }

        public Builder setCache(Cache cache) {
            this.asyncTaskConfiguration.cache = cache;
            return this;
        }

        public Builder setUseHeaderExpiresAt(boolean val) {
            this.asyncTaskConfiguration.useHeaderExpiresAt = val;
            return this;
        }

        public Builder addRequestProperty(String key, String value) {
            this.asyncTaskConfiguration.requestProperty.put(key, value);
            return this;
        }

        public HttpAsyncTaskConfiguration build() {
            return this.asyncTaskConfiguration;
        }
    }
}
