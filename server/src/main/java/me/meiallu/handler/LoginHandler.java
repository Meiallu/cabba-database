package me.meiallu.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.meiallu.Cabba;
import me.meiallu.logging.LogType;
import me.meiallu.logging.Logger;

import java.nio.charset.StandardCharsets;

public class LoginHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String password = byteBuf.toString(StandardCharsets.UTF_8);

        if (!Cabba.getConfig().require_pass || password.equals(Cabba.getConfig().password)) {
            ctx.pipeline().addAfter("login", "action", new ActionHandler());
            ctx.pipeline().remove("login");

            Logger.log(LogType.INFO, "Client connection attempt successful.");
        } else {
            ctx.flush();
            ctx.close();

            Logger.log(LogType.INFO, "Client connection attempt failed, using \"" + password + "\".");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        throw new RuntimeException(cause);
    }
}
