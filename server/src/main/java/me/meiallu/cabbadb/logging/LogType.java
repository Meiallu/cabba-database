package me.meiallu.cabbadb.logging;

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
