package me.meiallu;

import me.meiallu.cabbadb.Cabba;

public class Main {

    public static void main(String[] args) {
        Cabba.connect("localhost", 6249, "Lvg8.JjTn$_ha]b");
        Cabba.createDiskDatabase("database");
    }
}