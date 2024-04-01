package me.meiallu.messaging;

public interface MessagingInterface {

    void onReceive(String from, Object... objects);
}
