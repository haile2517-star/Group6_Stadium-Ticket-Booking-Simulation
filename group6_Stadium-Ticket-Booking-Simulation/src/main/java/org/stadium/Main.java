package org.stadium;

import org.stadium.view.MainView;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
        }
        MainView mainView = new MainView();
        mainView.start();
    }
}
