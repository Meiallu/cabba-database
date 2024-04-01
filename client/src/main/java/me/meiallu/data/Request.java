package me.meiallu.data;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class Request implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

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
