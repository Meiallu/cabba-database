package me.meiallu.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.meiallu.Cabba;
import me.meiallu.data.Action;
import me.meiallu.data.Request;
import me.meiallu.database.Database;
import me.meiallu.database.DiskDatabase;
import me.meiallu.database.MemoryDatabase;
import me.meiallu.logging.LogType;
import me.meiallu.logging.Logger;
import me.meiallu.messaging.Message;

import java.io.*;

public class ActionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] data = ByteBufUtil.getBytes(byteBuf);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            Request request = (Request) objectStream.readObject();
            Database database = Cabba.getDatabases().get(request.getValues()[0]);

            Action action = Action.values()[request.getAction()];

            switch (action) {
                case RENAME:
                    database.rename(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case KEYS:
                    sendMessage(ctx.channel(), null, null, database.keys());
                    break;
                case DBSIZE:
                    sendMessage(ctx.channel(), null, null, database.dbSize());
                    break;
                case GETDEL:
                    Object getDelObject = database.getDelObject(request.getValues()[1]);
                    sendMessage(ctx.channel(), null, null, getDelObject);
                    break;
                case GETSET:
                    Object getSetObject = database.getSetObject(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, getSetObject);
                    break;
                case INSERT:
                    database.insert(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case SET:
                    database.set(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case OSET:
                    database.set(request.getValues()[1], request.getObject());
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case GET:
                    sendMessage(ctx.channel(), null, null, database.get(request.getValues()[1]));
                    break;
                case OGET:
                    sendMessage(ctx.channel(), null, null, database.getObject(request.getValues()[1]));
                    break;
                case DEL:
                    database.del(request.getValues()[1]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case EXISTS:
                    sendMessage(ctx.channel(), null, null, database.exists(request.getValues()[1]));
                    break;
                case HEXISTS:
                    sendMessage(ctx.channel(), null, null, database.hexists(request.getValues()[1], request.getValues()[2]));
                    break;
                case HSET:
                    database.hset(request.getValues()[1], request.getValues()[2], request.getValues()[3]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case HGET:
                    sendMessage(ctx.channel(), null, null, database.hget(request.getValues()[1], request.getValues()[2]));
                    break;
                case HDEL:
                    database.hdel(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case SADD:
                    database.sadd(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case SREM:
                    database.srem(request.getValues()[1], request.getValues()[2]);
                    sendMessage(ctx.channel(), null, null, true);
                    break;
                case SCONTAINS:
                    sendMessage(ctx.channel(), null, null, database.scontains(request.getValues()[1], request.getValues()[2]));
                    break;
                case ADD_CHANNEL:
                    Cabba.getChannels().put(request.getValues()[0], ctx.channel());
                    break;
                case MESSAGE:
                    sendMessage(Cabba.getChannels().get(request.getValues()[0]), request.getValues()[0], request.getValues()[1], request.getObject());
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
            Logger.log(LogType.INFO, "Received '" + action.name() + "' request.");
        } catch (IOException | ClassNotFoundException exception) {
            sendMessage(ctx.channel(), null, null, false);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        throw new RuntimeException(cause);
    }

    public void sendMessage(Channel channel, String destiny, String sender, Object object) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(new Message(destiny, sender, object));
            objectStream.close();

            ByteBuf buf = Unpooled.wrappedBuffer(byteStream.toByteArray());
            channel.writeAndFlush(buf);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
