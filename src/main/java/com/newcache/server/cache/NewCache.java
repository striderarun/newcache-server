package com.newcache.server.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.newcache.server.command.object.CommandObject;

import java.util.concurrent.ConcurrentMap;

/**
 * A Singleton wrapper around Caffeine caching library.
 * https://github.com/ben-manes/caffeine
 *
 * Initialized using the Initialization-on-demand Holder pattern for concurrent access.
 * https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
 *
 * Cache size is set to 10.
 */
public class NewCache {

    private Cache<String, CommandObject> cache;

    private NewCache(int cacheSize) {
        cache = Caffeine.newBuilder().maximumSize(cacheSize).build();
    }

    private static class CacheHolder {
        static final NewCache INSTANCE = new NewCache(10);
    }

    public static NewCache getInstance() {
        return CacheHolder.INSTANCE;
    }

    public CommandObject get(String key) {
        ConcurrentMap<String, CommandObject> map = cache.asMap();
        return map.get(key);
    }

    public void put(String key, CommandObject commandObject) {
        cache.put(key, commandObject);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }



}
