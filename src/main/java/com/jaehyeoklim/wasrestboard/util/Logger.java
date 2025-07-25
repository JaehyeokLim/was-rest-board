package main.java.com.jaehyeoklim.wasrestboard.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Logger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");

    public static void log(Object obj) {
        String time = LocalTime.now().format(formatter);
        System.out.printf("%s [%20s] %s\n", time, Thread.currentThread().getName(), obj);
    }
}
