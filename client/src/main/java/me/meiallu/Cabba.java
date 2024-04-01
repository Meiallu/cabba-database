package me.meiallu;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import me.meiallu.data.Action;
import me.meiallu.data.Request;
import me.meiallu.database.Database;
import me.meiallu.database.Handler;
import me.meiallu.messaging.Message;
import me.meiallu.messaging.MessagingInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Cabba {

    @Getter
    @Setter
    private static Handler receiver;
    @Getter
    private static Channel channel;
    @Setter
    @Getter
    private static volatile Message response;
    @Getter
    private static HashMap<String, MessagingInterface> listeners;

    public static void connect(String host, int port, String password) {
        channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new Handler(password));
                    }
                })
                .connect(host, port).channel();

        channel.closeFuture();
        listeners = new HashMap<>();
    }

    public static void addMessagingChannel(String name, MessagingInterface listener) {
        Request request = new Request(Action.ADD_CHANNEL, name);
        Cabba.sendRequest(request);

        listeners.put(name, listener);
    }

    public static void sendMessage(String to, String from, Object... objects) {
        Request request = new Request(Action.MESSAGE, objects, to, from);
        sendRequest(request);
    }

    public static Database createMemoryDatabase(String name) {
        Request request = new Request(Action.CREATE_MEMORY, name);
        sendRequest(request);
        return new Database(name);
    }

    public static Database createDiskDatabase(String name) {
        Request request = new Request(Action.CREATE_DISK, name);
        sendRequest(request);
        return new Database(name);
    }

    public static void deleteDatabase(String name) {
        Request request = new Request(Action.DELETE_DATABASE, name);
        sendRequest(request);
    }

    public static void sendRequest(Request request) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(request);
            objectStream.close();

            ByteBuf byteBuf = Unpooled.wrappedBuffer(byteStream.toByteArray());
            Cabba.getChannel().writeAndFlush(byteBuf);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Database getDatabase(String name) {
        return new Database(name);
    }
}
