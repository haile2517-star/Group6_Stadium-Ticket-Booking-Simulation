package org.stadium;

import org.stadium.view.AuthView;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  CHƯƠNG TRÌNH MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG - GROUP 6 (LAB211)    ");
        System.out.println("  Phân hệ: Xác thực & Quản lý tài khoản (Auth / Account)          ");
        System.out.println("==================================================================");
        AuthView authView = new AuthView();
        authView.start();
    }
}
