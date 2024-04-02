package me.meiallu.cabbadb.database;

import lombok.Getter;
import me.meiallu.cabbadb.Cabba;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class MemoryDatabase implements Database, Serializable {

    private final String name;
    private HashMap<String, Object> values;

    @Override
    public void rename(String oldKey, String newKey) {
        values.put(newKey, values.get(oldKey));
    }

    @Override
    public Set<String> keys() {
        return values.keySet();
    }

    @Override
    public long dbSize() {
        return values.size();
    }

    @Override
    public Object getDelObject(String key) {
        Object object = values.get(key);
        values.remove(key);
        return object;
    }

    @Override
    public Object getSetObject(String key, Object object) {
        Object oldObject = values.get(key);
        values.put(key, object);
        return oldObject;
    }

    @Override
    public void insert(String key, Object object) {
        if (!values.containsKey(key))
            values.put(key, object);
    }

    @Override
    public void set(String key, Object object) {
        values.put(key, object);
    }

    @Override
    public String get(String key) {
        return (String) values.get(key);
    }

    @Override
    public Object getObject(String key) {
        return values.get(key);
    }

    @Override
    public void del(String key) {
        values.remove(key);
    }

    @Override
    public boolean exists(String... keys) {
        for (String key : keys)
            if (!values.containsKey(key))
                return false;

        return true;
    }

    @Override
    public boolean hexists(String key, String... hashKeys) {
        HashMap<String, String> hash = values.get(key) == null || !(values.get(key) instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) values.get(key);

        for (String loopKey : hashKeys)
            if (!hash.containsKey(loopKey))
                return false;

        return true;
    }

    @Override
    public void hset(String key, String hashKey, String value) {
        HashMap<String, String> hash = values.get(key) == null || !(values.get(key) instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) values.get(key);

        hash.put(hashKey, value);
        values.put(key, hash);
    }

    @Override
    public String hget(String key, String hashKey) {
        return values.get(key) == null || !(values.get(key) instanceof HashMap) ?
                null :
                ((HashMap<String, String>) values.get(key)).get(hashKey);
    }

    @Override
    public void hdel(String key, String hashkey) {
        HashMap<String, String> hash = values.get(key) == null || !(values.get(key) instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) values.get(key);

        hash.remove(hashkey);
        values.put(key, hash);
    }

    @Override
    public void sadd(String key, String value) {
        Set<String> set = values.get(key) == null || !(values.get(key) instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) values.get(key);

        set.add(value);
        values.put(key, set);
    }

    @Override
    public void srem(String key, String value) {
        Set<String> set = values.get(key) == null || !(values.get(key) instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) values.get(key);

        set.remove(value);
        values.put(key, set);
    }

    @Override
    public Set<String> smembers(String key) {
        return values.get(key) == null || !(values.get(key) instanceof HashSet) ?
                null :
                (Set<String>) values.get(key);
    }

    @Override
    public boolean scontains(String key, String value) {
        return values.get(key) != null && values.get(key) instanceof HashSet && ((Set<String>) values.get(key)).contains(value);
    }

    @Override
    public void delete() {
        values = null;
        Cabba.getDatabases().remove(this);
    }

    public MemoryDatabase(String name) {
        this.values = new HashMap<>();
        this.name = name;

        Cabba.getDatabases().put(name, this);
    }
}
