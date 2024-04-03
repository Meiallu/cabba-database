package me.meiallu.cabbadb.util;

import lombok.Getter;

@Getter
public enum LogType {

    INFO("[INFO] "),
    WARN("[WARN] "),
    ERROR("[ERROR] ");

    private final String prefix;

    LogType(String prefix) {
        this.prefix = prefix;
    }
}
