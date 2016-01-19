package com.bradenhart.hcdemoui;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradenhart on 18/01/16.
 */
public enum Difficulty {

    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3),
    INSANE("Insane", 4);

    private String name;
    private final Integer value;
    private static Map<Integer, Difficulty> mappedValues;

    static {
        mappedValues = new HashMap<>();
        for(Difficulty difficulty : Difficulty.values()) {
            mappedValues.put(difficulty.value, difficulty);
        }
    }

    Difficulty(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

//    public static Difficulty lookUpByName(String name) {
//        return mappedValues.
//    }

    public static String[] getNames() {
        int size = Difficulty.values().length;
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = Difficulty.values()[i].getName();
        }
        return names;
    }

}
