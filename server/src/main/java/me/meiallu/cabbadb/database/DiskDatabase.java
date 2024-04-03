package me.meiallu.cabbadb.database;

import me.meiallu.cabbadb.Cabba;
import me.meiallu.cabbadb.util.Util;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
public class DiskDatabase implements Database, Serializable {

    private final String name;

    @Override
    public void rename(String oldKey, String newKey) {
        new File("cabba/" + name + "/" + oldKey).renameTo(new File("cabba/" + name + "/" + newKey));
    }

    @Override
    public Set<String> keys() {
        String[] files = new File("cabba/" + name).list();
        return files == null ? new HashSet<>() : Set.of(files);
    }

    @Override
    public long dbSize() {
        String[] files = new File("cabba/" + name).list();
        return files == null ? 0 : files.length;
    }

    @Override
    public Object getDelObject(String key) {
        Object object = get(key);
        del(key);
        return object;
    }

    @Override
    public Object getSetObject(String key, Object object) {
        Object oldObject = get(key);
        set(key, object);
        return oldObject;
    }

    @Override
    public void insert(String key, Object object) {
        if (!exists(key))
            set(key, object);
    }

    @Override
    public void set(String key, Object object) {
        Util.writeObjectToFile(object, "cabba/" + name + "/" + key);
    }

    @Override
    public String get(String key) {
        return (String) Util.readFileToObject("cabba/" + name + "/" + key);
    }

    @Override
    public Object getObject(String key) {
        return Util.readFileToObject("cabba/" + name + "/" + key);
    }

    @Override
    public void del(String key) {
        new File("cabba/" + name + "/" + key).delete();
    }

    @Override
    public boolean exists(String... keys) {
        for (String key : keys)
            if (!new File("cabba/" + name + "/" + key).exists())
                return false;

        return true;
    }

    @Override
    public boolean hexists(String key, String... hashKeys) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        HashMap<String, String> hashMap = !(object instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) object;

        for (String loopKey : hashKeys)
            if (!hashMap.containsKey(loopKey))
                return false;

        return true;
    }

    @Override
    public void hset(String key, String hashKey, String value) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        HashMap<String, String> hashMap = !(object instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) object;

        hashMap.put(hashKey, value);
        Util.writeObjectToFile(hashMap, "cabba/" + name + "/" + key);
    }

    @Override
    public String hget(String key, String hashKey) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        HashMap<String, String> hashMap = !(object instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) object;

        return hashMap.get(hashKey);
    }

    @Override
    public void hdel(String key, String hashkey) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        HashMap<String, String> hashMap = !(object instanceof HashMap) ?
                new HashMap<>() :
                (HashMap<String, String>) object;

        hashMap.remove(hashkey);
        Util.writeObjectToFile(hashMap, "cabba/" + name + "/" + key);
    }

    @Override
    public void sadd(String key, String value) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        Set<String> set = !(object instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) object;

        set.add(value);
        Util.writeObjectToFile(set, "cabba/" + name + "/" + key);
    }

    @Override
    public void srem(String key, String value) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        Set<String> set = !(object instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) object;

        set.remove(value);
        Util.writeObjectToFile(set, "cabba/" + name + "/" + key);
    }

    @Override
    public Set<String> smembers(String key) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        return !(object instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) object;
    }

    @Override
    public boolean scontains(String key, String value) {
        Object object = Util.readFileToObject("cabba/" + name + "/" + key);

        Set<String> set = !(object instanceof HashSet) ?
                new HashSet<>() :
                (Set<String>) object;

        return set.contains(value);
    }

    @Override
    public void delete() {
        try {
            FileUtils.deleteDirectory(new File("cabba/" + name));
            Cabba.getDatabases().remove(this);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public DiskDatabase(String name) {
        this.name = name;
        new File("cabba/" + name).mkdir();
        Cabba.getDatabases().put(name, this);
    }
}