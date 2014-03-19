package com.hanacek.android.utilLib.cache;

/**
 * Builder
 */
public class CacheConfiguration {

    /**
     * Each item saved to cache has its expiration timestamp, cacheValidity is used to know after how much time the expired cache should be removed
     * So expired cache still stays until time exceeds the expirationTs + cacheValidity
     *
     * Day in default
     */
    protected long cacheValidity = 86400000;

    protected CacheConfiguration() {

    }

    public static class Builder {

        private CacheConfiguration cacheConfiguration;

        public Builder() {
            this.cacheConfiguration = new CacheConfiguration();
        }

        public Builder setCacheValidity(long val) {
            this.cacheConfiguration.cacheValidity = val;
            return this;
        }

        public CacheConfiguration build() {
            return this.cacheConfiguration;
        }
    }

}
