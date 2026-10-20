package org.stadium;

import org.stadium.view.MainView;

/**
 * Điểm khởi chạy chính của hệ thống mô phỏng đặt vé sân vận động.
 * Khởi chạy giao diện dòng lệnh MainView trung tâm (Tuần 6 - View Layer).
 */
public class Main {

    public static void main(String[] args) {
        MainView mainView = new MainView();
        mainView.start();
    }
}
