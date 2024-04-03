package me.meiallu.cabbadb.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.meiallu.cabbadb.Cabba;
import me.meiallu.cabbadb.util.LogType;
import me.meiallu.cabbadb.util.Util;

import java.nio.charset.StandardCharsets;

public class LoginHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String password = byteBuf.toString(StandardCharsets.UTF_8);

        if (!Cabba.getConfig().require_pass || password.equals(Cabba.getConfig().password)) {
            ctx.pipeline().addAfter("login", "action", new ActionHandler());
            ctx.pipeline().remove("login");

            Util.log(LogType.INFO, "Client connection attempt successful.");
        } else {
            ctx.flush();
            ctx.close();

            Util.log(LogType.INFO, "Client connection attempt failed, using \"" + password + "\".");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        throw new RuntimeException(cause);
    }
}
