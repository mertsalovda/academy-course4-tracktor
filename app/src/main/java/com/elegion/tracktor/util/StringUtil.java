package com.elegion.tracktor.util;

import com.elegion.tracktor.data.model.Track;

import java.util.Locale;

public class StringUtil {

    public static String getTimeText(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getDistanceText(double value) {
        return round(value, 0) + " м.";
    }

    public static String round(double value, int places) {
        return String.format("%." + places + "f", value);
    }

    public static String getTextForShare(Track track, String[] actionType) {
        return new StringBuilder()
                .append("Время: " + StringUtil.getTimeText(track.getDuration()))
                .append("\n")
                .append("Расстояние: " + MathUtils.round(track.getDistance(), 2) + " м")
                .append("\n")
                .append("Средняя скорость: " + MathUtils.round(track.getAverageSpeed(), 2) + " км/ч")
                .append("\n")
                .append("Потрачено калорий: " + track.getCalories() + " ккал")
                .append("\n")
                .append("Вид активности: " + actionType[track.getActionType()])
                .append("\n")
                .append("Комментарий: " + track.getComment())
                .toString();
    }
}
