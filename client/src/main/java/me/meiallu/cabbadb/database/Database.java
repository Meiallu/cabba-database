package me.meiallu.cabbadb.database;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.meiallu.cabbadb.Cabba;
import me.meiallu.cabbadb.data.Action;
import me.meiallu.cabbadb.data.Request;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class Database {

    private final String name;

    public boolean rename(String oldKey, String newKey) {
        Request request = new Request(Action.RENAME, name, oldKey, newKey);
        return (boolean) sendRequest(request);
    }

    public Set<String> keys() {
        Request request = new Request(Action.KEYS, name);
        return (Set<String>) sendRequest(request);
    }

    public long dbSize() {
        Request request = new Request(Action.DBSIZE, name);
        return (long) sendRequest(request);
    }

    public String getDel(String key) {
        return (String) getDelObject(key);
    }

    public Object getDelObject(String key) {
        Request request = new Request(Action.GETDEL, name, key);
        return sendRequest(request);
    }

    public String getSet(String key, String value) {
        return (String) getSetObject(key, value);
    }

    public Object getSetObject(String key, Object object) {
        Request request = new Request(Action.GETSET, object, name, key);
        return sendRequest(request);
    }

    public boolean insert(String key, Object object) {
        Request request = new Request(Action.INSERT, object, name, key);
        return (boolean) sendRequest(request);
    }

    public boolean set(String key, String value) {
        Request request = new Request(Action.SET, name, key, value);
        return (boolean) sendRequest(request);
    }

    public boolean set(String key, Object object) {
        Request request = new Request(Action.OSET, object, name, key);
        return (boolean) sendRequest(request);
    }

    public String get(String key) {
        Request request = new Request(Action.GET, name, key);
        return (String) sendRequest(request);
    }

    public Object getObject(String key) {
        Request request = new Request(Action.OGET, name, key);
        return sendRequest(request);
    }

    public boolean del(String key) {
        Request request = new Request(Action.DEL, name, key);
        return (boolean) sendRequest(request);
    }

    public boolean exists(String... key) {
        Request request = new Request(Action.EXISTS, name, key);
        return (boolean) sendRequest(request);
    }

    public boolean hexists(String key, String... hashkey) {
        Request request = new Request(Action.HEXISTS, hashkey, name, key);
        return (boolean) sendRequest(request);
    }

    public boolean hset(String key, String hashKey, String value) {
        Request request = new Request(Action.HSET, name, key, hashKey, value);
        return (boolean) sendRequest(request);
    }

    public String hget(String key, String hashKey) {
        Request request = new Request(Action.HGET, name, key, hashKey);
        return (String) sendRequest(request);
    }

    public boolean hdel(String key, String hashkey) {
        Request request = new Request(Action.HDEL, name, key, hashkey);
        return (boolean) sendRequest(request);
    }

    public boolean sadd(String key, String value) {
        Request request = new Request(Action.SADD, name, key, value);
        return (boolean) sendRequest(request);
    }

    public boolean srem(String key, String value) {
        Request request = new Request(Action.SREM, name, key, value);
        return (boolean) sendRequest(request);
    }

    public ArrayList<String> smembers(String key) {
        return (ArrayList<String>) getObject(key);
    }

    public boolean scontains(String key, String value) {
        Request request = new Request(Action.SCONTAINS, name, key, value);
        return (boolean) sendRequest(request);
    }

    public Object sendRequest(Request request) {
        String gson = new Gson().toJson(request);
        byte[] data = gson.getBytes(StandardCharsets.UTF_8);

        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        Cabba.getChannel().writeAndFlush(byteBuf);

        while (Cabba.getResponse() == null)
            Thread.onSpinWait();

        Object object = Cabba.getResponse().getObject();
        Cabba.setResponse(null);

        return object;
    }

    public Database(String name) {
        this.name = name;
    }
}
