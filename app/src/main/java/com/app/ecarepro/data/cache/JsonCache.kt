package com.app.ecarepro.data.cache

import android.util.LruCache
import com.google.gson.Gson

class JsonCache(maxSize: Int = 50) {
    private val cache: LruCache<String, String> = LruCache(maxSize)
    private val gson = Gson()

    // Put an entry in the cache

    // Check if cache is available for the given key
    fun isCacheAvailable(key: String): Boolean {
        return cache.get(key) != null
    }


    // Store JSON object in the cache
    fun <T> store(key: String, value: T) {
        val jsonString = gson.toJson(value)
        cache.put(key, jsonString)
    }

    // Retrieve JSON object from the cache
    fun <T> retrieve(key: String, type: Class<T>): T? {
        val jsonString = cache.get(key) ?: return null
        return gson.fromJson(jsonString, type)
    }

    // Delete an entry
    fun delete(key: String) {
        cache.remove(key)
    }

    // Clear the cache
    fun clear() {
        cache.evictAll()
    }
}
