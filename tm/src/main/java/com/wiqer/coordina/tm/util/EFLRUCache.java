package com.wiqer.coordina.tm.util;

import java.util.LinkedHashMap;
import java.util.Map;


/**

 * Created by liuzhao on 14-5-15.

 */

public class EFLRUCache<K,V> extends LinkedHashMap {

    private final int MAX_CACHE_SIZE;

    public EFLRUCache(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_CACHE_SIZE;
    }

}