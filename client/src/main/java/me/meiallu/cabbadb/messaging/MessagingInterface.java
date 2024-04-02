package me.meiallu.cabbadb.messaging;

public interface MessagingInterface {

    void onReceive(String from, Object... objects);
}
