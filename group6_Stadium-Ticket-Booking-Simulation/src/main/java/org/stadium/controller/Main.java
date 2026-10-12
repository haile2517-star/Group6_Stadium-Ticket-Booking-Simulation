package model;


import controller.StadiumController1;
import model.Section;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        StadiumController1 controller = new StadiumController1();

        System.out.println("=== STADIUM SECTIONS ===");

        List<Section> sections = controller.getSections("STA01");

        if (sections.isEmpty()) {
            System.out.println("No sections found.");
        } else {
            for (Section section : sections) {
                System.out.println(
                        section.getSectionId() + " - "
                        + section.getSectionName()
                );
            }
        }

        System.out.println("\n=== VIP SEAT MAP ===");
        System.out.println(controller.buildSeatMap("SEC01"));
    }
}
