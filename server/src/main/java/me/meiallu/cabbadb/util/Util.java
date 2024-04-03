package me.meiallu.cabbadb.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Util {

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

    public static void log(LogType type, String msg) {
        System.out.println(type.getPrefix() + msg);
    }
}
