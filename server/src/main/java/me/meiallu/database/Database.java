package me.meiallu.database;

import java.util.Set;

public interface Database {

    void rename(String oldKey, String newKey);

    Set<String> keys();

    long dbSize();

    Object getDelObject(String key);

    Object getSetObject(String key, Object object);

    void insert(String key, Object object);

    void set(String key, Object object);

    String get(String key);

    Object getObject(String key);

    void del(String key);

    boolean exists(String... keys);

    boolean hexists(String key, String... hashKeys);

    void hset(String key, String hashKey, String value);

    String hget(String key, String hashKey);

    void hdel(String key, String hashkey);

    void sadd(String key, String value);

    void srem(String key, String value);

    Set<String> smembers(String key);

    boolean scontains(String key, String value);

    void delete();
}
