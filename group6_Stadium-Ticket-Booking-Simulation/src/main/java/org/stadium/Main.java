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
        System.out.println("======================================================================");
        System.out.println("   HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING)");
        System.out.println("                        NHÓM 6 - LAB211                               ");
        System.out.println("======================================================================\n");

        MainView mainView = new MainView();
        mainView.start();
    }
}
