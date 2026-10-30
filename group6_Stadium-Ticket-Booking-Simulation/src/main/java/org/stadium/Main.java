package org.stadium;

import org.stadium.controller.MainController;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
        }
        MainController app = new MainController();
        app.start();
    }
}
