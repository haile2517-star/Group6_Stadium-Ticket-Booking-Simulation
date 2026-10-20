package org.stadium;

import org.stadium.view.MainView;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Điểm khởi chạy chính của hệ thống mô phỏng đặt vé sân vận động.
 * Khởi chạy giao diện dòng lệnh MainView trung tâm (Tuần 6 - View Layer).
 */
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
