package me.meiallu.cabbadb.logging;

public class Logger {

    public static void log(LogType type, String msg) {
        System.out.println(type.getPrefix() + msg);
    }
}
