package me.meiallu.database;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import me.meiallu.Cabba;
import me.meiallu.messaging.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

public class Handler extends ChannelInboundHandlerAdapter {

    private final String password;

    @Override
    @SneakyThrows
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf passwordBuf = Unpooled.copiedBuffer(password, StandardCharsets.UTF_8);
        ctx.channel().writeAndFlush(passwordBuf);

        Thread.sleep(500);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = (ByteBuf) msg;

            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            Message message = (Message) objectStream.readObject();

            if (message.sender() != null) {
                String destiny = message.destiny();
                String sender = message.sender();
                Object[] objects = message.objects();

                Cabba.getListeners().get(destiny).onReceive(sender, objects);
                return;
            }

            Cabba.setResponse(message);
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        throw new RuntimeException(cause);
    }

    public Handler(String password) {
        this.password = password;
    }
}