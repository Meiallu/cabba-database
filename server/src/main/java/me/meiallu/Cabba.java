package me.meiallu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import me.meiallu.data.Config;
import me.meiallu.database.Database;
import me.meiallu.handler.LoginHandler;
import me.meiallu.logging.LogType;
import me.meiallu.logging.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
                Cabba.writeObjectToFile(databases, "cabba/memory.dump");
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
        Logger.log(LogType.INFO, "Database initialized successfully. Listening on port " + config.port + ".");
    }

    public static Object readFileToObject(String path) {
        try {
            File file = new File(path);
            InputStream fileAsStream = new FileInputStream(file);

            String data = new String(fileAsStream.readAllBytes(), StandardCharsets.UTF_8);
            byte[] byteArray = Base64.getDecoder().decode(data);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            return objectStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }
    }

    public static void writeObjectToFile(Object object, String path) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

            objectStream.writeObject(object);
            objectStream.close();

            byte[] bytes = byteStream.toByteArray();
            String value = Base64.getEncoder().encodeToString(bytes);

            FileUtils.writeStringToFile(new File(path), value, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
