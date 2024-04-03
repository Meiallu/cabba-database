package me.meiallu.cabbadb.handler;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.meiallu.cabbadb.Cabba;
import me.meiallu.cabbadb.data.Action;
import me.meiallu.cabbadb.data.Request;
import me.meiallu.cabbadb.database.Database;
import me.meiallu.cabbadb.database.DiskDatabase;
import me.meiallu.cabbadb.database.MemoryDatabase;
import me.meiallu.cabbadb.util.LogType;
import me.meiallu.cabbadb.util.Util;

import java.nio.charset.StandardCharsets;

public class ActionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String gson = byteBuf.toString(StandardCharsets.UTF_8);

        Request request = new Gson().fromJson(gson, Request.class);
        Database database = Cabba.getDatabases().get(request.getValues()[0]);

        Action action = Action.values()[request.getAction()];

        switch (action) {
            case RENAME:
                database.rename(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case KEYS:
                answer(ctx.channel(), database.keys());
                break;
            case DBSIZE:
                answer(ctx.channel(), database.dbSize());
                break;
            case GETDEL:
                Object getDelObject = database.getDelObject(request.getValues()[1]);
                answer(ctx.channel(), getDelObject);
                break;
            case GETSET:
                Object getSetObject = database.getSetObject(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), getSetObject);
                break;
            case INSERT:
                database.insert(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case SET:
                database.set(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case OSET:
                database.set(request.getValues()[1], request.getObject());
                answer(ctx.channel(), true);
                break;
            case GET:
                answer(ctx.channel(), database.get(request.getValues()[1]));
                break;
            case OGET:
                answer(ctx.channel(), database.getObject(request.getValues()[1]));
                break;
            case DEL:
                database.del(request.getValues()[1]);
                answer(ctx.channel(), true);
                break;
            case EXISTS:
                answer(ctx.channel(), database.exists(request.getValues()[1]));
                break;
            case HEXISTS:
                answer(ctx.channel(), database.hexists(request.getValues()[1], request.getValues()[2]));
                break;
            case HSET:
                database.hset(request.getValues()[1], request.getValues()[2], request.getValues()[3]);
                answer(ctx.channel(), true);
                break;
            case HGET:
                answer(ctx.channel(), database.hget(request.getValues()[1], request.getValues()[2]));
                break;
            case HDEL:
                database.hdel(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case SADD:
                database.sadd(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case SREM:
                database.srem(request.getValues()[1], request.getValues()[2]);
                answer(ctx.channel(), true);
                break;
            case SCONTAINS:
                answer(ctx.channel(), database.scontains(request.getValues()[1], request.getValues()[2]));
                break;
            case ADD_CHANNEL:
                Cabba.getChannels().put(request.getValues()[0], ctx.channel());
                break;
            case MESSAGE:
                String messageGson = new Gson().toJson(new Request(Action.MESSAGE, request.getObject(), request.getValues()[0], request.getValues()[1]));
                byte[] messageData = messageGson.getBytes(StandardCharsets.UTF_8);

                ByteBuf messageBuf = Unpooled.wrappedBuffer(messageData);
                Cabba.getChannels().get(request.getValues()[0]).writeAndFlush(messageBuf);
                break;
            case DELETE_DATABASE:
                database.delete();
                break;
            case CREATE_MEMORY:
                new MemoryDatabase(request.getValues()[0]);
                break;
            case CREATE_DISK:
                new DiskDatabase(request.getValues()[0]);
                break;
        }
        Util.log(LogType.INFO, "Received '" + action.name() + "' request.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        throw new RuntimeException(cause);
    }

    public void answer(Channel channel, Object object) {
        String gson = new Gson().toJson(new Request(Action.ANSWER, object, null, null));
        byte[] data = gson.getBytes(StandardCharsets.UTF_8);

        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        channel.writeAndFlush(byteBuf);
    }
}
