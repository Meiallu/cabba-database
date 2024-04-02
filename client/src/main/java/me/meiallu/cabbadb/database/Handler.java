package me.meiallu.cabbadb.database;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import me.meiallu.cabbadb.Cabba;
import me.meiallu.cabbadb.data.Action;
import me.meiallu.cabbadb.data.Request;

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
        ByteBuf byteBuf = (ByteBuf) msg;
        String gson = byteBuf.toString(StandardCharsets.UTF_8);

        Request request = new Gson().fromJson(gson, Request.class);

        if (Action.values()[request.getAction()] == Action.ANSWER) {
            Cabba.setResponse(request);
            return;
        }

        String destiny = request.getValues()[0];
        String sender = request.getValues()[1];

        Object[] objects = (Object[]) request.getObject();
        Cabba.getListeners().get(destiny).onReceive(sender, objects);
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