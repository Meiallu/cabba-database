package me.meiallu.cabbadb;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import me.meiallu.cabbadb.data.Config;
import me.meiallu.cabbadb.database.Database;
import me.meiallu.cabbadb.handler.LoginHandler;
import me.meiallu.cabbadb.util.LogType;
import me.meiallu.cabbadb.util.Util;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Cabba {

    @Getter
    private static Config config;
    @Getter
    @Setter
    private static HashMap<String, Database> databases;
    @Getter
    private static HashMap<String, Channel> channels;

    public static void main(String[] args) throws Exception {
        config = Config.load();
        channels = new HashMap<>();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Util.writeObjectToFile(databases, "cabba/memory.dump");
            }
        }, 0, config.save_delay);

        Channel channel = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast("login", new LoginHandler());
                    }
                })
                .bind(config.port).channel();

        channel.closeFuture();
        Util.log(LogType.INFO, "Database initialized successfully. Listening on port " + config.port + ".");
    }
}
