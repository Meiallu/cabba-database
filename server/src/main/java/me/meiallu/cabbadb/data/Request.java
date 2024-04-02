package me.meiallu.cabbadb.data;

import lombok.Getter;

@Getter
public class Request {

    private final int action;
    private final String[] values;
    private Object object;

    public Request(Action action, String... values) {
        this.action = action.ordinal();
        this.values = values;
    }

    public Request(Action action, Object object, String... values) {
        this.action = action.ordinal();
        this.values = values;
        this.object = object;
    }
}