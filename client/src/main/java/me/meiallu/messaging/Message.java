package me.meiallu.messaging;

import java.io.Serializable;

public record Message(String destiny, String sender, Object... objects) implements Serializable {
}