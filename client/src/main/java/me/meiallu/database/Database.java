package me.meiallu.database;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.meiallu.Cabba;
import me.meiallu.data.Action;
import me.meiallu.data.Request;

import java.io.*;
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
        Request request = new Request(Action.GETDEL, name);
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

    public Set<String> smembers(String key) {
        return (Set<String>) getObject(key);
    }

    public boolean scontains(String key, String value) {
        Request request = new Request(Action.SCONTAINS, name, key, value);
        return (boolean) sendRequest(request);
    }

    public Object getObject(ByteBuf byteBuf) {
        try {
            byte[] data = ByteBufUtil.getBytes(byteBuf);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            return objectStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Object sendRequest(Request request) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(request);
            objectStream.close();

            ByteBuf byteBuf = Unpooled.wrappedBuffer(byteStream.toByteArray());
            Cabba.getChannel().writeAndFlush(byteBuf);

            while (Cabba.getResponse() == null)
                Thread.onSpinWait();

            Object received = Cabba.getResponse().objects()[0];
            Cabba.setResponse(null);

            return received;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Database(String name) {
        this.name = name;
    }
}
