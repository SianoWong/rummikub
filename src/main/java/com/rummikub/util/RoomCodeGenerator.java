package com.rummikub.util;

import java.time.LocalTime;

public class RoomCodeGenerator {

    private static final int TIME_LENGTH = 4;
    private static final int USER_LENGTH = 4;
    private static final int RADIX = 36;

    private RoomCodeGenerator() {
    }

    public static String generate(Long userId) {
        int secondOfDay = LocalTime.now().toSecondOfDay();
        String timePart = leftPad(Integer.toString(secondOfDay, RADIX).toUpperCase(), TIME_LENGTH);
        String userPart = leftPad(Long.toString(userId, RADIX).toUpperCase(), USER_LENGTH);
        return timePart + userPart;
    }

    private static String leftPad(String value, int length) {
        if (value.length() >= length) {
            return value;
        }
        return "0".repeat(length - value.length()) + value;
    }
}
