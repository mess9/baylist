package org.baylist.util.log;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtil {

    public static String reduceEmptyLines(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                result.append(line).append("\n");
            }
        }
        if (!result.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public static String reduceEmptyLines(StringBuilder sb) {
        String[] lines = sb.toString().split("\n");
        sb.setLength(0); // Очистим StringBuilder
        for (String line : lines) {
            if (!line.trim().isEmpty()) { // Проверяем, что строка не пустая
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

}
